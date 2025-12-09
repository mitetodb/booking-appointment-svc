package app.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminUpdateUserDTO {

    @NotBlank
    private String role;

    @NotBlank
    private String status;
}
