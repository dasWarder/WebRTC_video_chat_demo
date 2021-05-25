package com.example.webrtcvideochat.socket;

public enum Message {
    MSG_TYPE_TEXT("text"),
    MSG_TYPE_OFFER("offer"),
    MSG_TYPE_ANSWER("answer"),
    MSG_TYPE_ICE("ice"),
    MSG_TYPE_JOIN("join"),
    MSG_TYPE_LEAVE("leave");

    private final String messageName;

    Message(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageName() {
        return this.messageName;
    }
}
