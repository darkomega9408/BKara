package com.beast.bkara.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.beast.bkara.MainActivity;
import com.beast.bkara.R;
import com.beast.bkara.databinding.FragmentListSongBinding;
import com.beast.bkara.databinding.FragmentPlaylistSongsBinding;
import com.beast.bkara.model.Playlist;
import com.beast.bkara.model.Song;
import com.beast.bkara.util.ItemClickSupport;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistSongsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistSongsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistSongsFragment extends Fragment {
    private FragmentPlaylistSongsBinding binding;
    private SongViewModel songVm;

    RecyclerView rvSongList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PLAYLIST = "param";

    // TODO: Rename and change types of parameters
    private Playlist playlist;
    private ArrayList<Song> songList;

    private OnFragmentInteractionListener mListener;

    public PlaylistSongsFragment() {

    }


    public static PlaylistSongsFragment newInstance(Playlist param) {
        PlaylistSongsFragment fragment = new PlaylistSongsFragment();
        Bundle args = new Bundle();
        args.putParcelable(PLAYLIST, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            playlist = (Playlist) getArguments().getParcelable(PLAYLIST);
            songList = playlist.getSongList();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist_songs, container, false);
        View v = binding.getRoot();
        rvSongList = (RecyclerView) v.findViewWithTag("frag_list_song_rvSongList");
        songVm = new SongViewModel(songList);

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
