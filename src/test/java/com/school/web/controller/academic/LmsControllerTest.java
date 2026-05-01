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
class LmsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(roles = "STUDENT")
	void shouldAllowStudentAccessToLms() throws Exception {
		mockMvc.perform(get("/academic/lms/course/1"))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void shouldAllowAdminAccessToLms() throws Exception {
		mockMvc.perform(get("/academic/lms/course/1"))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	void shouldRedirectToLoginForUnauthenticated() throws Exception {
		mockMvc.perform(get("/academic/lms/course/1"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(roles = "PARENT")
	void shouldDenyParentAccessToLms() throws Exception {
		mockMvc.perform(get("/academic/lms/course/1"))
				.andExpect(status().isForbidden());
	}
}
