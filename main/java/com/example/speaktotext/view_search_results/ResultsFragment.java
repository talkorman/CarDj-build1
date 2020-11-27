package com.example.speaktotext.view_search_results;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.speaktotext.R;
import com.example.speaktotext.models.SongData;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ResultsFragment extends Fragment {
    private RecyclerView rvSongs;
    private TextView tvResults;
    private static ArrayList<SongData> resultsToShow = new ArrayList<>();


   public static ResultsFragment newInstance(ArrayList<SongData> results){
    ResultsFragment resultsFragment = new ResultsFragment();
       resultsToShow = results;
       return resultsFragment;
   }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_results_view, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvSongs = view.findViewById(R.id.view_list);
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ResultsAdapter adapter = null;
        try {
            adapter = new ResultsAdapter(resultsToShow, getContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();

        }
        rvSongs.setAdapter(adapter);

    }
}

