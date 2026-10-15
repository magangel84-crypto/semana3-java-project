package com.example.webapp.unir.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Protege las paginas privadas: si no hay usuario autenticado en la sesion, redirige al login.
 * Cuando el usuario existe, lo publica como atributo de request para que el JSP lo muestre.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** Clave de sesion donde se guarda el usuario autenticado. */
    public static final String SESSION_USER = "authenticatedUser";

    /** Atributo de request con el usuario autenticado (lo consumen los JSP). */
    public static final String REQUEST_USERNAME = "username";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        Object user = (session != null) ? session.getAttribute(SESSION_USER) : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }
        request.setAttribute(REQUEST_USERNAME, user);
        return true;
    }
}
