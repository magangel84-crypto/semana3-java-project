package com.example.webapp.unir.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.webapp.unir.config.WebConfig;
import com.example.webapp.unir.service.CredentialsValidator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Test de la capa web (slice): no arranca Tomcat ni renderiza JSP,
 * valida mapeos, modelo, sesion y redirecciones.
 */
@WebMvcTest(LoginController.class)
@Import({ WebConfig.class, AuthInterceptor.class })
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CredentialsValidator credentialsValidator;

    @Test
    void showsLoginForm() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    @Test
    void redirectsToWelcomeAndCreatesSessionOnValidCredentials() throws Exception {
        given(credentialsValidator.isValid("demo", "Demo1234")).willReturn(true);

        mockMvc.perform(formPost("demo", "Demo1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/welcome"))
                .andExpect(request().sessionAttribute(AuthInterceptor.SESSION_USER, "demo"));
    }

    @Test
    void returnsToLoginWithErrorOnInvalidCredentials() throws Exception {
        given(credentialsValidator.isValid("demo", "mala")).willReturn(false);

        mockMvc.perform(formPost("demo", "mala"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("errorMessage", "Usuario o contraseña incorrectos."));
    }

    @Test
    void reportsValidationErrorsForEmptyFields() throws Exception {
        mockMvc.perform(formPost("", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void welcomeRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void welcomeExposesAuthenticatedUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthInterceptor.SESSION_USER, "demo");

        mockMvc.perform(get("/welcome").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(request().attribute(AuthInterceptor.REQUEST_USERNAME, "demo"));
    }

    @Test
    void logoutInvalidatesSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthInterceptor.SESSION_USER, "demo");

        mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        assertThat(session.isInvalid()).isTrue();
    }

    private static MockHttpServletRequestBuilder formPost(String username, String password) {
        return post("/").param("username", username).param("password", password);
    }
}
