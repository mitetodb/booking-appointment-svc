package app.service;

import app.model.dto.AppointmentViewDTO;
import app.model.dto.CreateAppointmentRequest;
import app.model.Appointment;
import app.model.Doctor;
import app.model.User;
import app.repository.AppointmentRepository;
import app.repository.DoctorRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final UserRepository userRepo;
    private final ModelMapper mapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // --- Validate 20min slots ---
    private void validateSlot(LocalDateTime time) {
        int minutes = time.getMinute();
        if (minutes % 20 != 0) {
            throw new RuntimeException("Invalid slot. Appointments are every 20 minutes.");
        }
    }

    // --- Validate doctor working hours ---
    private void validateDoctorAvailability(Doctor doc, LocalDateTime time) {
        int dow = time.getDayOfWeek().getValue(); // 1-7
        var wh = doc.getWorkingHours().stream()
                .filter(w -> w.getDayOfWeek() == dow)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Doctor not working this day"));

        String start = wh.getStartTime(); // "08:00"
        String end = wh.getEndTime();     // "16:00"

        var slotStr = time.toLocalTime().toString().substring(0,5);

        if (slotStr.compareTo(start) < 0 || slotStr.compareTo(end) > 0) {
            throw new RuntimeException("Outside working hours");
        }
    }

    // --- Validate conflicts ---
    private void validateExistingConflicts(Doctor doc, LocalDateTime time) {
        List<Appointment> all = appointmentRepo.findByDoctorId(doc.getId());
        boolean conflict = all.stream().anyMatch(a ->
                a.getDateTime().truncatedTo(MINUTES)
                        .equals(time.truncatedTo(MINUTES))
        );
        if (conflict) {
            throw new RuntimeException("Doctor already has appointment at this time.");
        }
    }

    // --- Create appointment ---
    public AppointmentViewDTO createAppointment(UUID doctorId, CreateAppointmentRequest req) {

        User user = getCurrentUser();

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LocalDateTime time = LocalDateTime.parse(req.getDateTime());

        validateSlot(time);
        validateDoctorAvailability(doctor, time);
        validateExistingConflicts(doctor, time);

        Appointment a = new Appointment();
        a.setDoctor(doctor);
        a.setUser(user);
        a.setDateTime(time);
        a.setType(req.getType());
        a.setPaymentType(req.getPaymentType());
        a.setStatus("BOOKED");

        appointmentRepo.save(a);

        AppointmentViewDTO dto = new AppointmentViewDTO();
        dto.setId(a.getId());
        dto.setDoctorId(doctor.getId().toString());
        dto.setDoctorName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
        dto.setDateTime(a.getDateTime().toString());
        dto.setType(a.getType());
        dto.setPaymentType(a.getPaymentType());
        dto.setStatus(a.getStatus());

        return dto;
    }

    // update
    public AppointmentViewDTO editMyAppointment(UUID id, CreateAppointmentRequest req) {

        User user = getCurrentUser();

        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!a.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed to modify this appointment");
        }

        LocalDateTime time = LocalDateTime.parse(req.getDateTime());

        validateSlot(time);
        validateDoctorAvailability(a.getDoctor(), time);
        validateExistingConflicts(a.getDoctor(), time);

        a.setDateTime(time);
        a.setType(req.getType());
        a.setPaymentType(req.getPaymentType());
        appointmentRepo.save(a);

        AppointmentViewDTO dto = new AppointmentViewDTO();
        dto.setId(a.getId());
        dto.setDoctorId(a.getDoctor().getId().toString());
        dto.setDoctorName(a.getDoctor().getUser().getFirstName() + " " + a.getDoctor().getUser().getLastName());
        dto.setDateTime(a.getDateTime().toString());
        dto.setType(a.getType());
        dto.setPaymentType(a.getPaymentType());
        dto.setStatus(a.getStatus());

        return dto;
    }

    // --- Cancel ---
    public void cancelAppointment(UUID id) {
        User user = getCurrentUser();

        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!a.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot cancel someone else's appointment.");
        }

        a.setStatus("CANCELLED");
        appointmentRepo.save(a);
    }

    // --- My appointments ---
    public List<AppointmentViewDTO> getMyAppointments() {
        User user = getCurrentUser();

        return appointmentRepo.findByUserId(user.getId()).stream().map(a -> {
            AppointmentViewDTO dto = new AppointmentViewDTO();
            dto.setId(a.getId());
            dto.setDoctorId(a.getDoctor().getId().toString());
            dto.setDoctorName(a.getDoctor().getUser().getFirstName() + " " + a.getDoctor().getUser().getLastName());
            dto.setDateTime(a.getDateTime().toString());
            dto.setType(a.getType());
            dto.setPaymentType(a.getPaymentType());
            dto.setStatus(a.getStatus());
            return dto;
        }).toList();
    }
}

