package org.egov.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.hibernate.validator.constraints.NotEmpty;

@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Getter
public class Token {
    private static final String IST = "Asia/Calcutta";
    @NotEmpty
    private final String tenantId;
    private String identity;
    private String number;
    private String uuid;
    private LocalDateTime expiryDateTime;
    private Long timeToLiveInSeconds;
    @Setter
    private boolean validated;

    public boolean isExpired() {
        return LocalDateTime.now(ZoneId.of(IST)).isAfter(expiryDateTime);
    }
}


