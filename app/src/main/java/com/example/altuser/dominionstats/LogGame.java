package com.example.altuser.dominionstats;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.altuser.dominionstats.dbHandlers.MainDbHandler;
import com.example.altuser.dominionstats.models.Expansion;
import com.example.altuser.dominionstats.models.Game;
import com.example.altuser.dominionstats.models.Player;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LogGame extends Activity {

    private MainDbHandler dbHandler;

    private ArrayAdapter dialogAdapter;
    private ListView dialogListView;

    private ArrayAdapter<String> gameOptionsAdapter;
    private ListView gameOptionsListView;

    private List<Expansion> expansionList;
    private List<Player> playerList;

    private List<Expansion> selectedExpansions = new ArrayList<>();
    private List<Player> selectedPlayers = new ArrayList<>();
    private Player winner;

//    private DatePickerDialog datePickerDialog;
//    private Date selectedDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_game);

        //setDateTimeField();
        TextView gameDate = (TextView) findViewById(R.id.gameDate);
        gameDate.setText(DateFormat.getDateInstance().format(new Date()));

        final List<String> titles = new ArrayList<>();
        titles.add("Expansions");
        titles.add("Players");
        titles.add("Winner");

        dbHandler = new MainDbHandler(this, null, null, 1);
        expansionList = dbHandler.getExpansions();
        playerList = dbHandler.getPlayers();

        gameOptionsListView = (ListView) findViewById(R.id.gameOptions);
        gameOptionsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,  titles);
        gameOptionsListView.setAdapter(gameOptionsAdapter);
        gameOptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = titles.get(position);
                int choiceModeSingle = title.equals("Winner") ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_MULTIPLE;
                switch (title) {
                    case "Expansions" :
                        dialogAdapter = new ArrayAdapter<>(LogGame.this,
                                android.R.layout.simple_list_item_multiple_choice, expansionList);
                        break;
                    case "Players" :
                        dialogAdapter = new ArrayAdapter<>(LogGame.this,
                                android.R.layout.simple_list_item_multiple_choice, playerList);
                        break;
                    case "Winner" :
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
            case "Expansions" :
                selectedExpansions = getSelectedExpansions();
                TextView selectedExpensionsTextView = (TextView) findViewById(R.id.selectedExpansions);
                selectedExpensionsTextView.setText(selectedExpansions.toString());
                selectedExpensionsTextView.setTypeface(null, Typeface.NORMAL);
                break;
            case "Players" :
                selectedPlayers = getSelectedPlayers();
                TextView selectedPlayersTextView = (TextView) findViewById(R.id.selectedPlayers);
                selectedPlayersTextView.setText(selectedPlayers.toString());
                selectedPlayersTextView.setTypeface(null, Typeface.NORMAL);
                break;
            case "Winner" :
                winner = getSelectedPlayers().get(0);
                TextView winnerTextView = (TextView) findViewById(R.id.winner);
                winnerTextView.setText(winner.getPlayerName());
                winnerTextView.setTypeface(null, Typeface.NORMAL);
                break;
            default :
                break;
        }
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
