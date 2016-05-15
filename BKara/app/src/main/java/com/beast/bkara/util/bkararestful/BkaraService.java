package com.beast.bkara.util.bkararestful;

import android.content.Context;
import android.databinding.ObservableList;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beast.bkara.Controller;
import com.beast.bkara.MainActivity;
import com.beast.bkara.R;
import com.beast.bkara.model.Record;
import com.beast.bkara.model.Song;
import com.beast.bkara.model.User;
import com.beast.bkara.model.supportmodel.HotSong;
import com.beast.bkara.model.supportmodel.RatingRecord;
import com.beast.bkara.model.supportmodel.RatingSong;
import com.beast.bkara.model.supportmodel.UserGCM;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Darka on 4/22/2016.
 */
public class BkaraService {

    private static BkaraService ourInstance = new BkaraService();

    public static BkaraService getInstance() {
        return ourInstance;
    }

    private final String RESTFUL_URL = "http://restfulservice-bkara.rhcloud.com/bkaraservice/";
            //"http://192.168.1.108:8084/myteam/bkaraservice/";
            //"http://192.168.0.103:8080/myteam/bkaraservice/";
            //"https://bkararestfulservice.herokuapp.com/bkaraservice/";
    private BkaraRestfulApi bkaraRestful;

    public enum HistoryList{
        SONG, RECORD
    }

    public enum WhichList {
        ALL, HOT, NEW, SEARCH
    }

    public enum SongSearchFilter {
        SONG_NAME, SINGER_NAME
    }

    public enum RecordSearchFilter {
        SONG_ID, USER_ID
    }

    private void SetupRestfulService() {
        // Restful server return error if keep using dd-MM-yyyy
        // Can not parse date "24-04-2016:00:59:319": not compatible with any of standard forms
        // ("yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd"))
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDateDeserializer())
                .setDateFormat(/*"dd-MM-yyyy:HH:mm:SS"*/"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RESTFUL_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        bkaraRestful = retrofit.create(BkaraRestfulApi.class);
    }

    private MainActivity context;
    public void setContext(MainActivity context){
        this.context = context;
    }

    public void GetSongList(final WhichList whichList, final ObservableList<Song> songList, final ProgressBar progressBar) {
        Call<List<Song>> call = null;
        Call<List<HotSong>> callHotSong = null;

        if (whichList == WhichList.ALL)
            call = bkaraRestful.getSongListAll();
        else if (whichList == WhichList.NEW)
            call = bkaraRestful.getSongListNew();
        else
            callHotSong = bkaraRestful.getSongListHot();

        if (call != null) {
            call.enqueue(new Callback<List<Song>>() {
                @Override
                public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                    Log.d("RESTFUL CALL", "SUCCESS");
                    songList.clear();
                    songList.addAll(response.body());

                    ArrayList<Song> lstSongsHistory =  context.getLstSongsHistory();
                    if( lstSongsHistory != null && lstSongsHistory.size() > 0 && songList != null && songList.size() > 0)
                        for (Song song : songList) {
                            for (Song s: lstSongsHistory ) {
                                if( song.getSong_id().equals(s.getSong_id()) ) {
                                    song.setLastTimeViewed(s.getLastTimeViewed());
                                    s.setView(song.getView());
                                }
                            }
                        }

                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<List<Song>> call, Throwable t) {
                    Log.d("RESTFUL CALL", "FAILED " + t.getMessage());
                    GetSongList(whichList, songList, progressBar);
                }
            });
        }

