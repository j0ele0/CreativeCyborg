package com.example.creativcyborg.activities.phone;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.creativcyborg.R;
import com.example.creativcyborg.entities.Room;
import com.example.creativcyborg.gateway.ServerAPI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Interaktionslogik der activity_welcome.xml
 * Stellt einen Startbildschirm dar, wenn die App auf einem Smartphone ausgeführt wird.
 */
public class WelcomeActivity extends AppCompatActivity
{
    private static final int RECORD_AUDIO_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setBarColors();

        // Berechtigung anfordern, um auf das Mikrofon zuzugreifen
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
        }
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
     * Zeigt dem Benutzer einen Dialog an, um einen neuen Raum anzulegen
     * und öffnet bei Erfolg die ListeningActivity.
     */
    public void addRoom_OnClick(View view)
    {
        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setHint("Name");
        TextInputEditText editText = new TextInputEditText(textInputLayout.getContext());
        editText.requestFocus();
        textInputLayout.addView(editText);
        textInputLayout.setPadding(60, 40, 60, 0);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Raum erstellen")
                .setView(textInputLayout)
                .setPositiveButton("Erstellen", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (editText.getText().toString().isEmpty())
                        {
                            return;
                        }

                        new Thread(() ->
                        {
                            try
                            {
                                Room createdRoom = ServerAPI.addRoom(editText.getText().toString());

                                if (createdRoom != null)
                                {
                                    //Intent intent = new Intent(WelcomeActivity.this, ListeningActivity.class);
                                    Intent intent = new Intent(WelcomeActivity.this, ListenToTopicActivity.class);

                                    intent.putExtra("roomId", createdRoom.getId());
                                    intent.putExtra("roomName", createdRoom.getName());
                                    WelcomeActivity.this.startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(WelcomeActivity.this, "Fehler beim Hinzufügen des Raums.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    /**
     * Öffnet die Raumübersicht.<
     */
    public void joinRoom_OnClick(View view)
    {
        //startActivity(new Intent(this, SelectRoomActivity.class));
        startActivity(new Intent(this, SelectRoomActivity.class));
    }
}