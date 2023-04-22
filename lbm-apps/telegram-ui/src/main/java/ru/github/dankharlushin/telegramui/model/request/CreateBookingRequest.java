package ru.github.dankharlushin.telegramui.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
public final class CreateBookingRequest implements BotRequest {

    private Integer labUnitId;
    private String labName;
    private LocalDate startDate;
    private LocalTime startTime;
    private String requesterName;
    private boolean delivered = false;

    @Override
    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(final String requesterName) {
        this.requesterName = requesterName;
    }
}
