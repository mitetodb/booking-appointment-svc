package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter @Setter
public class DoctorDetailsDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String specialty;
    private String imageUrl;
    private boolean worksWithHealthInsurance;

    private List<WorkingHoursDTO> workingHours;
}

