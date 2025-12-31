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

    private final AssetService assetService;
    private final InfraService infraService;

    public AssetController(AssetService assetService, InfraService infraService) {
        this.assetService = assetService;
        this.infraService = infraService;
    }

    @GetMapping
    public String listAssets(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Asset> assets = assetService.getAllAssets(pageable);
        model.addAttribute("assets", assets);
        return "infra/asset-list";
    }

    @GetMapping("/new")
    public String newAssetForm(Model model) {
        model.addAttribute("asset", new Asset());
        model.addAttribute("rooms", infraService.getAllRooms());
        return "infra/asset-form";
    }

    @PostMapping
    public String saveAsset(@Valid @ModelAttribute("asset") @NonNull Asset asset,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", infraService.getAllRooms());
            return "infra/asset-form";
        }
        try {
            assetService.saveAsset(asset);
            redirectAttributes.addFlashAttribute("successMessage", "Activo guardado exitosamente");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al guardar el activo: " + e.getMessage());
            model.addAttribute("rooms", infraService.getAllRooms());
            return "infra/asset-form";
        }
        return "redirect:/infra/assets";
    }

    @GetMapping("/edit/{id}")
    public String editAssetForm(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        return assetService.getAssetById(id)
                .map(asset -> {
                    model.addAttribute("asset", asset);
                    model.addAttribute("rooms", infraService.getAllRooms());
                    return "infra/asset-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Activo no encontrado");
                    return "redirect:/infra/assets";
                });
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteAsset(@PathVariable @NonNull Long id, RedirectAttributes redirectAttributes) {
        try {
            assetService.deleteAsset(id);
            redirectAttributes.addFlashAttribute("successMessage", "Activo eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el activo");
        }
        return "redirect:/infra/assets";
    }
}
