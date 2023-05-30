package com.example.creativcyborg.tools;

import android.media.MediaRecorder;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class AudioRecorder {
    private MediaRecorder recorder;
    private String audioFilePath = null;
    private final static int audioEncodingBitRate = 128000;
    private final static int audioSamplingRate = 44100;

    public AudioRecorder(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public void prepareMediaRecorderAndRecord()
    {
        this.recorder = new MediaRecorder();
        this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        this.recorder.setOutputFile(audioFilePath);
        this.recorder.setAudioEncodingBitRate(audioEncodingBitRate);
        this.recorder.setAudioSamplingRate(audioSamplingRate);

        try
        {
            this.recorder.prepare();
            this.recorder.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void prepare() throws IOException {
        this.recorder.prepare();
    }

    public void start() throws IOException {
        this.recorder.start();
    }

    public void stop() {
        this.recorder.stop();
    }

    public void release(){
        this.recorder.release();
    }

    public MediaRecorder getRecorder() {
        return recorder;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }
}
