package com.example.creativcyborg.activities.glass;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.creativcyborg.R;
import com.example.creativcyborg.entities.ContentPart;
import com.example.creativcyborg.entities.Room;
import com.example.creativcyborg.gateway.ServerAPI;
import com.example.creativcyborg.tools.GlassGestureDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Interaktionslogik der acitivty_select_room_on_glass.xml
 * Ermöglicht die Auswahl eines Raumes, wenn die App auf einer Google Glass ausgeführt wird.
 */
public class SelectRoomOnGlassActivity extends AppCompatActivity implements GlassGestureDetector.OnGestureListener
{
    private GlassGestureDetector glassGestureDetector;

    TextView roomNameTextView;
    ProgressBar spinner;
    TextView infoTextView;
    List<Room> rooms;
    int currentRoomIndex = 0;


    Thread receiveRoomsThread;
    boolean shouldThreadRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room_on_glass);

        glassGestureDetector = new GlassGestureDetector(this, this);

        this.roomNameTextView = findViewById(R.id.room_name_on_glass_textview);
        this.spinner=findViewById(R.id.select_rooms_spinner);
        this.infoTextView=findViewById(R.id.info_textview);
        this.rooms = new ArrayList<>();

        // Verfügbare Räume laden
        startReceiveRoomsThread();
    }

    /**
     * Startet einen Thread, der regelmäßig den Text eines Raums abruft.
     */
    private void startReceiveRoomsThread()
    {
        this.shouldThreadRunning = true;
        this.receiveRoomsThread = new Thread(() ->
        {
            while (this.shouldThreadRunning)
            {
                try         //sleep
                {
                    try{    //getRooms
                        this.rooms = ServerAPI.getAllRooms();
                        String finalName;
                        if(this.rooms.size()>0){
                            if(this.currentRoomIndex>=this.rooms.size()){
                                this.currentRoomIndex=0;
                            }
                            finalName=this.rooms.get(this.currentRoomIndex).getName();
                            viewSetRoom(finalName,true);
                        }else{
                            finalName = "Warte auf Meeting";
                            viewSetRoom(finalName,false);
                        }
                    }catch (IOException e){
                        viewSetRoom("Keine Verbindung",false);
                        //e.printStackTrace();
                        System.out.println("Keine Verbindung");
                    }
                    Thread.sleep(800);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        this.receiveRoomsThread.start();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        startReceiveRoomsThread();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.shouldThreadRunning = false;
    }






    /*
    private void loadRooms()
    {
        new Thread(() ->
        {
            try
            {
                // Räume ermitteln
                this.rooms = ServerAPI.getAllRooms();

                // Räume der ListView hinzufügen
                runOnUiThread(() ->
                {
                    if (rooms.size() > 0)
                    {
                        this.roomNameTextView.setText(rooms.get(0).getName());
                    }
                    else
                    {
                        this.roomNameTextView.setText("Warte auf Meeting.");
                    }
                });
            }
            catch (Exception e)
            {
                this.roomNameTextView.setText("Keine Verbindung.");
                e.printStackTrace();
            }
        }).start();
    }
    */
    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture)
    {
        switch (gesture)
        {
            // Raum öffnen
            case TAP:
            {
                if(this.rooms.size()<=this.currentRoomIndex){
                    return true;
                }
                Room selectedRoom = this.rooms.get(this.currentRoomIndex);
                goToRoom(selectedRoom);
                break;
            }
            // Vorherigen Raum anzeigen
            case SWIPE_FORWARD:
            {
                if (this.currentRoomIndex == 0)
                {
                    break;
                }

                this.currentRoomIndex--;
                Room previousRoom = this.rooms.get(this.currentRoomIndex);
                changeRoomTextOnUI(previousRoom.getName());
                break;
            }
            // Nächsten Raum anzeigen
            case SWIPE_BACKWARD:
            {
                if (this.currentRoomIndex >= this.rooms.size() - 1)
                {
                    break;
                }

                this.currentRoomIndex++;
                Room nextRoom = this.rooms.get(this.currentRoomIndex);
                changeRoomTextOnUI(nextRoom.getName());
                break;
            }
            case SWIPE_UP:
            {
                finish();
                break;
            }

            default:
                break;
        }

        return true;
    }

    /**
     * Ändert auf der UI den aktuellen Raumnamen mit einer Animation.
     * @param newRoomName Der neue Name.
     */
    private void changeRoomTextOnUI(String newRoomName)
    {
        this.roomNameTextView.animate().alpha(0).setDuration(200).withEndAction(() ->
        {
            this.roomNameTextView.setText(newRoomName);
            this.roomNameTextView.animate().alpha(1).setDuration(200).start();
        }).start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (glassGestureDetector.onTouchEvent(ev))
        {
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void goToRoom(Room selectedRoom){
        Intent intent = new Intent(SelectRoomOnGlassActivity.this, RecieveOutputActivity.class);
        intent.putExtra("roomId", selectedRoom.getId());
        intent.putExtra("roomName", selectedRoom.getName());
        SelectRoomOnGlassActivity.this.startActivity(intent);
    }

    // Wenn sich der Inhalt verändert hat, Animation starten und Text auf UI ändern
    private void viewSetRoom(String roomName,boolean roomVorhanden){
        String info;
        int spinnerSichtbarbeit;
        if(roomVorhanden){
            info= "Tab um beizutreten";
            spinnerSichtbarbeit= View.INVISIBLE;
        }else{
            info="";
            spinnerSichtbarbeit=View.VISIBLE;
        }
        if (!this.roomNameTextView.getText().toString().contentEquals(roomName))
        {
            runOnUiThread(() ->
            {
                this.roomNameTextView.animate().alpha(0).setDuration(400).withEndAction(() ->
                {
                    this.roomNameTextView.setText(roomName);
                    this.infoTextView.setText(info);
                    this.spinner.setVisibility(spinnerSichtbarbeit);
                    this.roomNameTextView.animate().alpha(1).setDuration(400).start();
                }).start();
            });
        }
    }
}