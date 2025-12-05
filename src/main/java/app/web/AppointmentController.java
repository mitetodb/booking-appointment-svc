package app.web;

import app.model.dto.AppointmentViewDTO;
import app.model.dto.CreateAppointmentRequest;
import app.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book/{doctorId}")
    public AppointmentViewDTO book(
            @PathVariable UUID doctorId,
            @RequestBody CreateAppointmentRequest req
    ) {
        return appointmentService.createAppointment(doctorId, req);
    }

    @DeleteMapping("/{id}")
    public void cancel(@PathVariable UUID id) {
        appointmentService.cancelAppointment(id);
    }

    @GetMapping("/my")
    public List<AppointmentViewDTO> myAppointments() {
        return appointmentService.getMyAppointments();
    }
}
