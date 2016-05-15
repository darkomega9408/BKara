package com.beast.bkara.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.beast.bkara.model.supportmodel.RatingRecord;
import com.beast.bkara.model.supportmodel.RatingSong;

import java.util.ArrayList;

/**
 * Created by VINH on 4/22/2016.
 */
public class User implements Parcelable {

    private Long userId;
    private String userName;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String avatarLink;
    private String country;
    private String address;
    private ArrayList<Record> records;
    private ArrayList<RatingRecord> ratingRecords;
    private ArrayList<RatingSong> ratingSongs;

    public User() {

    }
    
    public User(User user){
        this.userId = user. userId;
        this.userName = user. userName;
        this.email = user. email;
        this.password = user. password;
        this.firstName = user. firstName;
        this.lastName = user. lastName;
        this.phoneNumber = user. phoneNumber;
        this.avatarLink = user. avatarLink;
        this.country = user. country;
        this.address = user. address;
        this.records = user. records;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getUserId());
        dest.writeString(getUserName());
        dest.writeString(getEmail());
        dest.writeString(getPassword());
        dest.writeString(getFirstName());
        dest.writeString(getLastName());
        dest.writeString(getPhoneNumber());
        dest.writeString(getAvatarLink());
        dest.writeString(getCountry());
        dest.writeString(getAddress());
        dest.writeList(getRecords());
    }

    private User(Parcel in) {
        setUserId(in.readLong());
        setUserName(in.readString());
        setEmail(in.readString());
        setPassword(in.readString());
        setFirstName(in.readString());
        setLastName(in.readString());
        setPhoneNumber(in.readString());
        setAvatarLink(in.readString());
        setCountry(in.readString());
        setAddress(in.readString());

        ArrayList<Record> listRecord = null;
        in.readList(listRecord,ArrayList.class.getClassLoader());
        setRecords(listRecord);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public ArrayList<RatingRecord> getRatingRecords() {
        return ratingRecords;
    }

    public void setRatingRecords(ArrayList<RatingRecord> ratingRecords) {
        this.ratingRecords = ratingRecords;
    }

    public ArrayList<RatingSong> getRatingSongs() {
        return ratingSongs;
    }

    public void setRatingSongs(ArrayList<RatingSong> ratingSongs) {
        this.ratingSongs = ratingSongs;
    }
}
