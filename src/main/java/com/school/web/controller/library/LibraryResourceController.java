package com.school.web.controller.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.core.validation.ValidId;
import com.school.library.entity.DigitalResource;
import com.school.library.service.LibraryService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/library/resources")
@Validated
public class LibraryResourceController {

    private static final String FORM_VIEW = "library/resource-form";
    private static final String LIST_VIEW = "library/resource-list";
    private static final String REDIRECT_RESOURCE = "redirect:/library/resources";
    private static final String ERROR_MSG = "Error al guardar recurso: ";
    private static final String SUCCESS_MSG = "Recurso guardado exitosamente";
    private static final String MSG_SUCCESS = "successMessage";
    private static final String MSG_ERROR = "errorMessage";
    private static final String STR_ERROR = "error";

    private static final Logger logger = LoggerFactory.getLogger(LibraryResourceController.class);
    private final LibraryService libraryService;

    public LibraryResourceController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public String listResources(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DigitalResource> resources = libraryService.getAllDigitalResources(pageable);
        model.addAttribute("resources", resources);
        return LIST_VIEW;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("resource", new DigitalResource());
        return FORM_VIEW;
    }

    @PostMapping("/save")
    public String saveResource(@ModelAttribute("resource") DigitalResource resource,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            libraryService.saveDigitalResource(resource);
            redirectAttributes.addFlashAttribute(MSG_SUCCESS, SUCCESS_MSG);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(MSG_ERROR, ERROR_MSG + e.getMessage());
            return FORM_VIEW;
        }
        return REDIRECT_RESOURCE;
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable @NonNull Long id, Model model, RedirectAttributes redirectAttributes) {
        DigitalResource resource = libraryService.getDigitalResourceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));
        model.addAttribute("resource", resource);
        return FORM_VIEW;
    }

    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public String deleteResource(@PathVariable @ValidId String id,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Long resourceId;

        try {
            resourceId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            logger.warn("Invalid resource ID format: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute(MSG_ERROR, "ID de recurso inválido");
            return REDIRECT_RESOURCE;
        }

        try {
            libraryService.deleteDigitalResource(resourceId);
            logger.info("Digital resource {} deleted successfully by IP: {}", resourceId, clientIp);
            redirectAttributes.addFlashAttribute(MSG_SUCCESS, "Recurso digital eliminado exitosamente");
        } catch (EntityNotFoundException e) {
            logger.warn("Attempt to delete non-existent resource ID: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute(MSG_ERROR, "Recurso digital no encontrado");
        } catch (IllegalStateException e) {
            logger.warn("Business rule violation deleting resource ID: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute(MSG_ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting resource ID: {} from IP: {}", id, clientIp, e);
            redirectAttributes.addFlashAttribute(MSG_ERROR, "Error interno del sistema");
        }
        return REDIRECT_RESOURCE;
    }

    @DeleteMapping("/api/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteResourceApi(@PathVariable @ValidId String id,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Long resourceId;

        try {
            resourceId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            logger.warn("Invalid resource ID format in API call: {} from IP: {}", id, clientIp);
            return ResponseEntity.badRequest().body(java.util.Map.of(STR_ERROR, "ID de recurso inválido"));
        }

        try {
            libraryService.deleteDigitalResource(resourceId);
            logger.info("Digital resource {} deleted via API by IP: {}", resourceId, clientIp);
            return ResponseEntity.ok(java.util.Map.of("message", "Recurso digital eliminado exitosamente"));
        } catch (EntityNotFoundException e) {
            logger.warn("API call to delete non-existent resource ID: {} from IP: {}", id, clientIp);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of(STR_ERROR, "Recurso digital no encontrado"));
        } catch (Exception e) {
            logger.error("API delete error for resource ID: {} from IP: {}", id, clientIp, e);
            return ResponseEntity.status(500).body(java.util.Map.of(STR_ERROR, "Error eliminando recurso digital"));
        }
    }
}
