package com.example.webrtcvideochat.service;

import com.example.webrtcvideochat.model.Room;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

public interface RoomService {

    Set<Room> getRooms();

    Boolean addRoom(Room room);

    Optional<Room> findRoomByStringId(String id);

    String getRoomId(Room room);

    Map<String, WebSocketSession> getClients(Room room);

    WebSocketSession addClient(Room room, String name, WebSocketSession session);

    WebSocketSession removeClientByName(Room room, String name);
}
