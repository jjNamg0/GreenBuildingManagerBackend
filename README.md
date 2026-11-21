# GreenBuildingManagerBackend

API REST de **Green Building Manager**, una aplicación para administrar conjuntos residenciales de Cundinamarca: conjuntos, unidades, espacios comunes, reservas, eventos, invitaciones y un asistente con IA.

Este repositorio es el **back**. El front en Angular está en el repositorio **GreenBuildingManagerFrontend**.

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje |
| Spring Boot 3.4.5 | Framework (web, JPA, seguridad, correo) |
| Spring Security + JWT (jjwt 0.11.5) | Inicio de sesión y roles |
| BCrypt | Contraseñas guardadas como hash |
| springdoc-openapi (Swagger) | Documentación y pruebas de la API |
| ModelMapper | Conversión entre entidades y DTOs |
| Gson + HttpClient | Llamadas a servicios externos (rutas e IA) |
| JUnit 5 + Mockito | Pruebas unitarias |
| Maven (`mvnw`) | Compilación; se empaqueta como `war` |

## Requisitos

- **Java 21.** Si tu IDE o Maven usa Java 25 o 26, las pruebas con Mockito fallan.
- Internet, para el correo de Gmail, las rutas de OpenStreetMap y la IA de NVIDIA.

## Configuración (`application.properties`)

| Propiedad | Para qué sirve |
|---|---|
| `server.port` | Puerto del back (8080) |
| `jwt.secret` | Clave que firma los tokens de sesión. Debe ser larga y secreta |
| `app.admin.username` / `app.admin.password` | Administrador que se crea al arrancar si no existe |
| `app.datos-ejemplo` | `true` carga los conjuntos de ejemplo al arrancar si todavía no hay conjuntos; `false` no carga nada |
| `app.front.url` | Dirección del front, usada en los enlaces de correos e invitaciones |
| `app.correo.remitente` | Correo que aparece como remitente |
| `app.ruta.url` / `app.ruta.apikey` | Servidor de rutas (OSRM de OpenStreetMap, no pide llave) |
| `app.ia.url` / `app.ia.modelo` / `app.ia.apikey` | API de chat de NVIDIA para el asistente |
| `app.ia.max-caracteres` / `max-historial` / `pausa-ms` | Límites del asistente: largo de la pregunta, mensajes de contexto y espera entre preguntas |
| `spring.mail.*` | Servidor de Gmail y su contraseña de aplicación |
| `springdoc.swagger-ui.*` | Opciones de Swagger |

## Estructura

```
src/main/java/co/edu/unbosque/GreenBuilding/
├── configuration/   Swagger, ModelMapper y carga inicial del administrador y los datos de ejemplo
├── controller/      Endpoints REST
├── dto/             Objetos que entran y salen de la API
├── exception/       Excepciones propias (una por tipo de dato inválido)
├── model/           Entidades JPA
├── repository/      Repositorios de Spring Data
├── security/        JWT, filtro de autenticación y reglas de acceso
├── service/         Lógica de negocio y llamadas externas
└── util/            Utilidades (validaciones, fechas, texto, distancias, AES)

src/test/java/co/edu/unbosque/GreenBuilding/
├── security/        Pruebas de JwtUtil
├── service/         Pruebas de los 15 servicios
└── util/            Pruebas de las utilidades y de ExceptionChecker
```

Cada petición sigue el mismo camino: el **controlador** recibe el DTO y lo valida con `ExceptionChecker`, el **servicio** aplica las reglas y devuelve un código `int` (0 = éxito, 1..n = cada error), y el controlador convierte ese código en la respuesta HTTP.

## Seguridad

- **Login con JWT:** `POST /auth/login` devuelve un token que dura 24 horas. El front lo envía en cada petición como `Authorization: Bearer <token>`.
- **Roles:** `ADMIN` (administración) y `USER` (residente). Las reglas por ruta están en `SecurityConfig`.
- **Contraseñas:** se guardan con BCrypt.
- **Intentos de login:** después de 5 contraseñas incorrectas seguidas, la cuenta queda bloqueada 15 minutos y el login responde `429`.
- **Códigos por correo:** verificación de la cuenta y recuperación de contraseña, válidos 15 minutos y con 5 intentos.
- **Auditoría:** cada acción queda registrada con usuario, fecha, IP y ubicación (si el navegador la envía).

## Validaciones

Todas están en `util/ExceptionChecker`. Si un dato no cumple, se lanza una excepción propia y el controlador responde `400` con el mensaje.

