package app.service;

import app.model.dto.DoctorDetailsDTO;
import app.model.dto.DoctorListViewDTO;
import app.model.Doctor;
import app.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final ModelMapper mapper;

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
}

