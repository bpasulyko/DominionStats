package com.example.altuser.dominionstats.dbHandlers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.altuser.dominionstats.models.Expansion;
import com.example.altuser.dominionstats.models.Game;
import com.example.altuser.dominionstats.models.Player;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class GameDbHandler {

    private final PlayerDbHandler playerDbHandler;

    //    Player table (from PlayerDbHandler.java)
    private static final String TABLE_PLAYER = "player";
    public static final String COLUMN_PLAYER_ID = "playerId";

//    Expansion table
    private static final String TABLE_EXPANSION = "Expansion";
    public static final String COLUMN_EXPANSION_ID = "expansionId";
    public static final String COLUMN_EXPANSION_NAME = "expansionName";
    public static final String CREATE_EXPANSION_TABLE = "CREATE TABLE " +
            TABLE_EXPANSION + "("
            + COLUMN_EXPANSION_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_EXPANSION_NAME + " TEXT)";

//    Game table
    private static final String TABLE_GAME = "Game";
    public static final String COLUMN_GAME_ID = "gameId";
    public static final String COLUMN_GAME_DATE = "date";
    public static final String CREATE_GAME_TABLE = "CREATE TABLE " +
            TABLE_GAME + "("
            + COLUMN_GAME_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_GAME_DATE + " TEXT,"
            + COLUMN_PLAYER_ID + " INTEGER,"
            + " FOREIGN KEY (" + COLUMN_PLAYER_ID + ") REFERENCES " + TABLE_PLAYER + "(" + COLUMN_PLAYER_ID + "));";

//    GameExpansion table
    private static final String TABLE_GAME_EXPANSION = "GameExpansion";
    public static final String CREATE_GAME_EXPANSION_TABLE = "CREATE TABLE " +
            TABLE_GAME_EXPANSION + "("
            + COLUMN_EXPANSION_ID + " INTEGER,"
            + COLUMN_GAME_ID + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_EXPANSION_ID + ") REFERENCES " + TABLE_EXPANSION + "(" + COLUMN_EXPANSION_ID + "),"
            + "FOREIGN KEY (" + COLUMN_GAME_ID + ") REFERENCES " + TABLE_GAME + "(" + COLUMN_GAME_ID + "),"
            + "PRIMARY KEY (" + COLUMN_EXPANSION_ID + ", " + COLUMN_GAME_ID + "))";

//    GamePlayer table
    private static final String TABLE_GAME_PLAYER = "GamePlayer";
    public static final String CREATE_GAME_PLAYER_TABLE = "CREATE TABLE " +
            TABLE_GAME_PLAYER + "("
            + COLUMN_PLAYER_ID + " INTEGER,"
            + COLUMN_GAME_ID + " INTEGER, "
            + "FOREIGN KEY (" + COLUMN_PLAYER_ID + ") REFERENCES " + TABLE_PLAYER + "(" + COLUMN_PLAYER_ID + "),"
            + "FOREIGN KEY (" + COLUMN_GAME_ID + ") REFERENCES " + TABLE_GAME + "(" + COLUMN_GAME_ID + "),"
            + "PRIMARY KEY (" + COLUMN_PLAYER_ID + ", " + COLUMN_GAME_ID + "))";

    public GameDbHandler(PlayerDbHandler playerDbHandler) {
        this.playerDbHandler = playerDbHandler;
    }

    public void createGameTables(SQLiteDatabase db) {
        db.execSQL(CREATE_EXPANSION_TABLE);
        db.execSQL(CREATE_GAME_TABLE);
        db.execSQL(CREATE_GAME_EXPANSION_TABLE);
        db.execSQL(CREATE_GAME_PLAYER_TABLE);
    }

    public void dropGameTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPANSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_EXPANSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME_PLAYER);
    }

    public boolean addGame(SQLiteDatabase db, Game game) {
        ContentValues gameValues = new ContentValues();
        gameValues.put(COLUMN_GAME_DATE, game.getGameDate().toString());
        gameValues.put(COLUMN_PLAYER_ID, game.getWinner().getId());
        long gameId = db.insert(TABLE_GAME, null, gameValues);

        for (Expansion expansion : game.getExpansions()) {
            ContentValues gameExpansionValues = new ContentValues();
            gameExpansionValues.put(COLUMN_EXPANSION_ID, expansion.getId());
            gameExpansionValues.put(COLUMN_GAME_ID, gameId);
            db.insert(TABLE_GAME_EXPANSION, null, gameExpansionValues);
        }

        for (Player player : game.getPlayers()) {
            ContentValues gamePlayerValues = new ContentValues();
            gamePlayerValues.put(COLUMN_PLAYER_ID, player.getId());
            gamePlayerValues.put(COLUMN_GAME_ID, gameId);
            db.insert(TABLE_GAME_PLAYER, null, gamePlayerValues);
        }
        return true;
    }

    public boolean deleteSelectedGame(SQLiteDatabase db, int gameId) {
        boolean result = false;
        boolean gameExpansionsDeleted = deleteGameExpansion(db, gameId);
        boolean gamePlayersDeleted;
        if (gameExpansionsDeleted) {
            gamePlayersDeleted = deleteGamePlayers(db, gameId);
            if (gamePlayersDeleted) {
                result = deleteGame(db, gameId);
            }
        }
        return result;
    }

    private boolean deleteGame(SQLiteDatabase db, int gameId) {
        int rowsAffected = db.delete(TABLE_GAME, COLUMN_GAME_ID + " = ?",
                new String[]{String.valueOf(gameId)});
        return rowsAffected > 0;
    }

    private boolean deleteGamePlayers(SQLiteDatabase db, int gameId) {
        int rowsAffected = db.delete(TABLE_GAME_PLAYER, COLUMN_GAME_ID + " = ?",
                new String[]{String.valueOf(gameId)});
        return rowsAffected > 0;
    }

    private boolean deleteGameExpansion(SQLiteDatabase db, int gameId) {
        int rowsAffected = db.delete(TABLE_GAME_EXPANSION, COLUMN_GAME_ID + " = ?",
                new String[]{String.valueOf(gameId)});
        return rowsAffected > 0;
    }

    public List<Game> getGames(SQLiteDatabase db) {
        String gameQuery = "SELECT * FROM " + TABLE_GAME;
        Cursor cursor = db.rawQuery(gameQuery, null);
        List<Game> games = new ArrayList<>();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idColumn = cursor.getColumnIndex(COLUMN_GAME_ID);
            int dateColumn = cursor.getColumnIndex(COLUMN_GAME_DATE);
            int winnerColumn = cursor.getColumnIndex(COLUMN_PLAYER_ID);
            do {
                String string = cursor.getString(winnerColumn);
                Player winner = string != null ? playerDbHandler.getPlayerById(db, Integer.parseInt(string)) : new Player();
                Date gameDate = new Date(cursor.getString(dateColumn));
                games.add(new Game(
                        Integer.parseInt(cursor.getString(idColumn)),
                        gameDate,
                        winner));
            } while(cursor.moveToNext());
            cursor.close();
        }
        games = setExpansionsAndPlayers(db, games);
        return games;
    }

    private List<Game> setExpansionsAndPlayers(SQLiteDatabase db, List<Game> games) {
        for(Game game : games){
            game.setExpansions(getGameExpansions(db, game.getId()));
            game.setPlayers(playerDbHandler.getGamePlayers(db, game.getId(), COLUMN_GAME_ID));
        }
        return games;
    }

    private List<Expansion> getGameExpansions(SQLiteDatabase db, int gameId) {
        String query = "SELECT * FROM " + TABLE_EXPANSION + " e "
                + " JOIN  " + TABLE_GAME_EXPANSION + " ge ON e." + COLUMN_EXPANSION_ID + " = ge." + COLUMN_EXPANSION_ID
                + " JOIN  " + TABLE_GAME + " g ON ge." + COLUMN_GAME_ID + " = g." + COLUMN_GAME_ID
                + " WHERE g." + COLUMN_GAME_ID + " = " + gameId;
        Cursor cursor = db.rawQuery(query, null);
        List<Expansion> expansions = new ArrayList<>();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idColumn = cursor.getColumnIndex(COLUMN_EXPANSION_ID);
            int nameColumn = cursor.getColumnIndex(COLUMN_EXPANSION_NAME);
            do {
                expansions.add(new Expansion(Integer.parseInt(cursor.getString(idColumn)), cursor.getString(nameColumn)));
            } while(cursor.moveToNext());
            cursor.close();
        }
        return expansions;
    }

    public List<Expansion> getExpansions(SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE_EXPANSION;
        Cursor cursor = db.rawQuery(query, null);
        List<Expansion> expansions = new ArrayList<>();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idColumn = cursor.getColumnIndex(COLUMN_EXPANSION_ID);
            int nameColumn = cursor.getColumnIndex(COLUMN_EXPANSION_NAME);
            do {
                expansions.add(new Expansion(Integer.parseInt(cursor.getString(idColumn)), cursor.getString(nameColumn)));
            } while(cursor.moveToNext());
            cursor.close();
        }
        return expansions;
    }

    public void insertExpansions(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(1, 'Dominion')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(2, 'Intrigue')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(3, 'Seaside')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(4, 'Alchemy')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(5, 'Prosperity')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(6, 'Cornucopia')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(7, 'Hinterlands')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(8, 'Dark Ages')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(9, 'Guilds')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(10, 'Adventures')");
        db.execSQL("INSERT INTO " + TABLE_EXPANSION + " VALUES(11, 'Empires')");
    }

    public int getGamesWon(SQLiteDatabase db, int playerId) {
        String query = "SELECT * FROM " + TABLE_GAME + " WHERE " + COLUMN_PLAYER_ID + " = " + playerId;
        Cursor cursor = db.rawQuery(query, null);
        int gamesWon = cursor.getCount();
        cursor.close();
        return gamesWon;
    }

    public String getLastWin(SQLiteDatabase db, int playerId) {
        String gameDate = "";
        String query = "SELECT " + COLUMN_GAME_DATE
                + " FROM " + TABLE_GAME
                + " WHERE " + COLUMN_PLAYER_ID + " = " + playerId
                + " ORDER BY " + COLUMN_GAME_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int gameDateColumn = cursor.getColumnIndex(COLUMN_GAME_DATE);
            gameDate = cursor.getString(gameDateColumn);
            cursor.close();
        }
        return gameDate;
    }

    public String getBestExpansion(SQLiteDatabase db, int playerId) {
        String bestExp = "";
        List<Integer> gamesWon = getGamesWonByPlayer(db, playerId);
        String gamesWonString = gamesWon.toString();

        if (gamesWon.size() > 0) {
            int bestExpId = getBestExpansionId(db, gamesWonString);
            bestExp = getExpansionById(db, bestExp, bestExpId);
        }
        return bestExp;
    }

    private String getExpansionById(SQLiteDatabase db, String bestExp, int bestExpId) {
        String query = "SELECT " + COLUMN_EXPANSION_NAME
                + " FROM " + TABLE_EXPANSION
                + " WHERE " + COLUMN_EXPANSION_ID + " = " + bestExpId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idColumn = cursor.getColumnIndex(COLUMN_EXPANSION_NAME);
            bestExp = cursor.getString(idColumn);
            cursor.close();
        }
        return bestExp;
    }

    private int getBestExpansionId(SQLiteDatabase db, String gamesWonString) {
        int bestExp = 0;
        String query = "SELECT " + COLUMN_EXPANSION_ID + ", COUNT(*) as 'Num' "
                + " FROM " + TABLE_GAME_EXPANSION
                + " WHERE " + COLUMN_GAME_ID + " IN (" + gamesWonString.substring(1, gamesWonString.length() - 1)  + ")"
                + " ORDER BY 'Num' DESC";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idColumn = cursor.getColumnIndex(COLUMN_EXPANSION_ID);
            bestExp = cursor.getInt(idColumn);
            cursor.close();
        }
        return bestExp;
    }

    private List<Integer> getGamesWonByPlayer(SQLiteDatabase db, int playerId) {
        List<Integer> gamesWon = new ArrayList<>();
        String query = "SELECT " + COLUMN_GAME_ID
                + " FROM " + TABLE_GAME
                + " WHERE " + COLUMN_PLAYER_ID + " = " + playerId;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idColumn = cursor.getColumnIndex(COLUMN_GAME_ID);
            do {
                gamesWon.add(cursor.getInt(idColumn));
            } while(cursor.moveToNext());
            cursor.close();
        }
        return gamesWon;
    }
}
