package app.service;

import app.model.dto.DoctorAppointmentDTO;
import app.model.*;
import app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {

    private final DoctorRepository doctorRepo;
    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;

    private Doctor getCurrentDoctor() {
        String email =
                SecurityContextHolder.getContext().getAuthentication().getName();

        return doctorRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    // DTO mapper
    private DoctorAppointmentDTO toDto(Appointment a) {
        DoctorAppointmentDTO dto = new DoctorAppointmentDTO();
        dto.setId(a.getId());
        dto.setPatientId(a.getUser().getId().toString());
        dto.setPatientName(a.getUser().getFirstName() + " " + a.getUser().getLastName());
        dto.setDateTime(a.getDateTime().toString());
        dto.setType(a.getType());
        dto.setPaymentType(a.getPaymentType());
        dto.setStatus(a.getStatus());
        return dto;
    }

    public List<DoctorAppointmentDTO> getMyAppointments() {
        Doctor doctor = getCurrentDoctor();

        return appointmentRepo.findByDoctorId(doctor.getId())
                .stream().map(this::toDto).toList();
    }

    private void validateSlot(LocalDateTime t) {
        if (t.getMinute() % 20 != 0)
            throw new RuntimeException("Appointments must be at 20-minute intervals");
    }

    private void validateWorkingHours(Doctor d, LocalDateTime time) {
        int dow = time.getDayOfWeek().getValue();

        WorkingHours wh = d.getWorkingHours().stream()
                .filter(x -> x.getDayOfWeek() == dow)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Doctor not working this day"));

        String slot = time.toLocalTime().toString().substring(0,5);

        if (slot.compareTo(wh.getStartTime()) < 0 ||
                slot.compareTo(wh.getEndTime()) > 0)
            throw new RuntimeException("Outside working hours");
    }

    private void validateConflict(Doctor d, LocalDateTime time, UUID ignoreId) {
        boolean conflict = appointmentRepo.findByDoctorId(d.getId())
                .stream()
                .anyMatch(a ->
                        !a.getId().equals(ignoreId) &&
                                a.getDateTime().truncatedTo(MINUTES)
                                        .equals(time.truncatedTo(MINUTES))
                );

        if (conflict)
            throw new RuntimeException("Conflicting slot");
    }

    public DoctorAppointmentDTO moveAppointment(UUID id, int minutesOffset) {

        if (minutesOffset != 20 && minutesOffset != -20) {
            throw new RuntimeException("Offset must be +20 or -20");
        }

        Doctor doctor = getCurrentDoctor();

        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!a.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Not your appointment");
        }

        LocalDateTime newTime = a.getDateTime().plusMinutes(minutesOffset);

        validateSlot(newTime);
        validateWorkingHours(doctor, newTime);
        validateConflict(doctor, newTime, a.getId());

        a.setDateTime(newTime);
        appointmentRepo.save(a);

        return toDto(a);
    }

    public DoctorAppointmentDTO editAppointment(UUID id, LocalDateTime newTime,
                                                String type, String paymentType) {
        Doctor doctor = getCurrentDoctor();

        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!a.getDoctor().getId().equals(doctor.getId()))
            throw new RuntimeException("Access denied");

        validateSlot(newTime);
        validateWorkingHours(doctor, newTime);
        validateConflict(doctor, newTime, a.getId());

        a.setDateTime(newTime);
        a.setType(type);
        a.setPaymentType(paymentType);
        appointmentRepo.save(a);

        return toDto(a);
    }

    public void cancelAppointment(UUID id) {
        Doctor doctor = getCurrentDoctor();

        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!a.getDoctor().getId().equals(doctor.getId()))
            throw new RuntimeException("Access denied");

        a.setStatus("CANCELLED");
        appointmentRepo.save(a);
    }
}

