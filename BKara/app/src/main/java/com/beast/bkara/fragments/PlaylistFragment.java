package com.beast.bkara.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.beast.bkara.MainActivity;
import com.beast.bkara.R;
import com.beast.bkara.databinding.FragmentListSongBinding;
import com.beast.bkara.databinding.FragmentPlaylistBinding;
import com.beast.bkara.dialogfragments.AddPlaylistDialogFragment;
import com.beast.bkara.dialogfragments.RatingDialogFragment;
import com.beast.bkara.model.Playlist;
import com.beast.bkara.model.Song;
import com.beast.bkara.util.ItemClickSupport;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.viewmodel.PlaylistViewModel;
import com.beast.bkara.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistFragment extends Fragment {

    public static final int DIALOG_FRAGMENT = 1;

    private PlaylistFragment mInstance = this;
    private FragmentPlaylistBinding binding;
    private PlaylistViewModel playlistVm;

    private RecyclerView rvPlaylists;
    private ArrayList<Playlist> playlists;
    private Button btnAddPlaylist;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistFragment newInstance(String param1, String param2) {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        playlists = ((MainActivity) getActivity()).getPlaylist();

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false);
        View v = binding.getRoot();
        rvPlaylists = (RecyclerView) v.findViewWithTag("frag_playlists_rv_playlist");
        btnAddPlaylist = (Button) v.findViewById(R.id.frag_playlist_add);

        playlistVm = new PlaylistViewModel(playlists);

        binding.setPlaylistVm(playlistVm);

        ItemClickSupport.addTo(rvPlaylists).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Playlist playlist = null;
                        playlist = playlistVm.getPlaylistAt(position);

                        // Display karaoke fragment and add song to history
                        if (playlist != null) {
                            Fragment playlistSongsFragment = PlaylistSongsFragment.newInstance(playlist);
                            ((MainActivity) getActivity()).displayCustomFragment(playlistSongsFragment, playlist.getName());
                        }
                    }
                }
        );

        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPlaylistDialogFragment addPlaylistDialogFragment = AddPlaylistDialogFragment.newInstance(null, null);
                addPlaylistDialogFragment.setTargetFragment(mInstance, DIALOG_FRAGMENT);
                addPlaylistDialogFragment.show(getChildFragmentManager(), "AddPlaylistDialog");
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case DIALOG_FRAGMENT:

                if (resultCode == Activity.RESULT_OK) {
                    Playlist playlist = new Playlist(data.getStringExtra("name"));
                    playlistVm.playlists.add(playlist);
                    playlists.add(playlist);
                }

                break;
        }
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
