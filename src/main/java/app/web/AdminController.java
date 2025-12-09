package app.web;

import app.model.dto.AdminAssistantDoctorDTO;
import app.model.dto.AdminUpdateUserDTO;
import app.model.dto.AdminUserViewDTO;
import app.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // --- Users CRUD / Roles / Status ---

    @GetMapping("/users")
    public List<AdminUserViewDTO> getAllUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/users/{id}")
    public AdminUserViewDTO updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody AdminUpdateUserDTO dto
    ) {
        return adminService.updateUser(id, dto);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
    }

    // --- Doctors & Assistants lists (за assign UI) ---

    @GetMapping("/doctors")
    public List<AdminUserViewDTO> getAllDoctors() {
        return adminService.getAllDoctors();
    }

    @GetMapping("/assistants")
    public List<AdminUserViewDTO> getAllAssistants() {
        return adminService.getAllAssistants();
    }

    // --- Assistant ↔ Doctor mapping ---

    @GetMapping("/assistant/{assistantId}/doctors")
    public List<AdminAssistantDoctorDTO> getAssistantDoctors(
            @PathVariable UUID assistantId
    ) {
        return adminService.getAssistantDoctors(assistantId);
    }

    @PostMapping("/assistant/{assistantId}/assign/{doctorId}")
    public void assignDoctor(
            @PathVariable UUID assistantId,
            @PathVariable UUID doctorId
    ) {
        adminService.assignDoctorToAssistant(assistantId, doctorId);
    }

    @DeleteMapping("/assistant/{assistantId}/unassign/{doctorId}")
    public void unassignDoctor(
            @PathVariable UUID assistantId,
            @PathVariable UUID doctorId
    ) {
        adminService.unassignDoctorFromAssistant(assistantId, doctorId);
    }
}

