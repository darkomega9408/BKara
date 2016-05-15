package com.beast.bkara.fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.beast.bkara.Controller;
import com.beast.bkara.MainActivity;
import com.beast.bkara.dialogfragments.PlaylistDialogFragment;
import com.beast.bkara.dialogfragments.RatingDialogFragment;
import com.beast.bkara.dialogfragments.SaveRecordDialogFragment;
import com.beast.bkara.util.RecordListRecyclerViewAdapter;
import com.beast.bkara.R;
import com.beast.bkara.databinding.FragmentKaraokeBinding;
import com.beast.bkara.model.Song;
import com.beast.bkara.viewmodel.RecordViewModel;
import com.beast.bkara.viewmodel.SongViewModel;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.hedgehog.ratingbar.RatingBar;

import java.io.File;
import java.io.IOException;

import me.tatarka.bindingcollectionadapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter.ItemViewArg;
import me.tatarka.bindingcollectionadapter.factories.BindingRecyclerViewAdapterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KaraokeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KaraokeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KaraokeFragment extends Fragment {

    private ToggleButton btnRecord;
    private ToggleButton btnExpand;
    private RatingBar ratingBar;
    private ExpandableRelativeLayout erlSongInfo;
    private TextView tvAddPlaylist;

    private String recordPath = "";

    private boolean isFragmentPause = false;

    private FragmentKaraokeBinding binding;

    // Controller
    private Controller controller;

    // Media Recorder
    private MediaRecorder mRecorder = null;

    // Youtube Player
    private YouTubePlayer yPlayer;

    // Bingding adapter for record list recyclerview - used for handle playing multiple records
    public BindingRecyclerViewAdapterFactory mFactory = new BindingRecyclerViewAdapterFactory() {
        @Override
        public <T> BindingRecyclerViewAdapter<T> create(RecyclerView recyclerView, ItemViewArg<T> arg) {
            return new RecordListRecyclerViewAdapter<>(arg, getActivity(), true);
        }
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String WHICH_SONG = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Song whichSong;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public KaraokeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param whichSong Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KaraokeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KaraokeFragment newInstance(Song whichSong, String param2) {
        KaraokeFragment fragment = new KaraokeFragment();
        Bundle args = new Bundle();
        args.putParcelable(WHICH_SONG, whichSong);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            whichSong = (Song) getArguments().getParcelable(WHICH_SONG);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        controller = (Controller) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_karaoke, container, false);
        View v = binding.getRoot();
        ratingBar = (RatingBar) v.findViewById(R.id.frag_karaoke_ratingbar);
        tvAddPlaylist = (TextView) v.findViewById(R.id.frag_karaoke_btnAddToPlaylist);

        binding.setSong(whichSong);
        binding.setKaraokeFrag(this);

        final YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.frag_karaoke_youtubeViewer, youTubePlayerFragment);
        transaction.replace(R.id.frag_karaoke_frame_recordlist, RecordsFragment.newInstance(true, whichSong));
        transaction.commit();

        controller = (Controller) getActivity().getApplicationContext();
        youTubePlayerFragment.initialize(controller.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
                Log.d("KARAOKEFRAGMENT", "Youtube Initialization");
                yPlayer = youTubePlayer;
                if (!wasRestored) {
                    youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean isFullScreen) {
                            ((MainActivity) getActivity()).showToolbar(!isFullScreen);
                        }
                    });
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    youTubePlayer.loadVideo(whichSong.getVideo_id());
                    youTubePlayer.play();

                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                String errorMessage = error.toString();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.d("errorMessage: ", errorMessage);
            }

        });

        btnRecord = (ToggleButton) v.findViewById(R.id.frag_karaoke_btnRecord);

        if (controller.isLogin()) {
            btnRecord.setEnabled(true);
        }

        btnRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    recordPath = GenerateRecordPath();
                    startRecording(recordPath);
                }
                else {
                    if (yPlayer != null)
                        yPlayer.pause();
                    stopRecording();

                    if (!isFragmentPause && controller.isLogin()) {
                        DialogFragment saveRecordDialog = SaveRecordDialogFragment.newInstance(recordPath);
                        saveRecordDialog.show(getChildFragmentManager(), null);
                    } else
                        DeleteRecordFromLocal();

                }
            }
        });

        btnExpand = (ToggleButton) v.findViewById(R.id.frag_karaoke_btnExpand);
        erlSongInfo = (ExpandableRelativeLayout) v.findViewById(R.id.frag_karaoke_erl_songInfo);
        btnExpand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCollapse) {
                if (isCollapse)
                    erlSongInfo.collapse();
                else
                    erlSongInfo.expand();
            }
        });

        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.isLogin()) {
                    RatingDialogFragment ratingDialogFragment = RatingDialogFragment.newInstance(whichSong, true);
                    ratingDialogFragment.show(getChildFragmentManager(), null);
                }
            }
        });

        tvAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistDialogFragment playlistDialogFragment = PlaylistDialogFragment.newInstance(whichSong, null);
                playlistDialogFragment.show(getChildFragmentManager(), null);
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        isFragmentPause = false;
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentPause = true;
        if (btnRecord.isChecked())
            btnRecord.toggle();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Get current song
     *
     * @return
     */
    public Song getSong() {
        return whichSong;
    }

    /**
     * Enable record function for logged in user
     *
     * @param isEnable
     */
    public void enableRecord(boolean isEnable) {
        if (!isEnable)
            btnRecord.setChecked(false);
        btnRecord.setEnabled(isEnable);
    }

    /**
     * Refresh record list
     */
    public void ReloadRecordList() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frag_karaoke_frame_recordlist, RecordsFragment.newInstance(true, whichSong));
        transaction.commit();
    }

    ////////////////////////////////////////////////////////////////////////
    ///////////////////////////PRIVATE SECTION//////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    /**
     * Generate path for record file in internal storage
     * @return
     */
    private String GenerateRecordPath() {
        //String path = Environment.getExternalStorageDirectory() + "/";
        String path = getActivity().getFilesDir() + "/";
        Log.i(getClass().getSimpleName(), "@GenerateRecordPath: " + path);

        return path + controller.getCurrUser().getUserId() + "_" + whichSong.getVideo_id() + "_" + String.valueOf(System.currentTimeMillis()) + ".mp3";
    }

    /**
     *
     * @param filepath
     */
    private void startRecording(String filepath) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(96000);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setOutputFile(filepath);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    /**
     *
     */
    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     *
     */
    private void DeleteRecordFromLocal() {
        File file = new File(recordPath);
        file.delete();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
