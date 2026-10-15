package com.example.webapp.unir.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Valida las credenciales contra los valores configurados.
 *
 * <p>Las credenciales se leen de la configuracion ({@code app.security.username} /
 * {@code app.security.password}), que a su vez se puede sobrescribir con las variables de
 * entorno {@code APP_SECURITY_USERNAME} / {@code APP_SECURITY_PASSWORD} sin recompilar.</p>
 *
 * <p>La comparacion se hace con {@link MessageDigest#isEqual} para evitar comparaciones
 * dependientes del tiempo de ejecucion.</p>
 */
@Service
public class CredentialsValidator {

    private final String expectedUsername;
    private final String expectedPassword;

    public CredentialsValidator(@Value("${app.security.username}") String expectedUsername,
                                @Value("${app.security.password}") String expectedPassword) {
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
    }

    public boolean isValid(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return constantTimeEquals(expectedUsername, username)
                && constantTimeEquals(expectedPassword, password);
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8));
    }
}
