package com.beast.bkara.model.supportmodel;

import com.beast.bkara.model.Song;
import com.beast.bkara.model.User;

/**
 * Created by Darka on 4/28/2016.
 */
public class RatingSong {

    private Long id;
    private Song song;
    private User user;
    private int rateValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getRateValue() {
        return rateValue;
    }

    public void setRateValue(int rateValue) {
        this.rateValue = rateValue;
    }
}
