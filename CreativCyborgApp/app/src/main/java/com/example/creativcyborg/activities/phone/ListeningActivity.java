package com.example.creativcyborg.activities.phone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.creativcyborg.R;
import com.example.creativcyborg.entities.ContentPart;
import com.example.creativcyborg.gateway.ServerAPI;
import com.example.creativcyborg.gateway.dto.ingoing.RoomContentDTO;
import com.example.creativcyborg.tools.AnimationHelper;
import com.example.creativcyborg.tools.AudioRecorder;
import com.example.creativcyborg.views.AudioVisualizer;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.slider.Slider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Timer;

/**
 * Interaktionslogik der activity_listening.xml
 */
public class ListeningActivity extends AppCompatActivity
{
    Thread receiveOutputThread;
    boolean shouldThreadRunning = false;
    TextView roomContentTextView;

    private static final long SEND_AUDIO_PERIOD = 6000;

    private AudioRecorder recorder;
    private Timer timer;
    private String audioFilePath = null;
    private String roomText;
    private boolean shouldRecordingThreadRun = true;
    private boolean isRecording = true;

    // Views
    private Button startStopAudioRecordButton;
    private ImageView micImageView;
    private MaterialSwitch generateImagesSwitch;
    private Slider creativitySlider;
    private TextView topicTextview;
    private AlertDialog closingDialog;
    private AudioVisualizer audioVisualizer;

