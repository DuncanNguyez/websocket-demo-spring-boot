package WebsocketDemo.users;

import WebsocketDemo.chat.ChatMessage;
import WebsocketDemo.chatrooms.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public User findByUsernameWithChatRoomsInfo(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        user.getChatRooms().forEach(chatRoom -> {
            chatRoom.getMembers().forEach(User::getId);
            chatRoom.getChatMessages().forEach(chatMessage -> chatMessage.getUsersWatched().forEach(User::getId));
        });
        return user;
    }
}
