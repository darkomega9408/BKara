package com.beast.bkara.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.beast.bkara.Controller;
import com.beast.bkara.MainActivity;
import com.beast.bkara.R;
import com.beast.bkara.databinding.FragmentListSongBinding;
import com.beast.bkara.model.Song;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.util.ItemClickSupport;
import com.beast.bkara.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Darka on 4/10/2016.
 */
public class SongListFragment extends Fragment {

    private FragmentListSongBinding binding;
    private SongViewModel songVm;


    RecyclerView rvSongList;
    ProgressBar progressBarWaiting;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String HISTORY_LIST = "param";
    private static final String WHICH_LIST = "param1";
    private static final String SEARCH_FILTER = "param2";
    private static final String SEARCH_VALUE = "param3";

    // TODO: Rename and change types of parameters
    private BkaraService.HistoryList historyList;
    private BkaraService.WhichList whichList;
    private BkaraService.SongSearchFilter searchFilter;
    private String searchValue;

    private OnFragmentInteractionListener mListener;

    public SongListFragment() {

    }


    public static SongListFragment newInstance(BkaraService.HistoryList param) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putSerializable(HISTORY_LIST, param);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SongListFragment newInstance(BkaraService.WhichList param1) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putSerializable(WHICH_LIST, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SongListFragment newInstance(BkaraService.SongSearchFilter param1, String param2) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putSerializable(SEARCH_FILTER, param1);
        args.putString(SEARCH_VALUE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            whichList = (BkaraService.WhichList) getArguments().getSerializable(WHICH_LIST);
            if (whichList == null) {
                searchFilter = (BkaraService.SongSearchFilter) getArguments().getSerializable(SEARCH_FILTER);
                searchValue = getArguments().getString(SEARCH_VALUE);
                if( searchFilter == null || searchValue == null )
                    historyList = (BkaraService.HistoryList) getArguments().getSerializable(HISTORY_LIST);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_song, container, false);
        View v = binding.getRoot();
        rvSongList = (RecyclerView) v.findViewWithTag("frag_list_song_rvSongList");
        progressBarWaiting = (ProgressBar) v.findViewWithTag("frag_list_song_progressBarWaiting");

        if (whichList != null)
            songVm = new SongViewModel(whichList, progressBarWaiting);
        else if( searchFilter != null && searchValue != null )
            songVm = new SongViewModel(searchFilter, searchValue, progressBarWaiting);
        else if( historyList == BkaraService.HistoryList.SONG ) {
            progressBarWaiting.setVisibility(View.GONE);
            songVm = new SongViewModel(((MainActivity) getActivity()).getLstSongsHistory());
        }
        else if( historyList == BkaraService.HistoryList.RECORD ){
            progressBarWaiting.setVisibility(View.GONE);
        }


        binding.setSongVm(songVm);

        ItemClickSupport.addTo(rvSongList).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Song song = null;
                        song = songVm.songList.get(position);

                        // Display karaoke fragment and add song to history
                        if (song != null) {
                            if( ((MainActivity) getActivity()).checkViewed(song) ) {
                                song.setView(song.getView() + 1);
                                song.setLastTimeViewed(new Date());
                                BkaraService.getInstance().UpdateSong(song);
                            }
                            ((MainActivity) getActivity()).addToHistory(song);
                            Fragment karaokeFragment = KaraokeFragment.newInstance(song, "bla");
                            ((MainActivity) getActivity()).displayCustomFragment(karaokeFragment, "Karaoke");
                        }
                    }
                }
        );



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
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
