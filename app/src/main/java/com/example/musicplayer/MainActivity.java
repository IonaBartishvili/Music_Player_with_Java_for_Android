package com.example.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.palette.graphics.Palette;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.musicplayer.App.CHANNEL_ID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static MediaPlayer mediaPlayer;
    private Button play_btn;
    private Button play_next;
    private Button play_previous;
    private Runnable runnable;
    private String[] songNames;
    private SeekBar seekBar;
    private TextView duration_current;
    private TextView duration_whole;
    private Timer timer;
    private TextView song_name;
    private TextView song_artist;
    private int position;
    private NotificationManagerCompat notificationManager;
    private CircleImageView albumCover;
    private int dominantColor;
    private LinearLayout play_button_background;
    private Button favourite;
    private ConstraintLayout parent;

    private boolean SHUFFLE = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // IDs
        seekBar = findViewById(R.id.seekBar);
        play_btn = findViewById(R.id.pla_btn);
        play_next = (Button) findViewById(R.id.next_btn);
        play_previous = (Button) findViewById(R.id.previous_btn);
        duration_current = findViewById(R.id.duration_current);
        duration_whole = findViewById(R.id.duration_whole);
        song_name = findViewById(R.id.song_name);
        song_artist = findViewById(R.id.song_artist);
        albumCover = findViewById(R.id.album_cover);
        play_button_background = findViewById(R.id.play_button_background);
        favourite = findViewById(R.id.favourite);
        parent = findViewById(R.id.parent);


        // Geting position from Intent
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");

        // ----------------------------------------------- //\
        ArrayList<SongModel> songModelArrayList = retrieveDataFromSharedPreferences();

        // Running Entire Music player Logic. Giving the Song list and the position of the song
        initMusicPlayer(position, songModelArrayList);

        // Play and Pause Click Listener
        play_btn.setOnClickListener(this);

        // Playing the Next Track
        playNextTrack(songModelArrayList);

        // Playing the Previos Track
        playPrevTrack(songModelArrayList);
    }

    void initMusicPlayer(int position, ArrayList<SongModel> songList) {
        // Creating Metadata recourses to get Artist name
        SongModel song = songList.get(position);
        String path = song.getPath();
        String title = song.getTitle();
        String artist = song.getArtist();
        Bitmap albumArt = song.getAlbumCover();
        albumCover.setImageBitmap(albumArt);


        // Customize the SnackBar for Later Use
        Snackbar snackbar = Snackbar.make(parent, "", Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.animate();

        // Getting and Setting the Vibrant Color
        setVibrantColorOnViews(albumArt, snackBarView);


        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SHUFFLE){
                    favourite.setBackgroundResource(R.drawable.ic_favourite_button_hover);
                    snackbar.setText("This Song is added to favourites").show();
                    SHUFFLE = true;
                } else {
                    favourite.setBackgroundResource(R.drawable.ic_favourite_button);
                    snackbar.setText("This Song is removed from favourites").show();
                    SHUFFLE = false;
                }


            }
        });


        // Converting Path to URI
        Uri uri = Uri.parse(path);
        // Creating MediaPlayer with given song's URI
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, uri);


        // Setting the Metadata to XML views
        song_name.setText(title);
        song_artist.setText(artist);


        // Setting the Mediaplayer Listener
        mediaPlayer_setOnPreparedLisntener();

        // Seekbar touch
        seekBar_setOnSeekBarChangeListener();

        // Playing the Next Track when current is over

    }

    private void setVibrantColorOnViews(Bitmap albumArt, View snackbar) {
        Palette.from(albumArt).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                int vibrantSwatch = palette.getDominantSwatch().getRgb();

                play_button_background.getBackground().setColorFilter(Color.parseColor("#" + Integer.toHexString(vibrantSwatch)), PorterDuff.Mode.SRC_IN);
                seekBar.getProgressDrawable().setColorFilter(Color.parseColor("#" + Integer.toHexString(vibrantSwatch)), PorterDuff.Mode.SRC_IN);
                seekBar.getThumb().setColorFilter(Color.parseColor("#" + Integer.toHexString(vibrantSwatch)), PorterDuff.Mode.SRC_IN);
                snackbar.setBackgroundColor(vibrantSwatch);
            }
        });
    }

    void playNextTrack(ArrayList<SongModel> songList) {
        play_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < songList.size() - 1) {
                    position++;
                } else {
                    position = 1;
                }

                initMusicPlayer(position, songList);
            }
        });
    }

    void playPrevTrack(ArrayList<SongModel> songList) {
        play_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= 0) {
                    position = songList.size();
                } else {
                    position--;
                }

                initMusicPlayer(position, songList);
            }
        });
    }

    void mediaPlayer_setOnPreparedLisntener() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mp.getDuration());
                mediaPlayer.start();
                createMusicNotification();
                changeSeekbar();
                play_btn.setBackgroundResource(R.drawable.ic_stop_button);
                duration_whole.setText(createTimeLabel(mediaPlayer.getDuration()));
            }
        });
    }

    private void createMusicNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID, "KOD Dev", NotificationManager.IMPORTANCE_HIGH);
//            notificationManager = getSystemService(NotificationManager.class);
//            if (notificationManager != null){
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
        notificationManager = NotificationManagerCompat.from(this);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.album_cover)
                .setContentTitle(song_name.getText().toString())
                .setContentText(song_artist.getText().toString())
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .build();

        notificationManager.notify(1, notification);
    }

    void seekBar_setOnSeekBarChangeListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    // Miliseconds > mins and secs
    private String createTimeLabel(int duration) {
        String timelabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timelabel = min + ":";
        if (sec < 10) {
            timelabel += "0";
        }

        timelabel += sec;

        return timelabel;
    }

    // Change seek bar listener (to change seekbar position per 1 second
    private void changeSeekbar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                    duration_current.setText(createTimeLabel(mediaPlayer.getCurrentPosition()));
                }
            }, 1000);

        }
    }

    @Override
    public void onClick(View v) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            createMusicNotification();
            // Creating the Notification Panel
            changeSeekbar();
            play_btn.setBackgroundResource(R.drawable.ic_stop_button);
        } else {
            mediaPlayer.pause();
            play_btn.setBackgroundResource(R.drawable.ic_play_button);
            notificationManager.cancel(1);
        }
    }

    public ArrayList<SongModel> retrieveDataFromSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("Shared Preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SongList", null);
        Type type = new TypeToken<ArrayList<SongModel>>(){}.getType();
        ArrayList<SongModel> songList = gson.fromJson(json, type);
        return songList;

    }
}



