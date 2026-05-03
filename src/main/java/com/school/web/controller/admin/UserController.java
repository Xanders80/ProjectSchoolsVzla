package com.school.web.controller.admin;

import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import com.school.core.controller.BaseDeleteController;
import com.school.core.entity.User;
import com.school.core.enums.Role;
import com.school.core.service.UserService;
import com.school.core.validation.ValidId;

@Controller
@RequestMapping("/admin/users")
@Validated
public class UserController extends BaseDeleteController {

	private static final String MSG_SUCCESS = "successMessage";
	private static final String MSG_ERROR = "errorMessage";
	private static final String USER_FORM_VIEW = "admin/user-form";
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	public UserController(UserService userService, PasswordEncoder passwordEncoder) {
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public String listUsers(Model model,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
				org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
		model.addAttribute("users", userService.findByDeletedFalse(pageable));
		model.addAttribute("roles", Role.values());
		return "admin/user-list";
	}

	@GetMapping("/new")
	public String newUserForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("roles", Role.values());
		return USER_FORM_VIEW;
	}

	@PostMapping
	public String saveUser(@jakarta.validation.Valid @ModelAttribute @NonNull User user,
			org.springframework.validation.BindingResult result, Model model,
			@RequestParam(required = false) String password) {
		if (result.hasErrors()) {
			model.addAttribute("roles", Role.values());
			return USER_FORM_VIEW;
		}
		if (user.getId() == null && password != null && !password.isEmpty()) {
			user.setPassword(passwordEncoder.encode(password));
		} else if (user.getId() != null && password != null && !password.isEmpty()) {
			user.setPassword(passwordEncoder.encode(password));
		}
		userService.save(user);
		return "redirect:/admin/users";
	}

	@GetMapping("/edit/{id}")
	public String editUserForm(@PathVariable @NonNull Long id, Model model) {
		model.addAttribute("user", userService.findById(id).orElseThrow());
		model.addAttribute("roles", Role.values());
		return USER_FORM_VIEW;
	}

	@RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
	public String deleteUser(@PathVariable @ValidId String id,
			RedirectAttributes redirectAttributes,
			HttpServletRequest request) {
		try {
			Long userId = Long.parseLong(id);
			userService.softDelete(userId, getCurrentUser());

			logDeleteAttempt("User", id, request, true, null);
			handleDeleteResult(true, "Usuario eliminado exitosamente", null, redirectAttributes);
		} catch (Exception e) {
			logDeleteAttempt("User", id, request, false, e.getMessage());
			handleDeleteResult(false, null, "Error al eliminar usuario", redirectAttributes);
		}
		return "redirect:/admin/users";
	}

	private String getCurrentUser() {
		org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
				.getContext().getAuthentication();
		return (auth != null) ? auth.getName() : "system";
	}
}
