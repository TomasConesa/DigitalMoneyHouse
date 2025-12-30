package com.digitalmoneyhouse.api_gateway.filter;

import com.digitalmoneyhouse.api_gateway.util.JwtUtil;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/login",
            "/users/register",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Obtenemos el path de la request
        String path = exchange.getRequest().getURI().getPath();

        // 2. Si es endpoint público, dejamos pasar sin validar token
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        // 3. Obtenemos el header Authorization
        List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);

        if (authHeaders.isEmpty() || !authHeaders.get(0).startsWith("Bearer ")) {
            // Si no hay Authorization o no empieza con "Bearer ", devolvemos 401
            return this.unauthorized(exchange, "Falta o es inválido el header Authorization");
        }

        // 4. Extraemos el token quitando el prefijo "Bearer "
        String token = authHeaders.get(0).substring(7);

        // 5. Validamos el token con JwtUtil
        if (!jwtUtil.isTokenValid(token)) {
            return this.unauthorized(exchange, "Token inválido o expirado");
        }

        // 6. Extraemos datos del token (claims)
        String userId = jwtUtil.getUserId(token);
        String email = jwtUtil.getEmail(token);

        // 7. Mutamos la request para agregar headers hacia los microservicios
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-User-Id", userId != null ? userId : "")
                .header("X-User-Email", email != null ? email : "")
                .build();

        // 8. Creamos un nuevo exchange con la request mutada
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        // 9. Continuamos la cadena de filtros con la request modificada
        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    // Helper para devolver un 401 UNAUTHORIZED con un mensaje simple
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}
