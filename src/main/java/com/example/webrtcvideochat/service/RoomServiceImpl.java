package com.example.webrtcvideochat.service;

import com.example.webrtcvideochat.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Service
public class RoomServiceImpl implements RoomService {

    private final Set<Room> rooms = new TreeSet<>(Comparator.comparing(Room::getId));

    public Set<Room> getRooms() {

        TreeSet<Room> copyRoomSet = new TreeSet<>(Comparator.comparing(Room::getId));
        copyRoomSet.addAll(rooms);

        return copyRoomSet;
    }

    public Boolean addRoom(Room room) {
        return rooms.add(room);
    }

    public Optional<Room> findRoomByStringId(String id) {
        return rooms.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public String getRoomId(Room room) {
        return room.getId();
    }

    public Map<String, WebSocketSession> getClients(Room room) {
        return Optional.ofNullable(room)
                .map(r -> Collections.unmodifiableMap(r.getClients()))
                .orElse(Collections.emptyMap());
    }

    public WebSocketSession addClient(Room room, String name, WebSocketSession session) {
        return room.getClients().put(name, session);
    }

    public WebSocketSession removeClientByName(Room room, String name) {
        return room.getClients().remove(name);
    }
}
