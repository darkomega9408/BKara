package com.beast.bkara.util.bkararestful;

import com.beast.bkara.model.Record;
import com.beast.bkara.model.Song;
import com.beast.bkara.model.User;
import com.beast.bkara.model.supportmodel.HotSong;
import com.beast.bkara.model.supportmodel.RatingRecord;
import com.beast.bkara.model.supportmodel.RatingSong;
import com.beast.bkara.model.supportmodel.UserGCM;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.POST;

/**
 * Created by Darka on 4/18/2016.
 */

// Interface for calling restful server
public interface BkaraRestfulApi {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("songlist/all")
    Call<List<Song>> getSongListAll();

    @GET("songlist/new")
    Call<List<Song>> getSongListNew();

    @GET("songlist/hot")
    Call<List<HotSong>> getSongListHot();

    @GET("songlist/search/songname/{songname}")
    Call<List<Song>> findSongsByName(@Path("songname") String songName);

    @GET("songlist/search/singername/{singername}")
    Call<List<Song>> findSongsBySingerName(@Path("singername") String singerName);

    @POST("saverecord")
    Call<Long> saveRecord(@Body Record record);

    @GET("recordlist/song/{songid}")
    Call<List<Record>> findRecordsBySongId(@Path("songid") Long songId);

    @GET("recordlist/user/{userid}")
    Call<List<Record>> findRecordsByUserId(@Path("userid") Long userId);

    @POST("rate/song")
    Call<Song> rateSong(@Body RatingSong ratingSong);

    @POST("rate/record")
    Call<Record> rateRecord(@Body RatingRecord ratingRecord);

    @POST("update/song")
    Call<Song> updateSong(@Body Song song);

    @POST("update/record")
    Call<Record> updateRecord(@Body Record record);

    @POST("update/user")
    Call<Void> updateUser(@Body User user);

    @POST("registerGCM")
    Call<UserGCM> registerGCM(@Body UserGCM userGCM);

    @POST("unregisterGCM")
    Call<UserGCM> unregisterGCM(@Body UserGCM userGCM);

    @GET("sendnoti/{senderId}/{receiverId}/{message}")
    Call<Void> sendNoti(@Path("senderId") Long senderId, @Path("receiverId") Long receiverId, @Path("message") String message);

    @POST("signUp")
    Call<User> signUp(@Body User user);

    @POST("login")
    Call<User> login(@Body User user);
}
