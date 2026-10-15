# 🔐 login — Java Web App (Maven WAR)

Aplicación web Java (empaquetado **WAR**) del laboratorio de preparación
**AWS Certified DevOps Engineer – Professional (DOP-C02)**, bloque *Semana 3*.
El objetivo de este repositorio es alojar el artefacto de aplicación que luego se
compila y despliega mediante un pipeline CI/CD (build → artefacto → despliegue).

---

## 🧱 Tecnología

| Elemento | Versión / detalle |
| :--- | :--- |
| Lenguaje | Java 17 (`maven.compiler.source/target` heredado del arquetipo: 1.7) |
| Build | Apache Maven 3.8.8, `packaging=war` |
| Coordenadas | `com.example.webapp.unir:login:1.0-SNAPSHOT` |
| Servlet container | **Apache Tomcat 9** (API `javax.servlet`) |
| Testing | JUnit 4 (sin pruebas aún) |

> ⚠️ **Importante:** el proyecto usa la API `javax.servlet`, por lo que debe
> desplegarse en **Tomcat 9** (o inferior). Tomcat 10+ usa `jakarta.servlet` y el
> WAR fallaría con `ClassNotFoundException`.

---

## 📂 Estructura

```text
login/
├── pom.xml
├── .gitignore
├── README.md
└── src/
    └── main/
        └── webapp/
            ├── index.jsp              # Página de ejemplo ("Hello World!")
            └── WEB-INF/
                └── web.xml            # Descriptor (DTD Servlet 2.3, del arquetipo)
```

Estado actual: **esqueleto generado por `maven-archetype-webapp`**. Todavía no
existe código Java (`src/main/java` ni `src/test/java`).

---

## 🚀 Compilar

```bash
mvn clean package
```

Genera el artefacto en `target/login.war`.

## ▶️ Ejecutar en local (Docker + Tomcat 9)

```bash
docker run --rm -d --name login-jsp -p 8080:8080 \
  -v "$PWD/target/login.war:/usr/local/tomcat/webapps/login.war:ro" \
  tomcat:9.0-jdk17-temurin
```

Abrir: **http://localhost:8080/login/**

La raíz (`http://localhost:8080/`) devuelve `404` porque las imágenes oficiales de
Tomcat dejan `webapps/` vacío (los webapps por defecto viven en `webapps.dist`).

Detener:

```bash
docker stop login-jsp
```

## 🧪 Verificar el despliegue sin navegador

```bash
curl -i http://localhost:8080/login/     # esperado: HTTP/1.1 200 + <h2>Hello World!</h2>
docker logs login-jsp | tail -20         # esperado: "Deployment ... has finished"
```

---

## 📌 Próximos pasos

1. Modernizar `pom.xml`: `maven.compiler.release=17` y `javax.servlet-api:3.1.0` (`provided`).
2. Implementar el flujo de login real: `LoginServlet`, `LogoutServlet`, `AuthFilter`, `login.jsp`, `welcome.jsp`.
3. Añadir pruebas unitarias (`src/test/java`) y ejecutarlas con `mvn test`.
4. Publicar el WAR versionado por commit en Amazon S3 (SSE-KMS) y desplegarlo
   mediante CodePipeline/CodeBuild/CodeDeploy hacia la infraestructura de la Semana 1.

---

*Repositorio del laboratorio práctico DOP-C02 — Semana 3.*
