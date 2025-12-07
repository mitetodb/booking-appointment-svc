package app.repository;

import app.model.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, UUID> {

    List<WorkingHours> findByDoctorId(UUID doctorId);

    WorkingHours findByDoctorIdAndDayOfWeek(UUID doctorId, Integer dayOfWeek);

}