package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Import View IDs
//        ListView listView = findViewById(R.id.listView);
        TextView adapterTextView = findViewById(R.id.adapterTextView);


        // Reading Files from root folder and creating Hashmap
        ArrayList<HashMap<String, String>> songList = getPlayList("/storage/");


        // Creating list for mp3 file paths
        String[] songNames = new String[songList.size()];
        ArrayList<String> songNames1 = new ArrayList<>();
        ArrayList<String> artist_list1 = new ArrayList<>();
        if (songList != null) {
            for (int i = 0; i < songList.size(); i++) {
                String fileName = songList.get(i).get("file_name");
                songNames[i] = fileName;
                songNames1.add(fileName);
                String filePath = songList.get(i).get("file_path");
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(filePath);
                String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                if (artist == ""){
                    artist_list1.add("Unkown Artist");
                } else {
                    artist_list1.add(artist);
                }
                //here you will get list of file name and file path that present in your device
            }
        }


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                getApplicationContext(),
//                R.layout.song_layout,
//                R.id.adapterTextView,
//                songNames
//        );
//        listView.setAdapter(adapter);
//
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                passSongToIntent(position, songList);
//            }
//        });

        ///////////// Recyvler View ///////////////
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

//        songAdapter = new SongAdapter(this, getSongList());
        songAdapter = new SongAdapter(this, songNames1, artist_list1);
        recyclerView.setAdapter(songAdapter);



        ///////////////////////////////////////////

    }


    void passSongToIntent(int position, ArrayList<HashMap<String, String>> songList){

        Intent goToSongActivity = new Intent(getApplicationContext(), MainActivity.class)
                .putExtra("position", position)
                .putExtra("songList", songList);
        startActivity(goToSongActivity);
    }


    ArrayList<HashMap<String, String>> getPlayList(String rootPath) {
        ArrayList<HashMap<String, String>> fileList = new ArrayList<>();


        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles(); //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }

}

