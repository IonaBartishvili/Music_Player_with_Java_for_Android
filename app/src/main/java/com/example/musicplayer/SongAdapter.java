package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<String> song_list;
    private ArrayList<SongModel> songModelArrayList;
    private String songPosition;
    public onSongItemClickListener onSongItemClickListener;

    SongAdapter(Context context, ArrayList<String> song_list, ArrayList<SongModel> songModelArrayList, onSongItemClickListener onSongItemClickListener){
        this.layoutInflater = LayoutInflater.from(context);
        this.song_list = song_list;
        this.songModelArrayList = songModelArrayList;
        this.onSongItemClickListener = onSongItemClickListener;
    }


    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.raw, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SongAdapter.ViewHolder holder, int position) {

        String song_name = song_list.get(position);
        String song_artist = songModelArrayList.get(position).getArtist();
        String songPosition = Integer.toString(position + 1);

        holder.song_name.setText(song_name);
        holder.song_artist.setText(song_artist);
        holder.songPosition.setText(songPosition);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSongItemClickListener.onItemClickListener(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return song_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView song_name, song_artist, songPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            song_name = itemView.findViewById(R.id.songRaw_name);
            song_artist = itemView.findViewById(R.id.songRaw_artist);
            songPosition = itemView.findViewById(R.id.songRaw_position);

        }
    }
}
