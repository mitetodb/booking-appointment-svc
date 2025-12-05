package app.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateAppointmentRequest {
    private String dateTime;
    private String type;
    private String paymentType;
}
