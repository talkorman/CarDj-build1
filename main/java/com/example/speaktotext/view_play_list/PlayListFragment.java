package com.example.speaktotext.view_play_list;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.speaktotext.R;
import com.example.speaktotext.models.play_lists.PlayListViewModel;
import com.example.speaktotext.models.play_lists.SongsDataSource;


public class PlayListFragment extends Fragment {

    private PlayListViewModel playListViewModel;
    private RecyclerView rvPlayList;


    public static PlayListFragment newInstance(){
        PlayListFragment playListFragment = new PlayListFragment();
        return playListFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        playListViewModel = new ViewModelProvider(this).get(PlayListViewModel.class);
        View root =  inflater.inflate(R.layout.fragment_play_list, container, false);
        rvPlayList = root.findViewById(R.id.rvPlayList);
        rvPlayList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SongsDataSource ds = new SongsDataSource(getContext());
        ds.getSongs().observe(getViewLifecycleOwner(), s->{
            PlayListAdapter adapter = new PlayListAdapter(s, getContext());
            rvPlayList.setAdapter(adapter);
        });

        return root;
    }


}
