package app.web;

import app.model.dto.*;
import app.service.DoctorScheduleService;
import app.service.DoctorService;
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
    private final DoctorService doctorService;

    @GetMapping("/me")
    public DoctorMeDTO getMe() {
        return doctorService.getMyProfile();
    }

    @PutMapping("/me/specialty")
    public void updateMySpecialty(@RequestBody UpdateSpecialtyRequest request) {
        doctorService.updateMySpecialty(request.getSpecialtyId());
    }

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
            @RequestBody List<WorkingHoursDTO> dtos
    ) {
        workingHoursService.updateWorkingHours(dtos);
    }

    @GetMapping("/working-hours")
    public List<WorkingHoursDTO> getWorkingHours() {
        return workingHoursService.getMyWorkingHours();
    }

    @DeleteMapping("/working-hours/{dayOfWeek}")
    public void deleteWorkingHours(
            @PathVariable int dayOfWeek
    ) {
        workingHoursService.deleteWorkingHoursForDay(dayOfWeek);
    }
}

