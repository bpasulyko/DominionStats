package com.example.altuser.dominionstats.dbHandlers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.altuser.dominionstats.models.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerDbHandler {

    private static final String TABLE_PLAYER = "Player";

    public static final String COLUMN_PLAYER_ID = "playerId";
    public static final String COLUMN_PLAYER_NAME = "playerName";

    private static final String TABLE_GAME_PLAYER = "GamePlayer";
    private static final String TABLE_GAME = "Game";

    public PlayerDbHandler() {
    }

    public String getCreateTableString() {
        return "CREATE TABLE " +
                TABLE_PLAYER + "("
                + COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_PLAYER_NAME + " TEXT)";
    }

    public String getDropTableString() {
        return "DROP TABLE IF EXISTS " + TABLE_PLAYER;
    }

    public boolean addPlayer(SQLiteDatabase db, Player player) {
        ContentValues values = new ContentValues();
        String query = "SELECT * FROM " + TABLE_PLAYER + " WHERE " + COLUMN_PLAYER_NAME + " =  \"" + player.getPlayerName() + "\"";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return false;
        } else {
            values.put(COLUMN_PLAYER_NAME, player.getPlayerName());
            db.insert(TABLE_PLAYER, null, values);
            return true;
        }
    }

    public List<Player> getPlayers(SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE_PLAYER;
        Cursor cursor = db.rawQuery(query, null);
        List<Player> players = new ArrayList<>();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(COLUMN_PLAYER_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_PLAYER_NAME);
            do {
                players.add(new Player(Integer.parseInt(cursor.getString(idIndex)), cursor.getString(nameIndex)));
            } while(cursor.moveToNext());
            cursor.close();
        }
        return players;
    }

    public Player getPlayerById(SQLiteDatabase db, int id) {
        String query = "SELECT * FROM " + TABLE_PLAYER + " WHERE " + COLUMN_PLAYER_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(COLUMN_PLAYER_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_PLAYER_NAME);
            Player player = new Player(Integer.parseInt(cursor.getString(idIndex)), cursor.getString(nameIndex));
            cursor.close();
            return player;
        }
        return null;
    }

    public List<Player> getGamePlayers(SQLiteDatabase db, int gameId, String gameIdColumn) {
        String query = "SELECT p." + COLUMN_PLAYER_ID + ", p." + COLUMN_PLAYER_NAME + " FROM " + TABLE_PLAYER + " p "
                + " JOIN  " + TABLE_GAME_PLAYER + " gp ON p." + COLUMN_PLAYER_ID + " = gp." + COLUMN_PLAYER_ID
                + " JOIN  " + TABLE_GAME + " g ON gp." + gameIdColumn + " = g." + gameIdColumn
                + " WHERE g." + gameIdColumn + " = " + gameId;
        Cursor cursor = db.rawQuery(query, null);
        List<Player> players = new ArrayList<>();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(COLUMN_PLAYER_ID);
            int nameIndex = cursor.getColumnIndex(COLUMN_PLAYER_NAME);
            do {
                players.add(new Player(Integer.parseInt(cursor.getString(idIndex)), cursor.getString(nameIndex)));
            } while(cursor.moveToNext());
            cursor.close();
        }
        return players;
    }

    public boolean deletePlayer(SQLiteDatabase db, int playerId) {
        boolean result = false;
        db.beginTransaction();
        try {
            deleteFromGamePlayer(db, playerId);
            setWonGamesToNull(db, playerId);
            deleteFromPlayer(db, playerId);
            db.setTransactionSuccessful();
            result = true;
        } catch(Exception ignored) {
            //ERROR
        }
        db.endTransaction();
        return result;
    }

    private void setWonGamesToNull(SQLiteDatabase db, int playerId) {
        db.execSQL("UPDATE " + TABLE_GAME + " SET " + COLUMN_PLAYER_ID + " = NULL WHERE " + COLUMN_PLAYER_ID + " = " + playerId);
    }

    private void deleteFromGamePlayer(SQLiteDatabase db, int playerId) {
        String query = "Select * FROM " + TABLE_GAME_PLAYER + " WHERE " + COLUMN_PLAYER_ID + " = " + playerId;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            db.delete(TABLE_PLAYER, COLUMN_PLAYER_ID + " = ?",
                    new String[] { String.valueOf(playerId) });
            cursor.close();
        }
    }

    private void deleteFromPlayer(SQLiteDatabase db, int playerId) {
        db.delete(TABLE_PLAYER, COLUMN_PLAYER_ID + " = ?",
                new String[]{String.valueOf(playerId)});
    }

    public int getGamesPlayed(SQLiteDatabase db, int playerId) {
        String query = "SELECT * FROM " + TABLE_GAME_PLAYER + " WHERE " + COLUMN_PLAYER_ID + " = " + playerId;
        Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount();
    }
}
