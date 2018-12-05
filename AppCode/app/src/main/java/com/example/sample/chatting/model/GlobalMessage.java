package com.example.sample.chatting.model;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

/**
 * Created by Hassan Javaid on 11/30/2018.
 */

public class GlobalMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;
    private String imageUrl;
    private String fileUrl;

    public GlobalMessage(){
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}
