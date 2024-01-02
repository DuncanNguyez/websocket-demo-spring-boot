package WebsocketDemo.users;

import WebsocketDemo.chat.ChatMessage;
import WebsocketDemo.chatrooms.ChatRoom;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "_user")
public class User {
    @Id
    @GeneratedValue
    private Integer id   ;
    private String username;
    private String email;
    private String password;
    private String image;

    @OneToMany(mappedBy = "sender")
    private List<ChatMessage> messagesSent;

    @ManyToMany
    private List<ChatMessage> messagesWatched;

    @ManyToMany
    private List<ChatRoom> chatRooms;
}
