package de.hsos.backgroundaudiorecorder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


//Open-Ai Key : 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
public class OpenAI {
    TextView textView2;
    String chatResponse;

    private static final String API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

    private Handler handler;

    public OpenAI(android.os.Handler handler, String chatResponse) {
         this.handler = handler;
         this.chatResponse = chatResponse;

    }

    public OpenAI() {
    }

    public String generateText(String prompt) throws IOException, JSONException {

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        String model="gpt-3.5-turbo";
        String system= prompt;

        JSONArray jsonArray= new JSONArray();
        try {
            jsonArray.put(new JSONObject().put("role","system").put("content",system));
            jsonArray.put(new JSONObject().put("role","user").put("content",prompt));
        } catch (JSONException e) {
            //throw new RuntimeException(e);
        }

        RequestBody bodyJ = RequestBody.create(mediaType, "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": "+jsonArray.toString()+",\n" +
                "  \"temperature\": "+0.7 +",\n"+
                "  \"presence_penalty\": "+0 +",\n"+
                "  \"frequency_penalty\": "+0 +
                "}");


        Request request = new Request.Builder()
                .url(API_ENDPOINT)
                .post(bodyJ)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Fehlerbehandlung
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Antwortbehandlung
                String responseBody = response.body().string();

                // Weitere Verarbeitung der Antwort
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray responseArray = jsonResponse.getJSONArray("choices");
                    for (int i = 0; i < responseArray.length(); i++) {

                        chatResponse = responseArray.getJSONObject(i).getJSONObject("message").get("content").toString();
                        System.out.println("ChatResponse "+chatResponse);
                        Bundle b = new Bundle();
                        b.putString("key", chatResponse);

                        Message msg = new Message();
                        msg.setData(b);
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    //throw new RuntimeException(e);
                }
            }
        });
        return chatResponse;
    }

    public void sendMessage(String message){

    }
}
