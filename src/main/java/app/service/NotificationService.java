package app.service;

import app.model.dto.NotificationDTO;
import app.model.Appointment;
import app.model.Notification;
import app.model.User;
import app.repository.AppointmentRepository;
import app.repository.NotificationRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;

    private User getCurrentUser() {
        String email =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<NotificationDTO> getMyNotifications() {
        User user = getCurrentUser();

        return notificationRepo.findByUserIdOrderByCreatedOnDesc(user.getId())
                .stream()
                .map(n -> {
                    NotificationDTO dto = new NotificationDTO();
                    dto.setId(n.getId());
                    dto.setMessage(n.getMessage());
                    dto.setRead(n.isRead());
                    dto.setCreatedOn(n.getCreatedOn().toString());
                    return dto;
                })
                .toList();
    }

    public void markAsRead(UUID id) {
        User user = getCurrentUser();

        Notification n = notificationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!n.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        n.setRead(true);
        notificationRepo.save(n);
    }

    public void generateReminders() {
        LocalDateTime now = LocalDateTime.now();

        // send reminder X minutes before appointment? (choose: 60 min)
        LocalDateTime reminderThreshold = now.plusMinutes(60);

        List<Appointment> upcoming = appointmentRepo.findAll().stream()
                .filter(a ->
                        a.getStatus().equals("BOOKED") &&
                                a.getDateTime().isAfter(now) &&
                                a.getDateTime().isBefore(reminderThreshold)
                )
                .toList();

        for (Appointment appointment : upcoming) {

            // check if reminder exists
            boolean alreadySent = notificationRepo.findByUserIdOrderByCreatedOnDesc(
                            appointment.getUser().getId())
                    .stream()
                    .anyMatch(n ->
                            n.getAppointment() != null &&
                                    n.getAppointment().getId().equals(appointment.getId())
                    );

            if (alreadySent) continue;

            Notification n = new Notification();
            n.setUser(appointment.getUser());
            n.setAppointment(appointment);
            n.setMessage(
                    "Reminder: You have an appointment with Dr. " +
                            appointment.getDoctor().getUser().getLastName() +
                            " at " + appointment.getDateTime()
            );
            n.setCreatedOn(LocalDateTime.now());
            n.setRead(false);

            notificationRepo.save(n);
        }
    }
}
