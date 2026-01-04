/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2025 [Tu Nombre o Empresa]
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import com.school.core.repository.UserRepository;
import com.school.core.validation.ValidId;

@Controller
@RequestMapping("/admin/users")
@Validated
public class UserController extends BaseDeleteController {

    private static final String USER_FORM_VIEW = "admin/user-form";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        model.addAttribute("users", userRepository.findByDeletedFalse(pageable));
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
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("user", userRepository.findById(id).orElseThrow());
        model.addAttribute("roles", Role.values());
        return USER_FORM_VIEW;
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteUser(@PathVariable @ValidId String id, 
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {
        try {
            Long userId = Long.parseLong(id);
            User user = userRepository.findById(userId).orElseThrow();
            user.setDeleted(true);
            user.setDeletedAt(java.time.LocalDateTime.now());
            user.setDeletedBy(getCurrentUser());
            userRepository.save(user);
            
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