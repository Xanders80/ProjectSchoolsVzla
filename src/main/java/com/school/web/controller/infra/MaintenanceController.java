package com.school.web.controller.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.core.service.UserService;
import com.school.infra.entity.MaintenanceRequest;
import com.school.infra.service.InfraService;
import com.school.infra.service.MaintenanceService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/infra/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final InfraService infraService;
    private final UserService userService;

    public MaintenanceController(MaintenanceService maintenanceService,
            InfraService infraService,
            UserService userService) {
        this.maintenanceService = maintenanceService;
        this.infraService = infraService;
        this.userService = userService;
    }

    @GetMapping
    public String listRequests(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        Page<MaintenanceRequest> requests = maintenanceService.getAllRequests(pageable);

        model.addAttribute("requests", requests);
        model.addAttribute("pendingCount", maintenanceService.countPendingRequests());

        return "infra/maintenance-list";
    }

    @GetMapping("/new")
    public String newRequestForm(Model model) {
        model.addAttribute("request", new MaintenanceRequest());
        model.addAttribute("rooms", infraService.getAllRooms());
        return "infra/maintenance-form";
    }

    @PostMapping
    public String saveRequest(@Valid @ModelAttribute("request") @NonNull MaintenanceRequest request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", infraService.getAllRooms());
            return "infra/maintenance-form";
        }

        try {
            // Set current user as requester
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String username = auth.getName();
                userService.findByUsername(username).ifPresent(request::setRequestedBy);
            }

            maintenanceService.saveRequest(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Solicitud de mantenimiento registrada exitosamente");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al guardar la solicitud: " + e.getMessage());
            model.addAttribute("rooms", infraService.getAllRooms());
            return "infra/maintenance-form";
        }
        return "redirect:/infra/maintenance";
    }

    @GetMapping("/edit/{id}")
    public String editRequestForm(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        return maintenanceService.getRequestById(id)
                .map(request -> {
                    model.addAttribute("request", request);
                    model.addAttribute("rooms", infraService.getAllRooms());
                    return "infra/maintenance-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Solicitud no encontrada");
                    return "redirect:/infra/maintenance";
                });
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable @NonNull Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            maintenanceService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Estado actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el estado");
        }
        return "redirect:/infra/maintenance";
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteRequest(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            maintenanceService.deleteRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Solicitud eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la solicitud");
        }
        return "redirect:/infra/maintenance";
    }
}
