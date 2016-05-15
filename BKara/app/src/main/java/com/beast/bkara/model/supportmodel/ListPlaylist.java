package com.beast.bkara.model.supportmodel;

import com.beast.bkara.model.Playlist;

import java.util.ArrayList;

/**
 * Created by dotha on 5/7/2016.
 */
public class ListPlaylist {
    public ArrayList<Playlist> getListPlaylist() {
        return lstPlaylist;
    }

    public void setLstPlaylist(ArrayList<Playlist> lstPlaylist) {
        this.lstPlaylist = lstPlaylist;
    }

    private ArrayList<Playlist> lstPlaylist;
}

