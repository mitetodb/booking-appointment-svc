package app.model;

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
@Table(name = "appointments")
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Doctor doctor;

    @ManyToOne
    private User user;

    private LocalDateTime dateTime;

    // PRIMARY / FOLLOW_UP
    private String type;

    // PRIVATE / NHIF (zdravna kasa)
    private String paymentType;

    // BOOKED / CANCELLED
    private String status;

    private LocalDateTime createdOn = LocalDateTime.now();
}
