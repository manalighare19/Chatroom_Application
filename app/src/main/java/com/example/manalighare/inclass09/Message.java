package com.example.manalighare.inclass09;

public class Message {
    String MessageKey,UserID,MessageText,imageUrl,Time,Name;

    public Message(String MessageKey,String UserID,String messageText, String imageUrl, String time, String name) {
        this.MessageKey=MessageKey;
        this.UserID=UserID;
        this.MessageText = messageText;
        this.imageUrl = imageUrl;
        Time = time;
        Name = name;
    }

    public Message() {
    }


}
