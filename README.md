📦 Digital Money House

Digital Money House (DMH) es una billetera digital desarrollada con arquitectura de microservicios.
Permite registrar usuarios, iniciar sesión mediante JWT, generar cuentas digitales con CVU y alias, realizar transferencias, agregar tarjetas, consultar saldos y movimientos.

🛠️ Tecnologías utilizadas:
- Java 17
- Spring Boot 3.5.5
- Spring Cloud (Eureka, Gateway, Feign)
- MySQL
- JPA/Hibernate
- JWT 
- Swagger/OpenAPI
- Git, GitHub
- Docker

Arquitectura del sistema:
Eureka Server (8761): Registro y descubrimiento de microservicios.

API Gateway (8080): Punto de entrada único.

Users-Service (8081): Gestión de usuarios.

Auth-Service (8082): Login y generación de JWT.

Account-Service (8083): Creación de CVU, alias y asignación de cuentas.

Base de datos MySQL compartida entre Users y Account.

Diagrama:
![Arquitectura DMH](docs/ArqDMH.png)

🚀 Guía de instalación y ejecución:
1️⃣ Clonar repositorio: 
git clone https://github.com/TomasConesa/DigitalMoneyHouse.git
cd DigitalMoneyHouse

2️⃣ Configurar variables de entorno:
Los microservicios requieren variables de entorno para funcionar correctamente (credenciales de BD, claves JWT).

📌 Importante:
El archivo .env del proyecto no se ejecuta automáticamente por Spring Boot y no debe subirse al repositorio.
Cada usuario debe definir estas variables en su propio entorno local (IntelliJ, Windows, Linux, Mac, Docker, etc.).

🔐 Variables necesarias por microservicio
Auth Service: 
JWT_SECRET=<clave secreta para firmar jwt>
JWT_EXPIRATION=3600000

Users Service y Account Service:
DB_URL=jdbc:mysql://<host>:3306/digital_money_house
DB_USERNAME=<usuario>
DB_PASSWORD=<contraseña>

3️⃣ Correr los servicios en IntelliJ en el siguiente orden:
  1️⃣ eureka-server
  2️⃣ api-gateway
  3️⃣ users-service
  4️⃣ account-service
  5️⃣ auth-service

4️⃣ Verificar que cada microservicio se registre en Eureka en: http://localhost:8761

5️⃣ Probar los endpoints en Postman o en Swagger desde:
http://localhost:8081/swagger-ui/index.html (Users Service).
http://localhost:8082/swagger-ui/index.html (Auth Service).
http://localhost:8083/swagger-ui/index.html (Account Service).

🛣️ Principales endpoints
Registro de usuarios: 
POST http://localhost:8080/users/register
Request: 
{
	  "name": "Kevin",
    "lastName": "Durant",
    "dni": "5757484389",
    "email": "kd@mail.com",
    "telephone": "8943785943789",
    "password": "kevindurant"
}
Response:
{
    "id": 34,
    "name": "Kevin",
    "lastName": "Durant",
    "dni": "5757484389",
    "email": "kd@mail.com",
    "telephone": "8943785943789",
    "accountResponse": {
        "accountId": 26,
        "cvu": "8952463860981875887255",
        "alias": "nube.bosque.arena"
    }
}
Verificar que:
El status sea 201/200.
La respuesta incluya los datos básicos del usuario.
Se haya creado la cuenta asociada.
Status 400 en caso de datos duplicados.

Login:
POST http://localhost:8080/auth/login
Request: 
{
	"email": "kd@mail.com",
  "password": "kevindurant"
}
Response: 
{
    "token": "Token generado",
    "roles": [Rol asociado] 
}
Verificar que: 
El status sea 200.
La respuesta devuelva un JWT (token de acceso).

Consultar cuenta por id:
GET http://localhost:8080/accounts/user/{userId}
Response: 
{
    "accountId": 10,
    "cvu": "7092880806685863729298",
    "alias": "océano.montaña.cielo"
}


🧪 Testing
Para facilitar la validación de los endpoints del sistema, el proyecto incluye una colección completa de Postman con todas las requests necesarias para probar el flujo.
Ruta: /docs/postman/DMH.postman_collection.json
Cómo usarla:
Levantar todos los microservicios desde IntelliJ.

Importar la colección en Postman desde File → Import.

Usar las requests ya configuradas apuntando al API Gateway (http://localhost:8080).

Agregar el JWT (en caso del logout) en el header Authorization: Bearer <token>.



💡 Decisiones Técnicas y Problemas Resueltos
✔️ Decisión: Uso de JWT para Autenticación

Se eligió JWT (JSON Web Tokens) por los siguientes motivos:

Es una solución ligera y rápida para microservicios.

No requiere mantener estado en el servidor.

Facilita la comunicación entre servicios detrás del API Gateway.

Permite escalar sin depender de sesiones compartidas.

En el futuro se puede migrar a Keycloak para manejar roles, permisos, SSO y administración centralizada.

✔️ Gestión de dependencias y compatibilidad con Spring Boot 3.5.x

Hubo problemas iniciales con:

Cambios en dependencias de Spring Cloud Gateway

Ajustes por migración de WebFlux / REST clásico

Compatibilidad entre springdoc-openapi y Spring Boot

Configuración YAML en microservicios separados

Se resolvió:

Utilizando la dependencia correcta para spring-cloud-gateway-server.

Reestructurando el application.yml para evitar conflictos.

Configurando Eureka para descubrimiento de servicios sin error.

Normalizando los puertos de cada microservicio.





