package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize and Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);

        // Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.player:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

        // Declaring IDs
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Asking || Checking the Storage Permission
        checkStoragePermission();


        // Getting MP3 files from both internal and external Storage /
        ArrayList<SongModel> songModelArrayList = readSongs("/storage/");
        ArrayList<SongModel> songModelArrayListFROM_INTERNAL = readSongs(Environment.getExternalStorageDirectory().getAbsolutePath());
        ArrayList<String> songNames = new ArrayList<>();
//        ArrayList<String> songArtists = new ArrayList<>();

        songModelArrayList.addAll(songModelArrayListFROM_INTERNAL);

        for (SongModel song : songModelArrayList){
            songNames.add(song.getTitle());
//            songArtists.add(song.getArtist());
        }

        // Put SongModel object's Array into SharedPreferences




        // --------------------------------------------------------------- /
        /////////////////////////////////////////////////////////////////////


        ///////////// Recyvler View ///////////////
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        SongAdapter songAdapter = new SongAdapter(this, songNames, songModelArrayList, new onSongItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                passSongToIntent();
                saveData(songModelArrayList, position);
            }
        });
        recyclerView.setAdapter(songAdapter);
        ///////////////////////////////////////////

    }

    void passSongToIntent() {

        Intent goToSongActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goToSongActivity);
    }

    public void saveData(ArrayList<SongModel> songModelArrayList, int position){
        SharedPreferences sharedPreferences = getSharedPreferences("Shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(songModelArrayList);
        editor.putString("SongList", json);
        editor.putInt("position", position);
        editor.apply();
    }

//    void checkStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
//                } else {
//                    ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
//                }
//            } else {
//                Toast.makeText(this, "Permission Pending...", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Permission Granted !", Toast.LENGTH_SHORT).show();
//        }
//
//
//    }

    public void checkStoragePermission(){
        if (Build.VERSION.SDK_INT >= 23){
            if (checkPermission()){
                //
            } else {
                requestPermission();
            }
        } else {
            Toast.makeText(this, "Permission Granted !", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {return false;}
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(Main2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }



    ArrayList<String> getPlayList(String rootPath) {
        ArrayList<String> fileList = new ArrayList<>();


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
//                    HashMap<String, String> song = new HashMap<>();
//                    song.put("file_path", file.getAbsolutePath());
//                    song.put("file_name", file.getName());
//                    fileList.add(song);
                    fileList.add(file.getAbsolutePath());
                }
            }
            return fileList;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<SongModel> readSongs(String rootPath) {
        ArrayList<String> songPathList = getPlayList(rootPath);
        ArrayList<SongModel> songList = new ArrayList<>();

        for (String path : songPathList){
            SongModel song = new SongModel(path);
            song.setFAVOURITE_KEY(false);
            songList.add(song);
        }

        return songList;
    }

}

