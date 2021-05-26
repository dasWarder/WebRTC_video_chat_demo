package com.example.webrtcvideochat.model;

import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;


/**
 * The class for the rooms entities
 */
@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Room {

    /**
     * The field of the room id must be NOT NULL
     */
    @NotNull
    private final String id;

    /**
     * The field of the room's clients list
     */
    private final Map<String, WebSocketSession> clients = new HashMap<>();

    public Room(String id) {
        this.id = id;
    }

}
