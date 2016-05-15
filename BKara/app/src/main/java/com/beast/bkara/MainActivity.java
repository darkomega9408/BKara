package com.beast.bkara;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beast.bkara.dialogfragments.AddPlaylistDialogFragment;
import com.beast.bkara.dialogfragments.LoginDialogFragment;
import com.beast.bkara.dialogfragments.PlaylistDialogFragment;
import com.beast.bkara.dialogfragments.RatingDialogFragment;
import com.beast.bkara.dialogfragments.SaveRecordDialogFragment;
import com.beast.bkara.dialogfragments.SignUpDialogFragment;
import com.beast.bkara.dialogfragments.UserInfoDialogFragment;
import com.beast.bkara.fragments.AboutFragment;
import com.beast.bkara.fragments.BlankFragment;
import com.beast.bkara.fragments.GenresFragment;
import com.beast.bkara.fragments.HistoryFragment;
import com.beast.bkara.fragments.HomeFragment;
import com.beast.bkara.fragments.KaraokeFragment;
import com.beast.bkara.fragments.PlaylistFragment;
import com.beast.bkara.fragments.PlaylistSongsFragment;
import com.beast.bkara.fragments.RecordsFragment;
import com.beast.bkara.fragments.SongListFragment;
import com.beast.bkara.fragments.SongsFragment;
import com.beast.bkara.model.Playlist;
import com.beast.bkara.model.Record;
import com.beast.bkara.model.Song;
import com.beast.bkara.model.User;
import com.beast.bkara.model.supportmodel.ListPlaylist;
import com.beast.bkara.model.supportmodel.ListRecordsHistory;
import com.beast.bkara.model.supportmodel.ListSongsHistory;
import com.beast.bkara.util.ComplexPreferences;
import com.beast.bkara.util.SongSearchView;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.util.gcm.RegistrationIntentService;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        BlankFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        SongsFragment.OnFragmentInteractionListener,
        SongListFragment.OnFragmentInteractionListener,
        GenresFragment.OnFragmentInteractionListener,
        KaraokeFragment.OnFragmentInteractionListener,
        RecordsFragment.OnFragmentInteractionListener,
        LoginDialogFragment.OnLoginDialogFragmentInteractionListener,
        SignUpDialogFragment.OnSignUpDialogFragmentInteractionListener,
        SaveRecordDialogFragment.OnFragmentInteractionListener,
        RatingDialogFragment.OnFragmentInteractionListener,
        UserInfoDialogFragment.OnFragmentInteractionListener,
        PlaylistDialogFragment.OnFragmentInteractionListener,
        PlaylistFragment.OnFragmentInteractionListener,
        PlaylistSongsFragment.OnFragmentInteractionListener,
        AddPlaylistDialogFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener
{

    private RelativeLayout mLayout;
    private LayoutInflater mLayoutInflater;
    private PopupWindow mPopupWindow;
    private DialogFragment loginFragment,signUpFragment;
    private Fragment currFragment;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Controller controller;

    /**
     * Use to store all listened songs recently
     */
    private ArrayList<Song> lstSongsHistory = new ArrayList<>();

    /**
     * Use to store all listened records recently
     */
    private ArrayList<Record> lstRecordsHistory = new ArrayList<>();

    /**
     * Use to store all playlist
     */
    private ArrayList<Playlist> lstPlaylist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "Ver 18");
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

        controller = (Controller) getApplicationContext();

        // Set up universal image loader
        ImageLoaderConfiguration imageLoaderConfig = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(imageLoaderConfig);

        // Get views
        mLayout = (RelativeLayout) findViewById(R.id.content_main_rl_layout);

        // Setup application action bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Init bkara service
        BkaraService.getInstance().setContext(this);

        // Get shared preference of @listSongsHistory && @lstRecordsHistory && @lstPlaylist
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getBaseContext(), Constants.MY_PREF, MODE_PRIVATE);
        ListSongsHistory listSongsHistory =  complexPreferences.getObject(Constants.MY_PREF_SONGS_HISTORY, ListSongsHistory.class);
        if( listSongsHistory != null )
            lstSongsHistory = listSongsHistory.getLstSongsHistory();

        ListRecordsHistory listRecordsHistory = complexPreferences.getObject(Constants.MY_PREF_RECORDS_HISTORY, ListRecordsHistory.class);
        if( listRecordsHistory != null )
            lstRecordsHistory = listRecordsHistory.getLstRecordsHistory();

        ListPlaylist listPlaylist = complexPreferences.getObject(Constants.MY_PREF_PLAYLIST, ListPlaylist.class);
        if( listPlaylist != null )
            lstPlaylist = listPlaylist.getListPlaylist();


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    // Setup navigation drawer
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

        // Set the default view to Home item
        displayView(R.id.nav_home);

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        CircularImageView avatar = (CircularImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( controller.isLogin() )
                    UserInfoDialogFragment.newInstance(null, null).show(getSupportFragmentManager(), "User Info");
            }
        });

        autoLoginIfRemembered();
    }

    /**
     * Auto login if user has checked remember me
     */
    private void autoLoginIfRemembered() {
        SharedPreferences pref = getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        if( pref.getBoolean(Constants.MY_PREF_REMEMBER_ME, false)  ) {
            User user = new User();
            user.setUserName(pref.getString(Constants.MY_PREF_USER_NAME, ""));
            user.setPassword(pref.getString(Constants.MY_PREF_PASSWORD,""));
            BkaraService.getInstance().login(user, new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if(  response.isSuccessful() && response.body() != null) {
                        Toast.makeText( MainActivity.this , "Welcome "+ response.body().getUserName(), Toast.LENGTH_LONG).show();
                        setUserInfoAfterLoginSuccessfully(response.body());
                    }
                    else Toast.makeText(MainActivity.this, R.string.user_not_existed_error, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(MainActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * On pause -> save preference : @lstSongsHistory && @lstRecordsHistory
     */
    @Override
    protected void onPause() {
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getBaseContext(), Constants.MY_PREF, MODE_PRIVATE);

        if( lstSongsHistory.size() > 0 ) {
            ListSongsHistory listSongsHistory = new ListSongsHistory();
            listSongsHistory.setLstSongsHistory(lstSongsHistory);
            complexPreferences.putObject(Constants.MY_PREF_SONGS_HISTORY, listSongsHistory);
        }

        if( lstRecordsHistory.size() > 0  ){
            ListRecordsHistory listRecordsHistory = new ListRecordsHistory();
            listRecordsHistory.setLstRecordsHistory(lstRecordsHistory);
            complexPreferences.putObject(Constants.MY_PREF_RECORDS_HISTORY, listRecordsHistory);
        }

        if( lstSongsHistory.size() > 0 ) {
            ListPlaylist listPlaylist = new ListPlaylist();
            listPlaylist.setLstPlaylist(lstPlaylist);
            complexPreferences.putObject(Constants.MY_PREF_PLAYLIST, listPlaylist);
        }

        complexPreferences.commit();

        super.onPause();
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            // Double back pressed
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.press_twice_exit_app_msg, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Handle SearchView behaviour
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SongSearchView searchView = (SongSearchView) MenuItemCompat.getActionView(searchItem);

        final MenuItem filterBySongName = menu.findItem(R.id.filter_by_song_name);
        final MenuItem filterBySingerName = menu.findItem(R.id.filter_by_singer_name);

        searchView.setIconifiedByDefault(true);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.findItem(R.id.action_filter).setVisible(true);
            }
        });

        searchView.setOnSearchViewCollapsedEventListener(new SongSearchView.OnSearchViewCollapsedEventListener() {
            @Override
            public void onSearchViewCollapsed() {
                menu.findItem(R.id.action_filter).setVisible(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599

                if (filterBySongName.isChecked()) {
                    Log.d("BKARA", "SEARCH BY SONG NAME");
                    displayCustomFragment(SongListFragment.newInstance(BkaraService.SongSearchFilter.SONG_NAME, query), "SEARCH SONG");
                } else {
                    Log.d("BKARA", "SEARCH BY SINGER NAME");
                    displayCustomFragment(SongListFragment.newInstance(BkaraService.SongSearchFilter.SINGER_NAME, query), "SEARCH SONG");
                }

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.filter_by_song_name:
                item.setChecked(true);
                return true;
            case R.id.filter_by_singer_name:
                item.setChecked(true);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displayView(item.getItemId());
        return true;
    }

    /**
     * Display view based on selected navigation item
     * @param viewId selected navigation item's id
     */
    private void displayView(int viewId) {
        Fragment fragment = new BlankFragment();
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                title = "BKara";
                break;
            case R.id.nav_songs:
                fragment = new SongsFragment();
                title = "Songs";
                break;
            case R.id.nav_genre:
                fragment = new GenresFragment();
                title = "Genres";
                break;
            case R.id.nav_playlist:
                fragment = new PlaylistFragment();
                title = "Playlist";
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                title = "History";
                break;
            case R.id.nav_records:
                fragment = RecordsFragment.newInstance(false, null);
                title = "Records";
                break;
            case R.id.nav_setting:
                break;
            case R.id.nav_about:
                fragment = new AboutFragment();
                title = "About";
                break;
        }

        // Change fragment
        if (fragment != null) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, title);
            ft.commit();
        }

        // Set page title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        // Close navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void displayCustomFragment(Fragment fragment, String title) {
        // Change fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, title).addToBackStack(title);
            ft.commit();
        }

        // Set page title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }


    }

    private void displayPopup(int layoutId) {
        // Inflate the layout
        mLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) mLayoutInflater.inflate(layoutId, null);

        // Get screen width and height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        // Create popup window
        mPopupWindow = new PopupWindow(container, (int) (height * 0.7), (int) (width * 0.8), true);
        mPopupWindow.showAtLocation(mLayout, Gravity.CENTER, 0, 0);
    }

    public void fragHomeNewSongClick(View v) {
        displayCustomFragment(SongsFragment.newInstance(SongsFragment.NEW_TAB_POSITION), "Songs");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onUpdateUserInfoSuccessfully(User user) {
        setUserInfoAfterLoginSuccessfully(user);
    }

    /**
     * Add listened song to history list to the top of the list and remove the old one if any
     * @param song to be added
     */
    public void addToHistory(Song song) {

        // Loop through the song list to find the matched one if any then put it into the top of the history list
        Iterator<Song> iterator = lstSongsHistory.iterator();
        while( iterator.hasNext() ){
            Song s = iterator.next();
            if( s.getSong_id().equals(song.getSong_id()) ) {
                iterator.remove();
                break;
            }
        }

        // Change position to top list
        lstSongsHistory.add(0,song);
    }

    /**
     * Add listened record to history list to the top of the list and remove the old one if any
     * @param record to be added
     */
    public void addToHistory(Record record) {
        // Loop through the song list to find the matched one if any then put it into the top of the history list
        Iterator<Record> iterator = lstRecordsHistory.iterator();
        while( iterator.hasNext() ){
            Record r = iterator.next();
            if( r.getId().equals(record.getId()) ) {
                iterator.remove();
                break;
            }
        }

        // Change position to top list
        lstRecordsHistory.add(0,record);
    }

    /**
     *
     * @param playlist
     */
    public void addPlaylist(Playlist playlist) {
        // Loop through the song list to find the matched one if any then put it into the top of the history list
        Iterator<Playlist> iterator = lstPlaylist.iterator();
        while( iterator.hasNext() ){
            Playlist r = iterator.next();
            if( r.getName().equals(playlist.getName()) ) {
                iterator.remove();
                break;
            }
        }

        // Add to list playlist
        lstPlaylist.add(playlist);
    }

    /**
     *
     * @return
     */
    public ArrayList<Playlist> getPlaylist() {
        return lstPlaylist;
    }

    /**
     * Check if a song is viewed one day after now then add 1 'view' to song.getView()
     * @param song
     * @return
     */
    public boolean checkViewed(Song song) {
        if (song.getLastTimeViewed() == null || new Date().getTime() - song.getLastTimeViewed().getTime() >= Constants.TIME_BETWEEN_VIEWS_COUNT)
                return true;
        return false;
    }

    /**
     * Check if a record is viewed one day after now then add 1 'view' to record.getView()
     * @param record
     * @return
     */
    public boolean checkViewed(Record record) {
            if (record.getLastTimeViewed() == null || new Date().getTime() - record.getLastTimeViewed().getTime() >= Constants.TIME_BETWEEN_VIEWS_COUNT)
                return true;
        return false;
    }

    public ArrayList<Song> getLstSongsHistory() {
        return lstSongsHistory;
    }


    public ArrayList<Record> getLstRecordsHistory() {
        return lstRecordsHistory;
    }


    public void login(View v) {
        loginFragment = LoginDialogFragment.newInstance(null, null);
        loginFragment.show(getSupportFragmentManager(),"LOGIN");
    }

    @Override
    public void onLoginDialogFragmentInteraction(Uri uri) {

    }

    @Override
    public void onOpenSignUpForm() {
        signUpFragment = SignUpDialogFragment.newInstance(null, null);
        signUpFragment.show(getSupportFragmentManager(),"SIGN UP");
    }


    @Override
    public void onLoginSuccessfully(User user, boolean isRemembered) {

        if( loginFragment != null && loginFragment.getDialog() != null )
            loginFragment.dismiss();
        else {
            loginFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("LOGIN");
            loginFragment.getDialog().dismiss();
        }

        setUserInfoAfterLoginSuccessfully(user);
        saveUserToSharedPreference(user,isRemembered);
    }


    @Override
    public void onSignUpSuccessfully(User user) {
        if( loginFragment != null && loginFragment.getDialog() != null )
            loginFragment.dismiss();

        if( signUpFragment != null && signUpFragment.getDialog() != null )
            signUpFragment.dismiss();

        setUserInfoAfterLoginSuccessfully(user);
    }


    private void saveUserToSharedPreference(User user, boolean isRemembered) {

        SharedPreferences pref = getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(Constants.MY_PREF_REMEMBER_ME, isRemembered);
        if( isRemembered ) {
            edit.putString(Constants.MY_PREF_PASSWORD, user.getPassword());
            edit.putString(Constants.MY_PREF_USER_NAME, user.getUserName());
        }
        edit.commit();
    }

    private void setUserInfoAfterLoginSuccessfully(User user) {
        final TextView welcome, login , logout;

        welcome = (TextView) findViewById(R.id.nav_header_tv_welcome);
        login = (TextView) findViewById(R.id.nav_header_tv_login);
        logout = (TextView) findViewById(R.id.nav_header_tv_logout);

        if ( welcome == null || login == null || logout == null)
            return;

        login.setVisibility(View.GONE);

        // it also means user has logon -> isLogin() returns true
        controller.setCurrUser(user);

        welcome.setText(user.getUserName());
        welcome.setVisibility(View.VISIBLE);

        logout.setVisibility(View.VISIBLE);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();

            }
        });

        if( user.getAvatarLink() != null ) {
            CircularImageView avatar = (CircularImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
            ImageLoader.getInstance().displayImage(user.getAvatarLink(), avatar);
        }

        if ( (currFragment = getSupportFragmentManager().findFragmentByTag("Karaoke")) != null)
            ((KaraokeFragment) currFragment).enableRecord(true);
        else if ( (currFragment = getSupportFragmentManager().findFragmentByTag("Records")) != null)
            displayView(R.id.nav_records);

        RegistrationIntentService.GetInstance().registerAtServer(controller);
    }

    private void logout() {
        // isLogin() returns false
        controller.setCurrUser(null);

        saveUserToSharedPreference(null,false);

        findViewById(R.id.nav_header_tv_welcome).setVisibility(View.GONE);
        findViewById(R.id.nav_header_tv_login).setVisibility(View.VISIBLE);
        findViewById(R.id.nav_header_tv_logout).setVisibility(View.GONE);
        CircularImageView avatar = (CircularImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
        avatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_account));

        if ( (currFragment = getSupportFragmentManager().findFragmentByTag("Karaoke")) != null)
            ((KaraokeFragment) currFragment).enableRecord(false);
        else if ( (currFragment = getSupportFragmentManager().findFragmentByTag("Records")) != null)
            displayView(R.id.nav_records);

        RegistrationIntentService.GetInstance().unregisterAtServer();
    }

    public void showToolbar(boolean isShow) {
        if (isShow)
            toolbar.setVisibility(View.VISIBLE);
        else
            toolbar.setVisibility(View.GONE);
    }
}
