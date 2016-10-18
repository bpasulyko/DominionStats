package com.example.altuser.dominionstats.dbHandlers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.altuser.dominionstats.models.Expansion;
import com.example.altuser.dominionstats.models.Game;
import com.example.altuser.dominionstats.models.Player;

import java.util.Date;
import java.util.List;

public class MainDbHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dominionStatsDB.db";
    private PlayerDbHandler playerDbHandler = new PlayerDbHandler();
    private GameDbHandler gameDbHandler = new GameDbHandler(playerDbHandler);

    public MainDbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(playerDbHandler.getCreateTableString());
        gameDbHandler.createGameTables(db);
        gameDbHandler.insertExpansions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(playerDbHandler.getDropTableString());
        gameDbHandler.dropGameTables(db);
        onCreate(db);
    }

    public boolean addGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean gameAdded = gameDbHandler.addGame(db, game);
        db.close();
        return gameAdded;
    }

    public boolean deleteSelectedGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean gameDeleted = gameDbHandler.deleteSelectedGame(db, game.getId());
        db.close();
        return gameDeleted;
    }

    public boolean addPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean playerAdded = playerDbHandler.addPlayer(db, player);
        db.close();
        return playerAdded;
    }

    public boolean deletePlayer(int playerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean playerDeleted = playerDbHandler.deletePlayer(db, playerId);
        db.close();
        return playerDeleted;
    }

    public List<Player> getPlayers() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Player> players = playerDbHandler.getPlayers(db);
        db.close();
        return players;
    }

    public List<Expansion> getExpansions() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Expansion> expansions = gameDbHandler.getExpansions(db);
        db.close();
        return expansions;
    }

    public List<Game> getGames() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<Game> games = gameDbHandler.getGames(db);
        db.close();
        return games;
    }

    public int getGamesPlayed(int playerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int gamesPlayed = playerDbHandler.getGamesPlayed(db, playerId);
        db.close();
        return gamesPlayed;
    }

    public int getGamesWon(int playerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int gamesWon = gameDbHandler.getGamesWon(db, playerId);
        db.close();
        return gamesWon;
    }

    public String getLastWin(int playerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String lastWin = gameDbHandler.getLastWin(db, playerId);
        db.close();
        return lastWin;
    }

    public String getBestExpansion(int playerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String bestExpansion = gameDbHandler.getBestExpansion(db, playerId);
        db.close();
        return bestExpansion;
    }
}
