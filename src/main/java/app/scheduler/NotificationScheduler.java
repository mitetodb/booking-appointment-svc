package app.scheduler;

import app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    // every 1 minute
    @Scheduled(fixedRate = 60000)
    public void runReminderScan() {
        notificationService.generateReminders();
    }
}

