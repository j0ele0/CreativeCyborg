package com.example.creativcyborg.activities.phone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.creativcyborg.R;
import com.example.creativcyborg.adapters.RoomListAdapter;
import com.example.creativcyborg.entities.Room;
import com.example.creativcyborg.gateway.ServerAPI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.elevation.SurfaceColors;

import java.util.List;

/**
 * Interaktionslogik der activity_select_room.xml
 * Ermöglicht die Auswahl eines Raums, wenn die App auf einem Smartphone ausgeführt wird.
 */
public class SelectRoomActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
        setBarColors();

        // ToolBar konfigurieren
        MaterialToolbar toolbar = findViewById(R.id.select_room_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        // ListView konfigurieren
        ListView roomsListView = findViewById(R.id.room_ListView);
        roomsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long l)
            {
                Room room = (Room) adapter.getItemAtPosition(position);

                Intent intent = new Intent(SelectRoomActivity.this, ListeningActivity.class);
                intent.putExtra("roomId", room.getId());
                intent.putExtra("roomName", room.getName());
                intent.putExtra("topic", room.getTopic());
                SelectRoomActivity.this.startActivity(intent);
            }
        });

        // Verfügbare Räume laden
        loadRooms();
    }

    /**
     * Setzt die NavigationBar- und StatusBar-Farbe der App passend zur Activity.
     */
    private void setBarColors()
    {
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_0.getColor(this));
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // Text- und Icon-Farbe der StatusBar im hellen Design auf dunkel setzen
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * Lädt alle verfügbaren Räume und setzt diese auf der UI.
     */
    private void loadRooms()
    {
        new Thread(() ->
        {
            try
            {
                // Räume ermitteln
                List<Room> rooms = ServerAPI.getAllRooms();

                // Räume der ListView hinzufügen
                runOnUiThread(() ->
                {
                    RoomListAdapter adapter = new RoomListAdapter(this, R.layout.room_listview_row, rooms);
                    ListView roomsListView = findViewById(R.id.room_ListView);
                    roomsListView.setAdapter(adapter);
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();
    }
}