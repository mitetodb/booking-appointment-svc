package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssistantAppointmentDTO {
    private UUID id;
    private String patientName;
    private String dateTime;
    private String type;
    private String paymentType;
}

