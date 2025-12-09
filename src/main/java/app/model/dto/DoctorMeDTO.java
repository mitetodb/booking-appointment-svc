package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DoctorMeDTO {

    private UUID id;          // doctor id
    private Integer specialtyId;
}
