package com.beast.bkara.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.widget.ProgressBar;

import com.beast.bkara.BR;
import com.beast.bkara.R;
import com.beast.bkara.model.Song;
import com.beast.bkara.util.bkararestful.BkaraService;

import java.util.ArrayList;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by Darka on 4/9/2016.
 */
public class SongViewModel {

    public ObservableList<Song> songList;
    public final ItemView songView = ItemView.of(BR.song, R.layout.song_item);

    private BkaraService bkaraService;

    public SongViewModel(ArrayList<Song> lstSongsHistory){
        songList = new ObservableArrayList<>();
        songList.addAll(lstSongsHistory);
    }

    public SongViewModel(BkaraService.WhichList whichList, ProgressBar progressBar) {
        bkaraService = BkaraService.getInstance();
        songList = new ObservableArrayList<>();
        bkaraService.GetSongList(whichList, songList, progressBar);
    }

    public SongViewModel(BkaraService.SongSearchFilter searchFilter, String searchValue , ProgressBar progressBar) {
        bkaraService = BkaraService.getInstance();
        songList = new ObservableArrayList<>();
        bkaraService.FindSongs(searchFilter, searchValue, songList, progressBar);
    }

}
