package com.example.sample.chatting.model;



public class Group extends Room{
    public String id;
    public ListFriend listFriend;

    public Group(){
        listFriend = new ListFriend();
    }
}
