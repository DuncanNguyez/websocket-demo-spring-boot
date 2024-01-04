const socket = new SockJS("/ws");
const stompClient = Stomp.over(socket);
const addFriendElement = document.querySelector("#addFriend");
const listRoomsELement = document.querySelector(".listRooms");
const listMessagesElement = document.querySelector(".listMessages");
const messageElement = document.querySelector("#message");
const username = document.querySelector(".username h3");
let user;
const connect = async () => {
  await getInfo();
  addFriendHandler();
  sendMessageHandler();
  stompClient.connect({}, onConnected, onError);
};

const onConnected = () => {
  stompClient.subscribe(`/room/${user.username}/in-room`, (payload) => {
    const chatRoom = JSON.parse(payload.body);
    console.log({ newRoom: chatRoom });
    user.chatRooms = [...user.chatRooms, chatRoom];
    renderChatRooms();
  });
  user.chatRooms.forEach(({ id }) => {
    stompClient.subscribe(`/room/${id}/queue/message`, resolveMessage);
  });
  console.log("websocket connected");
};

const onError = () => {};
const sendMessageHandler = () => {
  messageElement.onkeydown = (e) => {
    if (e.key == "Enter") {
      const activeElement = document.querySelector(".active input");
      const activeId = activeElement?.value;
      if (!activeId) {
        alert("Not room active");
        return;
      }
      const chatRoom = user.chatRooms.find((room) => room.id == activeId);
      console.log({ chatRoom });
      const chatMessage = {
        content: messageElement.value,
        sender: { id: user.id },
        recipient: chatRoom,
        usersWatched: [user],
      };
      console.log({ chatMessage });
      stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
      messageElement.value = "";
    }
  };
};
const resolveMessage = (payload) => {
  const chatMessage = JSON.parse(payload.body);
  const chatRoomId = chatMessage.recipient.id;
  const activeId = document.querySelector(".active input").value;
  user.chatRooms = user.chatRooms.map((room) => {
    if (room.id == chatRoomId) {
      const roomUpdated = {
        ...room,
        chatMessages: [...room.chatMessages, chatMessage],
      };
      if (chatRoomId == activeId) {
        renderRoomMessages(roomUpdated);
      }
      return roomUpdated;
    }
    return room;
  });
};
const getInfo = async () => {
  const res = await fetch("/user/info");
  if (res.ok) {
    user = await res.json();
    username.innerHTML = user.username;
    renderChatRooms();
  } else {
    console.log("not get user info");
  }
};
const renderChatRooms = () => {
  listRoomsELement.innerHTML = "";
  user.chatRooms.forEach((room) => {
    listRoomsELement.innerHTML += `<div class="room">
                                          <input type="hidden" value="${room.id}">
                                          <h3>${room.name}</h3>
                                      </div>`;
  });
  roomHandler();
};
const roomHandler = () => {
  const roomsElement = document.querySelectorAll(".room");
  roomsElement.forEach((element) => {
    element.onclick = () => {
      roomsElement.forEach((e) => e.classList.remove("active"));
      element.classList.add("active");
      const roomId = element.querySelector("input").value;
      const room = user.chatRooms.find((r) => r.id == roomId);
      if (room) renderRoomMessages(room);
    };
  });
};
const renderRoomMessages = (room) => {
  listMessagesElement.innerHTML = "";
  if (!room.chatMessages) return;
  room.chatMessages.forEach((chatMessage) => {
    const mss =
      chatMessage.sender.id == user.id
        ? `<div class="m-3 right">
                <span class="p-2 messageItem bg-primary text-white"> ${chatMessage.content} </span>
            </div>`
        : `<div class="m-3">
                <span class="p-2 messageItem bg-white text-primary"> ${chatMessage.content} </span>
            </div>`;
    listMessagesElement.innerHTML += mss;
  });
};
const addFriendHandler = () => {
  addFriendElement.onkeydown = async (e) => {
    if (e.key == "Enter") {
      const value = addFriendElement.value;

      const res = await fetch(`/user/${value}`);
      if (res.ok) {
        const friend = await res.json();
        if (
          user.chatRooms.find(
            (room) =>
              room.type == "NORMAL" &&
              room.members.find((member) => member.id == friend.id)
          )
        ) {
          console.log("already friend");
          return;
        }
        const chatRoom = {
          name: `${user.username}_${friend.username}`,
          type: "NORMAL",
          members: [user, friend],
        };
        stompClient.send("/app/room.create", {}, JSON.stringify(chatRoom));
        addFriendElement.value = "";
      }
    }
  };
};
connect();
