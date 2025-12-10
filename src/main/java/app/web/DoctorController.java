package app.web;

import app.model.dto.*;
import app.service.AppointmentService;
import app.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    @GetMapping
    public List<DoctorListViewDTO> getAll() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public DoctorDetailsDTO getById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id);
    }

    @GetMapping("/assistants")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AssistantListItemDTO> getAssistants() {
        return doctorService.getAllAssistants();
    }

    @PutMapping("/me/assistant")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> updateMyAssistant(@RequestBody UpdateDoctorAssistantDTO request) {
        doctorService.updateMyAssistant(request.getAssistantId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/appointments/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AppointmentViewDTO> updateAppointment(
            @PathVariable UUID appointmentId,
            @RequestBody CreateAppointmentRequest request
    ) {
        AppointmentViewDTO updated = appointmentService.editDoctorAppointment(appointmentId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId) {
        appointmentService.doctorCancelAppointment(appointmentId);
        return ResponseEntity.ok().build(); // или .noContent().build()
    }
}
