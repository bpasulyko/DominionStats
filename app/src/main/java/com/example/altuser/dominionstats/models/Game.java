package com.example.altuser.dominionstats.models;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
    private int id;
    private Date gameDate;
    private Player winner;
    private List<Player> players = new ArrayList<>();
    private List<Expansion> expansions = new ArrayList<>();

    public Game() {}

    public Game(Date gameDate, Player winner) {
        this.gameDate = gameDate;
        this.winner = winner;
    }

    public Game(int id, Date gameDate, Player winner) {
        this.id = id;
        this.gameDate = gameDate;
        this.winner = winner;
    }

    public Game(Date gameDate, Player winner, List<Player> players, List<Expansion> expansions) {
        this.gameDate = gameDate;
        this.winner = winner;
        this.players = players;
        this.expansions = expansions;
    }

    public Game(int id, Date gameDate, Player winner, List<Player> players, List<Expansion> expansions) {
        this.id = id;
        this.gameDate = gameDate;
        this.winner = winner;
        this.players = players;
        this.expansions = expansions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getGameDate() {
        return gameDate;
    }

    public void setGameDate(Date gameDate) {
        this.gameDate = gameDate;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Expansion> getExpansions() {
        return expansions;
    }

    public void setExpansions(List<Expansion> expansions) {
        this.expansions = expansions;
    }

//    @Override
//    public String toString() {
//        return gameDate + " -- " + playerName;
//    }

    @Override //FULL TO STRING METHOD
    public String toString() {
        return gameDate + "\n" + expansions.toString() + "\n" + players.toString() + "\nWinner:  " + winner;
    }
}
