package com.example.webrtcvideochat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class WebRtcVideoChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebRtcVideoChatApplication.class, args);
    }

}