| Dato | Regla |
|---|---|
| Correo | Formato `usuario@dominio.com`, máximo 255 caracteres y de un dominio permitido: Gmail, Hotmail, Outlook, Live, MSN, Yahoo, iCloud, me.com, AOL, Proton o `unbosque.edu.co` (lista en `DOMINIOS_PERMITIDOS`) |
| Contraseña | 8 o más caracteres, con mayúscula, minúscula, número y símbolo |
| Teléfono | Exactamente 10 dígitos |
| Textos | Sin `<` ni `>`. Máximo 255 caracteres (nombres, direcciones), 500 (motivos), 1000 (URL de imagen) y 2000 (descripciones) |
| Imagen | Opcional; si viene, debe empezar por `http://` o `https://` |
| Fecha y hora | `aaaa-mm-dd` que exista y `HH:mm`; la hora de inicio antes que la de fin |
| Números | Capacidad, asistentes y cupo mayores que 0 |
| Coordenadas | Obligatorias en un conjunto y dentro de -90/90 y -180/180 |
| Invitaciones | De 1 a 30 correos por envío |

Además, los servicios revisan las reglas de negocio: no reservar en el pasado ni fuera del horario del espacio, no pasar la capacidad, no cruzar reservas con eventos, máximo 60 días de anticipación, respetar el cupo de los eventos, etc.

## Módulos de la API

| Ruta base | Qué hace | Quién |
|---|---|---|
| `/auth` | Registro, verificación, login, recuperar contraseña, logout | Público |
| `/user` | Perfil propio, gestión de usuarios, asignar unidad | Usuario / Admin |
| `/conjunto` | Conjuntos residenciales | Consulta pública, gestión Admin |
| `/unidad` | Apartamentos y casas | Admin |
| `/espacio` | Espacios comunes (salón, piscina, BBQ…) | Residente consulta, Admin gestiona |
| `/reserva` | Reservas de espacios, disponibilidad, aprobar, rechazar y cancelar | Residente / Admin |
| `/evento` | Eventos del conjunto y asistencia | Residente / Admin |
| `/invitacion` | Enlaces públicos y envío de invitaciones por correo | Público / Residente / Admin |
| `/recomendacion` | Eventos, espacios y conjuntos recomendados | Residente |
| `/ruta` | Ruta (carro, bicicleta o caminando) hasta un conjunto | Público |
| `/ia` | Asistente de ayuda con IA | Usuarios con sesión |
| `/auditoria` | Registro de acciones con filtros | Admin |
| `/resumen` | Resumen para el panel del administrador | Admin |

La lista completa de endpoints, con ejemplos, está en Swagger.

## Generar el `war` en Eclipse (Enterprise Java)

El proyecto ya está configurado como `war` en el `pom.xml` (`<packaging>war</packaging>`) y trae `ServletInitializer`, así que solo hay que empaquetarlo.

### 1. Revisar que Eclipse use Java 21

1. Clic derecho en el proyecto → **Properties** → **Java Build Path** → pestaña **Libraries**.
2. En **JRE System Library** debe decir **JavaSE-21**. Si no:
   - selecciónala → **Edit…** → **Execution environment** → **JavaSE-21** → **Finish** → **Apply and Close**;
   - si no aparece Java 21, agrégalo en **Window → Preferences → Java → Installed JREs → Add… → Standard VM** y elige la carpeta donde está instalado el JDK 21.

### 2. Actualizar el proyecto Maven

Clic derecho en el proyecto → **Maven** → **Update Project…** → marca el proyecto → **OK**.

Así Eclipse descarga las dependencias y aplica la configuración del `pom.xml`.

### 3. Crear la configuración para empaquetar

1. Clic derecho en el proyecto → **Run As** → **Maven build…** (la opción **con los tres puntos**).
2. Llena la ventana:
   - **Name:** `Generar war`
   - **Goals:** `clean package`
   - Marca la casilla **Skip Tests**. Las pruebas no van dentro del `war`, así que no hace falta correrlas para empaquetar.
3. En la pestaña **JRE**, revisa que esté seleccionado **Java 21** (no 25 ni 26).
4. Clic en **Run**.

### 4. Esperar el resultado

En la pestaña **Console** de Eclipse debe aparecer al final:

```text
[INFO] BUILD SUCCESS
```

### 5. Buscar el archivo

1. Clic derecho en el proyecto → **Refresh** (o F5).
2. Abre la carpeta **target**. Ahí está el archivo `.war`, por ejemplo `GreenBuilding-1.3.war`. El nombre lleva la `<version>` del `pom.xml`.

Ese es el archivo que se sube al servidor.

### 6. Las siguientes veces

La configuración queda guardada. Para volver a generar el `war`:
- Clic derecho en el proyecto → **Run As** → **Maven build** (sin los tres puntos) → elige **Generar war**.
- O usa la flecha del botón verde **Run** en la barra de arriba → **Generar war**.
