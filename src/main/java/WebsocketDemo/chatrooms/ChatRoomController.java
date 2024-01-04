package WebsocketDemo.chatrooms;

import WebsocketDemo.utils.HibernateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.InvocationTargetException;

@Controller
@Slf4j
public class ChatRoomController {
    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private HibernateProcessor hibernateProcessor;
    @GetMapping("/room/{id}")
    public ResponseEntity<ChatRoom> getChatRoom(@PathVariable("id") Integer id) throws InvocationTargetException, IllegalAccessException {
        ChatRoom chatRoom = chatRoomService.findById(id);
        return ResponseEntity.ok(hibernateProcessor.deepUnProxyAndClone(chatRoom));
    }

    @MessageMapping("/room.create")
    public void addRoom(@Payload ChatRoom chatRoom) {
        ChatRoom chatRoomSaved = chatRoomService.save(chatRoom);
        chatRoomSaved.getMembers().forEach(user -> {
            log.info(user.getUsername());
            String destination = "/room/" + user.getUsername() + "/in-room";
            try {
                messagingTemplate.convertAndSend(destination, hibernateProcessor.deepUnProxyAndClone(chatRoomSaved));
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
