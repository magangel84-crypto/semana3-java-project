package com.example.webapp.unir.config;

import com.example.webapp.unir.web.AuthInterceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Configuracion MVC:
 * <ul>
 *   <li>Registra el interceptor de autenticacion sobre las rutas privadas.</li>
 *   <li>Declara explicitamente el resolver de vistas JSP (no dependemos de valores por
 *       defecto no documentados).</li>
 * </ul>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/welcome");
    }

    /**
     * Resuelve los nombres de vista devueltos por los controladores
     * (p. ej. "login" -> /WEB-INF/jsp/login.jsp).
     */
    @Bean
    public InternalResourceViewResolver jspViewResolver(
            @Value("${spring.mvc.view.prefix:/WEB-INF/jsp/}") String prefix,
            @Value("${spring.mvc.view.suffix:.jsp}") String suffix) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(prefix);
        resolver.setSuffix(suffix);
        return resolver;
    }
}
