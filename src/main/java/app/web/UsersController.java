package app.web;

import app.model.dto.ChangePasswordDTO;
import app.model.dto.UserProfileUpdateDTO;
import app.model.dto.UserViewDTO;
import app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UsersController {

    private final UserService userService;

    @GetMapping("/me")
    public UserViewDTO getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PutMapping("/me")
    public UserViewDTO updateProfile(
            @Valid @RequestBody UserProfileUpdateDTO dto
    ) {
        return userService.updateProfile(dto);
    }

    @PutMapping("/me/change-password")
    public void changePassword(
            @Valid @RequestBody ChangePasswordDTO dto
    ) {
        userService.changePassword(dto);
    }
}
