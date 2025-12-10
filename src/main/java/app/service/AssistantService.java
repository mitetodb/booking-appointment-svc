package app.service;

import app.model.dto.AssistantAppointmentDTO;
import app.model.dto.AssistantDoctorListDTO;
import app.model.*;
import app.model.dto.UserViewDTO;
import app.model.enums.UserRole;
import app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private final AssistantDoctorRepository assistantDoctorRepo;
    private final DoctorRepository doctorRepo;
    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;

    // common helpers
    private User getCurrentAssistant() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Assistant user not found"));
    }

    private void validateSlot(LocalDateTime time) {
        int min = time.getMinute();
        if (min % 20 != 0) {
            throw new RuntimeException("Slots must be 20-minute intervals.");
        }
    }

    private void validateDoctorSchedule(Doctor doctor, LocalDateTime time) {
        int dow = time.getDayOfWeek().getValue(); // 1-7

        var wh = doctor.getWorkingHours().stream()
                .filter(x -> x.getDayOfWeek() == dow)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Doctor not working this day"));

        String slot = time.toLocalTime().toString().substring(0, 5);

        if (slot.compareTo(wh.getStartTime()) < 0 || slot.compareTo(wh.getEndTime()) > 0) {
            throw new RuntimeException("Slot outside working hours");
        }
    }

    private void validateConflicts(Doctor doctor, LocalDateTime time) {
        boolean conflict = appointmentRepo
                .findByDoctorId(doctor.getId()).stream()
                .anyMatch(a -> a.getDateTime()
                        .truncatedTo(MINUTES)
                        .equals(time.truncatedTo(MINUTES)));

        if (conflict) {
            throw new RuntimeException("Doctor already has appointment at this time");
        }
    }

    // assistant assigned doctors
    public List<AssistantDoctorListDTO> getAssignedDoctors() {

        User assistant = getCurrentAssistant();

        List<AssistantDoctor> mappings =
                assistantDoctorRepo.findByAssistantId(assistant.getId());

        return mappings.stream()
                .map(map -> {
                    Doctor d = map.getDoctor();
                    AssistantDoctorListDTO dto = new AssistantDoctorListDTO();
                    dto.setId(d.getId());
                    dto.setFirstName(d.getUser().getFirstName());
                    dto.setLastName(d.getUser().getLastName());
                    dto.setSpecialty(d.getSpecialty());
                    dto.setImageUrl(d.getUser().getImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // doctor appointments
    public List<AssistantAppointmentDTO> getDoctorAppointments(UUID doctorId) {

        User assistant = getCurrentAssistant();

        boolean allowed =
                assistantDoctorRepo.findByAssistantId(assistant.getId())
                        .stream()
                        .anyMatch(m -> m.getDoctor().getId().equals(doctorId));

        if (!allowed) {
            throw new RuntimeException("Not allowed to access this doctor's schedule");
        }

        return appointmentRepo.findByDoctorId(doctorId).stream()
                .map(a -> {
                    AssistantAppointmentDTO dto = new AssistantAppointmentDTO();
                    dto.setId(a.getId());
                    dto.setType(a.getType());
                    dto.setPaymentType(a.getPaymentType());
                    dto.setDateTime(a.getDateTime().toString());
                    dto.setPatientName(a.getUser().getFirstName() + " " + a.getUser().getLastName());
                    // if AssistantAppointmentDTO has these fields, you can also set:
                    // dto.setPatientId(a.getUser().getId());
                    // dto.setStatus(a.getStatus());
                    return dto;
                })
                .toList();
    }

    // create appointment
    public AssistantAppointmentDTO createAppointment(UUID doctorId, Map<String, Object> payload) {

        User assistant = getCurrentAssistant();

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // check access
        boolean allowed = assistantDoctorRepo.findByAssistantId(assistant.getId())
                .stream()
                .anyMatch(m -> m.getDoctor().getId().equals(doctorId));
        if (!allowed) throw new RuntimeException("Not allowed for this doctor");

        // 1) Parse dateTime (assuming ISO-formatted string, e.g. "2025-12-31T10:20:00")
        LocalDateTime time = LocalDateTime.parse(payload.get("dateTime").toString());

        // 2) Get patientId from payload (IMPORTANT)
        Object patientIdObj = payload.get("patientId");
        if (patientIdObj == null) {
            throw new RuntimeException("patientId is required");
        }

        UUID patientId;
        try {
            patientId = UUID.fromString(patientIdObj.toString());
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid patientId format");
        }

        // 3) Load patient user
        User patient = userRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // optional: ensure this user is actually a patient
        if (patient.getRole() != UserRole.USER) {
            throw new RuntimeException("Selected user is not a patient");
        }

        // 4) Other fields
        String type = payload.get("type").toString();
        String paymentType = payload.get("paymentType").toString();

        // 5) Validations
        validateSlot(time);
        validateDoctorSchedule(doctor, time);
        validateConflicts(doctor, time);

        // 6) Create appointment FOR PATIENT, not assistant!
        Appointment a = new Appointment();
        a.setDoctor(doctor);
        a.setUser(patient);                 // <-- THIS IS THE KEY FIX
        a.setDateTime(time);
        a.setType(type);
        a.setPaymentType(paymentType);
        a.setStatus("BOOKED");

        appointmentRepo.save(a);

        // 7) Build DTO
        AssistantAppointmentDTO dto = new AssistantAppointmentDTO();
        dto.setId(a.getId());
        dto.setType(type);
        dto.setPaymentType(paymentType);
        dto.setDateTime(time.toString());
        dto.setPatientName(patient.getFirstName() + " " + patient.getLastName());
        // if DTO supports it:
        // dto.setPatientId(patient.getId());
        // dto.setStatus(a.getStatus());
        // dto.setDoctorId(doctor.getId());

        return dto;
    }

    // edit/cancel appointment
    public AssistantAppointmentDTO editAppointment(UUID id, Map<String, Object> payload) {

        User assistant = getCurrentAssistant();

        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // check ownership (assistant must be assigned to this doctor)
        boolean allowed = assistantDoctorRepo.findByAssistantId(assistant.getId())
                .stream()
                .anyMatch(m -> m.getDoctor().getId().equals(a.getDoctor().getId()));

        if (!allowed) {
            throw new RuntimeException("Not allowed to modify this appointment");
        }

        LocalDateTime time = LocalDateTime.parse(payload.get("dateTime").toString());
        String type = payload.get("type").toString();
        String paymentType = payload.get("paymentType").toString();

        validateSlot(time);
        validateDoctorSchedule(a.getDoctor(), time);
        validateConflicts(a.getDoctor(), time);

        a.setDateTime(time);
        a.setType(type);
        a.setPaymentType(paymentType);
        appointmentRepo.save(a);

        AssistantAppointmentDTO dto = new AssistantAppointmentDTO();
        dto.setId(a.getId());
        dto.setPatientName(a.getUser().getFirstName() + " " + a.getUser().getLastName());
        dto.setType(a.getType());
        dto.setPaymentType(a.getPaymentType());
        dto.setDateTime(a.getDateTime().toString());
        return dto;
    }

    public void cancelAppointment(UUID id) {

        User assistant = getCurrentAssistant();

        Appointment a = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        boolean allowed = assistantDoctorRepo.findByAssistantId(assistant.getId())
                .stream()
                .anyMatch(m -> m.getDoctor().getId().equals(a.getDoctor().getId()));

        if (!allowed) {
            throw new RuntimeException("You cannot cancel this appointment");
        }

        a.setStatus("CANCELLED");
        appointmentRepo.save(a);
    }

    // patient list
    public List<UserViewDTO> getAllPatients() {
        return userRepo.findAll().stream()
                .filter(u -> u.getRole() == UserRole.USER)
                .map(this::toUserViewDTO)
                .toList();
    }

    private UserViewDTO toUserViewDTO(User u) {
        UserViewDTO dto = new UserViewDTO();
        dto.setId(u.getId());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setEmail(u.getEmail());
        return dto;
    }
}