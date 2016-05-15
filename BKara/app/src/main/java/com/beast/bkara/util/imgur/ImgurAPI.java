package com.beast.bkara.util.imgur;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * Created by VINH on 4/21/2016.
 */
public interface ImgurAPI {
    String server = "https://api.imgur.com";

    @Multipart
    @POST("/3/image")
    Call<ImageResponse> postImage(
            @Header("Authorization") String auth,
            @Query("title") String title,
            @Query("description") String description,
            @Query("album") String albumId,
            @Query("account_url") String username,
            @Part("image") RequestBody file
    );
}
