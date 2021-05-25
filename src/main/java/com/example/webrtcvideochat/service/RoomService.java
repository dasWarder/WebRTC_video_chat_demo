package com.example.webrtcvideochat.service;

import com.example.webrtcvideochat.model.Room;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

/**
 * The interface to describe behavior of Room Service layer
 */
public interface RoomService {
    /**
     * The method to receive a set of all rooms
     * @return the set of all rooms
     */
    Set<Room> getRooms();

    /**
     * The method to add a new room for a session
     * @param room the room that must be added to the set of rooms
     * @return the boolean value, true - case when the room was successfully added,
     * false - case when the room not possible to add
     */
    Boolean addRoom(Room room);

    /**
     * The method to find a room by its ID
     * @param id the ID of a room from the set of all rooms
     * @return the optional object, where a room in case if the room with @param id exists
     * and null in case when the room doesn't exist
     */
    Optional<Room> findRoomByStringId(String id);

    /**
     * The method to get a collection of all clients for the room
     * @param room the room, which clients need to receive
     * @return the Map where key - userName nad value - a session
     */
    Map<String, WebSocketSession> getClients(Room room);

    /**
     * The method to add a client into the room
     * @param room the room where the client must be added
     * @param name the name of the client
     * @param session a session of a client
     * @return the session for added client
     */
    WebSocketSession addClient(Room room, String name, WebSocketSession session);

    /**
     * The method to remove a client from the room by a name
     * @param room the object of the room where the client must be removed
     * @param name the name of the client that must be removed
     * @return the session for the removed client
     */
    WebSocketSession removeClientByName(Room room, String name);
}
