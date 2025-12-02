package app.repository;

import app.model.AssistantDoctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssistantDoctorRepository extends JpaRepository<AssistantDoctor, UUID> {
    List<AssistantDoctor> findByAssistantId(UUID id);
}
