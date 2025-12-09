package app.service;

import app.model.Doctor;
import app.model.WorkingHours;
import app.model.dto.WorkingHoursDTO;
import app.repository.DoctorRepository;
import app.repository.WorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public void updateWorkingHours(List<WorkingHoursDTO> dtos) {
        Doctor doctor = getCurrentDoctor();

        for (WorkingHoursDTO dto : dtos) {
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

    public void deleteWorkingHoursForDay(int dayOfWeek) {
        Doctor doctor = getCurrentDoctor();

        WorkingHours wh = whRepo.findByDoctorIdAndDayOfWeek(doctor.getId(), dayOfWeek);
        if (wh == null) {
            throw new RuntimeException("No working hours for this day");
        }

        whRepo.delete(wh);
    }

    public List<WorkingHoursDTO> getMyWorkingHours() {
        Doctor doctor = getCurrentDoctor();

        return whRepo.findByDoctorId(doctor.getId())
                .stream()
                .map(wh -> {
                    WorkingHoursDTO dto = new WorkingHoursDTO();
                    dto.setDayOfWeek(wh.getDayOfWeek());
                    dto.setStartTime(wh.getStartTime());
                    dto.setEndTime(wh.getEndTime());
                    return dto;
                })
                .toList();
    }
}
