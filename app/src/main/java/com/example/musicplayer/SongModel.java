package com.example.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import java.io.Serializable;

public class SongModel {
    // General Information About The Song
    private String TITLE;
    private String ARTIST;
    private String PATH;

    // Favourite Boolean
    private boolean FAVOURITE_KEY;
    int vibrantSwatch;

    // METADATA EXTRACTOR
    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

    SongModel(String PATH) {
        this.PATH = PATH;
        mediaMetadataRetriever.setDataSource(PATH);
        TITLE = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if (artist == "" || artist == " " || artist == null || artist == "<Unknown>") {
            ARTIST = "Unkown Artist";
        } else {
            ARTIST = artist;
        }
    }

    /////////////// SETTERS //////////////////

    public void setFAVOURITE_KEY(boolean favourite_key) {
        this.FAVOURITE_KEY = favourite_key;
    }


    /////////////// GETTERS //////////////////

    public String getTitle() {
        return TITLE;
    }

    public String getArtist() {
        return ARTIST;
    }

    public String getPath() {
        return PATH;
    }

    public Bitmap getAlbumCover() {
        byte[] pictureSetData = mediaMetadataRetriever.getEmbeddedPicture();
        Bitmap bitmap;

        if (pictureSetData != null) {
            bitmap = BitmapFactory.decodeByteArray(pictureSetData, 0, pictureSetData.length);
            return bitmap;

        }

        return null;
    }

    public boolean getFAVOURITE_KEY() {
        return FAVOURITE_KEY;
    }

    public String getDominantColorHEX(Bitmap bitmap) {


        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                vibrantSwatch = palette.getDominantSwatch().getRgb();
            }
        });

        return "#" + Integer.toHexString(vibrantSwatch);
    }

}
