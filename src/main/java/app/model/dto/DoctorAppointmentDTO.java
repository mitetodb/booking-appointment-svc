package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class DoctorAppointmentDTO {
    private UUID id;
    private String patientName;
    private String patientId;
    private String dateTime;
    private String type;
    private String paymentType;
    private String status;
}
