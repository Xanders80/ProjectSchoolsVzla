package com.school.web.controller.infra;

import com.school.infra.entity.Building;
import com.school.infra.entity.Room;
import com.school.infra.service.InfraService;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String listBuildings(Model model) {
        model.addAttribute("buildings", infraService.getAllBuildings());
        return "infra/building-list";
    }

    @GetMapping("/buildings/new")
    public String newBuildingForm(Model model) {
        model.addAttribute("building", new Building());
        return BUILDING_FORM_VIEW;
    }

    @PostMapping("/buildings")
    public String saveBuilding(@ModelAttribute @NonNull Building building) {
        infraService.saveBuilding(building);
        return "redirect:/infra/buildings";
    }

    @RequestMapping(value = "/buildings/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteBuilding(@PathVariable @NonNull Long id) {
        infraService.deleteBuilding(id);
        return "redirect:/infra/buildings";
    }

    // Room Routes
    @GetMapping("/rooms")
    public String listRooms(Model model) {
        model.addAttribute("rooms", infraService.getAllRooms());
        model.addAttribute("buildings", infraService.getAllBuildings()); // For filtering if needed
        return "infra/room-list";
    }

    @GetMapping("/rooms/new")
    public String newRoomForm(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("buildings", infraService.getAllBuildings());
        return ROOM_FORM_VIEW;
    }

    @PostMapping("/rooms")
    public String saveRoom(@ModelAttribute @NonNull Room room) {
        infraService.saveRoom(room);
        return "redirect:/infra/rooms";
    }

    @RequestMapping(value = "/rooms/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteRoom(@PathVariable @NonNull Long id) {
        infraService.deleteRoom(id);
        return "redirect:/infra/rooms";
    }

    @GetMapping("/buildings/edit/{id}")
    public String editBuildingForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("building", infraService.getBuildingById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid building Id:" + id)));
        return BUILDING_FORM_VIEW;
    }

    @GetMapping("/rooms/edit/{id}")
    public String editRoomForm(@PathVariable @NonNull Long id, Model model) {
        model.addAttribute("room",
                infraService.getRoomById(id).orElseThrow(() -> new IllegalArgumentException("Invalid room Id:" + id)));
        model.addAttribute("buildings", infraService.getAllBuildings());
        return ROOM_FORM_VIEW;
    }
}
