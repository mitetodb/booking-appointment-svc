package app.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateWorkingHoursDTO {
    private int dayOfWeek;
    private String startTime;
    private String endTime;
}
