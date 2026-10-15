package com.example.webapp.unir.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario de login, validados con Bean Validation antes de llegar al controlador.
 */
public class LoginForm {

    @NotBlank(message = "El usuario es obligatorio.")
    @Size(max = 50, message = "El usuario no puede superar los 50 caracteres.")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 4, max = 50, message = "La contraseña debe tener entre 4 y 50 caracteres.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
