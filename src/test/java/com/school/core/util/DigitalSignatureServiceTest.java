package com.school.core.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DigitalSignatureServiceTest {

	@Autowired
	private DigitalSignatureService signatureService;

	@Test
	void shouldGenerateConsistentHash() {
		String content = "test-content-123";
		String hash1 = signatureService.generateVerificationHash(content);
		String hash2 = signatureService.generateVerificationHash(content);
		assertEquals(hash1, hash2);
		assertNotNull(hash1);
		assertFalse(hash1.isEmpty());
	}

	@Test
	void shouldVerifyValidHash() {
		String content = "evaluation|95.5|signer";
		String hash = signatureService.generateVerificationHash(content);
		assertTrue(signatureService.verifyIntegrity(content, hash));
	}

	@Test
	void shouldRejectTamperedContent() {
		String content = "evaluation|95.5|signer";
		String hash = signatureService.generateVerificationHash(content);
		assertFalse(signatureService.verifyIntegrity("evaluation|99.0|signer", hash));
	}

	@Test
	void shouldProduceDifferentHashesForDifferentContent() {
		String hash1 = signatureService.generateVerificationHash("content-a");
		String hash2 = signatureService.generateVerificationHash("content-b");
		assertNotEquals(hash1, hash2);
	}
}
