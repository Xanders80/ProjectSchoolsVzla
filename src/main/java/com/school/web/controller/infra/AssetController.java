package com.school.web.controller.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
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

import com.school.infra.entity.Asset;
import com.school.infra.service.AssetService;
import com.school.infra.service.InfraService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/infra/assets")
public class AssetController {
    private static final String ROOMS = "rooms";
    private static final String ASSET_FORM = "infra/asset-form";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String REDIRECT_ASSETS = "redirect:/infra/assets";

    private final AssetService assetService;
    private final InfraService infraService;

    public AssetController(AssetService assetService, InfraService infraService) {
        this.assetService = assetService;
        this.infraService = infraService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalAssets", assetService.countAssets());
        model.addAttribute("activeAssets", assetService.countAssetsByStatus(com.school.infra.enums.AssetStatus.ACTIVE));
        model.addAttribute("inMaintenanceAssets",
                assetService.countAssetsByStatus(com.school.infra.enums.AssetStatus.IN_MAINTENANCE));
        model.addAttribute("totalValue", assetService.getTotalActiveAssetsValue());

        model.addAttribute("expiredWarranties", assetService.getAssetsWithExpiredWarranty());

        model.addAttribute("categoryLabels", assetService.getAssetsByCategory().keySet().stream()
                .map(com.school.infra.enums.AssetCategory::getDisplayName).toArray());
        model.addAttribute("categoryData", assetService.getAssetsByCategory().values().toArray());

        model.addAttribute("statusLabels", assetService.getAssetsByStatus().keySet().stream()
                .map(com.school.infra.enums.AssetStatus::getDisplayName).toArray());
        model.addAttribute("statusData", assetService.getAssetsByStatus().values().toArray());

        return "infra/asset-dashboard";
    }

    @GetMapping
    public String listAssets(Model model,
            @RequestParam(required = false) com.school.infra.enums.AssetCategory category,
            @RequestParam(required = false) com.school.infra.enums.AssetStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (category != null || status != null) {
            // Filtrado sin paginación por ahora (debido a limitaciones del servicio actual)
            java.util.List<Asset> filteredList = assetService.filterAssets(category, status);
            // Convertir a Page para compatibilidad con la vista
            int start = Math.min((int) PageRequest.of(page, size).getOffset(), filteredList.size());
            int end = Math.min((start + size), filteredList.size());
            Page<Asset> assetsPage = new org.springframework.data.domain.PageImpl<>(
                    java.util.Objects.requireNonNull(filteredList.subList(start, end),
                            "Lista de activos no puede ser null"),
                    PageRequest.of(page, size), filteredList.size());
            model.addAttribute("assets", assetsPage);
        } else {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
            Page<Asset> assets = assetService.getAllAssets(pageable);
            model.addAttribute("assets", assets);
        }
        return "infra/asset-list";
    }

    @GetMapping("/new")
    public String newAssetForm(Model model) {
        model.addAttribute("asset", new Asset());
        model.addAttribute(ROOMS, infraService.getAllRooms());
        return ASSET_FORM;
    }

    @PostMapping
    public String saveAsset(@Valid @ModelAttribute("asset") @NonNull Asset asset,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute(ROOMS, infraService.getAllRooms());
            return ASSET_FORM;
        }
        try {
            if (asset.getId() == null) {
                // Nuevo activo
                assetService.createAsset(asset);
            } else {
                // Actualizar activo existente
                assetService.updateAsset(
                        java.util.Objects.requireNonNull(asset.getId(), "ID de activo no puede ser null"), asset);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Activo guardado exitosamente");
        } catch (Exception e) {
            model.addAttribute(ERROR_MESSAGE, "Error al guardar el activo: " + e.getMessage());
            model.addAttribute(ROOMS, infraService.getAllRooms());
            return ASSET_FORM;
        }
        return REDIRECT_ASSETS;
    }

    @GetMapping("/edit/{id}")
    public String editAssetForm(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        return assetService.getAssetById(id)
                .map(asset -> {
                    model.addAttribute("asset", asset);
                    model.addAttribute(ROOMS, infraService.getAllRooms());
                    return ASSET_FORM;
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Activo no encontrado");
                    return REDIRECT_ASSETS;
                });
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteAsset(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            assetService.deleteAsset(id);
            redirectAttributes.addFlashAttribute("successMessage", "Activo eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Error al eliminar el activo");
        }
        return REDIRECT_ASSETS;
    }
}
