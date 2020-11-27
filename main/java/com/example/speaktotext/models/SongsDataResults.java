package com.example.speaktotext.models;
import androidx.lifecycle.MutableLiveData;
import com.example.speaktotext.models.*;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SongsDataResults {
    private StringBuilder apiAddress;

    public SongsDataResults(String s){
        apiAddress = new StringBuilder();
        this.apiAddress.append(YoutubeConfig.getApiAddress()).append(s);

    }

    public void readSearch(MutableLiveData<ArrayList<SongData>>callback){

        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(apiAddress.toString()).build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                System.out.println("failed to receive results from API");
               ArrayList<SongData> dummy = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    dummy.add(JsonSongDummy.getDummyResults());
                }
              
               callback.postValue(dummy);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("response");
                ArrayList<SongData> songs = new ArrayList<>();
                String json = response.body().string();
                try{
                    JSONObject rootObject = new JSONObject(json);
                    JSONArray resultsJson = rootObject.getJSONArray("items");
                    for (int i = 0; i < resultsJson.length(); i++) {
                        JSONObject songJsonObject = resultsJson.getJSONObject(i);
                        String videoId = songJsonObject.getJSONObject("id").getString("videoId"),
                                title = songJsonObject.getJSONObject("snippet").getString("title"),
                                description = songJsonObject.getJSONObject("snippet").getString("description"),
                                photoUrl = songJsonObject.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                        songs.add(new SongData(videoId, title, description, photoUrl));
                    }
                    System.out.println(songs.toString());
                    callback.postValue(songs);
            } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    });
}
}
