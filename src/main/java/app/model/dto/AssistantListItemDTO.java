package app.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AssistantListItemDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String status;
}
