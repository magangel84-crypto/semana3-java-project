package com.example.webapp.unir.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

/**
 * Test de integracion: arranca la aplicacion completa (Tomcat embebido + JSP) y
 * comprueba por HTTP real que las vistas JSP se compilan y renderizan, respetando
 * el context-path configurado y la codificacion UTF-8.
 *
 * <p>Usa {@link HttpClient} con redirecciones deshabilitadas y decodificacion UTF-8
 * explicita para que las aserciones sean deterministas.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "app.security.username=demo",
        "app.security.password=Demo1234"
})
class LoginPageRenderingTest {

    private static final String CONTEXT_PATH = "/login";

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @Test
    void rendersLoginViewWithJspAndUtf8() throws Exception {
        HttpResponse<String> response = get("/");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body())
                .contains("name=\"username\"")
                .contains("/css/styles.css")
                .contains("Contraseña");           // valida la codificacion UTF-8 de punta a punta
    }

    @Test
    void servesStaticCss() throws Exception {
        assertThat(get("/css/styles.css").statusCode()).isEqualTo(200);
    }

    @Test
    void redirectsToWelcomeOnSuccessfulLogin() throws Exception {
        HttpResponse<String> response = postForm("/", "username=demo&password=Demo1234");

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().firstValue("location").orElse(""))
                .contains(CONTEXT_PATH + "/welcome");
    }

    @Test
    void showsErrorMessageOnBadCredentials() throws Exception {
        HttpResponse<String> response = postForm("/", "username=demo&password=incorrecta");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("incorrectos");
    }

    @Test
    void showsValidationMessageOnEmptyFields() throws Exception {
        HttpResponse<String> response = postForm("/", "username=&password=");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("obligatorio");
    }

    @Test
    void protectsWelcomePageWithoutSession() throws Exception {
        HttpResponse<String> response = get("/welcome");

        assertThat(response.statusCode()).isEqualTo(302);
        assertThat(response.headers().firstValue("location").orElse(""))
                .endsWith(CONTEXT_PATH + "/");
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri(path)).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> postForm(String path, String formBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody, StandardCharsets.UTF_8))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + CONTEXT_PATH + path);
    }
}
