package app.web;

import app.model.dto.AssistantAppointmentDTO;
import app.model.dto.AssistantDoctorListDTO;
import app.service.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/assistant")
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService assistantService;

    @GetMapping("/doctors")
    public List<AssistantDoctorListDTO> getDoctors() {
        return assistantService.getAssignedDoctors();
    }

    @GetMapping("/doctor/{doctorId}/appointments")
    public List<AssistantAppointmentDTO> getAppointments(
            @PathVariable UUID doctorId
    ) {
        return assistantService.getDoctorAppointments(doctorId);
    }

    @PostMapping("/doctor/{doctorId}/appointments")
    public AssistantAppointmentDTO create(
            @PathVariable UUID doctorId,
            @RequestBody Map<String, Object> request
    ) {
        return assistantService.createAppointment(doctorId, request);
    }

    @PutMapping("/appointments/{id}")
    public AssistantAppointmentDTO update(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request
    ) {
        return assistantService.editAppointment(id, request);
    }

    @DeleteMapping("/appointments/{id}")
    public void cancel(@PathVariable UUID id) {
        assistantService.cancelAppointment(id);
    }
}
