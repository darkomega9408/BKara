package com.beast.bkara.model;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Darka on 4/19/2016.
 */
public class Singer implements Parcelable {
    private int singer_id;
    private String name;
    private String poster;
    private ArrayList<Song> songs;

    public Singer() {

    }

    public int getSinger_id() {
        return singer_id;
    }

    public void setSinger_id(int singer_id) {
        this.singer_id = singer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    //Parcelling part
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(getSinger_id());
        parcel.writeString(getName());
        parcel.writeString(getPoster());
    }

    private Singer(Parcel in) {
        setSinger_id(in.readInt());
        setName(in.readString());
        setPoster(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Singer> CREATOR
            = new Parcelable.Creator<Singer>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Singer createFromParcel(Parcel in) {
            return new Singer(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Singer[] newArray(int size) {
            return new Singer[size];
        }
    };

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
