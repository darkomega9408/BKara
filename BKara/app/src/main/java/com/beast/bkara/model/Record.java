package com.beast.bkara.model;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.beast.bkara.model.supportmodel.RatingRecord;
import com.beast.bkara.util.RatingBarView;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Darka on 4/16/2016.
 */
public class Record extends BaseObservable implements Parcelable {

    @SerializedName("recordId")
    private Long id;

    private int length;
    private int view;
    private Date date_created;
    private float rating;
    private Song song;
    private User user;
    private String stream_link;
    private Date lastTimeViewed = new Date(0);
    private ArrayList<RatingRecord> ratingRecords;

    public Record() {

    }

    public static Creator<Record> getCREATOR() {
        return CREATOR;
    }

    public Date getLastTimeViewed() {
        return lastTimeViewed;
    }

    public void setLastTimeViewed(Date lastTimeViewed) {
        this.lastTimeViewed = lastTimeViewed;
    }

    @BindingAdapter("app:setDate")
    public static void setDate(TextView view, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String strDate = sdf.format(date);
        view.setText(strDate);
    }

    @BindingAdapter("app:setView")
    public static void setView(TextView view, int recordView) {
        view.setText(String.valueOf(recordView) + " View(s)");
    }

    @BindingAdapter("app:setUsername")
    public static void setUsername(TextView view, User user) {
        view.setText(user.getUserName());
    }

    @BindingAdapter("app:setSongname")
    public static void setSongname(TextView view, Song song) {
        view.setText(song.getTitle());
    }

    @BindingAdapter("app:setRating")
    public static void setRating(RatingBarView view, float rating) {
        view.setStar(rating);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getStream_link() {
        return stream_link;
    }

    public void setStream_link(String stream_link) {
        this.stream_link = stream_link;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeInt(getLength());
        dest.writeInt(getView());
        dest.writeValue(getDate_created());
        dest.writeFloat(getRating());
        dest.writeString(getStream_link());
        dest.writeParcelable(getSong(), flags);
        dest.writeParcelable(getUser(), flags);
    }

    private Record(Parcel in) {
        setId(in.readLong());
        setLength(in.readInt());
        setView(in.readInt());
        setDate_created((Date) in.readValue(null));
        setRating(in.readFloat());
        setStream_link(in.readString());
        setSong((Song) in.readParcelable(Song.class.getClassLoader()));
        setUser((User) in.readParcelable(User.class.getClassLoader()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static final Parcelable.Creator<Record> CREATOR
            = new Parcelable.Creator<Record>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}
