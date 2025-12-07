package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssistantDoctorListDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String specialty;
    private String imageUrl;
}
