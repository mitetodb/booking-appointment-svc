package app.service;

import app.model.dto.UpdateWorkingHoursDTO;
import app.model.Doctor;
import app.model.WorkingHours;
import app.repository.DoctorRepository;
import app.repository.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorWorkingHoursService {

    private final DoctorRepository doctorRepo;
    private final WorkingHoursRepository whRepo;

    private Doctor getCurrentDoctor() {
        String email =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return doctorRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public void updateWorkingHours(UpdateWorkingHoursDTO dto) {
        Doctor doctor = getCurrentDoctor();

        WorkingHours wh = doctor.getWorkingHours().stream()
                .filter(x -> x.getDayOfWeek() == dto.getDayOfWeek())
                .findFirst()
                .orElseGet(() -> {
                    WorkingHours newWH = new WorkingHours();
                    newWH.setDoctor(doctor);
                    newWH.setDayOfWeek(dto.getDayOfWeek());
                    doctor.getWorkingHours().add(newWH);
                    return newWH;
                });

        wh.setStartTime(dto.getStartTime());
        wh.setEndTime(dto.getEndTime());

        whRepo.save(wh);
    }
}
