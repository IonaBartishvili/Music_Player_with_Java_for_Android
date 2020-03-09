package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

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


        // Geting position from Intent
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");
        // Recieving the songs List from Intent
        ArrayList<HashMap<String, String>> songList = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("songList");

        // Running Entire Music player Logic. Giving the Song list and the position of the song
        initMusicPlayer(position, songList);

        // Play and Pause Click Listener
        play_btn.setOnClickListener(this);

        // Playing the Next Track
        playNextTrack(songList);

        // Playing the Previos Track
        playPrevTrack(songList);
    }

    void initMusicPlayer(int position, ArrayList<HashMap<String, String>> songList) {
        // Creating Metadata recourses to get Artist name
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        String path = songList.get(position).get("file_path");
        String name = songList.get(position).get("file_name");
        String artist;
        mediaMetadataRetriever.setDataSource(path);
        artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);


        // Converting Path to URI
        Uri uri = Uri.parse(path);
        // Creating MediaPlayer with given song's URI
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, uri);


        // Setting the Metadata to XML views
        song_name.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        song_artist.setText(artist);


        // Setting the Mediaplayer Listener
        mediaPlayer_setOnPreparedLisntener(songList);

        // Seekbar touch
        seekBar_setOnSeekBarChangeListener();

        // Playing the Next Track when current is over
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextTrack(songList);
            }
        });

        if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
            playNextTrack(songList);
        }
        ;

    }

    void playNextTrack(ArrayList<HashMap<String, String>> songList) {
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

    void playPrevTrack(ArrayList<HashMap<String, String>> songList) {
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

    void mediaPlayer_setOnPreparedLisntener(ArrayList<HashMap<String, String>> songList) {
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

    private ArrayList<File> readSongs(File[] root) {
        ArrayList<File> arrayList = new ArrayList<>();

        for (File file : root) {
            if (file.isDirectory()) {
                arrayList.addAll(readSongs(root));
            } else {
                if (file.getName().endsWith(".mp3")) {
                    arrayList.add(file);
                }

            }
        }


        return arrayList;
    }


    public void onClick(ArrayList<HashMap<String, String>> songList) {

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
}



