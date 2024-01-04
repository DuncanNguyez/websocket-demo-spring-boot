package WebsocketDemo.chatrooms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatRoom findById(Integer id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    @Transactional
    public ChatRoom save(ChatRoom chatRoom) {

         return chatRoomRepository.save(chatRoom);

    }
}