    // Extras
    private long roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);
        setBarColors();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // Views abrufen
        this.roomContentTextView = findViewById(R.id.term1);
        this.startStopAudioRecordButton = findViewById(R.id.startStopAudioRecord_Button);
        //this.micImageView = findViewById(R.id.mic_imageview);
        this.audioVisualizer = findViewById(R.id.audio_visualizer);
        this.generateImagesSwitch = findViewById(R.id.generate_images_switch);
        this.creativitySlider = findViewById(R.id.creativity_slider);
        this.recorder = new AudioRecorder( getExternalCacheDir().getAbsolutePath() + "/audio.mp4");


        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Extras abrufen
        this.roomId = getIntent().getLongExtra("roomId", 1);
        String roomName = getIntent().getStringExtra("roomName");
        String topic = getIntent().getStringExtra("topic");
        //this.topicTextview.setText();

        // ToolBar konfigurieren
        MaterialToolbar toolbar = findViewById(R.id.listening_toolbar);
        setSupportActionBar(toolbar);


        toolbar.setTitle(topic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopBackgroundTasks();
                finish();
            }
        });

        startRecordAudioProcess();
        this.audioVisualizer.startAnimation();

    }

    public void changeRecordState_onClick(View view)
    {
        if (this.isRecording)
        {
            stopBackgroundTasks();

            this.isRecording = false;
            this.startStopAudioRecordButton.setText("Aufnahme fortführen");
            this.audioVisualizer.stopAnimation();
        }
        else
        {
            startRecordAudioProcess();

            this.isRecording = true;
            this.startStopAudioRecordButton.setText("Aufnahme stoppen");
            this.audioVisualizer.startAnimation();
        }
    }

    public void changeGenerateImagesState_onClick(View view)
    {
        this.generateImagesSwitch.setChecked(!this.generateImagesSwitch.isChecked());
    }

    /**
     * Konfiguriert die Media-Recorder-Instanz und startet die Aufnahme.
     */


    /**
     * Startet den Prozess, der koninuierlich Audio aufnimmt und Intervallweise die Audiodaten an den Server sendet.
     */
    private void startRecordAudioProcess()
    {
        new Thread(() ->
        {
            // MediaRecorder das erste Mal starten
            recorder.prepareMediaRecorderAndRecord();

            try
            {
                Thread.sleep(SEND_AUDIO_PERIOD);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            while (shouldRecordingThreadRun)
            {
                // MediaRecorder stoppen
                recorder.stop();
                recorder.release();

                try
                {
                    byte[] audioData = Files.readAllBytes(Paths.get(recorder.getAudioFilePath()));
                    boolean generateImages = generateImagesSwitch.isChecked();
                    float temperature = creativitySlider.getValue();

                    // MediaRecorder erneut starten
                    recorder.prepareMediaRecorderAndRecord();

                    // Wärenddessen AudioDaten senden
                    long startTime = System.currentTimeMillis();
                    boolean sended = ServerAPI.sendAudioData(roomId, audioData, generateImages, temperature);
                    long sendDuration = System.currentTimeMillis() - startTime;
                    System.out.println("AudioData sended: " + sended);

                    System.out.println("Duration: " + sendDuration);

                    // Wenn die Verarbeitung zu schnell ging, etwas warten
                    if (sendDuration < SEND_AUDIO_PERIOD)
                    {
                        Thread.sleep(SEND_AUDIO_PERIOD - sendDuration);
                        System.out.println("Waiting: " + (SEND_AUDIO_PERIOD - sendDuration));
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }


        }).start();
    }

    /**
     * Stoppt alle laufenden Hintergrundprozesse.
     */
    private void stopBackgroundTasks()
    {
        /*
        // Ladedialog anzeigen
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(40, 40, 40, 40);
        CircularProgressIndicator indicator = new CircularProgressIndicator(this);
        TextView text = new TextView(this);
        text.setText("Verbindungen werden getrennt...");
        text.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(indicator);
        linearLayout.addView(text);

        runOnUiThread(() ->
        {
            this.closingDialog = new MaterialAlertDialogBuilder(this)
                    .setView(linearLayout)
                    .setCancelable(false)
                    .show();
        });
        */

        // Prozesse beenden
        try
        {
            this.shouldRecordingThreadRun = false;
            this.recorder.stop();
            this.recorder.release();
        }
        catch (Exception ex)
        {
        }
    }

    /**
     * Setzt die NavigationBar- und StatusBar-Farbe der App passend zur Activity.
     */
    private void setBarColors()
    {
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_5.getColor(this));
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));

        // Text- und Icon-Farbe der StatusBar im hellen Design auf dunkel setzen
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }



    @Override
    public void onBackPressed()
    {
        stopBackgroundTasks();
        finish();
    }

    /**
     * Startet einen Thread, der regelmäßig den Text eines Raums abruft.
     */
    private void startReceiveOutputThread()
    {
        this.shouldThreadRunning = true;
        this.receiveOutputThread = new Thread(() ->
        {
            while (this.shouldThreadRunning)
            {
                try
                {
                    try {   //getRoomContent
                        //Collection<ContentPart> content = ServerAPI.getRoomContent(this.roomId);
                        RoomContentDTO roomContent = ServerAPI.getRoomContentInterval(this.roomId);
                        if(roomContent!=null){
                            //System.out.println(roomContent);
                            Collection<ContentPart> content = roomContent.content;
                             roomText = "";
                            boolean imageExists= false;
                            for (ContentPart contentPart : content)
                            {
                                roomText += contentPart.getIdea() + "\n";
                                //System.out.println("Recieved Text: " + roomText);
                            }

                            // Wenn sich der Inhalt verändert hat, Animation starten und Text auf UI ändern

                        }
                    }catch (IOException e) {
                        System.out.println("Keine Verbindung");
                        //e.printStackTrace();
                    }


                    String finalRoomText = "";
                    if(roomText != null){
                        finalRoomText = roomText;
                    }

                    // Wenn sich der Inhalt verändert hat, Animation starten und Text auf UI ändern
                    if (this.roomContentTextView.getText().toString().contentEquals(finalRoomText) == false)
                    {
                        String finalRoomText1 = finalRoomText;
                        runOnUiThread(() ->
                        {
                            this.roomContentTextView.animate().alpha(0).setDuration(400).withEndAction(() ->
                            {
                                this.roomContentTextView.setText(finalRoomText1);
                                this.roomContentTextView.animate().alpha(1).setDuration(400).start();
                            }).start();
                        });

                        Thread.sleep(800);
                    }
                }

                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        this.receiveOutputThread.start();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        startReceiveOutputThread();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.shouldThreadRunning = false;
    }




}