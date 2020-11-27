package com.example.speaktotext.models;

public class YoutubeConfig {
    public YoutubeConfig(){

    }
    private static final String API_KEY = "AIzaSyCZSptUp9DxNIJjt14YBWJ-ufYHpNi0ZUc";
    private static final String API_ADDRESS = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=rating&type=video&videoDefinition=any&videoDuration=short&videoEmbeddable=any&key=";
    public static String getApiAddress(){
        return API_ADDRESS + API_KEY + "&q=";
    }
}

