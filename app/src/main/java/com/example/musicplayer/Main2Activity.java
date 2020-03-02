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

        checkStoragePermission();

        // Import View IDs
//        ListView listView = findViewById(R.id.listView);


        // Reading Files from Sd Card and creating Hashmap
        ArrayList<HashMap<String, String>> songList = getPlayList("/storage/");
        // Reading FIles from Internal Storage and Creating Hashmap
        ArrayList<HashMap<String, String>> filesFromInternalStorage = getPlayListFromInternalStorage(Environment.getExternalStorageDirectory().getAbsolutePath());
//        for (HashMap<String, String> song : filesFromInternalStorage){
//            songList.add(song);
//        }


        // Creating list for mp3 file paths
        ArrayList<String> songNames = new ArrayList<>();
        ArrayList<String> artist_list = new ArrayList<>();
        if (songList != null) {
            for (int i = 0; i < songList.size(); i++) {
//                // Getting the song Title and Path from the Hashmap
//                String fileName = songList.get(i).get("file_name");
//                String filePath = songList.get(i).get("file_path");
//
//                Uri uri = Uri.parse(fileName);
//                // Creating MediameatadataRetriever in order to get Artist with file Path
//                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//                mediaMetadataRetriever.setDataSource(String.valueOf(uri));
//
//                // Adding items in Arraylist in order to pass the data through Intent
//                songNames.add(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
//                String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//                songNames.add(fileName);
//                if (artist == "" || artist == " " || artist == null || artist == "<uknown>"){
//                    artist_list.add("Unkown Artist");
//                } else {
//                    artist_list.add(artist);
//                }
                String fileName = songList.get(i).get("file_name");
                songNames.add(fileName);

                String filePath = songList.get(i).get("file_path");
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(filePath);
                String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                if (artist == ""){
                    artist_list.add("Unkown Artist");
                } else {
                    artist_list.add(artist);
                }
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
        songAdapter = new SongAdapter(this, songNames, artist_list, new onSongItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                passSongToIntent(position, songList);
            }
        });
        recyclerView.setAdapter(songAdapter);



        ///////////////////////////////////////////

    }

    private ArrayList<HashMap<String, String>> getPlayListFromInternalStorage(String absolutePath) {
        ArrayList<HashMap<String, String>> songList = new ArrayList<>();

        // SongCurse
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(uri, null, null, null, null);

        HashMap<String, String> song = new HashMap<>();

        if (songCursor != null && songCursor.moveToFirst()){
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);

                song.put("song_name", currentTitle);
                song.put("song_artist", currentArtist);

                songList.add(song);
            } while (songCursor.moveToNext());
        }


        return songList;

    }

    void checkStoragePermission(){
        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            Toast.makeText(this, "Permission Pending...", Toast.LENGTH_SHORT).show();
        }
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

