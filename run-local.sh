#!/usr/bin/env bash
# =============================================================================
# Arranca login como aplicacion Spring Boot ejecutable:  java -jar login.war
#
# ¿Por qué copiar el WAR?  Tomcat 11 embebido (StandardJarScanner) NO puede
# abrir el fichero cuando la ruta contiene espacios: el classloader del WAR
# entrega la URL codificada (war:file:/.../AWS%20DevOps%20.../login.war) y falla
# con NoSuchFileException, dejando los JSP sin compilar (HTTP 500).
# Por eso se ejecuta desde una ruta sin espacios (por defecto ~/.local/share/login).
#
# Uso:
#   ./run-local.sh                 # build + run en el puerto 8080
#   PORT=9090 ./run-local.sh       # otro puerto
#   LOGIN_RUNTIME_DIR=/opt/login ./run-local.sh
# =============================================================================
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUNTIME_DIR="${LOGIN_RUNTIME_DIR:-$HOME/.local/share/login}"
PORT="${PORT:-8080}"

echo "==> Compilando ($PROJECT_DIR)"
mvn -B -f "$PROJECT_DIR/pom.xml" clean package

echo "==> Copiando el WAR a una ruta sin espacios: $RUNTIME_DIR"
mkdir -p "$RUNTIME_DIR"
cp "$PROJECT_DIR/target/login.war" "$RUNTIME_DIR/login.war"

echo "==> Arrancando: http://localhost:${PORT}/login/   (Ctrl+C para parar)"
exec java -jar "$RUNTIME_DIR/login.war" --server.port="$PORT"
