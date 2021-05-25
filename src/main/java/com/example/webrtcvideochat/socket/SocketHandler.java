package com.example.webrtcvideochat.socket;

import com.example.webrtcvideochat.model.Room;
import com.example.webrtcvideochat.model.WebSocketMessage;
import com.example.webrtcvideochat.service.RoomServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

/**
 * The main method that implements TextWebSocketHandler to send messages to WebRTC
 */
@Slf4j
@Component
public class SocketHandler extends TextWebSocketHandler {

    /**
     * The room service bean
     */
    private RoomServiceImpl roomService;

    /**
     * The object mapper for converting JSON objects to string
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The map whe key - a session id, value - a room
     */
    private Map<String, Room> sessionIdToRoomMap = new HashMap<>();

    @Autowired
    public SocketHandler(RoomServiceImpl roomService) {
        this.roomService = roomService;
    }

    /**
     * The method that called after the session connection was closed
     * @param session the session for which the connection was closed
     * @param status the status of close
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("A session with id={} was closed", session.getId());
        sessionIdToRoomMap.remove(session.getId());
    }

    /**
     * The method that called after creating the successful connection for the session.
     * @param session the session for which was created a connection
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("A session with id={} was successfully started", session.getId());

        /**
         * Send the message to WebRTC about successful connection and joining
         */
        sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN.getMessageName(),
                Boolean.toString(!sessionIdToRoomMap.isEmpty()), null, null));
    }

    /**
     * The method to work with difference messages types.
     * @see Message
     * @param session the session object
     * @param textMessage the message that was received on the @param session
     * @throws IOException in case of problems during answering process
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {
        WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class);
        String userName = message.getFrom();
        String data = message.getData();
        String type = message.getType();
        Room room;

        log.info("The message of a type={} was received", type);

        if (type.equals(MSG_TYPE_OFFER.getMessageName()) ||
                type.equals(MSG_TYPE_ANSWER.getMessageName()) ||
                type.equals(MSG_TYPE_ICE.getMessageName())) {


            Object candidate = message.getCandidate();
            Object sdp = message.getSdp();
            Room rm = sessionIdToRoomMap.get(session.getId());


            if (rm != null) {
                log.info("Moved through the collection of clients for room with id={} to send a message", rm.getId());
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

            log.info("The client with a name={} added to the room with id={} on session with id={}", userName, room.getId(), session.getId());

            sessionIdToRoomMap.put(session.getId(), room);

        } else if (type.equals(MSG_TYPE_LEAVE.getMessageName())) {

            room = sessionIdToRoomMap.get(session.getId());
            Optional<String> client = roomService.getClients(room).entrySet().stream()
                    .filter(entry -> Objects.equals(entry.getValue().getId(), session.getId()))
                    .map(Map.Entry::getKey)
                    .findAny();

            log.info("The client with a name={} was removed from the room with id={} on session with id={}",
                    userName, room.getId(), session.getId());

            client.ifPresent(c -> roomService.removeClientByName(room, c));

        } else if (type.equals(MSG_TYPE_TEXT.getMessageName())) {

            Room rm = sessionIdToRoomMap.get(session.getId());

            if(rm != null) {
                Map<String, WebSocketSession> clients = roomService.getClients(rm);

                log.info("Move through the collection of clients for the room with id={} to send a message on the session with id={}",
                        rm.getId(), session.getId());

                for (Map.Entry<String, WebSocketSession> client : clients.entrySet()) {
                        client.getValue().sendMessage(textMessage);
                }
            }
        }
    }

    /**
     * The method to map the message and convert it to the string.
     * @param session the session object
     * @param message the message that must be converted
     */
    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            log.info("Try to send a message of the type={} on the session with id={}",message.getType(), session.getId());

            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.info("IOException occurred during the sendMessage() method on session with id={}", session.getId());

            e.printStackTrace();
        }
    }
}
