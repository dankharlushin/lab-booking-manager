package ru.github.dankharlushin.lbmlib.data.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OsSessionExpireNotification {

    private Long id;
    private String osUsername;
    private String labAppName;
    private Integer expireInMinutes;
    private NotificationUrgency urgencyLevel;
}
