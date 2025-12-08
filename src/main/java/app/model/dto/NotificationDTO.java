package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class NotificationDTO {
    private UUID id;
    private String message;
    private boolean read;
    private String createdOn;
}

