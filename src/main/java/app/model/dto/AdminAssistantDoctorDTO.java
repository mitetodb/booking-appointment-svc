package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class AdminAssistantDoctorDTO {
    private UUID doctorId;
    private String doctorName;
    private String doctorSpecialty;
}
