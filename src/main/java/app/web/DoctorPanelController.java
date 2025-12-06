package app.web;

import app.model.dto.DoctorAppointmentDTO;
import app.model.dto.UpdateWorkingHoursDTO;
import app.service.DoctorScheduleService;
import app.service.DoctorWorkingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorPanelController {

    private final DoctorScheduleService scheduleService;
    private final DoctorWorkingHoursService workingHoursService;

    @GetMapping("/appointments")
    public List<DoctorAppointmentDTO> myAppointments() {
        return scheduleService.getMyAppointments();
    }

    @PutMapping("/appointments/{id}/move")
    public DoctorAppointmentDTO move(
            @PathVariable UUID id,
            @RequestParam int offset // +20 or -20 minutes
    ) {
        return scheduleService.moveAppointment(id, offset);
    }

    @PutMapping("/appointments/{id}")
    public DoctorAppointmentDTO edit(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body
    ) {
        LocalDateTime dt = LocalDateTime.parse(body.get("dateTime"));
        return scheduleService.editAppointment(
                id,
                dt,
                body.get("type"),
                body.get("paymentType")
        );
    }

    @DeleteMapping("/appointments/{id}")
    public void cancel(@PathVariable UUID id) {
        scheduleService.cancelAppointment(id);
    }

    @PutMapping("/working-hours")
    public void updateWorkingHours(
            @RequestBody UpdateWorkingHoursDTO dto
    ) {
        workingHoursService.updateWorkingHours(dto);
    }
}

