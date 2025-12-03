package app.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserProfileUpdateDTO {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String imageUrl;

    private String country;

    private String address;
}
