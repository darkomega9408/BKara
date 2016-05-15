package com.beast.bkara.model;

import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dotha on 5/6/2016.
 */
public class Playlist extends BaseObservable implements Parcelable {
    private String name;
    private ArrayList<Song> songList;

    public Playlist(String name) {
        this.name = name;
        songList = new ArrayList<Song>();
    }

    public Playlist(String name, ArrayList<Song> songList) {
        this.name = name;
        this.songList = songList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public void addSong(Song song) {
        Long song_id = song.getSong_id();

        for (Song s: songList) {
            if (s.getSong_id().equals(song_id)) {
                return;
            }
        }

        this.songList.add(song);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeTypedList(getSongList());
    }

    private Playlist(Parcel in) {
        setName(in.readString());
        in.readTypedList(songList, Song.CREATOR);
    }

    public static final Parcelable.Creator<Playlist> CREATOR
            = new Parcelable.Creator<Playlist>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
}
