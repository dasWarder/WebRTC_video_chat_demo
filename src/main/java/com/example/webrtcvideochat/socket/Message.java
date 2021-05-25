package com.example.webrtcvideochat.socket;

/**
 * The class with different message types that WEbRTC could send to the server
 */
public enum Message {

    MSG_TYPE_TEXT("text"),

    MSG_TYPE_OFFER("offer"),

    MSG_TYPE_ANSWER("answer"),

    MSG_TYPE_ICE("ice"),

    MSG_TYPE_JOIN("join"),

    MSG_TYPE_LEAVE("leave");

    /**
     * The field with the text representation of the message type
     */
    private final String messageName;

    Message(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageName() {
        return this.messageName;
    }
}
