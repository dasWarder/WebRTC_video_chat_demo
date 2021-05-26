package com.example.webrtcvideochat.service;

import com.example.webrtcvideochat.RoomServiceCleanAbstractTest;
import com.example.webrtcvideochat.model.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;


class RoomServiceImplTest extends RoomServiceCleanAbstractTest {

    private WebSocketSession session = Mockito.mock(WebSocketSession.class);

    @Test
    public void shouldCorrectlyReturnRooms() {
        Room firstRoom = new Room("first");
        Room secondRoom = new Room("second");
        Room thirdRoom = new Room("third");

        Set<Room> rooms = new HashSet<>(Arrays.asList(firstRoom, secondRoom, thirdRoom));

        roomService.addRoom(firstRoom);
        roomService.addRoom(secondRoom);
        roomService.addRoom(thirdRoom);

        Set<Room> actualRooms = roomService.getRooms();

        Assertions.assertEquals(rooms, actualRooms);
    }

    @Test
    public void shouldCorrectlySaveRoom() {
        Room testRoom = new Room("test");
        Boolean answer = roomService.addRoom(testRoom);

        Assertions.assertEquals(true, answer);

        Optional<Room> possibleRoom = roomService.findRoomByStringId(testRoom.getId());
        Assertions.assertNotNull(possibleRoom.get());
        Assertions.assertEquals(testRoom, possibleRoom.get());
    }

    @Test
    public void shouldCorrectlyAddClient() {
        Room testRoom = new Room("test");

        Boolean answer = roomService.addRoom(testRoom);
        Assertions.assertEquals(true, answer);

        String clientName = "TestName";
        Mockito.when(session.getId()).thenReturn("SessionId");

        roomService.addClient(testRoom, clientName, session);
        Map<String, WebSocketSession> clients = new HashMap<>();
        clients.put(clientName, session);

        Map<String, WebSocketSession> actualClients = roomService.getClients(testRoom);

        Assertions.assertEquals(clients, actualClients);
    }

    @Test
    public void shouldCorrectlyRemoveClient() {
        Room testRoom = new Room("test");
        String userName = "testName";

        Boolean answer = roomService.addRoom(testRoom);
        Assertions.assertEquals(true, answer);

        roomService.addClient(testRoom, userName, session);
        Assertions.assertNotNull(roomService.getClients(testRoom));

        roomService.removeClientByName(testRoom, userName);
        Assertions.assertTrue(roomService.getClients(testRoom).isEmpty());
    }


}