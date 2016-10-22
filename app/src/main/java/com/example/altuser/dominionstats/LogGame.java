package com.example.altuser.dominionstats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.altuser.dominionstats.dbHandlers.MainDbHandler;
import com.example.altuser.dominionstats.models.Expansion;
import com.example.altuser.dominionstats.models.Game;
import com.example.altuser.dominionstats.models.Player;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogGame extends Activity {

    public static final String WINNER = "Winner";
    public static final String EXPANSIONS = "Expansions";
    public static final String PLAYERS = "Players";
    public static final String NONE_SELECTED = "None selected";
    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    private MainDbHandler dbHandler;

    private ArrayAdapter dialogAdapter;
    private ListView dialogListView;

    private List<Expansion> selectedExpansions = new ArrayList<>();
    private List<Player> selectedPlayers = new ArrayList<>();
    private Player winner;

    ListView gameOptionsListView;

    Map<String, Map<String, String>> titles = new HashMap<>();

//    private DatePickerDialog datePickerDialog;
//    private Date selectedDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_game);

        //setDateTimeField();
        TextView gameDate = (TextView) findViewById(R.id.gameDate);
        gameDate.setText(DateFormat.getDateInstance().format(new Date()));
        populateTitles();

        dbHandler = new MainDbHandler(this, null, null, 1);
        final List<Expansion> expansionList = dbHandler.getExpansions();
        final List<Player> playerList = dbHandler.getPlayers();

        gameOptionsListView = (ListView) findViewById(R.id.gameOptions);
        gameOptionsListView.setAdapter(createListAdapter(Arrays.asList(titles.get(EXPANSIONS), titles.get(PLAYERS), titles.get(WINNER))));
        gameOptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = ((TextView) (view.findViewById(android.R.id.text1))).getText().toString();
                int choiceModeSingle = title.equals(WINNER) ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_MULTIPLE;
                switch (title) {
                    case EXPANSIONS:
                        dialogAdapter = new ArrayAdapter<>(LogGame.this,
                                android.R.layout.simple_list_item_multiple_choice, expansionList);
                        break;
                    case PLAYERS:
                        dialogAdapter = new ArrayAdapter<>(LogGame.this,
                                android.R.layout.simple_list_item_multiple_choice, playerList);
                        break;
                    case WINNER:
                        dialogAdapter = new ArrayAdapter<>(LogGame.this,
                                android.R.layout.simple_list_item_single_choice, selectedPlayers);
                        break;
                    default :
                        break;
                }

                buildListDialog(view, title, choiceModeSingle);
            }
        });
    }

    private void populateTitles() {
        titles.put(EXPANSIONS, new HashMap<String, String>() {{
            put(TITLE, EXPANSIONS);
            put(SUBTITLE, NONE_SELECTED);
        }});
        titles.put(PLAYERS, new HashMap<String, String>() {{
            put(TITLE, PLAYERS);
            put(SUBTITLE, NONE_SELECTED);
        }});
        titles.put(WINNER, new HashMap<String, String>() {{
            put(TITLE, WINNER);
            put(SUBTITLE, NONE_SELECTED);
        }});
    }

    private SimpleAdapter createListAdapter(List<Map<String, String>> list) {
        final String[] fromMapKey = new String[] {TITLE, SUBTITLE};
        final int[] toLayoutId = new int[] {android.R.id.text1, android.R.id.text2};
        return new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_2,
                fromMapKey, toLayoutId);
    }

    public void showDatePicker(View view) {
//        datePickerDialog.show();
    }

//    private void setDateTimeField() {
//        Calendar newCalendar = Calendar.getInstance();
//        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Calendar newDate = Calendar.getInstance();
//                newDate.set(year, monthOfYear, dayOfMonth);
//                TextView gameDate = (TextView) findViewById(R.id.gameDate);
//                gameDate.setText(DateFormat.getDateInstance().format(newDate.getTime()));
//                gameDate.setTypeface(null, Typeface.NORMAL);
//                selectedDate = new Date(newDate.getTimeInMillis());
//            }
//
//        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//    }

    public void logGame(View view) {
        if (selectedExpansions.size() > 0 && selectedPlayers.size() > 0 && winner != null) {
            Game game = new Game(new Date(), winner, selectedPlayers, selectedExpansions);
            boolean gameAdded = dbHandler.addGame(game);
            if (gameAdded) {
                renderGameAddedDialog();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please fill in all fields");
            builder.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void renderGameAddedDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Game Logged");
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(LogGame.this, HomeScreen.class);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alert = builder1.create();
        alert.show();
    }

    public void recordListSelection(View v, String title) {
        switch (title) {
            case EXPANSIONS:
                selectedExpansions = getSelectedExpansions();
                titles.put(title, new HashMap<String, String>() {{
                    put(TITLE, EXPANSIONS);
                    put(SUBTITLE, selectedExpansions.toString());
                }});
                break;
            case PLAYERS:
                selectedPlayers = getSelectedPlayers();
                winner = null;
                titles.put(title, new HashMap<String, String>() {{
                    put(TITLE, PLAYERS);
                    put(SUBTITLE, selectedPlayers.toString());
                }});
                titles.put(WINNER, new HashMap<String, String>() {{
                    put(TITLE, WINNER);
                    put(SUBTITLE, NONE_SELECTED);
                }});
                break;
            case WINNER:
                winner = getSelectedPlayers().get(0);
                titles.put(title, new HashMap<String, String>() {{
                    put(TITLE, WINNER);
                    put(SUBTITLE, winner.getPlayerName());
                }});
            default :
                break;
        }
        gameOptionsListView.setAdapter(createListAdapter(Arrays.asList(titles.get(EXPANSIONS), titles.get(PLAYERS), titles.get(WINNER))));
    }

    private List<Expansion> getSelectedExpansions() {
        SparseBooleanArray checked = dialogListView.getCheckedItemPositions();
        List<Expansion> selectedItems = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                selectedItems.add((Expansion) dialogAdapter.getItem(position));
            }
        }
        return selectedItems;
    }

    private List<Player> getSelectedPlayers() {
        SparseBooleanArray checked = dialogListView.getCheckedItemPositions();
        List<Player> selectedItems = new ArrayList<>();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                selectedItems.add((Player) dialogAdapter.getItem(position));
            }
        }
        return selectedItems;
    }

    public void buildListDialog(final View view, final String title, int choiceMode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose " + title);

        dialogListView = new ListView(this);
        dialogListView.setChoiceMode(choiceMode);
        dialogListView.setAdapter(dialogAdapter);
        builder.setView(dialogListView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recordListSelection(view, title);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
