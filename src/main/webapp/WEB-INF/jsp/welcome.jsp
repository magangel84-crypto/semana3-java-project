<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>login · Bienvenido</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<main class="card">
    <h1>Bienvenido, <c:out value="${username}"/>!</h1>
    <p class="subtitle">Autenticación correcta · sesión activa</p>

    <form method="post" action="${pageContext.request.contextPath}/logout">
        <button type="submit" class="secondary">Cerrar sesión</button>
    </form>

    <p class="hint">Servido por Tomcat 11 embebido: <code>java -jar target/login.war</code></p>
</main>
</body>
</html>
