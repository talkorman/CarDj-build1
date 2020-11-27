package com.example.speaktotext.models.play_lists;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.speaktotext.models.SongData;
import java.util.ArrayList;
import java.util.List;

public class PlayListViewModel extends AndroidViewModel {
    private SongsDataSource ds;
    private MutableLiveData<ArrayList<SongData>>songs;

    public PlayListViewModel(@NonNull Application application) {
        super(application);
        songs = new MutableLiveData<>();
        };

        public LiveData<List<SongData>> getSongs(){
         ds = new SongsDataSource(getApplication());
        return ds.getSongs();
        }

        public void add(SongData song){
        ds.add(song);
        }
    }

