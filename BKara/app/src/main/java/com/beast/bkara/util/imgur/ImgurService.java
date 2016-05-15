package com.beast.bkara.util.imgur;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by VINH on 4/22/2016.
 */
public class ImgurService {
    private static final String MY_IMGUR_CLIENT_ID = "7ef92c3fd3ff48d";
    private static final String MY_IMGUR_CLIENT_SECRET = "34548d97461b899d6b505ad41ed56f7d17f09259";

    private static ImgurAPI imgurAPI = null;
    private static ImgurService instance = null;

    private ImgurService() {

    }

    private static void init(){
        instance = new ImgurService();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ImgurAPI.server)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        imgurAPI = retrofit.create(ImgurAPI.class);
    }

    /*
        Client Auth
    */
    private String getClientAuth() {
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }

    public static ImgurService getInstance() {
        if( instance == null )
            init();
        return instance;
    }

    public void uploadFileToImgur(ImageUpload imgUpload , Callback<ImageResponse> cb){

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/*"), imgUpload.getImage());


        // finally, execute the request
        Call<ImageResponse> call = imgurAPI.postImage( getClientAuth(), imgUpload.getTitle(), imgUpload.getDescription() ,
                imgUpload.getAlbumId(), null, requestFile);
        call.enqueue(cb);
    }
}
