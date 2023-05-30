package com.example.creativcyborg.activities.glass;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.creativcyborg.R;
import com.example.creativcyborg.gateway.ServerAPI;
import com.example.creativcyborg.entities.ContentPart;
import com.example.creativcyborg.gateway.dto.ingoing.RoomContentDTO;
import com.example.creativcyborg.tools.GlassGestureDetector;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Interaktionslogik der activity_receive_output.xml
 * Empfängt die Inhalte eines Raumes. Wird aufgerufen, wenn die App auf einer Google Glass ausgeführt wird.
 */
public class RecieveOutputActivity extends AppCompatActivity implements GlassGestureDetector.OnGestureListener
{
    private GlassGestureDetector glassGestureDetector;
    TextView roomContentTextView;
    WebView webView;
    ProgressBar spinner;
    long roomId;
    Thread receiveOutputThread;
    boolean shouldReceiveOutputThreadRunning = false;
    String aktuellesBild ="";
    List<ContentPart> contentBuffer;
    private static int BUFFER_LENGHT= 4;
    private int inputInterval=6;
    private boolean showImages=false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.contentBuffer=new LinkedList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieve_output_on_glass);
        this.spinner = findViewById(R.id.recive_output_spinner);
        this.roomContentTextView = findViewById(R.id.roomContentTextView);
        this.webView = findViewById(R.id.webView);
        String URL ="https://media.istockphoto.com/id/952696392/de/vektor/tv-test-karte.jpg?s=612x612&w=0&k=20&c=2H27UPACVlrYcQ9jX1cSWUmk95thrEyUUrOR5DfiXcs=";
        setImageLink("");

        this.roomId = getIntent().getLongExtra("roomId", 1);
        this.glassGestureDetector = new GlassGestureDetector(this,this);
        ContentPart content=new ContentPart();
        content.setImageLink(URL);
        this.contentBuffer.add(content);
        this.showImages=true;
    }

    /**
     * Startet einen Thread, der regelmäßig die Ideen eines Raums abruft.
     */
    private void startReceiveOutputThread()
    {
        this.shouldReceiveOutputThreadRunning = true;
        this.receiveOutputThread = new Thread(() ->
        {
            while (this.shouldReceiveOutputThreadRunning)
            {
                try {       //sleep
                    try {   //getRoomContent
                        //Collection<ContentPart> content = ServerAPI.getRoomContent(this.roomId);
                        RoomContentDTO roomContent = ServerAPI.getRoomContentInterval(this.roomId);
                        if(roomContent!=null){
                            System.out.println(roomContent);
                            Collection<ContentPart> content = roomContent.content;
                            String roomText = "";
                            boolean imageExists= false;
                            for (ContentPart contentPart : content)
                            {
                                if(!contentPart.getImageLink().equals("")){
                                    this.contentBuffer.add(contentPart);
                                    imageExists =true;
                                }
                                roomText += contentPart.getIdea() + "\n";
                            }
                            if(imageExists){
                                this.inputInterval=roomContent.audioFrequenz;
                            }else{
                                this.inputInterval=1;
                            }
                            this.showImages=imageExists;
                            String finalRoomText = roomText;
                            // Wenn sich der Inhalt verändert hat, Animation starten und Text auf UI ändern

                            this.spinner.setVisibility(View.INVISIBLE);
                        }else{
                            this.spinner.setVisibility(View.VISIBLE);
                        }
                    }catch (IOException e) {
                        System.out.println("Keine Verbindung");
                        this.spinner.setVisibility(View.VISIBLE);
                        //e.printStackTrace();
                    }
                    Thread.sleep(this.inputInterval*1000/3);
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
        this.shouldReceiveOutputThreadRunning = false;
    }
    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture)
    {
        switch (gesture)
        {
            case TAP:
            case SWIPE_FORWARD:
            {
                //naechstes Bild
                break;
            }

            case SWIPE_BACKWARD:
            {
                //letztes Bild
                break;
            }
            case SWIPE_UP:
            case SWIPE_DOWN:
            {
                finish();
                break;
            }
            default:
                break;
        }
        return true;
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

    private void updateView(String ideen){
        if(this.showImages){
            while(this.contentBuffer.size()>BUFFER_LENGHT){
                this.contentBuffer.remove(0);
            }
            this.aktuellesBild=this.contentBuffer.remove(0).getImageLink();
            runOnUiThread(() ->
            {
                this.roomContentTextView.animate().alpha(0).setDuration(400).withEndAction(() ->
                {
                    this.webView.setVisibility(View.VISIBLE);
                    this.roomContentTextView.setVisibility(View.INVISIBLE);
                    this.roomContentTextView.setText(ideen);
                    this.roomContentTextView.animate().alpha(1).setDuration(400).start();
                }).start();
            });


        }else{
            if (!this.roomContentTextView.getText().toString().contentEquals(ideen)&&!ideen.equals(""))
            {
                runOnUiThread(() ->
                {
                    this.roomContentTextView.animate().alpha(0).setDuration(400).withEndAction(() ->
                    {
                        this.webView.setVisibility(View.INVISIBLE);
                        this.roomContentTextView.setVisibility(View.VISIBLE);
                        this.roomContentTextView.setText(ideen);
                        this.roomContentTextView.animate().alpha(1).setDuration(400).start();
                    }).start();
                });
            }
        }
    }

    private void setImageLink(String link){
        if(link.contentEquals("")) {
            this.webView.setVisibility(View.INVISIBLE);
        }
        if(link.equals(this.aktuellesBild)){
            return;
        }
        String html = "<html><body style=\"margin: 0%\"><img src='" + link + "' width=\"auto\" height=\"100%\"\"/></body></html>";
        runOnUiThread(() ->
        {
            this.webView.setVisibility(View.VISIBLE);
            this.webView.loadData(html, "text/html", null);

        });
        this.aktuellesBild=link;
    }
}