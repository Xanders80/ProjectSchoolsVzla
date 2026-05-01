package com.school.core.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InputSanitizerTest {

	@Autowired
	private InputSanitizer inputSanitizer;

	@Test
	void shouldRemoveScriptTags() {
		String maliciousInput = "<script>alert('xss')</script>Hello";
		String result = inputSanitizer.sanitizeInput(maliciousInput);
		assertFalse(result.contains("<script>"));
		assertTrue(result.contains("Hello"));
	}

	@Test
	void shouldDetectSqlInjection() {
		String sqlInjection = "'; DROP TABLE users; --";
		assertTrue(inputSanitizer.containsSqlInjection(sqlInjection));
	}

	@Test
	void shouldThrowExceptionForDangerousInput() {
		String dangerousInput = "test'; DELETE FROM users; --";
		assertThrows(IllegalArgumentException.class,
				() -> inputSanitizer.sanitizeForDatabase(dangerousInput));
	}

	@Test
	void shouldEscapeHtmlCharacters() {
		String htmlInput = "<div>Test & 'quote'</div>";
		String result = inputSanitizer.sanitizeInput(htmlInput);
		assertEquals("&lt;div&gt;Test &amp; &#39;quote&#39;&lt;/div&gt;", result.trim());
	}

	@Test
	void shouldNotFlagFalsePositives() {
		String legitimateInput = "Selection committee update record";
		assertFalse(inputSanitizer.containsSqlInjection(legitimateInput));
	}
}
