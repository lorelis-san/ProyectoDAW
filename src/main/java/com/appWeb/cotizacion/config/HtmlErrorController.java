package com.appWeb.cotizacion.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class HtmlErrorController {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", "Página no encontrada");
        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handle403(AccessDeniedException ex, Model model) {
        model.addAttribute("error", "Acceso denegado");
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String handle500(Exception ex, Model model) {
        model.addAttribute("error", "Error interno del servidor");
        return "error/500";
    }
    @GetMapping("/error/401")
    public String error401() {
        return "error/401"; // Se refiere a templates/error/401.html
    }

}
