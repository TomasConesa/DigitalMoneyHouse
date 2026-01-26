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

🔧 Testing automatizado
El módulo `api-tests` es un proyecto independiente que utiliza:
- JUnit 5
- RestAssured
- Assertions con AssertJ


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


🚀 Sprint 2 – Funcionalidades implementadas

En el Sprint 2 se incorporaron nuevas funcionalidades orientadas a la gestión de cuentas, movimientos y tarjetas, junto con mejoras en testing y validación de reglas de negocio.

🔹 Gestión de información de cuenta
- Obtener información detallada de una cuenta (CVU, alias y saldo).
- Endpoint:
    - GET /accounts/{accountId}/info

🔹 Visualización de movimientos
- Consulta de los últimos 5 movimientos de una cuenta, ordenados del más reciente al más antiguo.
- Endpoint:
    - GET /accounts/{accountId}/transactions

🔹 Gestión de tarjetas
- Crear una tarjeta de débito o crédito.
    - POST /cards
- Asociar una tarjeta existente a una cuenta.
    - POST /accounts/{accountId}/cards
- Listar las tarjetas asociadas a una cuenta.
    - GET /cards/{accountId}/cards
- Eliminar una tarjeta asociada a una cuenta.
    - DELETE /accounts/{accountId}/cards/{cardId}

📌 Reglas de negocio implementadas:
- Una tarjeta solo puede estar asociada a una única cuenta.
- No se permite asociar una tarjeta que ya pertenece a otra cuenta (HTTP 409).
- Validaciones de existencia de cuenta y tarjeta (HTTP 404).

🔐 Seguridad
- Todos los endpoints están protegidos mediante JWT.
- Autorización vía header:
  Authorization: Bearer <token>


🚀 Sprint 3 – Funcionalidades implementadas

En el Sprint 3 se incorporaron funcionalidades orientadas a ingresar dinero en cuentas y consultar la actividad completa de la billetera, permitiendo visualizar el historial y el detalle de transferencias.

🔹 Historial completo de actividad de una cuenta

Como usuario, puedo ver toda la actividad realizada con mi billetera, desde la más reciente a la más antigua.

✅ Endpoint:

GET /accounts/{accountId}/activity

📌 Recibe: token JWT + id de cuenta
📌 Devuelve: movimientos históricos (ordenados por fecha descendente)

✅ Respuestas esperadas:
200 OK
400 Bad Request
403 Forbidden (sin permisos)
500 Internal Server Error

🔹 Detalle de una transferencia específica

Como usuario, puedo consultar el detalle de una actividad puntual (una transferencia en específico).

✅ Endpoint:

GET /accounts/{accountId}/activity/{transferId}

📌 Recibe: token JWT + id de cuenta + id transferencia
📌 Devuelve: detalle completo de la transferencia seleccionada

✅ Respuestas esperadas:
200 OK
400 Bad Request
403 Forbidden (sin permisos)
404 Not Found (id inexistente)
500 Internal Server Error

🔹 Ingreso de dinero a la billetera desde tarjeta existente

Como usuario, puedo ingresar dinero a mi cuenta utilizando una tarjeta registrada (débito/crédito).

✅ Endpoint:

POST /accounts/{accountId}/transactions/deposit

📌 Recibe: token JWT + detalle de la transferencia + monto
📌 Devuelve: confirmación de operación

✅ Respuestas esperadas:
201 Created
400 Bad Request
403 Forbidden (sin permisos)
404 Not Found (cuenta no existente)
500 Internal Server Error

🤖 Testing automatizado (Sprint 3)
Se agregaron los casos de prueba automatizados correspondientes a este sprint dentro del módulo api-tests.

🚀 Sprint 4 – Funcionalidades implementadas

En el Sprint 4 se incorporaron funcionalidades orientadas a transferir dinero entre cuentas, permitiendo enviar saldo disponible a otro usuario a través de CBU/CVU/alias, y consultar los últimos destinatarios utilizados.

🔹 Consulta de últimos destinatarios

Como usuario, puedo consultar rápidamente los últimos destinatarios a los que realicé transferencias.

✅ Endpoint:

GET /accounts/{accountId}/transferences

📌 Recibe: token JWT + id de la cuenta
📌 Devuelve: últimos destinatarios utilizados para transferencias

✅ Respuestas esperadas:
200 OK
400 Bad Request
500 Internal Server Error

🔹 Transferir dinero a otra cuenta (por CVU/alias)

Como usuario, puedo transferir dinero desde mi billetera a otra cuenta utilizando CBU/CVU/alias, siempre que tenga fondos suficientes.

✅ Endpoint:

POST /accounts/{accountId}/transferences

📌 Recibe: token JWT + id de la cuenta
📌 Devuelve: confirmación de transferencia realizada

✅ Respuestas esperadas:
200 OK
400 Bad Request
410 Gone (fondos insuficientes)
500 Internal Server Error

📌 Reglas de negocio implementadas:
Validación de fondos suficientes antes de confirmar la transferencia.
Validación de datos requeridos (monto, destinatario).

🔍 Testing exploratorio (Sprint 4)
Se realizó Testing Exploratorio sobre las funcionalidades incorporadas en este sprint.
Se entregó un documento con notas, alcance y organización del test exploratorio.




