package com.example.creativcyborg.activities.phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.example.creativcyborg.R;
import com.example.creativcyborg.gateway.ServerAPI;
import com.example.creativcyborg.gateway.return_values.TopicResponse;
import com.example.creativcyborg.tools.AnimationHelper;
import com.example.creativcyborg.tools.AudioRecorder;
import com.example.creativcyborg.views.AudioVisualizer;
import com.google.android.material.elevation.SurfaceColors;

import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Interaktionslogik der activity_landing.xml
 */
public class LandingActivity extends AppCompatActivity
{
    private static final int RECORD_AUDIO_REQUEST_CODE = 1;
    private static final long BIG_BUTTON_IMPULSE_SPEED = 2000;

    /**
     * Definiert eine Zustandsmenge, in der die LandingActivity sein kann.
     */
    private enum ActivityState
    {
        Ready,
        Listening,
        Processing
    }

    private ActivityState activityState = ActivityState.Ready;
    private AudioRecorder recorder;
    private TopicResponse topicResponse;

    // Views
    private AudioVisualizer audioVisualizer;
    private ImageView topIcon;
    private TextView topTextView;
    private TextView detailTextView;
    private ViewFlipper helperTextsViewFlipper;
    private FrameLayout bigButton;
    private ViewSwitcher mainViewSwitcher;
    private ViewFlipper bigButtonContentViewSwitcher;
    private FrameLayout circle1;
    private FrameLayout circle2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        setBarColors();

        // Views abrufen
        this.audioVisualizer = findViewById(R.id.audio_visualizer);
        this.topIcon = findViewById(R.id.top_icon);
        this.topTextView = findViewById(R.id.top_textview);
        this.detailTextView = findViewById(R.id.detail_top_textview);
        this.helperTextsViewFlipper = findViewById(R.id.helper_texts_viewflipper);
        this.bigButton = findViewById(R.id.big_button);
        this.mainViewSwitcher = findViewById(R.id.main_view_switcher);
        this.bigButtonContentViewSwitcher = findViewById(R.id.circle_inner_content_viewswitcher);
        this.circle1 = findViewById(R.id.impulse_circle_1);
        this.circle2 = findViewById(R.id.impulse_circle_2);

        this.recorder = new AudioRecorder(getExternalCacheDir().getAbsolutePath() + "/audio.mp4");
        startBigButtonAnimation();

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
     * Steuert den Activity-Status, wenn auf den großen Button geklickt wird.
     */
    public void bigButton_onClick(View view)
    {
        switch (this.activityState)
        {
            case Ready:
            {
                startListeningState();
                break;
            }
            case Listening:
            {
                processAudioData();
                break;
            }
            case Processing:
            {
                break;
            }
            default:
                break;
        }
    }

    /**
     * Startet den Listening-Vorgang.
     */
    private void startListeningState()
    {
        this.activityState = ActivityState.Listening;
        this.recorder.prepareMediaRecorderAndRecord();

        this.bigButtonContentViewSwitcher.showNext();
        this.audioVisualizer.startAnimation();
        animateImpulseCircles();
        changeHelperTextsVisiblity(false);
        AnimationHelper.changeTextWithAnimation(this.topTextView, "Ich arbeite mich ins Thema ein...");
        AnimationHelper.changeTextWithAnimation(this.detailTextView, "Zum Beenden erneut antippen");
        AnimationHelper.changeImageSrcWithAnimation(this.topIcon, getDrawable(R.drawable.ic_outline_cloud));
    }

