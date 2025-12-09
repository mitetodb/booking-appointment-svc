package app.service;

import app.model.dto.AdminAssistantDoctorDTO;
import app.model.dto.AdminUpdateUserDTO;
import app.model.dto.AdminUserViewDTO;
import app.model.AssistantDoctor;
import app.model.Doctor;
import app.model.User;
import app.model.enums.UserRole;
import app.repository.AssistantDoctorRepository;
import app.repository.DoctorRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepo;
    private final DoctorRepository doctorRepo;
    private final AssistantDoctorRepository assistantDoctorRepo;
    private final ModelMapper mapper;

    // --- 1) List all users ---
    public List<AdminUserViewDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(u -> {
                    AdminUserViewDTO dto = new AdminUserViewDTO();
                    dto.setId(u.getId());
                    dto.setEmail(u.getEmail());
                    dto.setFirstName(u.getFirstName());
                    dto.setLastName(u.getLastName());
                    dto.setRole(u.getRole().name());
                    dto.setStatus(u.getStatus());
                    return dto;
                })
                .toList();
    }

    // --- 2) Update user role & status ---
    public AdminUserViewDTO updateUser(UUID id, AdminUpdateUserDTO dto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserRole newRole = UserRole.valueOf(dto.getRole());
        user.setRole(newRole);
        user.setStatus(dto.getStatus());

        userRepo.save(user);

        // if role is DOCTOR and no Doctor entity yet -> create one
        if (newRole == UserRole.DOCTOR) {
            doctorRepo.findByUserId(user.getId())
                    .orElseGet(() -> {
                        Doctor doctor = new Doctor();
                        doctor.setUser(user);
                        doctor.setSpecialty("General"); // може да се смени по-късно
                        doctor.setWorksWithHealthInsurance(false);
                        return doctorRepo.save(doctor);
                    });
        }

        // If role is not DOCTOR, можем да оставим Doctor записа – не болка,
        // или може да добавиш logika за „архив“. За проекта това е достатъчно.

        AdminUserViewDTO view = new AdminUserViewDTO();
        view.setId(user.getId());
        view.setEmail(user.getEmail());
        view.setFirstName(user.getFirstName());
        view.setLastName(user.getLastName());
        view.setRole(user.getRole().name());
        view.setStatus(user.getStatus());
        return view;
    }

    // --- 3) Delete user ---
    public void deleteUser(UUID id) {
        userRepo.deleteById(id);
    }

    // --- 4) Get all doctors (for assigning) ---
    public List<AdminUserViewDTO> getAllDoctors() {
        return doctorRepo.findAll().stream()
                .map(d -> {
                    User u = d.getUser();
                    AdminUserViewDTO dto = new AdminUserViewDTO();
                    dto.setId(u.getId());
                    dto.setEmail(u.getEmail());
                    dto.setFirstName(u.getFirstName());
                    dto.setLastName(u.getLastName());
                    dto.setRole(u.getRole().name());
                    dto.setStatus(u.getStatus());
                    return dto;
                })
                .toList();
    }

    // --- 5) Get all assistants ---
    public List<AdminUserViewDTO> getAllAssistants() {
        return userRepo.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ASSISTANT)
                .map(u -> {
                    AdminUserViewDTO dto = new AdminUserViewDTO();
                    dto.setId(u.getId());
                    dto.setEmail(u.getEmail());
                    dto.setFirstName(u.getFirstName());
                    dto.setLastName(u.getLastName());
                    dto.setRole(u.getRole().name());
                    dto.setStatus(u.getStatus());
                    return dto;
                })
                .toList();
    }

    // --- 6) Get doctors assigned to assistant ---
    public List<AdminAssistantDoctorDTO> getAssistantDoctors(UUID assistantId) {
        List<AssistantDoctor> mappings =
                assistantDoctorRepo.findByAssistantId(assistantId);

        return mappings.stream().map(m -> {
            AdminAssistantDoctorDTO dto = new AdminAssistantDoctorDTO();
            dto.setDoctorId(m.getDoctor().getId());
            dto.setDoctorName(m.getDoctor().getUser().getFirstName() + " " +
                    m.getDoctor().getUser().getLastName());
            dto.setDoctorSpecialty(m.getDoctor().getSpecialty());
            return dto;
        }).toList();
    }

    // --- 7) Assign doctor to assistant ---
    public void assignDoctorToAssistant(UUID assistantUserId, UUID doctorId) {

        User assistant = userRepo.findById(assistantUserId)
                .orElseThrow(() -> new RuntimeException("Assistant not found"));

        if (assistant.getRole() != UserRole.ASSISTANT) {
            throw new RuntimeException("User is not an assistant");
        }

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        boolean exists = assistantDoctorRepo.findByAssistantId(assistant.getId())
                .stream()
                .anyMatch(m -> m.getDoctor().getId().equals(doctorId));

        if (exists) return;

        AssistantDoctor mapping = new AssistantDoctor();
        mapping.setAssistant(assistant);
        mapping.setDoctor(doctor);
        assistantDoctorRepo.save(mapping);
    }

    // --- 8) Remove doctor from assistant ---
    public void unassignDoctorFromAssistant(UUID assistantUserId, UUID doctorId) {

        User assistant = userRepo.findById(assistantUserId)
                .orElseThrow(() -> new RuntimeException("Assistant not found"));

        List<AssistantDoctor> mappings = assistantDoctorRepo.findByAssistantId(assistant.getId());

        mappings.stream()
                .filter(m -> m.getDoctor().getId().equals(doctorId))
                .findFirst()
                .ifPresent(assistantDoctorRepo::delete);
    }
}

