package com.example.webrtcvideochat.service;

import com.example.webrtcvideochat.model.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

/**
 * The class that implements RoomService interface.
 * @see RoomService
 */
@Slf4j
@Service
public class RoomServiceImpl implements RoomService {

    /**
     * The field with a set of all rooms
     */
    private final Set<Room> rooms = new TreeSet<>(Comparator.comparing(Room::getId));

    /**
     * The method that implement getRooms command.
     * @see RoomService#getRooms()
     * @return the set of all rooms
     */
    @Override
    public Set<Room> getRooms() {

        log.info("Get list of all rooms");
        TreeSet<Room> copyRoomSet = new TreeSet<>(Comparator.comparing(Room::getId));
        copyRoomSet.addAll(rooms);

        return copyRoomSet;
    }

    /**
     * The method that implements addRoom command.
     * @see RoomService#addRoom(Room)
     * @param room the room that must be added to the set of rooms
     * @return true - successfully added, false - not added
     */
    @Override
    public Boolean addRoom(Room room) {
        log.info("Add a room with id={} to the room set", room.getId());
        return rooms.add(room);
    }

    /**
     * The method that implements findRoomByStringId.
     * @see RoomService#findRoomByStringId(String)
     * @param id the ID of a room from the set of all rooms
     * @return the optional object, where the room - in case if the room with @param id exist
     * and NULL - in case when the room with @param id doesn't exist
     */
    @Override
    public Optional<Room> findRoomByStringId(String id) {
        log.info("Try to find a room with id={}", id);
        return rooms.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    /**
     * The method that implements getClients command.
     * @see RoomService#getClients(Room)
     * @param room the room, which clients need to receive
     * @return the Map of clients and sessions for them
     */
    @Override
    public Map<String, WebSocketSession> getClients(Room room) {
        log.info("Try to get the clients for the room with id={}", room.getId());
        return Optional.ofNullable(room)
                .map(r -> Collections.unmodifiableMap(r.getClients()))
                .orElse(Collections.emptyMap());
    }

    /**
     * The method that implements addClient command.
     * @see RoomService#addClient(Room, String, WebSocketSession)
     * @param room the room where the client must be added
     * @param name the name of the client
     * @param session a session of a client
     * @return the session for the added client
     */
    @Override
    public WebSocketSession addClient(Room room, String name, WebSocketSession session) {
        log.info("Add a client with name={} to a room with id={}", name, room.getId());
        return room.getClients().put(name, session);
    }

    /**
     * The method that implements removeClientByName command.
     * @see RoomService#removeClientByName(Room, String)
     * @param room the object of the room where the client must be removed
     * @param name the name of the client that must be removed
     * @return the session for the removed client
     */
    @Override
    public WebSocketSession removeClientByName(Room room, String name) {
        log.info("Remove a client with name={} from a room with id={}", name, room.getId());
        return room.getClients().remove(name);
    }
}
