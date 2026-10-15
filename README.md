# 🔐 login — Java Web App (Spring Boot, WAR ejecutable con JSP)

Aplicación web Java del laboratorio de preparación **AWS Certified DevOps Engineer – Professional
(DOP-C02)**, bloque *Semana 3*. El repositorio aloja el artefacto de aplicación que luego se
compila y despliega mediante un pipeline CI/CD (build → artefacto → despliegue).

> **Cambio de arquitectura (v1 → v2):** el proyecto nació como WAR + JSP para un Tomcat externo.
> Ahora es una aplicación **Spring Boot 4.1.1** autocontenida que se ejecuta con **`java -jar`**
> (Tomcat embebido) **manteniendo las vistas JSP**.

---

## 🧱 Tecnología

| Elemento | Versión / detalle |
| :--- | :--- |
| Framework | **Spring Boot 4.1.1** (Spring Framework 7) |
| Java | **17** (`java.version=17`) |
| Build | Apache Maven ≥ 3.6.3 (`packaging=war`, `finalName=login`) |
| Servidor | **Tomcat 11.0.x embebido** (Jakarta EE 11 / Servlet 6.1) |
| Vistas | **JSP + JSTL 3.0** (`jakarta.tags.core`) |
| Validación | Bean Validation (`spring-boot-starter-validation`) |
| Testing | JUnit 5 · AssertJ · MockMvc · `HttpClient` |
| Coordenadas | `com.example.webapp.unir:login:1.0-SNAPSHOT` |

### ⚠️ Notas técnicas clave

- **JSP no funciona en un JAR ejecutable.** La documentación de Spring Boot (*JSP Limitations*)
  indica que con Tomcat los JSP funcionan *"if you use war packaging. An executable war will work
  when launched with `java -jar`"*. Por eso el artefacto es un **WAR ejecutable**, no un JAR.
- `spring-boot-starter-tomcat` y `tomcat-embed-jasper` van en scope **`provided`**: Spring Boot los
  empaqueta en `WEB-INF/lib-provided`, de modo que el **mismo WAR** corre con `java -jar` **y** se
  puede desplegar en un Tomcat externo.
- Usa **Servlet 6.1 / Tomcat 11**: **no** funciona en Tomcat 9 (`javax.servlet`).

---

## 📂 Estructura

```text
login/
├── pom.xml
├── Dockerfile                     # multi-stage: maven:3.9-jdk17 → temurin:17-jre
├── run-local.sh                   # java -jar desde una ruta sin espacios (ver aviso abajo)
├── .dockerignore
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/webapp/unir/
    │   │   ├── LoginApplication.java          # @SpringBootApplication + SpringBootServletInitializer
    │   │   ├── config/WebConfig.java          # interceptor + resolver de vistas JSP
    │   │   ├── service/CredentialsValidator.java
    │   │   └── web/
    │   │       ├── LoginController.java       # GET/POST "/" · GET "/welcome" · POST "/logout"
    │   │       ├── AuthInterceptor.java       # protege /welcome
    │   │       └── dto/LoginForm.java         # @NotBlank / @Size
    │   ├── resources/
    │   │   ├── application.properties
    │   │   └── static/css/styles.css
    │   └── webapp/WEB-INF/jsp/
    │       ├── login.jsp
    │       └── welcome.jsp
    └── test/java/com/example/webapp/unir/
        ├── service/CredentialsValidatorTest.java
        └── web/
            ├── LoginControllerTest.java       # @WebMvcTest + MockMvc
            └── LoginPageRenderingTest.java    # contexto completo + HTTP real (renderiza JSP)
```

---

## 🚀 Compilar y ejecutar

```bash
# 1) Build -> target/login.war (ejecutable)
mvn -B clean package

# 2) Arrancar como aplicación autocontenida (Tomcat 11 embebido)
java -jar target/login.war
```

Abrir 👉 **http://localhost:8080/login/**

```bash
java -jar target/login.war --server.port=9090    # otro puerto
PORT=9090 java -jar target/login.war             # vía variable de entorno
mvn spring-boot:run                              # modo desarrollo
mvn -B test                                      # solo tests
```

## 🔑 Credenciales

| Variable | Valor por defecto | Uso |
| :--- | :--- | :--- |
| `APP_SECURITY_USERNAME` | `demo` | usuario |
| `APP_SECURITY_PASSWORD` | `Demo1234` | contraseña |

```bash
APP_SECURITY_USERNAME=ana APP_SECURITY_PASSWORD='S3cret!' java -jar target/login.war
```

> En producción **no** se usan los valores por defecto: se inyectan por variables de entorno
> (o AWS Secrets Manager) sin recompilar.

## 🗺️ Rutas

