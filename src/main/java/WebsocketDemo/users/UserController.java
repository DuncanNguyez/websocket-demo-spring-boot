package WebsocketDemo.users;

import WebsocketDemo.utils.HibernateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.InvocationTargetException;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserService userService;

    @Autowired
    private HibernateProcessor hibernateProcessor;

    @MessageMapping("/user.connect")
    @SendToUser("/user/{username}/connected")
    public void connect() {
    }

    @MessageMapping("/user.disconnect")
    @SendToUser("/user/{username}/disconnected")
    public void disconnect() {
    }

    @GetMapping("/user/info")
    public ResponseEntity<User> getInfo(@AuthenticationPrincipal UserDetails userDetails) throws InvocationTargetException, IllegalAccessException {
        User user = userService.findByUsernameWithChatRoomsInfo(userDetails.getUsername());
        User userIsUn = hibernateProcessor.deepUnProxyAndClone(user);
        return ResponseEntity.ok(userIsUn);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username) throws InvocationTargetException, IllegalAccessException {
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(hibernateProcessor.deepUnProxyAndClone(user));
    }


}
