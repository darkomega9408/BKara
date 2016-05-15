package com.beast.bkara.model.supportmodel;

import com.beast.bkara.model.Song;

/**
 * Created by Darka on 5/3/2016.
 */
public class HotSong {

    private Long hotSong_id;
    private Song song;
    private String hotPoster;

    public Long getHotSong_id() {
        return hotSong_id;
    }

    public void setHotSong_id(Long hotSong_id) {
        this.hotSong_id = hotSong_id;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public String getHotPoster() {
        return hotPoster;
    }

    public void setHotPoster(String hotPoster) {
        this.hotPoster = hotPoster;
    }
}