    /**
     * Verarbeitet die erfassten Audiodaten, um das Thema zu erfassen.
     */
    private void processAudioData()
    {
        this.activityState = ActivityState.Processing;
        this.recorder.stop();
        this.recorder.release();

        this.bigButtonContentViewSwitcher.showNext();
        AnimationHelper.changeTextWithAnimation(detailTextView, "");

        new Thread(() ->
        {
            try
            {
                byte[] audioData = Files.readAllBytes(Paths.get(recorder.getAudioFilePath()));
                topicResponse = ServerAPI.sendTopicAudioData(1, audioData);

                runOnUiThread(() ->
                {
                    AnimationHelper.changeTextWithAnimation(topTextView, "Ich habe folgendes Thema erfasst:");
                    AnimationHelper.changeTextWithAnimation(detailTextView, topicResponse.shortTopic);
                    AnimationHelper.changeImageSrcWithAnimation(this.topIcon, getDrawable(R.drawable.ic_outline_auto_awesome));
                    this.mainViewSwitcher.showNext();
                });

                activityState = ActivityState.Ready;
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

    public void startMeeting_onClick(View view)
    {
        Intent intent = new Intent(LandingActivity.this, ListeningActivity.class);
        intent.putExtra("topic", topicResponse.shortTopic);
        LandingActivity.this.startActivity(intent);
        resetUI();
    }

    /**
     * Setzt das erfasste Thema zurück.
     */
    public void reset_onClick(View view)
    {
        resetUI();
    }

    /**
     * Setzt die UI auf den Start-Zustand zurück.
     */
    private void resetUI()
    {
        this.activityState = ActivityState.Ready;
        this.mainViewSwitcher.showPrevious();
        this.bigButtonContentViewSwitcher.showNext();
        changeHelperTextsVisiblity(true);
        AnimationHelper.changeTextWithAnimation(this.topTextView, "Beschreibt das Thema, bei dem ihr kreativ inspiriert werden möchtet.");
        AnimationHelper.changeTextWithAnimation(this.detailTextView, "Zum Starten antippen");
        AnimationHelper.changeImageSrcWithAnimation(this.topIcon, getDrawable(R.drawable.ic_outline_lightbulb));
    }

    /**
     * Startet eine Animation, welche den großen Button pulsieren lässt,
     */
    private void startBigButtonAnimation()
    {
        new Thread(() ->
        {
            while (true)
            {
                runOnUiThread(() ->
                {
                    bigButton.animate().scaleX(1.2f).scaleY(1.2f).setDuration(BIG_BUTTON_IMPULSE_SPEED).withEndAction(() ->
                    {
                        bigButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(BIG_BUTTON_IMPULSE_SPEED).start();
                    }).start();
                });

                try
                {
                    Thread.sleep(BIG_BUTTON_IMPULSE_SPEED * 2);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Animiert die Kreise, die angezeigt werden, wenn dsa Thema erfasst wird.
     */
    private void animateImpulseCircles()
    {
        new Thread(() ->
        {
            while (activityState == ActivityState.Listening)
            {
                runOnUiThread(() ->
                {
                    circle1.setAlpha(1);
                    circle1.setScaleX(1);
                    circle1.setScaleY(1);

                    circle1.animate().alpha(0).scaleX(2).scaleY(2).setDuration(2000).withEndAction(() ->
                    {
                        circle1.setAlpha(1);
                        circle1.setScaleX(1);
                        circle1.setScaleY(1);

                        circle1.animate().alpha(0).scaleX(2).scaleY(2).setDuration(2000).start();
                    }).start();

                    circle2.setAlpha(1);
                    circle2.setScaleX(1);
                    circle2.setScaleY(1);

                    circle2.animate().setStartDelay(1000).alpha(0).scaleX(2).scaleY(2).setDuration(2000).withEndAction(() ->
                    {
                        circle2.setAlpha(1);
                        circle2.setScaleX(1);
                        circle2.setScaleY(1);

                        circle2.animate().alpha(0).scaleX(2).scaleY(2).setDuration(2000).start();
                    }).start();
                });

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Blendet die Hilfstexte unten auf der Activity ein oder aus.
     * @param show True, wenn die Texte angezeigt werden sollen, sonst false.
     */
    private void changeHelperTextsVisiblity(boolean show)
    {
        if (show)
        {
            this.helperTextsViewFlipper.animate().alpha(1).setDuration(300).start();
        }
        else
        {
            this.helperTextsViewFlipper.animate().alpha(0).setDuration(300).start();
        }
    }
}