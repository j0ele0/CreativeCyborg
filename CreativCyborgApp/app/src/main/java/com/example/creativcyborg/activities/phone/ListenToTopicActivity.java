package com.example.creativcyborg.activities.phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.creativcyborg.R;
import com.example.creativcyborg.gateway.ServerAPI;
import com.example.creativcyborg.gateway.return_values.TopicResponse;
import com.google.android.material.elevation.SurfaceColors;
import com.example.creativcyborg.tools.AudioRecorder;

import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ListenToTopicActivity extends AppCompatActivity {
    private Button micButton;
    private TextView topicText;

    private AudioRecorder recorder;
    private  long roomId;
    private String roomName;
    private String topic;
    private String currentTopic;

    private static final int RECORD_AUDIO_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_to_topic);
        setBarColors();

        this.micButton = findViewById(R.id.startListeningToTopic);
        this.topicText = findViewById(R.id.hearedText);
        this.recorder = new AudioRecorder( getExternalCacheDir().getAbsolutePath() + "/audio.mp4");

        this.roomId = getIntent().getLongExtra("roomId", 1);
        this.roomName = getIntent().getStringExtra("roomName");
        this.currentTopic = getIntent().getStringExtra("currentTopic");
        if(currentTopic != null){
            this.topicText.setText(currentTopic);
            Button button = findViewById(R.id.startListeningActivity);
            button.setText("ändern");
        }

        micButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("Button Pressed");
                    recorder.prepareMediaRecorderAndRecord();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    System.out.println("Button Released");
                    recorder.stop();
                    recorder.release();

                    new Thread(() ->
                    {
                        try
                        {
                            byte[] audioData = Files.readAllBytes(Paths.get(recorder.getAudioFilePath()));
                            TopicResponse topicResponse = ServerAPI.sendTopicAudioData(roomId, audioData);
                            currentTopic = topicResponse.recognizedText;
                            topic = topicResponse.shortTopic;

                            runOnUiThread(() ->
                            {
                                topicText.setText(topic);
                            });
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }).start();
                }
                return false;
            }
        });



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

    public void openListeningActivity_onClick(View view)
    {
        Intent intent = new Intent(ListenToTopicActivity.this, ListeningActivity.class);
        intent.putExtra("roomId", this.roomId);
        intent.putExtra("roomName", this.roomName);
        intent.putExtra("topic", this.topic);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ListenToTopicActivity.this.startActivity(intent);
        finish();
    }


    public void getTopicFromWhisper(){
        this.topic = "Placeholder: Topic Transcripted by Whisper ";
    }


}