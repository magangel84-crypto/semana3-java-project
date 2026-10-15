<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>login · Iniciar sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<main class="card">
    <h1>Iniciar sesión</h1>
    <p class="subtitle">App Java <strong>login</strong> · Spring Boot 4.1.1 (WAR ejecutable con JSP)</p>

    <c:if test="${not empty errorMessage}">
        <p class="alert" role="alert"><c:out value="${errorMessage}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/">
        <label for="username">Usuario</label>
        <input id="username" name="username" type="text" maxlength="50"
               value="<c:out value='${loginForm.username}'/>"
               autocomplete="username" required autofocus>

        <label for="password">Contraseña</label>
        <input id="password" name="password" type="password" maxlength="50"
               autocomplete="current-password" required>

        <button type="submit">Entrar</button>
    </form>

    <p class="hint">Credenciales demo: <code>demo / Demo1234</code> — configurables con
        <code>APP_SECURITY_USERNAME</code> y <code>APP_SECURITY_PASSWORD</code>.</p>
</main>
</body>
</html>
