package com.example.speaktotext;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.speaktotext.models.*;
import com.example.speaktotext.models.play_lists.SongsDataSource;
import com.example.speaktotext.view_play_list.*;
import com.example.speaktotext.view_player.*;
import com.example.speaktotext.view_search_results.*;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PlayerFragment.OnSongEndListener {
    //properties declarations
    private TextView tvText;
    private SpeechRecognizer recognizer;
    private Intent intent;
    private TextToSpeech textToSpeech;
    private StringBuilder voice;
    private StringBuilder searchingString;
    private ArrayList<SongData> resultsObjects;
    private MutableLiveData<ArrayList<SongData>> searchResults;
    private MutableLiveData<String> userSpokenText;
   private MutableLiveData<String> searchWords;
   private MutableLiveData<String> deviceSpokenText;
   private int countSayResults;
   private AppState appState;
   private SongsDataSource dataSource;
   private int songId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //properties setup
       voice = new StringBuilder();
        searchingString = new StringBuilder();
        tvText = findViewById(R.id.tvText);
        userSpokenText = new MutableLiveData<>();
        deviceSpokenText = new MutableLiveData<>();
        searchWords = new MutableLiveData<>();
        searchResults = new MutableLiveData<>();
        resultsObjects = new ArrayList<>();
        dataSource = new SongsDataSource(this);
        countSayResults = 0;
        appState = new AppState();
        appState.setAppState(AppState.GREETING);

        userSpokenText.observe(this, (s)->{
            System.out.println("user spoken");
            recognizer.stopListening();
            translateSpokenText(s);
        });

        searchWords.observe(this, (s)->{
            SongsDataResults ds = new SongsDataResults(s);
            ds.readSearch(searchResults);
        });

        searchResults.observe(this, (s)->{
            System.out.println("got response");
            resultsObjects = s;
            showSearchResults(s);
            showPlayList();
            appState.setAppState(AppState.DEVICE_SAY_OPTION);
            countSayResults = 0;
            tellOptions(resultsObjects);
        });

        deviceSpokenText.observe(this, (s)->{
            System.out.println("Device spoken");
            if(appState.getAppState() == AppState.DEVICE_SAY_OPTION){
                appState.setAppState(AppState.USER_ACCEPTING_OPTION);
                recognizer.startListening(intent);
            }else if(appState.getAppState() == AppState.GREETING){
                recognizer.startListening(intent);
            }
        });
        setUpTextToSpeach();
        setUpSpeachToText();
        showPlayList();
        showSearchResults(resultsObjects);
        displaySong("tH2rgPqi8Ag");
    }


    public void tellOptions(ArrayList<SongData> songs){

        int delay = countSayResults == 0 ? 2000 : 10;
        Handler h = new Handler();
            Runnable delayBeforeDeviceSay = new Runnable() {
                @Override
                public void run() {
                    recognizer.stopListening();
                    String words = songs.get(countSayResults).getTitle();
                    String wordsToSpeak = words.length() >= 20 ? words.substring(0, 20) : words;
                    System.out.println("listen");
                    appState.setAppState(AppState.DEVICE_SAY_OPTION);
                    speak(wordsToSpeak);
                }
            };
                h.postDelayed(delayBeforeDeviceSay, delay);
    }

    public void displaySong(String song){
        countSayResults = 0;
        recognizer.startListening(intent);
        closePlayer();
        PlayerFragment player = PlayerFragment.newInstance(song);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.youtubeplayerfragment, player).commit();
    }
    public void start(View view){
        appState.setAppState(AppState.GREETING);
        countSayResults = 0;
        recognizer.startListening(intent);
    }


    @Override
    public void onSongEnd(Boolean songEnded) {
        tvText.setText("Song Ended");
        closePlayer();
    }

    public void closePlayer(){
        FragmentManager fm = getSupportFragmentManager();
        PlayerFragment player = (PlayerFragment)fm.findFragmentById(R.id.youtubeplayerfragment);
        if(player != null){
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(player).commit();
        }
    }
    public void closeSearchResults(){
        FragmentManager fm = getSupportFragmentManager();
        ResultsFragment resultsFragment = (ResultsFragment) fm.findFragmentById(R.id.results_view);
        if(resultsFragment != null){
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(resultsFragment).commit();
        }
    }
    public void showSearchResults(ArrayList<SongData> resultsObjects){
        appState.setAppState(AppState.SHOW_RESULTS);
        closeSearchResults();
        ResultsFragment resultsFragment = ResultsFragment.newInstance(resultsObjects);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.results_view, resultsFragment).commit();
        appState.setAppState(AppState.DEVICE_SAY_OPTION);
    }

    public void showPlayList(){
        PlayListFragment playListFragment = PlayListFragment.newInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.play_list_view, playListFragment).commit();
        appState.setAppState(AppState.DISPLAY_PLAYLIST);
    }

    public void setUpTextToSpeach(){
        textToSpeech = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS){
                            int lang = textToSpeech.setLanguage(Locale.US);
                            if(lang==TextToSpeech.LANG_MISSING_DATA||lang==TextToSpeech.LANG_NOT_SUPPORTED){
                                Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_LONG).show();
                            }
                            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                @Override
                                public void onStart(String utteranceId) {
                                    System.out.println("device speaking");
                                }

                                @Override
                                public void onDone(String utteranceId) {
                                    System.out.println("device spoken");
                                    deviceSpokenText.postValue(utteranceId);
                                }

                                @Override
                                public void onError(String utteranceId) {

                                }
                            });
                        }
                    }
                });
        textToSpeech.setLanguage(Locale.US);
    }

    public void speak(String s){
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null ,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
    }

    public void setUpSpeachToText(){
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches != null) {
                    System.out.println("got user speach result");
                    String string = matches.get(0);
                    recognizer.stopListening();
                    userSpokenText.postValue(string);
                }
            }
            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("User Ready for speach");
            }
            @Override
            public void onBeginningOfSpeech() {
                System.out.println("User beginning of speach");
            }
            @Override
            public void onRmsChanged(float rmsdB) { }
            @Override
            public void onBufferReceived(byte[] buffer) { }
            @Override
            public void onEndOfSpeech() {
                System.out.println("end user speach");
            }
            @Override
            public void onError(int error) {
                System.out.println("user speach error " + error);
                if(error == 1){
                    translateSpokenText("");
                }
            }
            @Override
            public void onPartialResults(Bundle partialResults) { }
            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
    }

    public void translateSpokenText(String s) {
        String words = s.toLowerCase();
        System.out.println("count is " + countSayResults);
        System.out.println("translate");
        System.out.println(words);
        if (appState.getAppState() == AppState.GREETING && words.contains("i want")) {
            appState.setAppState(AppState.SEARCH);
            countSayResults = 0;
            voice.setLength(0);
            searchingString.setLength(0);
            searchingString.append(s);
            searchingString.delete(0, 7);
            voice.append("I'm going to search for ");
            String item = searchingString.toString();
            tvText.setText("Searching for " + item);
            speak(voice.toString() + item);
            item.replace(".", "/").replace(",", "/").replace(" ", "/");
            searchWords.postValue(item);
            return;
        }
        if(appState.getAppState() == AppState.GREETING && words.contains("")){
            countSayResults = 0;
            start(getCurrentFocus());
            return;
        }
        if( words.contains("reject")){
            System.out.println("cancel");
            countSayResults = 0;
            closePlayer();
            recognizer.startListening(intent);
            appState.setAppState(AppState.GREETING);
            return;
        }
        if(appState.getAppState() == AppState.USER_ACCEPTING_OPTION && words.contains("yes")){
            System.out.println("yes");
            textToSpeech.stop();
            appState.setAppState(AppState.DISPLAY_OPTION);
            displaySong(resultsObjects.get(countSayResults).getVideoId());
            countSayResults = 0;
            return;
        }
        if(appState.getAppState() == AppState.USER_ACCEPTING_OPTION && words.contains("no")){
            System.out.println("no");
            appState.setAppState(AppState.DEVICE_SAY_OPTION);
            if(countSayResults < resultsObjects.size() - 1) {
                countSayResults++;
                tellOptions(resultsObjects);
            }else{
                countSayResults = 0;
                appState.setAppState(AppState.GREETING);
                speak("you can search for a new song");
                start(getCurrentFocus());
            }
            return;
        }
        if((appState.getAppState() == AppState.USER_ACCEPTING_OPTION ||
            appState.getAppState() == AppState.DISPLAY_OPTION) && words.contains("to play list")){
            appState.setAppState(AppState.ADDTO_PLAYLIST);
            resultsObjects.get(countSayResults).setSongId(songId);
           dataSource.add(resultsObjects.get(countSayResults));
           showPlayList();
           countSayResults = 0;
           recognizer.startListening(intent);
            appState.setAppState(AppState.GREETING);
            return;
        }
        if(appState.getAppState() == AppState.USER_ACCEPTING_OPTION){
            speak("I repeat");
            countSayResults = 0;
            appState.setAppState(AppState.DEVICE_SAY_OPTION);
            tellOptions(resultsObjects);
        }
        if(words == "detete all"){
            dataSource.deleteAll();
        }
    }
}