package com.school.web.controller.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object statusAttribute = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (statusAttribute != null) {
            try {
                // Validar que el atributo sea convertible a número
                Integer statusCode = Integer.valueOf(statusAttribute.toString());

                HttpStatus httpStatus = HttpStatus.resolve(statusCode);
                if (httpStatus != null) {
                    switch (httpStatus) {
                        case FORBIDDEN:
                            return "error/403";
                        case NOT_FOUND:
                            return "error/404";
                        case INTERNAL_SERVER_ERROR:
                            return "error/500";
                        case UNAUTHORIZED:
                            return "error/401";
                        case CONFLICT:
                            return "error/409";
                        case UNPROCESSABLE_ENTITY:
                            return "error/422";
                        case SERVICE_UNAVAILABLE:
                            return "error/503";
                        case BAD_REQUEST:
                            return "error/400";
                        case METHOD_NOT_ALLOWED:
                            return "error/405";
                        default:
                            // Manejar códigos de error no específicos
                            if (statusCode >= 400 && statusCode < 500) {
                                return "error/client-error";
                            } else if (statusCode >= 500) {
                                return "error/server-error";
                            } else {
                                return "error/default";
                            }
                    }
                } else {
                    // Código de estado no reconocido por HttpStatus
                    logger.warn("Código de estado HTTP desconocido: {}", statusCode);
                    return "error/default";
                }
            } catch (NumberFormatException e) {
                // El atributo no es convertible a número
                logger.error("Error al convertir el código de estado: {}", statusAttribute, e);
                return "error/default";
            }
        }

        // Atributo ERROR_STATUS_CODE no encontrado
        logger.warn("No se encontró el atributo ERROR_STATUS_CODE en la solicitud");
        return "error/default";
    }

    @RequestMapping("/error/401")
    public String unauthorized() {
        return "error/401";
    }

    @RequestMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }

    @RequestMapping("/error/404")
    public String notFound() {
        return "error/404";
    }

    @RequestMapping("/error/405")
    public String methodNotAllowed() {
        return "error/405";
    }

    @RequestMapping("/error/409")
    public String conflict() {
        return "error/409";
    }

    @RequestMapping("/error/422")
    public String unprocessableEntity() {
        return "error/422";
    }

    @RequestMapping("/error/500")
    public String internalError() {
        return "error/500";
    }

    @RequestMapping("/error/503")
    public String serviceUnavailable() {
        return "error/503";
    }

    @RequestMapping("/error/400")
    public String badRequest() {
        return "error/400";
    }

    @RequestMapping("/error/client-error")
    public String clientError() {
        return "error/client-error";
    }

    @RequestMapping("/error/server-error")
    public String serverError() {
        return "error/server-error";
    }
}