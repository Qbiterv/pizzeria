package pl.auctane.brandenburg.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SessionDto {
    @EqualsAndHashCode.Include
    private String sessionId;
    private String userIp;
    private String userAgent;

    private Instant expirationDate;

    public SessionDto(String sessionId, UserDataDto userDataDto, long timeToExpire) {
        this.sessionId = sessionId;
        this.userAgent = userDataDto.getUserAgent();
        this.userIp = userDataDto.getUserIp();
        this.expirationDate = Instant.now().plusSeconds(timeToExpire*60);
    }
}
