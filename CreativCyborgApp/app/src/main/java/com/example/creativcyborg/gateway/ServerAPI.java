package com.example.creativcyborg.gateway;

import android.util.Pair;

import com.example.creativcyborg.entities.Room;
import com.example.creativcyborg.gateway.dto.ingoing.RoomContentDTO;
import com.example.creativcyborg.gateway.dto.outgoing.AudioDataDTO;
import com.example.creativcyborg.gateway.dto.outgoing.TopicAudioDTO;
import com.example.creativcyborg.entities.ContentPart;
import com.example.creativcyborg.gateway.dto.ingoing.TopicResponseDTO;
import com.example.creativcyborg.gateway.return_values.TopicResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Stellt den Zugriffspunkt für die API auf dem Server bereit, welche für die Verarbeitung der
 * Kernfunktionalitäten des Systems zuständig ist.
 */
public class ServerAPI
{
    private static final String BASE_URL = "https://simon-schnitker-apps.de/";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private static final int SERVER_TIMEOUT_SEC = 30;
    private static final OkHttpClient client;

    static
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(SERVER_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(SERVER_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(SERVER_TIMEOUT_SEC, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Ermittelt alle verfügbaren Räume.
     * @return Gibt eine Auflistung aller verfügbaren Räume zurück.
     */
    public static List<Room> getAllRooms() throws IOException
    {
        Request request = new Request.Builder()
                .url(BASE_URL + "room")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (response.code() == 200)
        {
            String json = response.body().string();
            return new Gson().fromJson(json, new TypeToken<List<Room>>(){}.getType());
        }

        return new ArrayList<>();
    }

    /**
     * Fügt einen neuen Raum hinzu.
     * @param name Der Name des Raums.
     * @return Gibt den erstellten Raum zurück.
     */
    public static Room addRoom(String name) throws JSONException, IOException
    {
        JSONObject json = new JSONObject();
        json.put("name", name);
        RequestBody body = RequestBody.create(json.toString(), MEDIA_TYPE_JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "room")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful())
        {
            String roomJson = response.body().string();
            return new Gson().fromJson(roomJson, Room.class);
        }

        return null;
    }

    /**
     * Löscht einen Raum.
     * @param roomId Die ID des Raums.
     * @return Gibt true zurück, wenn die Aktion erfolgreich war.
     */
    public static boolean deleteRoom(long roomId) throws IOException
    {
        Request request = new Request.Builder()
                .url(BASE_URL + "room/" + roomId)
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    /**
     * Sendet AudioDaten für einen Raum an die API, um kreative Beiträge an alle Empfänger zu verteilen.
     * @param roomId Die ID des Raums.
     * @param audioData Die Audiodaten.
     * @param generateImages True, wenn passende Bilder zu den Audiodaten genertiert werden sollen.
     * @param temperature Die Kreativität der Beiträge zwischen 0 und 1.
     * @return Gibt true zurück, wenn das Senden erfolgreich war.
     */
    public static boolean sendAudioData(long roomId, byte[] audioData, boolean generateImages, float temperature) throws IOException
    {
        AudioDataDTO dto = new AudioDataDTO();
        dto.audioData = audioData;
        dto.generateImages = generateImages;
        dto.temperature = temperature;

        RequestBody body = RequestBody.create(new Gson().toJson(dto), MEDIA_TYPE_JSON);

        Request request = new Request.Builder()
                .url(BASE_URL + "room/" + roomId + "/audio")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Responsecode:" + response.code());
        return response.isSuccessful();
    }

    /**
     * Ermittelt die Inhalte eines Raums.
     * @param roomId Die ID des Raums.
     * @return Gibt eine Auflistung von Text-Bild-Paaren zurück.
     * @throws IOException
     */
    public static Collection<ContentPart> getRoomContent(long roomId) throws IOException
    {
        Request request = new Request.Builder()
                .url(BASE_URL + "room/" + roomId + "/content")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (response.code() == 200)
        {
            String json = response.body().string();
            return new Gson().fromJson(json, new TypeToken<Collection<ContentPart>>(){}.getType());
        }

        return new ArrayList<>();
    }

    /**
     * Löscht die Inhalte eines Raums.
     * @param roomId Die ID des Raums.
     */
    public static boolean clearRoomContent(long roomId) throws IOException
    {
        Request request = new Request.Builder()
                .url(BASE_URL + "room/" + roomId + "/content")
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    /**
     * Sendet Audiodaten an den Server. Dieser wandelt diese in Text um, gibt ihn und eine Zusammenfassung zurück.
     * @param roomId Die ID des Raums.
     * @param audioData Die Audiodaten.
     * @return Gibt den erkannten Text und die Zusammenfassung zurück.
     */
    public static TopicResponse sendTopicAudioData (long roomId, byte[] audioData) throws JSONException, IOException
    {
        TopicAudioDTO dto = new TopicAudioDTO();
        dto.audioData = audioData;

        RequestBody body = RequestBody.create(new Gson().toJson(dto), MEDIA_TYPE_JSON);
        Request request = new Request.Builder()
                .url(BASE_URL + "room/" + roomId + "/topic")
                .patch(body)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful())
        {
            String json = response.body().string();
            TopicResponseDTO responseDTO = new Gson().fromJson(json, TopicResponseDTO.class);

            return new TopicResponse(response.code(), responseDTO.text, responseDTO.topic);
        }

        return new TopicResponse(response.code());
    }


    public static RoomContentDTO getRoomContentInterval(long roomId) throws IOException
    {
        Request request = new Request.Builder()
                .url(BASE_URL + "room/" + roomId + "/content")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        if (response.code() == 200)
        {

            String json = response.body().string();
            return new Gson().fromJson(json, new TypeToken<RoomContentDTO>(){}.getType());
        }

        return null;
    }
}
