package com.beast.bkara.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.beast.bkara.BR;
import com.beast.bkara.R;
import com.beast.bkara.model.Genre;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by Darka on 4/11/2016.
 */
public class GenreViewModel {

    public ObservableList<Genre> genreList;
    public final ItemView genreView = ItemView.of(BR.genre, R.layout.genre_item);

    public GenreViewModel() {
        InitDummyData();
    }

    private void InitDummyData() {
        genreList = new ObservableArrayList<>();

        Genre nhacTreGenre = new Genre();
        nhacTreGenre.setTitle("Nhac tre");
        genreList.add(nhacTreGenre);

        Genre nhacVangGenre = new Genre();
        nhacVangGenre.setTitle("Nhac vang");
        genreList.add(nhacVangGenre);

        Genre nhacUSUKGenre = new Genre();
        nhacUSUKGenre.setTitle("Nhac US/UK");
        genreList.add(nhacUSUKGenre);

        Genre nhacCachMang = new Genre();
        nhacCachMang.setTitle("Nhac cach mang");
        genreList.add(nhacCachMang);
    }

}
