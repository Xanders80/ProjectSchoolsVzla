package com.school.core.validation;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

	private static final Pattern SCRIPT_PATTERN = Pattern.compile(
			"(?i)<script[^>]*>.*?</script>", Pattern.DOTALL);

	private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
			"\\b(union\\s+select|insert\\s+into|drop\\s+table|alter\\s+table|exec(ute)?\\s+)\\b",
			Pattern.CASE_INSENSITIVE);

	public String sanitizeInput(String input) {
		if (input == null) return null;

		input = SCRIPT_PATTERN.matcher(input).replaceAll("");

		input = input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");

		return input.trim();
	}

	public boolean containsSqlInjection(String input) {
		if (input == null) return false;
		return SQL_INJECTION_PATTERN.matcher(input).find();
	}

	public String sanitizeForDatabase(String input) {
		if (input == null) return null;
		if (containsSqlInjection(input)) {
			throw new IllegalArgumentException("Input contains potentially dangerous content");
		}
		return sanitizeInput(input);
	}
}
