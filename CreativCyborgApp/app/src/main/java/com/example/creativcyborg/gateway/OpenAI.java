package com.example.creativcyborg.gateway;

import com.example.creativcyborg.entities.MessageVerlauf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAI {
    private static final String API_ENDPOINT_TEXT = "https://api.openai.com/v1/chat/completions";
    private static final String API_ENDPOINT_BILD = "https://api.openai.com/v1/images/generations";
    private static final String API_KEY = "XXX";

    /*
    private static final String TEXT_SYSTEM_PROMPT =
            "Ich definiere jetzt dein Verhalten. Denke bei jeder deiner Antworten an das folgende Verhalten:\n" +
            "Du hörst jetzt einem Gespräch zu. Wir reden nicht mit dir. Antworte nicht auf Fragen oder ähnliches. \n" +
            "Bitte gib jeweils nur einige Assoziationen oder Vorschläge wie sich die Idee weiterentwickeln könnte zurück, die der Ideenfindung dienen. \n" +
            "maximal 3 Ideen in maximal 3 Worten. \n" +
            "Bitte beachte achte nur unseren Input für diene Ideen. Bitte beachte deine eigenen Ideen nicht weiter, wenn sie uns gefallen, werden wir sie selbst sagen.\n" +
            "Gebe deine Antworten in folgenden Schema wieder: \n" +
            "\n" +
            "-: Idee\n" +
            "-: Idee\n" +
            "-: Idee\n" +
            "\n" +
            "Wenn du denkst, dass ich dich direkt anspreche, rede ich nur mit einem anderen Gesprächspartner. Antworte nicht direkt drauf sondern benutzte nur das beschriebene Schema.\n";

    */

    private static final String TEXT_SYSTEM_PROMPT = "Gib mir zu folgendem Text kreative Ideen in Stichpunkten. Zähle maximal 3 Stichpunkte auf und verwende pro Stichpunkt nur maximal 2 Wörter. Beginnen jeden Stichpunkt mit einem Bindestrich: ";

    private JSONObject systemMessage;

    private long roomId = 1;
    private MessageVerlauf messageVerlauf;

    private boolean generateBilder = false;
    private float temperature =0.7f;
    private float presence_penalty = 0;
    private float frequency_penalty = 0;


    public OpenAI() {
        this.messageVerlauf = new MessageVerlauf(20);
        try {
            this.systemMessage = new JSONObject().put("role", "system").put("content", OpenAI.TEXT_SYSTEM_PROMPT);
        }catch (JSONException e){
            //TODO
        }
    }

    public void setGenerateBilder(boolean generateBilder) {
        this.generateBilder = generateBilder;
    }

    public void generateText(String prompt) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        JSONArray messageArray = new JSONArray();
        JSONObject userMessage = new JSONObject().put("role", "user").put("content", prompt);

        this.messageVerlauf.add(userMessage);

        messageArray.put(this.systemMessage);
        for (JSONObject message : this.messageVerlauf.getMessageVerlauf()) {
            messageArray.put(message);
        }

        RequestBody bodyJ = RequestBody.create(mediaType, "{\n" +
                "  \"model\": \"gpt-3.5-turbo\",\n" +
                "  \"messages\": " + messageArray + ",\n" +
                "  \"temperature\": " + this.temperature + ",\n" +
                "  \"presence_penalty\": " + this.presence_penalty + ",\n" +
                "  \"frequency_penalty\": " + this.frequency_penalty +
                "}");

        Request request = new Request.Builder()
                .url(API_ENDPOINT_TEXT)
                .post(bodyJ)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response){
                processResponce(response);
            }
        });
    }

    /**
     * Extrahiert die Ideen aus dem Antworttext, der von ChatGPT gesendet wurde.
     * @param responseMessage Der Antworttext von ChatGPT.
     * @return Gibt eine Auflistung der enthaltenen Ideen zurück.
     */
    private Collection<String> extractResponseIdeas(String responseMessage)
    {
        Collection<String> ideas = new ArrayList<>();
        String[] lines = responseMessage.split("\\n");

        for (String line : lines)
        {
            if (line.startsWith("- "))
            {
                ideas.add(line);
            }
        }

        return ideas;
    }

    private void processResponce(Response response)
    {
        try
        {
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            JSONArray responseArray = jsonResponse.getJSONArray("choices");
            JSONObject jsonMessage = responseArray.getJSONObject(0).getJSONObject("message");
            this.messageVerlauf.add(jsonMessage);

            String message = jsonMessage.getString("content");
            System.out.println("ChatGPT: " + message);
            Collection<String> ideas = extractResponseIdeas(message);

            if (ideas.size() == 0)
            {
                return;
            }

/*
            Collection<String> ideen= new ArrayList<>();
            Scanner scanner = new Scanner(message);
            int i=0;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                if(line.startsWith("-: ")){
                    String idee= line.substring(2);
                    System.out.println(idee);
                    ideen.add(idee);
                }
            }
            */
            if(this.generateBilder){
                for(String bildIdee: ideas){
                    this.generateBild(bildIdee);
                }
            }else{
                //DistributorApi.setRoomContent(this.roomId, ideas);
            }

        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
    }

    public void generateBild(String idea){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        RequestBody bodyJ = RequestBody.create(mediaType, "{\n" +
                "  \"prompt\": \"" + idea + "\",\n" +
                "  \"size\": \"512x512\""+
                "}");
        Request request = new Request.Builder()
                .url(API_ENDPOINT_BILD)
                .post(bodyJ)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response){
                processImageResponce(idea,response);
            }
        });
    }

    private void processImageResponce(String idee,Response response){
        try {
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray responseArray = jsonResponse.getJSONArray("data");

            for (int i = 0; i < responseArray.length(); i++) {
                String url = responseArray.getJSONObject(i).getString("url");
                //DistributorApi.setRoomImage(this.roomId,new TextImageDTO(idee,url));
               // DistributorApi.addTextImagePair(this.roomId, idee, url);
            }
        }catch (Exception e){
            //TODO
        }
    }
}
