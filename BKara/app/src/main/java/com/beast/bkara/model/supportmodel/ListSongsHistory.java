package com.beast.bkara.model.supportmodel;

import com.beast.bkara.model.Song;

import java.util.ArrayList;

/**
 * Created by VINH on 5/1/2016.
 */
public class ListSongsHistory {
    public ArrayList<Song> getLstSongsHistory() {
        return lstSongsHistory;
    }

    public void setLstSongsHistory(ArrayList<Song> lstSongsHistory) {
        this.lstSongsHistory = lstSongsHistory;
    }

    private ArrayList<Song> lstSongsHistory;

}
