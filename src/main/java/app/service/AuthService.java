package app.service;

import app.model.User;
import app.model.dto.AuthResponse;
import app.model.dto.LoginRequest;
import app.model.dto.RegisterRequest;
import app.model.dto.UserViewDTO;
import app.model.enums.UserRole;
import app.repository.UserRepository;
import app.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final ModelMapper modelMapper;

    public AuthResponse register(RegisterRequest request) {

        userRepo.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email already in use");
                });

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(UserRole.USER);
        user.setStatus("ACTIVE");

        userRepo.save(user);

        String token = createTokenForUser(user);

        UserViewDTO view = modelMapper.map(user, UserViewDTO.class);
        view.setRole(user.getRole().name());

        return new AuthResponse(token, view);
    }

    public AuthResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authReq =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword());

        authManager.authenticate(authReq);

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String token = createTokenForUser(user);

        UserViewDTO view = modelMapper.map(user, UserViewDTO.class);
        view.setRole(user.getRole().name());

        return new AuthResponse(token, view);
    }

    private String createTokenForUser(User user) {
        return jwtUtil.generateToken(
                user.getEmail(),
                Map.of(
                        "id", user.getId().toString(),
                        "role", user.getRole().name()
                )
        );
    }
}
