package com.appWeb.cotizacion.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
        System.out.println("Usuario NO autenticado intentando acceder a: " + request.getRequestURI());

        // Limpiar cookie JWT si existe
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);

        // Verificar si es una petición AJAX/API
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        String requestURI = request.getRequestURI();

        if ("XMLHttpRequest".equals(requestedWith) ||
                (accept != null && accept.contains("application/json")) ||
                requestURI.startsWith("/api/")) {

            // Para peticiones AJAX/API, devolver JSON
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token expirado\",\"message\":\"Sesión expirada\",\"redirectUrl\":\"/loginView?expired=true\"}");

        } else {
            // Para peticiones de vistas normales, redirigir al login
            if (!response.isCommitted()) {
                response.sendRedirect("/loginView?expired=true");
            }
        }
    }
}
