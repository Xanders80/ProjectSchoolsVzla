package com.school.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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