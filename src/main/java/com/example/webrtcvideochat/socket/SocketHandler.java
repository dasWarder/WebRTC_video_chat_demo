package com.example.webrtcvideochat.socket;

import com.example.webrtcvideochat.model.Room;
import com.example.webrtcvideochat.model.WebSocketMessage;
import com.example.webrtcvideochat.service.RoomServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.example.webrtcvideochat.socket.Message.*;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private RoomServiceImpl roomService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Room> sessionIdToRoomMap = new HashMap<>();

    @Autowired
    public SocketHandler(RoomServiceImpl roomService) {
        this.roomService = roomService;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionIdToRoomMap.remove(session.getId());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN.getMessageName(),
                Boolean.toString(!sessionIdToRoomMap.isEmpty()), null, null));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {
        WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
        String userName = message.getFrom();
        String data = message.getData();
        String type = message.getType();
        Room room;

        if (type.equals(MSG_TYPE_OFFER.getMessageName()) ||
                type.equals(MSG_TYPE_ANSWER.getMessageName()) ||
                type.equals(MSG_TYPE_ICE.getMessageName())) {

            Object candidate = message.getCandidate();
            Object sdp = message.getSdp();
            Room rm = sessionIdToRoomMap.get(session.getId());

            if (rm != null) {
                Map<String, WebSocketSession> clients = roomService.getClients(rm);
                for (Map.Entry<String, WebSocketSession> client : clients.entrySet()) {
                    if (!client.getKey().equals(userName)) {
                        sendMessage(client.getValue(), new WebSocketMessage(
                                userName,
                                message.getType(),
                                data,
                                candidate,
                                sdp));
                    }
                }
            }
        } else if (type.equals(MSG_TYPE_JOIN.getMessageName())) {

            room = roomService.findRoomByStringId(data)
                    .orElseThrow(() -> new IOException("Invalid room number received!"));
            roomService.addClient(room, userName, session);
            sessionIdToRoomMap.put(session.getId(), room);

        } else if (type.equals(MSG_TYPE_LEAVE.getMessageName())) {

            room = sessionIdToRoomMap.get(session.getId());
            Optional<String> client = roomService.getClients(room).entrySet().stream()
                    .filter(entry -> Objects.equals(entry.getValue().getId(), session.getId()))
                    .map(Map.Entry::getKey)
                    .findAny();
            client.ifPresent(c -> roomService.removeClientByName(room, c));

        } else if (type.equals(MSG_TYPE_TEXT.getMessageName())) {

        }
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
