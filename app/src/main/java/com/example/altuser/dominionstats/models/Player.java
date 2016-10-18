package com.example.altuser.dominionstats.models;

import java.io.Serializable;

public class Player implements Serializable {

    private int id;
    private String playerName;

    public Player() {}

    public Player(int id, String playerName) {
        this.id = id;
        this.playerName = playerName;
    }

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public String toString() {
        return !(playerName == null) ? playerName : "-- Player deleted --";
    }

//    @Override //FULL TO STRING METHOD
//    public String toString() {
//        return this.equals(null) ? "Player deleted" : id + ":" + playerName;
//    }
}
