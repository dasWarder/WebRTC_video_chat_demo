package com.example.webrtcvideochat.model;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class WebSocketMessage {

    private String from;

    private String type;

    private String data;

    private Object candidate;

    private Object sdp;
}
