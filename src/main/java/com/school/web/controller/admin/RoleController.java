package com.school.web.controller.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.core.entity.Role;
import com.school.core.service.RoleService;

@Controller
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

	private static final String MSG_SUCCESS = "successMessage";
	private static final String MSG_ERROR = "errorMessage";
	private static final String REDIRECT_ROLES = "redirect:/admin/roles";

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping
	public String listRoles(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Model model) {
		Pageable pageable = PageRequest.of(page, size);
		var rolePage = roleService.findByDeletedFalse(pageable);

		model.addAttribute("roles", rolePage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", rolePage.getTotalPages());
		model.addAttribute("totalItems", rolePage.getTotalElements());

		return "admin/role-list";
	}

	@GetMapping("/new")
	public String showCreateForm(Model model) {
		model.addAttribute("role", new Role());
		model.addAttribute("isEdit", false);
		return "admin/role-form";
	}

	@PostMapping
	public String createRole(@ModelAttribute @NonNull Role role, RedirectAttributes redirectAttributes) {
		try {
			roleService.save(role);
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Rol creado exitosamente.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, "Error al crear el rol: " + e.getMessage());
		}
		return REDIRECT_ROLES;
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
		var roleOpt = roleService.findById(id);
		if (roleOpt.isPresent()) {
			model.addAttribute("role", roleOpt.get());
			model.addAttribute("isEdit", true);
			return "admin/role-form";
		} else {
			redirectAttributes.addFlashAttribute(MSG_ERROR, "Rol no encontrado.");
			return REDIRECT_ROLES;
		}
	}

	@PostMapping("/edit/{id}")
	public String updateRole(@PathVariable Long id, @ModelAttribute Role role, RedirectAttributes redirectAttributes) {
		try {
			role.setId(id);
			roleService.save(role);
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Rol actualizado exitosamente.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, "Error al actualizar el rol: " + e.getMessage());
		}
		return REDIRECT_ROLES;
	}

	@RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
	public String deleteRole(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
		try {
			roleService.softDelete(id, getCurrentUser());
			redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Rol eliminado exitosamente.");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, "Rol no encontrado.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(MSG_ERROR, "Error al eliminar el rol: " + e.getMessage());
		}
		return REDIRECT_ROLES;
	}

	private String getCurrentUser() {
		org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
				.getContext().getAuthentication();
		return (auth != null) ? auth.getName() : "system";
	}
}
