package com.example.speaktotext.models;

public class AppState {
    public static final int GREETING = 0;
    public static final int SEARCH = 1;
    public static final int SHOW_RESULTS = 2;
    public static final int DEVICE_SAY_OPTION = 3;
    public static final int USER_ACCEPTING_OPTION = 4;
    public static final int DISPLAY_OPTION = 5;
    public static final int ADDTO_PLAYLIST = 6;
    public static final int DISPLAY_PLAYLIST = 7;
    public static final int DISPLAYING_SONG = 8;
    private int appState;

    public void setAppState(int appState) {
        this.appState = appState;
    }

    public int getAppState() {
        return appState;
    }
}
