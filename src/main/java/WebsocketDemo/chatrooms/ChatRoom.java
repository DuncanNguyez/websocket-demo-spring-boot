package WebsocketDemo.chatrooms;

import WebsocketDemo.chat.ChatMessage;
import WebsocketDemo.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

public class ChatRoom {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ChatRoomType type;

    @ManyToMany
    private List<User> members;

    @OneToMany(mappedBy = "recipient")
    private List<ChatMessage> chatMessages;
}
