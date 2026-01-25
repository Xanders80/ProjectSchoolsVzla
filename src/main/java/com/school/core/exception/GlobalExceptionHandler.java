package com.school.core.exception;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(org.springframework.security.web.csrf.CsrfException.class)
    public String handleCsrfException(org.springframework.security.web.csrf.CsrfException ex,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        logger.warn("CSRF token invalid: {} - URL: {}", ex.getMessage(), request.getRequestURL());
        return "redirect:/login?csrf_error=true";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.warn("Access denied - Error ID: {} - URL: {} - Message: {}",
                errorId, request.getRequestURL(), ex.getMessage());

        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("errorId", errorId);
        mav.setStatus(HttpStatus.FORBIDDEN);
        return mav;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationErrors(MethodArgumentNotValidException ex,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.warn("Validation error - Error ID: {} - URL: {}", errorId, request.getRequestURL());

        redirectAttributes.addFlashAttribute("error", "Datos inválidos. Verifique los campos.");
        redirectAttributes.addFlashAttribute("errorId", errorId);
        return "redirect:" + request.getHeader("Referer");
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public String handleConstraintViolation(jakarta.validation.ConstraintViolationException ex,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.warn("Constraint violation - Error ID: {} - URL: {} - Message: {}",
                errorId, request.getRequestURL(), ex.getMessage());

        String errorMessage = ex.getConstraintViolations().stream()
                .map(jakarta.validation.ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.joining(", "));

        redirectAttributes.addFlashAttribute("error", "Error de validación: " + errorMessage);
        redirectAttributes.addFlashAttribute("errorId", errorId);
        return "redirect:" + (request.getHeader("Referer") != null ? request.getHeader("Referer") : "/");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        // Don't log favicon.ico and other static resource 404s as errors
        String path = request.getRequestURI();
        if (path.equals("/favicon.ico") || path.startsWith("/css/") || path.startsWith("/js/")
                || path.startsWith("/images/")) {
            logger.debug("Static resource not found: {}", path);
        } else {
            logger.warn("Resource not found: {}", path);
        }

        ModelAndView mav = new ModelAndView("error/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.error("Runtime error - Error ID: {} - URL: {} - Message: {}",
                errorId, request.getRequestURL(), ex.getMessage(), ex);

        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorId", errorId);
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        logger.error("Unexpected error - Error ID: {} - URL: {} - Message: {}",
                errorId, request.getRequestURL(), ex.getMessage(), ex);

        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorId", errorId);
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}