package app.service;

import app.model.dto.ChangePasswordDTO;
import app.model.dto.UserProfileUpdateDTO;
import app.model.dto.UserViewDTO;
import app.model.User;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    public UserViewDTO getCurrentUser() {
        User user = getAuthUser();
        return mapper.map(user, UserViewDTO.class);
    }

    public UserViewDTO updateProfile(UserProfileUpdateDTO dto) {
        User user = getAuthUser();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setImageUrl(dto.getImageUrl());
        user.setCountry(dto.getCountry());
        user.setAddress(dto.getAddress());

        userRepo.save(user);

        return mapper.map(user, UserViewDTO.class);
    }

    public void changePassword(ChangePasswordDTO dto) {
        User user = getAuthUser();

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepo.save(user);
    }

    private User getAuthUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }
}

