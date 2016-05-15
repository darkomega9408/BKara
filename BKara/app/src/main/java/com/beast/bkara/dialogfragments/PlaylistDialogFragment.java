package com.beast.bkara.dialogfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.beast.bkara.Constants;
import com.beast.bkara.MainActivity;
import com.beast.bkara.R;
import com.beast.bkara.model.Playlist;
import com.beast.bkara.model.Song;

import java.util.List;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SONG = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private Song mSong;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Local variable
    private Button btnAddToPlaylist;
    private RadioGroup rgPlaylist;
    private List<Playlist> playlists;
    private View view;

    public PlaylistDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param song Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistDialogFragment newInstance(Song song, String param2) {
        PlaylistDialogFragment fragment = new PlaylistDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(SONG, song);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSong = (Song) getArguments().getParcelable(SONG);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Choose a playlist");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_playlist_dialog, container, false);

        btnAddToPlaylist = (Button) view.findViewById(R.id.frag_playlist_dialog_add);
        rgPlaylist = (RadioGroup) view.findViewById(R.id.frag_playlist_dialog_playlist);

        addAddButtonListener();
        populatePlaylist();

        return view;
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

    public void addAddButtonListener() {
        btnAddToPlaylist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) view.findViewById(rgPlaylist.getCheckedRadioButtonId());

                for (Playlist playlist: playlists) {
                    if (playlist.getName().equals(rb.getText().toString())) {
                        playlist.addSong(mSong);
                        dismiss();
                        return;
                    }
                }
            }
        });
    }

    private void populatePlaylist() {
        playlists = ((MainActivity) getActivity()).getPlaylist();

        RadioButton rb;

        for (Playlist playlist: playlists) {
            rb = (RadioButton) getActivity().getLayoutInflater().inflate(R.layout.radio_button_template, null);
            rb.setText(playlist.getName());
            rb.setId(rgPlaylist.getChildCount());

            rgPlaylist.addView(rb);
        }

        ((RadioButton) rgPlaylist.getChildAt(0)).setChecked(true);
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
