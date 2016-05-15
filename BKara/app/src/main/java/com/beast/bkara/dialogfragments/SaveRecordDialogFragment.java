package com.beast.bkara.dialogfragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.beast.bkara.Controller;
import com.beast.bkara.R;
import com.beast.bkara.fragments.KaraokeFragment;
import com.beast.bkara.model.Record;
import com.beast.bkara.model.Song;
import com.beast.bkara.util.RecordExoPlayerHandler;
import com.beast.bkara.util.RecordPlayerHandler;
import com.beast.bkara.util.UploadToSoundCloudTask;
import com.beast.bkara.util.bkararestful.BkaraService;

import java.io.File;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SaveRecordDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SaveRecordDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveRecordDialogFragment extends DialogFragment {

    private RecordPlayerHandler recordPlayerHandler;
    private TextView textViewSave;
    private TextView textViewDiscard;

    private Controller controller;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String RECORD_PATH = "param1";

    // TODO: Rename and change types of parameters
    private String recordPath;

    private Song song;

    private OnFragmentInteractionListener mListener;

    public SaveRecordDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recordPath Parameter 1.
     * @return A new instance of fragment SaveRecordDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SaveRecordDialogFragment newInstance(String recordPath) {
        SaveRecordDialogFragment fragment = new SaveRecordDialogFragment();
        Bundle args = new Bundle();
        args.putString(RECORD_PATH, recordPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recordPath = getArguments().getString(RECORD_PATH);
        }

        recordPlayerHandler = new RecordPlayerHandler(getActivity(), false);
        controller = (Controller) getActivity().getApplicationContext();
        song = ((KaraokeFragment) getParentFragment()).getSong();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        View v = inflater.inflate(R.layout.fragment_save_record_dialog, container, false);

        ToggleButton toggleButton = (ToggleButton) v.findViewById(R.id.frag_save_record_btnPlay);
        SeekBar seekBar = (SeekBar) v.findViewById(R.id.frag_save_record_seekBar);
        textViewSave = (TextView) v.findViewById(R.id.frag_save_record_textview_save);
        textViewDiscard = (TextView) v.findViewById(R.id.frag_save_record_textview_discard);

        Record record = new Record();
        record.setStream_link(recordPath);
        recordPlayerHandler.AddRecordInfo(toggleButton, seekBar, null, record);

        textViewDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteRecordFromLocal();
                getDialog().dismiss();
            }
        });

        final Context mContext = getActivity();

        textViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Your record is being uploaded. You will receive a message when the upload is done", Toast.LENGTH_LONG).show();
                UploadToSoundCloudTask uploadToSoundCloudTask = new UploadToSoundCloudTask(recordPath,
                        controller.getCurrUser().getUserName(),
                        song.getTitle(),
                        new UploadToSoundCloudTask.OnUploadDoneInterface() {
                            @Override
                            public void OnUploadSuccess(String streamLink) {
                                DeleteRecordFromLocal();
                                final KaraokeFragment parentFragment = (KaraokeFragment) getParentFragment();
                                //parentFragment.getRecordViewModel().SaveRecord(streamLink);
                                Record record = new Record();
                                record.setDate_created(new Date());
                                record.setSong(song);
                                record.setUser(controller.getCurrUser());
                                record.setStream_link(streamLink + "?client_id=" + UploadToSoundCloudTask.CLIENT_ID);
                                BkaraService.getInstance().SaveRecord(record, new Callback<Long>() {
                                    @Override
                                    public void onResponse(Call<Long> call, Response<Long> response) {
                                        parentFragment.ReloadRecordList();
                                    }

                                    @Override
                                    public void onFailure(Call<Long> call, Throwable t) {

                                    }
                                });

                                Toast.makeText(mContext, "Upload record successfully !", Toast.LENGTH_SHORT).show();
                                // TODO: Create and save a record for user
                            }

                            @Override
                            public void OnUploadFailed() {
                                DeleteRecordFromLocal();
                                Toast.makeText(mContext, "Upload record failed !!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                uploadToSoundCloudTask.execute();
                getDialog().dismiss();
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
    public void onResume() {
        int width = getResources().getDimensionPixelSize(R.dimen.save_record_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.save_record_dialog_height);
        getDialog().getWindow().setLayout(width, height);
        super.onResume();
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
    public void onStop() {
        recordPlayerHandler.StopMediaPlayer();
        super.onStop();
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

    private void DeleteRecordFromLocal() {
        File file = new File(recordPath);
        file.delete();
    }
}
