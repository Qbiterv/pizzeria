package pl.auctane.brandenburg.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import pl.auctane.brandenburg.dtos.SessionDto;
import pl.auctane.brandenburg.dtos.UserDataDto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {

    @Value("${authentication.key}")
    private String authenticationKey;
    @Value("${session.time.to.expire}")
    private long timeToExpire;

    public SessionService() {
        sessions = new ArrayList<>();
    }


    List<SessionDto> sessions;

    public List<SessionDto> getAllSessions() {
        return sessions;
    }
    public boolean isSessionActive(String sessionId) {

        for(SessionDto session : sessions)
            if(session.getSessionId().equals(sessionId))
                return true;

        return false;
    }
    public boolean hasUserAccess(String sessionId, ServerHttpRequest request) throws Exception {
        UserDataDto userDataDto = getUserData(request);

        if(userDataDto == null) return false;

        for(SessionDto session : sessions)
            if (isUserSession(session, sessionId, userDataDto)) {
                if (isSessionExpired(session)) {
                    sessions.remove(session);
                    throw new Exception("Session expired");
                }
                return true;
            }

        return false;
    }
    public String createSession(String authorization, ServerHttpRequest request) throws IllegalArgumentException {
        UserDataDto userDataDto = getUserData(request);

        if(userDataDto == null) throw new IllegalArgumentException("Missing user data");

        //check if the authorization key is correct
        if(!authorization.equals(authenticationKey)) throw new IllegalArgumentException("Invalid authorization key");

        //check if the session is already created
        if (hasUserSession(userDataDto)) throw new IllegalArgumentException("Session already created");

        //create a session
        SessionDto session = new SessionDto(UUID.randomUUID().toString(), userDataDto, timeToExpire);

        //add session to the list
        sessions.add(session);

        return session.getSessionId();
    }

    private UserDataDto getUserData(ServerHttpRequest request) {
        String userAgent = request.getHeaders().getFirst("User-Agent");
        String ip = request.getRemoteAddress().getAddress().getHostAddress();

        //if any of the data is missing, return null
        if(userAgent == null || ip == null)
            return null;

        return new UserDataDto(userAgent, ip);
    }
    private boolean isSessionExpired(SessionDto session) {
        return session.getExpirationDate().isBefore(Instant.now());
    }
    private boolean hasUserSession(UserDataDto userDataDto) {
        for(SessionDto session : sessions)
            if(session.getUserAgent().equals(userDataDto.getUserAgent()) && session.getUserIp().equals(userDataDto.getUserIp()))
                return true;
        return false;
    }
    private boolean isUserSession(SessionDto session, String sessionId, UserDataDto userDataDto) {
        return session.getSessionId().equals(sessionId) && session.getUserAgent().equals(userDataDto.getUserAgent()) && session.getUserIp().equals(userDataDto.getUserIp());
    }


}
