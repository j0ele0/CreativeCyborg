package com.example.creativcyborg.entities;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class MessageVerlauf {

    private final List<JSONObject> messageVerlauf;
    private int maxAnz;

    public MessageVerlauf(int maxAnzahl){
        this.messageVerlauf = new LinkedList<>();
        this.setMaxAnz(maxAnzahl);
    }

    public void add(JSONObject message){
        this.messageVerlauf.add(message);
        if(this.messageVerlauf.size()>this.maxAnz){
            this.messageVerlauf.remove(0);
        }
    }

    public List<JSONObject> getMessageVerlauf() {
        return messageVerlauf;
    }

    public void setMaxAnz(int maxAnz) {
        this.maxAnz = maxAnz;
        if(this.maxAnz<1){
            this.maxAnz=1;
        }
    }
}
