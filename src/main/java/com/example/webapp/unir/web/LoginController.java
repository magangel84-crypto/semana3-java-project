package com.example.webapp.unir.web;

import java.util.Objects;
import java.util.stream.Collectors;

import com.example.webapp.unir.service.CredentialsValidator;
import com.example.webapp.unir.web.dto.LoginForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Flujo de login/logout.
 *
 * <p>Con {@code server.servlet.context-path=/login} las URLs publicas son:</p>
 * <ul>
 *   <li>{@code GET  /login/} &nbsp;-&gt; formulario</li>
 *   <li>{@code POST /login/} &nbsp;-&gt; valida credenciales y redirige a /login/welcome</li>
 *   <li>{@code GET  /login/welcome} -&gt; pagina privada (protegida por {@link AuthInterceptor})</li>
 *   <li>{@code POST /login/logout}&nbsp;-&gt; invalida la sesion</li>
 * </ul>
 */
@Controller
public class LoginController {

    private final CredentialsValidator credentialsValidator;

    public LoginController(CredentialsValidator credentialsValidator) {
        this.credentialsValidator = credentialsValidator;
    }

    @GetMapping("/")
    public String showLoginForm(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login";
    }

    @PostMapping("/")
    public String processLogin(@Valid @ModelAttribute("loginForm") LoginForm form,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", collectMessages(bindingResult));
            return "login";
        }

        String username = form.getUsername().trim();
        if (!credentialsValidator.isValid(username, form.getPassword())) {
            model.addAttribute("errorMessage", "Usuario o contraseña incorrectos.");
            return "login";
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(AuthInterceptor.SESSION_USER, username);
        return "redirect:/welcome";
    }

    /** El interceptor ya garantiza que existe sesion y publica el atributo {@code username}. */
    @GetMapping("/welcome")
    public String showWelcome() {
        return "welcome";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private static String collectMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }
}
