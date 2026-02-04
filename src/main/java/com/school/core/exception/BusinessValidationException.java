package com.school.core.exception;

/**
 * Excepción para validaciones de reglas de negocio.
 * Se lanza cuando una operación viola reglas de negocio específicas del
 * dominio,
 * como transiciones de estado inválidas, datos inconsistentes, etc.
 *
 * Esta excepción debe ser capturada en la capa de controladores para mostrar
 * mensajes de error amigables al usuario.
 */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
