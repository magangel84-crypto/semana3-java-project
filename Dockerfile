# =============================================================================
# login - Spring Boot 4.1.1 (WAR ejecutable con JSP)
# Build multi-stage: compila con Maven+JDK17 y ejecuta solo con JRE17.
# =============================================================================

# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Cachea las dependencias mientras el pom no cambie
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Usuario sin privilegios
RUN useradd --system --create-home --shell /usr/sbin/nologin appuser
COPY --from=build /build/target/login.war /app/login.war
USER appuser

ENV PORT=8080
EXPOSE 8080

# El WAR es ejecutable: arranca Tomcat 11 embebido con las vistas JSP
ENTRYPOINT ["java", "-jar", "/app/login.war"]
