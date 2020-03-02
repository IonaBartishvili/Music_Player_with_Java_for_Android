package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private static final int MY_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Declaring IDs
        recyclerView = findViewById(R.id.recyclerView);

        // Asking || Checking the Storage Permission
        checkStoragePermission();

        /////////////////// READING MP3 FILES FROM STORAGE ///////////////////
        // Reading Files from Sd Card and creating Hashmap
        ArrayList<HashMap<String, String>> songList = getPlayList("/storage/");
        // Reading FIles from Internal Storage and Creating Hashmap
        ArrayList<HashMap<String, String>> filesFromInternalStorage = getPlayList(Environment.getExternalStorageDirectory().getAbsolutePath());
        for (HashMap<String, String> song : filesFromInternalStorage) {
            songList.add(song);
        }
        /////////////////////////////////////////////////////////////////////

        // Creating list for mp3 file paths
        ArrayList<String> songNames = new ArrayList<>();
        ArrayList<String> artist_list = new ArrayList<>();
        if (songList != null) {
            for (int i = 0; i < songList.size(); i++) {
                // Getting the File path for song from Hashmap
                String filePath = songList.get(i).get("file_path");
                // Creating MediaMetadataRetriever instance in order to access Title and Artist
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(filePath);
                String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String song_name = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

                // adding resources to Lists.
                songNames.add(song_name);
                if (artist == "" || artist == " " || artist == null || artist == "<Unknown>") {
                    artist_list.add("Unkown Artist");
                } else {
                    artist_list.add(artist);
                }
            }
        }

        ///////////// Recyvler View ///////////////
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        songAdapter = new SongAdapter(this, songNames, artist_list, new onSongItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                passSongToIntent(position, songList);
            }
        });
        recyclerView.setAdapter(songAdapter);
        ///////////////////////////////////////////

    }

    void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            Toast.makeText(this, "Permission Pending...", Toast.LENGTH_SHORT).show();
        }
    }


    void passSongToIntent(int position, ArrayList<HashMap<String, String>> songList) {

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
                } else if (file.getPath().endsWith(".mp3")) {
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

