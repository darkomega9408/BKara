package com.beast.bkara.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.beast.bkara.BR;
import com.beast.bkara.R;
import com.beast.bkara.model.Playlist;
import com.beast.bkara.model.Song;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by dotha on 5/7/2016.
 */
public class PlaylistViewModel {

    public ObservableList<Playlist> playlists;
    public final ItemView playlistView = ItemView.of(BR.playlist, R.layout.playlist_item);

    public PlaylistViewModel(List<Playlist> lstPlaylist){
        playlists = new ObservableArrayList<>();
        playlists.addAll(lstPlaylist);
    }

    public Playlist getPlaylistAt(int index) {
        return this.playlists.get(index);
    }
}
