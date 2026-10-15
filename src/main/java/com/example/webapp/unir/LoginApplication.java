package com.example.webapp.unir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Punto de entrada de la aplicacion.
 *
 * <p>Extiende {@link SpringBootServletInitializer} para que el <strong>mismo WAR</strong>
 * funcione de las dos formas:</p>
 * <ul>
 *   <li><strong>Ejecutable:</strong> {@code java -jar target/login.war} (Tomcat embebido).</li>
 *   <li><strong>Desplegado:</strong> copiando el WAR en {@code webapps/} de un Tomcat 11 externo
 *       (Servlet 6.1 / Jakarta EE 11).</li>
 * </ul>
 */
@SpringBootApplication
public class LoginApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }
}
