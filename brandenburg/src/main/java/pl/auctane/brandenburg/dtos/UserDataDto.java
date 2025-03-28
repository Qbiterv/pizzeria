package pl.auctane.brandenburg.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UserDataDto {
    private final String userAgent;
    private final String userIp;

    public UserDataDto(String userAgent, String userIp) {
        this.userAgent = userAgent;
        this.userIp = userIp;
    }
}
