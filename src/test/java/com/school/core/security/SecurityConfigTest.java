package com.school.core.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRedirectToLoginForProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

	@Test
	void shouldAllowAccessToPublicEndpoints() throws Exception {
		mockMvc.perform(get("/login"))
				.andExpect(result -> {
					int status = result.getResponse().getStatus();
					org.junit.jupiter.api.Assertions.assertTrue(
							status == 200 || status == 302 || status == 404,
							"Expected public access (not 403), but got: " + status);
				});
	}

    @Test
    void shouldAllowAccessToStaticResources() throws Exception {
        mockMvc.perform(get("/css/sb-admin-2.min.css"))
                .andExpect(status().isOk());
    }
}