| Método | URL | Descripción |
| :--- | :--- | :--- |
| GET | `/login/` | Formulario de login |
| POST | `/login/` | Valida credenciales → redirige a `/login/welcome` |
| GET | `/login/welcome` | Página privada (protegida por `AuthInterceptor`) |
| POST | `/login/logout` | Invalida la sesión |
| GET | `/login/css/styles.css` | Recurso estático |

*(Context path configurado con `server.servlet.context-path=/login`.)*

---

## ⚠️ Importante: `java -jar` no funciona si la ruta tiene espacios

Este proyecto vive en `…/AWS DevOps Certification/semana 3/login`. **Tomcat 11 embebido no puede
compilar los JSP cuando la ruta del WAR contiene espacios**: el classloader del WAR ejecutable
entrega la URL codificada (`war:file:/…/AWS%20DevOps%20Certification/…/login.war`) y el
`StandardJarScanner` falla al abrir el fichero:

```text
WARN  o.a.tomcat.util.scan.StandardJarScanner : Failed to scan JAR [war:file:/home/.../AWS%20DevOps%20Certification/...]
java.nio.file.NoSuchFileException: /home/.../AWS%20DevOps%20Certification/semana%203/login/target/login.war
```

La aplicación arranca, pero `GET /login/` devuelve **500** porque el JSP no se puede compilar.

### Soluciones

| Opción | Comando | Notas |
| :--- | :--- | :--- |
| **A. Desarrollo (ruta actual)** | `mvn spring-boot:run` | ✅ Funciona con espacios (docBase real `src/main/webapp`) |
| **B. Ejecutable (`java -jar`)** | `./run-local.sh` | Copia el WAR a `~/.local/share/login/` y lo ejecuta allí |
| **C. Docker** | `docker build -t login . && docker run -p 8080:8080 login` | En el contenedor la ruta es `/app/login.war` |
| **D. Definitivo (recomendado)** | renombrar a `AWS-DevOps-Certification/semana-3` | Elimina la clase de problemas (Maven, Docker, scripts) |

> En AWS el problema **no existe**: el despliegue usa `/opt/login/login.war` o el contenedor
> (`/app/login.war`), ambas rutas sin espacios.

---

## 🧪 Verificar sin navegador

```bash
curl -i  http://localhost:8080/login/                     # 200 + formulario JSP
curl -i  http://localhost:8080/login/welcome              # 302 -> /login/ (sin sesión)
curl -i -X POST http://localhost:8080/login/ \
     -d 'username=demo&password=Demo1234'                 # 302 -> /login/welcome
curl -i -X POST http://localhost:8080/login/ \
     -d 'username=demo&password=mala'                     # 200 + mensaje de error
```

## 🐳 Docker

```bash
docker build -t login:1.0.0 .                             # multi-stage, no requiere JDK/Maven local

docker run --rm -d --name login -p 8080:8080 \
  -e APP_SECURITY_PASSWORD=Demo1234 login:1.0.0
```

## 🧰 Inspeccionar el artefacto

```bash
unzip -l target/login.war
# WEB-INF/lib/**            -> dependencias empaquetadas
# WEB-INF/lib-provided/**   -> Tomcat embebido + Jasper (scope provided)
# WEB-INF/jsp/*.jsp         -> vistas
# org/springframework/boot/loader/** -> launcher del WAR ejecutable
```

## 🧩 Diferencias con Spring Boot 3 que afectan a este proyecto

| Tema | Spring Boot 3 | **Spring Boot 4.1.1** |
| :--- | :--- | :--- |
| `@WebMvcTest` | `spring-boot-test-autoconfigure` | Módulo propio **`spring-boot-webmvc-test`**, paquete `org.springframework.boot.webmvc.test.autoconfigure` |
| Mocks en tests | `@MockBean` | **`@MockitoBean`** (`org.springframework.test.context.bean.override.mockito`) |
| Contenedor | Tomcat 10.1 / Servlet 6.0 | **Tomcat 11.0 / Servlet 6.1** |
| Java | 17+ | 17+ (hasta 26) |

## ☁️ Despliegue en AWS (Semana 1 / 3)

Al ser autocontenido (`java -jar`), la instancia EC2 privada del laboratorio **ya no necesita un
Tomcat externo**:

1. El pipeline publica `login.war` versionado en Amazon S3 (SSE-KMS).
2. La EC2 lo descarga por el **S3 Gateway Endpoint** (esa subred no tiene salida a Internet).
3. Una unidad **systemd** lo ejecuta: `ExecStart=/usr/bin/java -jar /opt/login/login.war`.
4. CodeDeploy (`appspec.yml` + `scripts/`) reinicia el servicio y valida con `curl`.
5. El ALB sigue publicando el puerto 80 hacia la instancia.

---

*Repositorio del laboratorio práctico DOP-C02 — Semana 3.*
