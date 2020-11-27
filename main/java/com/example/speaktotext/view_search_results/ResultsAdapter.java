package com.example.speaktotext.view_search_results;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.speaktotext.R;
import com.example.speaktotext.models.*;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import com.example.speaktotext.models.SongData;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder>{
    ArrayList<SongData> songs;
    Context context;

    public ResultsAdapter(ArrayList<SongData> songs, Context context) throws IOException, JSONException {
       // if(this.songs == null)this.songs = JsonSongDummy.getDummySongDataSource();
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View songView = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_result_item, parent, false);
        return new ViewHolder(songView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongData songData = songs.get(position);
        Picasso.get().load(songData.getPhotoUrl()).into(holder.img);
        holder.songName.setText(songData.getTitle());
        holder.description.setText(songData.getDescription());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
      public ImageView img;
      public TextView songName;
      public TextView description;

        public ViewHolder(@NonNull View songView) {
            super(songView);
            this.img = songView.findViewById(R.id.ivImage);
            this.songName = songView.findViewById(R.id.tvName);
            this.description = songView.findViewById(R.id.tvDescription);
        }
    }
}

