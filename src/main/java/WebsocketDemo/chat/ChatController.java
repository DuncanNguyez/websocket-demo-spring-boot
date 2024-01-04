package WebsocketDemo.chat;

import WebsocketDemo.utils.HibernateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.lang.reflect.InvocationTargetException;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private HibernateProcessor hibernateProcessor;

    @MessageMapping("/chat")
    public void chat(@Payload ChatMessage chatMessage) throws InvocationTargetException, IllegalAccessException {
        ChatMessage chatMessageSaved = chatMessageService.save(chatMessage);
        String destination = "/room/" + chatMessageSaved.getRecipient().getId() + "/queue/message";
        messagingTemplate.convertAndSend(destination, hibernateProcessor.deepUnProxyAndClone(chatMessageSaved));
    }
}
