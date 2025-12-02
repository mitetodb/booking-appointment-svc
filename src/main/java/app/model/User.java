package app.model;

import app.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String imageUrl;

    private String address;

    private String country;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    // ACTIVE, INACTIVE, BLOCKED, PENDING
    private String status;

    private LocalDateTime createdOn = LocalDateTime.now();
    private LocalDateTime updatedOn = LocalDateTime.now();
}
