package com.beast.bkara.util;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Params;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Darka on 4/24/2016.
 */
public class UploadToSoundCloudTask extends AsyncTask<Void, Void, String> {

    ApiWrapper wrapper;
    Token token;
    public final static String CLIENT_ID = "cab81827c76e36c852118774d8eef584";
    private final static String CLIENT_SECRET = "78b2abd83b532632c7d226be11b79602";
    private final static String USERNAME = "aeon19944";
    private final static String PASSWORD = "bkaraservice";

    public interface OnUploadDoneInterface {
        public void OnUploadSuccess(String streamLink);
        public void OnUploadFailed();
    }

    private OnUploadDoneInterface onUploadDoneInterface;
    private String username;
    private String songname;
    private String path;

    public UploadToSoundCloudTask(String path, String username, String songname, OnUploadDoneInterface onUploadDoneInterface) {
        this.onUploadDoneInterface = onUploadDoneInterface;
        this.username = username;
        this.songname = songname;
        this.path = path;
    }


    @Override
    protected String doInBackground(Void... params) {
        // TODO Auto-generated method stub
        try {
            wrapper = new ApiWrapper(CLIENT_ID,
                    CLIENT_SECRET,
                    null,
                    null);
            token = wrapper.login(USERNAME, PASSWORD);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return upload();
    }

    @Override
    protected void onPostExecute(String streamLink) {
        super.onPostExecute(streamLink);
        if (streamLink != null)
            onUploadDoneInterface.OnUploadSuccess(streamLink);
        else
            onUploadDoneInterface.OnUploadFailed();
    }

    public String upload()
    {
        try {
            Log.d("DDDDD", "uploading in background...");
            File audioFile = new File(path);
            // replace the hardcoded path with the path of your audio file
            audioFile.setReadable(true, false);
            HttpResponse resp = wrapper.post(Request.to(Endpoints.TRACKS)
                    .add(Params.Track.TITLE, songname + "_" + username + "_" + System.currentTimeMillis())
                    .add(Params.Track.TAG_LIST, "Bkara Record")
                    .withFile(Params.Track.ASSET_DATA, audioFile));

            String streamLink = "";
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(resp.getEntity().getContent()), 65728);
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                Log.d("DDDDD", "Response: " + sb.toString());

                JSONObject respond = new JSONObject(sb.toString());
                return streamLink = respond.getString("stream_url");
            }
            catch (IOException e) { e.printStackTrace(); }
            catch (Exception e) { e.printStackTrace(); }


            Log.d("DDDDD", "background thread done...");

        } catch (IOException exp) {
            Log.d("DDDDD",
                    "Error uploading audioclip: IOException: "
                            + exp.toString());

            onUploadDoneInterface.OnUploadFailed();

        }
        return null;
    }

}
