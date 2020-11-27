package com.example.speaktotext.view_play_list;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.speaktotext.R;
import com.example.speaktotext.models.SongData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    public List<SongData> songs;
    Context context;

    public PlayListAdapter(List<SongData> songs, Context context) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View songView = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_list_item, parent, false);
        return new ViewHolder(songView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongData songData = songs.get(position);
        Picasso.get().load(songData.getPhotoUrl()).into(holder.img);
        holder.songName.setText(songData.getTitle());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView img;
        public TextView songName;
        public ViewHolder(@NonNull View songView) {
            super(songView);
            this.img = songView.findViewById(R.id.ivImage);
            this.songName = songView.findViewById(R.id.tvName);
        }
    }
}
