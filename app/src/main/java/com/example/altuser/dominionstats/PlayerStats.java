package com.example.altuser.dominionstats;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.altuser.dominionstats.dbHandlers.MainDbHandler;
import com.example.altuser.dominionstats.models.Player;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;


public class PlayerStats extends Activity {

    Player player;
    MainDbHandler dbHandler = new MainDbHandler(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stats);

        Intent intent = getIntent();
        player = (Player) intent.getSerializableExtra(PlayerList.EXTRA_MESSAGE);
        ActionBar ab = getActionBar();
        assert ab != null;
        ab.setTitle(player.getPlayerName() + "'s Stats");
        loadPlayerStats();
    }

    private void loadPlayerStats() {
        TextView gamesPlayedField = (TextView) findViewById(R.id.gamesPlayedValue);
        TextView gamesWonField = (TextView) findViewById(R.id.gamesWonValue);
        TextView winRatioField = (TextView) findViewById(R.id.winRatioValue);
        TextView lastWinField = (TextView) findViewById(R.id.lastWinValue);
        TextView bestExpansionField = (TextView) findViewById(R.id.bestExpValue);

        BigDecimal gamesPlayed = new BigDecimal(dbHandler.getGamesPlayed(player.getId()));
        BigDecimal gamesWon = new BigDecimal(dbHandler.getGamesWon(player.getId()));
        BigDecimal winRatio;
        if (gamesPlayed.intValue() > 0) {
            winRatio = gamesWon.divide(gamesPlayed, MathContext.DECIMAL32).multiply(new BigDecimal(100));
        } else {
            winRatio = new BigDecimal(0);
        }

        gamesPlayedField.setText("" + gamesPlayed.intValue());
        gamesWonField.setText("" + gamesWon.intValue());
        winRatioField.setText(winRatio.setScale(1, BigDecimal.ROUND_HALF_UP) + "%");

        String lastWinString = dbHandler.getLastWin(player.getId());
        if (!lastWinString.equals("")) {
            Date lastWinDate = new Date(lastWinString);
            lastWinField.setText(DateFormat.getDateInstance().format(lastWinDate));
        }
        String bestExpansion = dbHandler.getBestExpansion(player.getId());
        if (!bestExpansion.equals("")) {
            bestExpansionField.setText(bestExpansion);
        }
    }

    public void deletePlayer(View view) {
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
        confirmationDialog.setMessage("Delete '" + player.getPlayerName() + "'?");
        confirmationDialog.setCancelable(true);

        confirmationDialog.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        MainDbHandler dbHandler = new MainDbHandler(PlayerStats.this,null,null,1);
                        boolean result = dbHandler.deletePlayer(player.getId());
                        if (result) {
                            renderPlayerDeletedDialog();
                        }
                    }
                });
        confirmationDialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog confirmationAlert = confirmationDialog.create();
        confirmationAlert.show();
    }

    private void renderPlayerDeletedDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Player deleted.");
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(PlayerStats.this, PlayerList.class);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alert = builder1.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_stats, menu);
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
