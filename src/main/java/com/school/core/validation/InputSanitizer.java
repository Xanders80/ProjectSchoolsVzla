package com.school.core.validation;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    private static final Pattern HTML_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<script[^>]*>.*?</script>");
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("(?i)(union|select|insert|update|delete|drop|create|alter|exec|execute)");

    public String sanitizeInput(String input) {
        if (input == null) return null;
        
        // Remover scripts
        input = SCRIPT_PATTERN.matcher(input).replaceAll("");
        
        // Remover HTML tags
        input = HTML_PATTERN.matcher(input).replaceAll("");
        
        // Escapar caracteres especiales
        input = input.replace("'", "&#39;")
                    .replace("\"", "&quot;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("&", "&amp;");
        
        return input.trim();
    }

    public boolean containsSqlInjection(String input) {
        return input != null && SQL_INJECTION_PATTERN.matcher(input).find();
    }

    public String sanitizeForDatabase(String input) {
        if (input == null) return null;
        if (containsSqlInjection(input)) {
            throw new IllegalArgumentException("Input contains potentially dangerous content");
        }
        return sanitizeInput(input);
    }
}