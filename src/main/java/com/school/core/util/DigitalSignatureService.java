package com.school.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

@Component
public class DigitalSignatureService {

    /**
     * Genera un hash SHA-256 de una cadena de texto para verificación de
     * integridad.
     */
    public String generateVerificationHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().withLowerCase().formatHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash: Algoritmo no encontrado", e);
        }
    }

    /**
     * Verifica si un hash coincide con un contenido dado.
     */
    public boolean verifyIntegrity(String content, String hash) {
        String generatedHash = generateVerificationHash(content);
        return generatedHash.equalsIgnoreCase(hash);
    }
}
