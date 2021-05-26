package com.example.webrtcvideochat.socket;

import com.example.webrtcvideochat.RoomServiceCleanAbstractTest;
import com.example.webrtcvideochat.model.Room;
import com.example.webrtcvideochat.model.WebSocketMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


class SocketHandlerTest extends RoomServiceCleanAbstractTest {

    private Room testRoom;

    private String userName;

    @Autowired
    private SocketHandler socketHandler;

    private WebSocketSession session = Mockito.mock(WebSocketSession.class);

    private ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();


    @BeforeEach
    public void init() {
        testRoom = new Room("test");
        userName = UUID.randomUUID().toString();
        roomService.addRoom(testRoom);
    }

    @Test
    public void shouldAddAndRemoveClientWhenConnectionOpenAndThenClosed() throws IOException {
        Map<String, WebSocketSession> clients = new HashMap<>();
        clients.put(userName, session);
        Mockito.when(session.getId()).thenReturn("sessionId");

        WebSocketMessage message = new WebSocketMessage(userName,"join", testRoom.getId(), null, null);
        socketHandler.handleTextMessage(session, new TextMessage(ow.writeValueAsString(message)));
        Map<String, WebSocketSession> actualClients = roomService.getClients(testRoom);

        Assertions.assertFalse(
                actualClients.isEmpty());

        Assertions.assertEquals(
                clients, actualClients);

        message = new WebSocketMessage(userName, "leave", testRoom.getId(), null, null);
        socketHandler.handleTextMessage(session, new TextMessage(ow.writeValueAsString(message)));

        socketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        Assertions.assertTrue(
                roomService.getClients(testRoom).isEmpty());
    }
}