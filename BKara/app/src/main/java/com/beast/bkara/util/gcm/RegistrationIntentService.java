package com.beast.bkara.util.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.beast.bkara.Controller;
import com.beast.bkara.R;
import com.beast.bkara.model.supportmodel.UserGCM;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Darka on 5/1/2016.
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_TOKEN = "gcmToken";

    public static RegistrationIntentService serviceInstance;
    private String registerId;
    private BkaraService bkaraService;
    private Long lastUserId;

    public RegistrationIntentService() {
        super(TAG);
        bkaraService = BkaraService.getInstance();
    }

    public static RegistrationIntentService GetInstance() {
        return serviceInstance;
    }

    public String getRegisterId() {
        return registerId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceInstance = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = getResources().getString(R.string.gcm_senderId);
        try {
            // request token that will be used by the server to send push notifications
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            registerId = token;
            Log.d(TAG, "GCM Registration Token: " + token);

            sharedPreferences.edit().putString(GCM_TOKEN, token).apply();

            // pass along this data
            //sendRegistrationToServer(token);
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();

            e.printStackTrace();
        }
    }

    public void registerAtServer(Controller controller) {
        // send network request
        if (controller != null && controller.isLogin()) {

            lastUserId = controller.getCurrUser().getUserId();
            UserGCM userGCM = new UserGCM();
            userGCM.setUserId(lastUserId);
            userGCM.setRegisterId(registerId);
            bkaraService.RegisterGCM(userGCM);

            // if registration sent was successful, store a boolean that indicates whether the generated token has been sent to server
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
        }
    }

    public void unregisterAtServer() {
        UserGCM userGCM = new UserGCM();
        userGCM.setUserId(lastUserId);
        bkaraService.UnregisterGCM(userGCM);
    }
}
