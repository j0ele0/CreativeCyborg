package com.example.googleglassesdummy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
        this.textView = findViewById(R.id.spokenText);

    }

    private static final int SPEECH_REQUEST = 109;

    private void displaySpeechRecognizer() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            // Do something with spokenText.
            this.textView.setText(spokenText);
            System.out.println(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}