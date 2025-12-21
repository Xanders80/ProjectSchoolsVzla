package com.school.web.controller.infra;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.school.infra.entity.Building;
import com.school.infra.entity.Room;
import com.school.infra.service.InfraService;

@Controller
@RequestMapping("/infra")
public class InfraController {

    private static final String BUILDING_FORM_VIEW = "infra/building-form";
    private static final String ROOM_FORM_VIEW = "infra/room-form";
    private final InfraService infraService;

    public InfraController(InfraService infraService) {
        this.infraService = infraService;
    }

    // Building Routes
    @GetMapping("/buildings")
    public String listBuildings(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        org.springframework.data.domain.Page<Building> buildingPage = infraService.getAllBuildings(pageable);
        model.addAttribute("buildings", buildingPage);
        return "infra/building-list";
    }

    @GetMapping("/buildings/new")
    public String newBuildingForm(Model model) {
        model.addAttribute("building", new Building());
        return BUILDING_FORM_VIEW;
    }

    @PostMapping("/buildings")
    public String saveBuilding(@jakarta.validation.Valid @ModelAttribute @NonNull Building building,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            return BUILDING_FORM_VIEW;
        }
        infraService.saveBuilding(building);
        return "redirect:/infra/buildings";
    }

    @GetMapping("/buildings/edit/{id}")
    public String editBuildingForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("building", infraService.getBuildingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid building Id:" + id)));
        return BUILDING_FORM_VIEW;
    }

    @RequestMapping(value = "/buildings/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteBuilding(@PathVariable @NonNull Long id) {
        infraService.deleteBuilding(id);
        return "redirect:/infra/buildings";
    }

    // Room Routes
    @GetMapping("/rooms")
    public String listRooms(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        org.springframework.data.domain.Page<Room> roomPage = infraService.getAllRooms(pageable);
        model.addAttribute("rooms", roomPage);
        model.addAttribute("buildings", infraService.getAllBuildings());
        return "infra/room-list";
    }

    @GetMapping("/rooms/new")
    public String newRoomForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("buildings", infraService.getAllBuildings());
        return ROOM_FORM_VIEW;
    }

    @PostMapping("/rooms")
    public String saveRoom(@jakarta.validation.Valid @ModelAttribute @NonNull Room room,
            org.springframework.validation.BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("buildings", infraService.getAllBuildings());
            return ROOM_FORM_VIEW;
        }
        infraService.saveRoom(room);
        return "redirect:/infra/rooms";
    }

    @GetMapping("/rooms/edit/{id}")
    public String editRoomForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("room",
                infraService.getRoomById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + id)));
        model.addAttribute("buildings", infraService.getAllBuildings());
        return ROOM_FORM_VIEW;
    }

    @RequestMapping(value = "/rooms/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteRoom(@PathVariable @NonNull Long id) {
        infraService.deleteRoom(id);
        return "redirect:/infra/rooms";
    }
}
