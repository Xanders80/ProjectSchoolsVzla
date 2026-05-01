package com.school.web.controller.academic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PromotionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldAllowAdminAccessToPromotions() throws Exception {
		mockMvc.perform(get("/academic/promotions/evaluate/1"))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	@WithMockUser(roles = "STUDENT")
	void shouldDenyStudentAccessToPromotions() throws Exception {
		mockMvc.perform(get("/academic/promotions/evaluate/1"))
				.andExpect(status().isForbidden());
	}

	@Test
	void shouldRedirectToLoginForUnauthenticated() throws Exception {
		mockMvc.perform(get("/academic/promotions/evaluate/1"))
				.andExpect(status().is3xxRedirection());
	}
}
