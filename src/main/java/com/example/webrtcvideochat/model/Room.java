package com.example.webrtcvideochat.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode
public class Room {

    @NotNull
    private final String id;

    private final Map<String, WebSocketSession> clients = new HashMap<>();

    public Room(String id) {
        this.id = id;
    }

}
