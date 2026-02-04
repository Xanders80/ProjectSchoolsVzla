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
import com.school.infra.enums.MaintenanceStatus;
import com.school.infra.service.InfraService;
import com.school.infra.service.MaintenanceService;
import com.school.infra.service.AssetService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/infra/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final InfraService infraService;
    private final UserService userService;
    private final AssetService assetService;

    public MaintenanceController(MaintenanceService maintenanceService,
            InfraService infraService,
            UserService userService,
            AssetService assetService) {
        this.maintenanceService = maintenanceService;
        this.infraService = infraService;
        this.userService = userService;
        this.assetService = assetService;
    }

    @PostMapping("/quick-create")
    public String createQuickRequest(@RequestParam @NonNull Long assetId,
            @RequestParam String description,
            @RequestParam com.school.infra.enums.MaintenancePriority priority,
            RedirectAttributes redirectAttributes) {
        try {
            MaintenanceRequest request = new MaintenanceRequest();
            request.setDescription(description);
            request.setPriority(priority);
            request.setType(com.school.infra.enums.MaintenanceType.CORRECTIVE); // Por defecto es correctivo para
                                                                                // reportes rápidos

            // Asignar el activo
            assetService.getAssetById(assetId).ifPresent(request::setAsset);

            // Usuario actual (requester)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                userService.findByUsername(auth.getName()).ifPresent(request::setRequestedBy);
            }

            maintenanceService.createRequest(request);
            redirectAttributes.addFlashAttribute("successMessage", "Reporte de falla creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear reporte: " + e.getMessage());
        }
        return "redirect:/infra/assets";
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

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // KPIs Numéricos
        model.addAttribute("totalPending", maintenanceService.countPendingRequests());
        model.addAttribute("totalInProgress", maintenanceService.countInProgressRequests());
        model.addAttribute("totalCosts", maintenanceService.getTotalMaintenanceCosts());

        // Gráficos - Estado
        java.util.Map<MaintenanceStatus, Long> byStatus = maintenanceService.getRequestsByStatus();
        model.addAttribute("statusLabels", byStatus.keySet().stream().map(MaintenanceStatus::getDisplayName).toArray());
        model.addAttribute("statusData", byStatus.values().toArray());

        // Gráficos - Prioridad
        java.util.Map<com.school.infra.enums.MaintenancePriority, Long> byPriority = maintenanceService
                .getRequestsByPriority();
        model.addAttribute("priorityLabels",
                byPriority.keySet().stream().map(com.school.infra.enums.MaintenancePriority::getDisplayName).toArray());
        model.addAttribute("priorityData", byPriority.values().toArray());

        // Gráficos - Tipo
        java.util.Map<com.school.infra.enums.MaintenanceType, Long> byType = maintenanceService.getRequestsByType();
        model.addAttribute("typeLabels",
                byType.keySet().stream().map(com.school.infra.enums.MaintenanceType::getDisplayName).toArray());
        model.addAttribute("typeData", byType.values().toArray());

        // Costos por Tipo (Gráfico de barras)
        java.util.Map<com.school.infra.enums.MaintenanceType, java.math.BigDecimal> costsByType = maintenanceService
                .getCostsByType();
        model.addAttribute("costLabels",
                costsByType.keySet().stream().map(com.school.infra.enums.MaintenanceType::getDisplayName).toArray());
        model.addAttribute("costData", costsByType.values().toArray());

        return "infra/maintenance-dashboard";
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
            if (request.getId() == null) {
                // New request: Set current user as requester
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    String username = auth.getName();
                    userService.findByUsername(username).ifPresent(request::setRequestedBy);
                }
            } else {
                // Editing existing: Ensure requester is preserved if not present in binding
                Long requestId = request.getId();
                if (requestId != null) {
                    maintenanceService.getRequestById(requestId).ifPresent(existing -> {
                        if (request.getRequestedBy() == null) {
                            request.setRequestedBy(existing.getRequestedBy());
                        }
                        if (request.getRequestDate() == null) {
                            request.setRequestDate(existing.getRequestDate());
                        }
                    });
                }
            }

            if (request.getId() == null) {
                // Nueva solicitud
                maintenanceService.createRequest(request);
            } else {
                // Actualizar solicitud existente
                maintenanceService.updateRequest(request.getId(), request);
            }
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
            // Convertir String a enum MaintenanceStatus
            MaintenanceStatus newStatus = MaintenanceStatus.valueOf(status.toUpperCase());
            maintenanceService.updateStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Estado actualizado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Estado inválido: " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el estado: " + e.getMessage());
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
