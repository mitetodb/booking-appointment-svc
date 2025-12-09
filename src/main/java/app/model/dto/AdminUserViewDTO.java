package app.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class AdminUserViewDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String status;
}
