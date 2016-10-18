package com.example.altuser.dominionstats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.altuser.dominionstats.dbHandlers.MainDbHandler;
import com.example.altuser.dominionstats.models.Player;


public class HomeScreen extends Activity {

    private String ADD_PLAYER_INPUT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
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
            Intent intent = new Intent(this, GameList.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadLogGameActivity(View view) {
        Intent intent = new Intent(this, LogGame.class);
        startActivity(intent);
    }

    public void loadAddPlayerActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Player");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ADD_PLAYER_INPUT = input.getText().toString();
                MainDbHandler dbHandler = new MainDbHandler(HomeScreen.this,null,null,1);
                if (ADD_PLAYER_INPUT.isEmpty()) {
                    renderInvalidNameDialog();
                } else {
                    Player player = new Player(ADD_PLAYER_INPUT);
                    boolean playerAdded = dbHandler.addPlayer(player);
                    if (playerAdded) {
                        input.setText("");
                    } else {
                        renderDuplicatePlayerDialog();
                    }
                }
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

    private void renderDuplicatePlayerDialog() {
        AlertDialog.Builder duplicateNameDialog = new AlertDialog.Builder(this);
        duplicateNameDialog.setMessage("Player already exists");
        duplicateNameDialog.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = duplicateNameDialog.create();
        alert.show();
    }

    private void renderInvalidNameDialog() {
        AlertDialog.Builder duplicateNameDialog = new AlertDialog.Builder(this);
        duplicateNameDialog.setMessage("Enter valid name");
        duplicateNameDialog.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = duplicateNameDialog.create();
        alert.show();
    }

    public void loadPlayerListActivity(View view) {
        Intent intent = new Intent(this, PlayerList.class);
        startActivity(intent);
    }
}
