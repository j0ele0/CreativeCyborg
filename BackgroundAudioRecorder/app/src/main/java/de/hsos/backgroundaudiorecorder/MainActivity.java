package de.hsos.backgroundaudiorecorder;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private TextView textView, textView2;
    //private OpenAI openAI;

    private Handler handler;
    private String chatResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.MODEL.contains("Glass")) {
            setContentView(R.layout.select_room_view);

        } else {
            setContentView(R.layout.activity_main);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        isBluetoothHeadsetConnected();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setBluetoothScoOn(true);
        audioManager.startBluetoothSco();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        System.out.println(pairedDevices.size());
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                System.out.println(deviceName);
                System.out.println(deviceHardwareAddress);

            }
        }

        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    Bundle b = msg.getData();
                    chatResponse = b.getString("key");

                    //System.out.println("Handler Hier: " + chatResponse);
                    textView2.setText("OpenAI: " + chatResponse);
                }
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
            }
        } else {
            startSpeechRecognizer();
        }
        //this.openAI = new OpenAI(this.handler, this.chatResponse);
    }

    private void startSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
                textView.setText("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

                speechRecognizer.startListening(speechRecognizerIntent);

            }

            @Override
            public void onError(int i) {
                speechRecognizer.startListening(speechRecognizerIntent);
            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

//                try {
//                    openAI.generateText(data.get(0));
//
//
////                    if(chatText != null){
////                        textView2.setText("OpenAI: " + chatText);
////                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                textView.setText(data.get(0));
            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        speechRecognizer.startListening(speechRecognizerIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startSpeechRecognizer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void isBluetoothHeadsetConnected() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }

        if( mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothAdapter.STATE_CONNECTED){
            System.out.println("BT connected");
        }
    }






}