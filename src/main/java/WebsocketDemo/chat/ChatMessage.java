package WebsocketDemo.chat;

import WebsocketDemo.chatrooms.ChatRoom;
import WebsocketDemo.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class    ChatMessage {
    @Id
    @GeneratedValue
    private Integer id;
    private String content;

    @CreatedDate
    private Date time;

    @ManyToOne
    private User sender;

    @ManyToOne
    private ChatRoom recipient;

    @ManyToMany(mappedBy = "messagesWatched")
    private List<User> usersWatched;
}
