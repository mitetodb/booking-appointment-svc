package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class AppointmentViewDTO {
    private UUID id;
    private String doctorName;
    private String doctorId;
    private String dateTime;
    private String type;
    private String paymentType;
    private String status;
}