        if (callHotSong != null) {
            callHotSong.enqueue(new Callback<List<HotSong>>() {
                @Override
                public void onResponse(Call<List<HotSong>> call, Response<List<HotSong>> response) {
                    Log.d("RESTFUL CALL", "SUCCESS");
                    songList.clear();
                    for (int i = 0; i < response.body().size(); i++)
                        songList.add(response.body().get(i).getSong());

                    ArrayList<Song> lstSongsHistory =  context.getLstSongsHistory();
                    if( lstSongsHistory != null && lstSongsHistory.size() > 0 && songList != null && songList.size() > 0)
                        for (Song song : songList) {
                            for (Song s: lstSongsHistory ) {
                                if( song.getSong_id().equals(s.getSong_id()) ) {
                                    song.setLastTimeViewed(s.getLastTimeViewed());
                                    s.setView(song.getView());
                                }
                            }
                        }

                    if (progressBar != null)
                        progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<List<HotSong>> call, Throwable t) {

                }
            });
        }

    }

    public void GetSongList(final WhichList whichList, Callback<List<Song>> callback) {
        Call<List<Song>> call;

        switch(whichList) {
            case NEW:
                call = bkaraRestful.getSongListNew();
                break;
            default:
                call = bkaraRestful.getSongListAll();
                break;
        }

        call.enqueue(callback);
    }

    public void GetSongListHot(Callback<List<HotSong>> callback) {
        Call<List<HotSong>> call = bkaraRestful.getSongListHot();
        call.enqueue(callback);
    }

    public void FindSongs(final SongSearchFilter searchFilter, final String searchValue, final ObservableList<Song> songList, final ProgressBar progressBar) {
        Call<List<Song>> call;

        switch(searchFilter) {
            case SINGER_NAME:
                call = bkaraRestful.findSongsBySingerName(searchValue);
                break;
            default:
                call = bkaraRestful.findSongsByName(searchValue);
                break;
        }

        call.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                Log.d("RESTFUL CALL", "SUCCESS " + response.toString());
                songList.clear();
                songList.addAll(response.body());

                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.d("RESTFUL CALL", "FAILURE " + t.getMessage());
                FindSongs(searchFilter, searchValue, songList, progressBar);
            }
        });
    }

    public void SaveRecord(final Record record) {
        Call<Long> call = bkaraRestful.saveRecord(record);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                Log.d("RESTFUL CALL", "SUCCESS SAVE RECORD " + response.body());
                record.setId( (response.body()) );
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.d("RESTFUL CALL", "FAIL SAVE RECORD " + t.getMessage());
            }
        });
    }

    public void SaveRecord(final Record record, Callback<Long> mCallback) {
        Call<Long> call = bkaraRestful.saveRecord(record);
        call.enqueue(mCallback);
    }

    public void FindRecords(final RecordSearchFilter searchFilter, final Long searchValue, final ObservableList<Record> recordList, final ProgressBar progressBar) {
        Call<List<Record>> call;

        switch (searchFilter) {
            case SONG_ID:
                call = bkaraRestful.findRecordsBySongId(searchValue);
                break;
            default:
                call = bkaraRestful.findRecordsByUserId(searchValue);
                break;
        }

        call.enqueue(new Callback<List<Record>>() {
            @Override
            public void onResponse(Call<List<Record>> call, Response<List<Record>> response) {
                Log.d("RESTFUL CALL", "SUCCESS");
                recordList.clear();
                recordList.addAll(response.body());

                ArrayList<Record> lstRecordsHistory =  context.getLstRecordsHistory();
                if( lstRecordsHistory != null && lstRecordsHistory.size() > 0 && recordList != null && recordList.size() > 0)
                    for (Record record : recordList) {
                        for (Record r: lstRecordsHistory ) {
                            if( record.getId().equals(r.getId()) ) {
                                record.setLastTimeViewed(r.getLastTimeViewed());
                                r.setView(record.getView());
                            }
                        }
                    }

                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Record>> call, Throwable t) {
                Log.d("RESTFUL CALL", "FAILED " + t.getMessage());
                FindRecords(searchFilter, searchValue, recordList, progressBar);
            }
        });
    }

    public void RateSong(RatingSong ratingSong) {
        Call<Song> call = bkaraRestful.rateSong(ratingSong);
        call.enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {
                Log.d("RESTFUL CALL", "SUCCESS RATING SONG " + response.body());
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {
                Log.d("RESTFUL CALL", "FAIL RATING SONG " + t.getMessage());
            }
        });
    }

    public void RateRecord(RatingRecord ratingRecord) {
        Call<Record> call = bkaraRestful.rateRecord(ratingRecord);
        call.enqueue(new Callback<Record>() {
            @Override
            public void onResponse(Call<Record> call, Response<Record> response) {
                Log.d("RESTFUL CALL", "SUCCESS RATING RECORD " + response.body());
            }

            @Override
            public void onFailure(Call<Record> call, Throwable t) {
                Log.d("RESTFUL CALL", "FAILED RATING RECORD " + t.getMessage());
            }
        });
    }

    public void UpdateSong(Song song) {
        Call<Song> call = bkaraRestful.updateSong(song);
        call.enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Call<Song> call, Response<Song> response) {

                Log.d("RESTFUL CALL", "SUCCESS UPDATE SONG " + response.body());
            }

            @Override
            public void onFailure(Call<Song> call, Throwable t) {
                Log.d("RESTFUL CALL", "FAILED UPDATE SONG " + t.getMessage());
            }
        });
    }

    public void UpdateRecord(Record record) {
        Call<Record> call = bkaraRestful.
                updateRecord(record);
        call.enqueue(new Callback<Record>() {
            @Override
            public void onResponse(Call<Record> call, Response<Record> response) {
                Log.d("RESTFUL CALL", "SUCCESS UPDATE RECORD " + response.body());
            }

            @Override
            public void onFailure(Call<Record> call, Throwable t) {
                Log.d("RESTFUL CALL", "FAILED UPDATE RECORD " + t.getMessage());
            }
        });
    }

    public void RegisterGCM(UserGCM userGCM) {
        Call<UserGCM> call = bkaraRestful.registerGCM(userGCM);
        call.enqueue(new Callback<UserGCM>() {
            @Override
            public void onResponse(Call<UserGCM> call, Response<UserGCM> response) {
            }

            @Override
            public void onFailure(Call<UserGCM> call, Throwable t) {
            }
        });
    }

    public void UnregisterGCM(UserGCM userGCM) {
        Call<UserGCM> call = bkaraRestful.unregisterGCM(userGCM);
        call.enqueue(new Callback<UserGCM>() {
            @Override
            public void onResponse(Call<UserGCM> call, Response<UserGCM> response) {
            }

            @Override
            public void onFailure(Call<UserGCM> call, Throwable t) {
            }
        });
    }

    public void SendNoti(Long senderId, Long receiverId, String message) {
        Call<Void> call = bkaraRestful.sendNoti(senderId, receiverId, message);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("RESTFUL CALL", "SEND NOTI SUCCESFULLY");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("RESTFUL CALL", "SEND NOTI UNSUCCESSFULLY");
            }
        });
    }

    public void UpdateUser(final User user, Callback<Void> cb){
        bkaraRestful.updateUser(user).enqueue(cb);
    }

    public void login(User user, Callback<User> cb){
        bkaraRestful.login(user).enqueue(cb);
    }

    public void signUp(User user, Callback<User> cb) {
        bkaraRestful.signUp(user).enqueue(cb);
    }

    private BkaraService() {
        SetupRestfulService();
    }

    public class JsonDateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String s = json.getAsJsonPrimitive().getAsString();
            Date d = new Date(Long.parseLong(s));
            return d;
        }
    }
}
