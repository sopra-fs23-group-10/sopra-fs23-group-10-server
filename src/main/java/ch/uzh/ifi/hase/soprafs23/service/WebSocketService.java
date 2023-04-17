package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WebSocketService {

    private final UserRepository userRepository;

    @Autowired
    protected SimpMessagingTemplate smesg;
    Logger log = LoggerFactory.getLogger(WebSocketService.class);

    public WebSocketService(@Qualifier("userRepository") UserRepository userRepository,
                            @Lazy UserService userService) {
        this.userRepository = userRepository;
    }

    public void sendMessageToClients(String destination, Object dto) {
        this.smesg.convertAndSend(destination, dto);
    }

    /*
    public void updatePoints(long userPoints, long userId) {
        userRepository.updatePoints(userPoints,userId);
    }
    */
}