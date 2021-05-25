package com.example.webrtcvideochat.model;


import lombok.*;


/**
 * The class to mapping WebSocket messages for a view
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class WebSocketMessage {

    /**
     * The field with the name of a user
     */
    private String from;

    /**
     * The field with a type of a text message
     */
    private String type;

    /**
     * The field with send data
     */
    private String data;

    /**
     * The field with candidate for WebRTC
     */
    private Object candidate;

    /**
     * The field with sdp for WebRTC
     */
    private Object sdp;
}
