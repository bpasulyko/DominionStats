package com.example.altuser.dominionstats;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.altuser.dominionstats.dbHandlers.MainDbHandler;
import com.example.altuser.dominionstats.models.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class PlayerList extends Activity {

    public static String EXTRA_MESSAGE = "com.mycompany.dominionstats.MESSAGE";
    ListView playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        playerList = (ListView) findViewById(R.id.playerList);
        loadPlayerList();
        playerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Player player = (Player) parent.getItemAtPosition(position);
                Intent intent = new Intent(PlayerList.this, PlayerStats.class);
                intent.putExtra(EXTRA_MESSAGE, player);
                startActivity(intent);
                finish();
            }
        });
    }

    public void loadPlayerList() {
        MainDbHandler dbHandler = new MainDbHandler(this,null,null,1);
        List<Player> players = dbHandler.getPlayers();

        if (players.size() > 0) {
            ArrayAdapter adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1,
                    players);
            playerList.setAdapter(adapter);
        } else {
            TextView noPlayersFound = new TextView(this);
            noPlayersFound.setText("No Players Found");
            noPlayersFound.setPaddingRelative(50, 50, 50, 50);
            noPlayersFound.setTypeface(null, Typeface.ITALIC);

            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.playerListLayout);
            linearLayout.addView(noPlayersFound);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_list, menu);
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
