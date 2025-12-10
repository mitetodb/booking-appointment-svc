package app.service;

import app.model.AssistantDoctor;
import app.model.User;
import app.model.dto.AssistantListItemDTO;
import app.model.dto.DoctorDetailsDTO;
import app.model.dto.DoctorListViewDTO;
import app.model.Doctor;
import app.model.dto.DoctorMeDTO;
import app.model.enums.Specialty;
import app.model.enums.UserRole;
import app.repository.AssistantDoctorRepository;
import app.repository.DoctorRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final UserRepository userRepo;
    private final AssistantDoctorRepository assistantDoctorRepo;


    public List<DoctorListViewDTO> getAllDoctors() {
        return doctorRepo.findAll().stream()
                .map(d -> {
                    DoctorListViewDTO dto = new DoctorListViewDTO();
                    dto.setId(d.getId());
                    dto.setFirstName(d.getUser().getFirstName());
                    dto.setLastName(d.getUser().getLastName());
                    dto.setSpecialty(d.getSpecialty());
                    dto.setImageUrl(d.getUser().getImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public DoctorDetailsDTO getDoctorById(UUID id) {
        Doctor doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorDetailsDTO dto = new DoctorDetailsDTO();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getUser().getFirstName());
        dto.setLastName(doctor.getUser().getLastName());
        dto.setImageUrl(doctor.getUser().getImageUrl());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setWorksWithHealthInsurance(doctor.isWorksWithHealthInsurance());

        dto.setWorkingHours(
                doctor.getWorkingHours().stream().map(wh -> {
                    var wd = new app.model.dto.WorkingHoursDTO();
                    wd.setDayOfWeek(wh.getDayOfWeek());
                    wd.setStartTime(wh.getStartTime());
                    wd.setEndTime(wh.getEndTime());
                    return wd;
                }).collect(Collectors.toList())
        );

        return dto;
    }

    public Doctor getCurrentDoctor() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return doctorRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public DoctorMeDTO getMyProfile() {
        Doctor doctor = getCurrentDoctor();

        DoctorMeDTO dto = new DoctorMeDTO();
        dto.setId(doctor.getId());

        Specialty s = Specialty.fromEnglishName(doctor.getSpecialty());
        dto.setSpecialtyId(s != null ? s.getId() : null);

        return dto;
    }

    public void updateMySpecialty(Integer specialtyId) {
        if (specialtyId == null) {
            throw new RuntimeException("specialtyId is required");
        }

        Specialty specialtyEnum = Specialty.fromId(specialtyId);

        Doctor doctor = getCurrentDoctor();
        doctor.setSpecialty(specialtyEnum.getEnglishName());
        doctorRepo.save(doctor);
    }

    public List<AssistantListItemDTO> getAllAssistants() {
        return userRepo.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ASSISTANT)
                .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                .map(u -> {
                    AssistantListItemDTO dto = new AssistantListItemDTO();
                    dto.setId(u.getId());
                    dto.setFirstName(u.getFirstName());
                    dto.setLastName(u.getLastName());
                    dto.setEmail(u.getEmail());
                    dto.setRole(u.getRole().name());
                    dto.setStatus(u.getStatus());
                    return dto;
                })
                .toList();
    }

    public void updateMyAssistant(UUID assistantId) {
        Doctor doctor = getCurrentDoctor();

        assistantDoctorRepo.deleteByDoctorId(doctor.getId());

        if (assistantId == null) {
            return;
        }

        User assistant = userRepo.findById(assistantId)
                .orElseThrow(() -> new RuntimeException("Assistant not found"));

        if (assistant.getRole() != UserRole.ASSISTANT) {
            throw new RuntimeException("User is not an assistant");
        }

        AssistantDoctor mapping = new AssistantDoctor();
        mapping.setAssistant(assistant);
        mapping.setDoctor(doctor);
        assistantDoctorRepo.save(mapping);
    }
}

