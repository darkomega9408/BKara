package com.beast.bkara;

import android.app.Application;

import com.beast.bkara.model.User;

/**
 * Created by Darka on 4/7/2016.
 */
public class Controller extends Application {

    public static final String YOUTUBE_API_KEY = "AIzaSyDyyVofQ_tgdvQEh30ikZ7LipiQbWeLA1g";


    private User currUser = null;

    private boolean _loginStatus = false;



    public Controller() {

    }

    public User getCurrUser() {
        return currUser;
    }

    public void setCurrUser(User currUser) {
        this.currUser = currUser;

        if( currUser == null )
            setLoginStatus(false);
        else setLoginStatus(true);
    }

    public boolean isLogin() {
        return _loginStatus;
    }

    private void setLoginStatus(boolean loginStatus) {
        this._loginStatus = loginStatus;
    }
}
