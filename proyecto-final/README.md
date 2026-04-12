## 🛠️ Tecnologías Utilizadas
* **Lenguaje:** Java 21 (LTS)
* **Framework:** Spring Boot 3.5.13
* **Gestor de dependencias:** Gradle (Groovy)
* **Arquitectura:** Spring Cloud (Config, Netflix Eureka, Gateway)

## 🏗️ Fase 1: Infraestructura Base Configurada

El proyecto utiliza un enfoque de configuración centralizada y descubrimiento de servicios dinámico. Actualmente, la infraestructura base consta de 3 servidores principales:

### 1. 🧠 Config Server (Puerto `8888`)
Actúa como el cerebro de las configuraciones. Todos los microservicios se conectan a este servidor al arrancar para descargar sus variables de entorno, puertos y credenciales.
* **Origen de datos:** Lee los archivos `.yml` directamente de la carpeta local `/config-repo` ubicada en la raíz del proyecto (Perfil `native`).

### 2. 📖 Discover Server - Eureka (Puerto `8761`)
Actúa como el "directorio telefónico" de la plataforma. Los microservicios se registran aquí mediante "latidos" (heartbeats). Permite que los servicios se encuentren entre sí por su nombre (ej. `catalog-service`) sin necesidad de conocer sus direcciones IP fijas (Alta Disponibilidad).

### 3. 🚪 API Gateway (Puerto `8080`)
Es el único punto de entrada para el cliente (Frontend). Enruta las peticiones HTTP al microservicio correspondiente consultando primero a Eureka. Aquí se centralizará la validación de seguridad (Tokens JWT) más adelante.

## 📂 Estructura del Proyecto (Monorepo)

```text
hotel-platform/
│
├── config-repo/                # ⚙️ Archivos .yml de configuración centralizada
│   ├── api-gateway.yml
│   └── discover-server.yml
│
├── config-server/              # Servidor de configuración de Spring Cloud
├── discover-server/            # Servidor de descubrimiento (Eureka)
├── api-gateway/                # Enrutador principal de peticiones
│
├── catalog-service/            # Lógica de propiedades (MongoDB)
├── security-service/           # Autenticación JWT (MongoDB)
├── booking-service/            # Gestión de reservas (PostgreSQL)
├── review-service/             # Reseñas de usuarios (PostgreSQL)
└── notification-service/       # Reportes Jasper y Correos (MongoDB)