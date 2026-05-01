package com.school.core.util;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DigitalSignatureService {

	@Value("${app.signature.secret:default-dev-key-change-in-prod}")
	private String secretKey;

	public String generateVerificationHash(String content) {
		try {
			javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(
					secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
			mac.init(keySpec);
			byte[] hmac = mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().withLowerCase().formatHex(hmac);
		} catch (Exception e) {
			throw new RuntimeException("Error generating HMAC", e);
		}
	}

	public boolean verifyIntegrity(String content, String hash) {
		String generatedHash = generateVerificationHash(content);
		return java.security.MessageDigest.isEqual(
				generatedHash.getBytes(StandardCharsets.UTF_8),
				hash.getBytes(StandardCharsets.UTF_8));
	}
}
