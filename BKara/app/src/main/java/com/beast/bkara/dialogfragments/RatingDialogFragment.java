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
import android.widget.TextView;

import com.beast.bkara.Controller;
import com.beast.bkara.R;
import com.beast.bkara.model.Record;
import com.beast.bkara.model.Song;
import com.beast.bkara.model.supportmodel.RatingRecord;
import com.beast.bkara.model.supportmodel.RatingSong;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.util.RatingBarView;
import com.hedgehog.ratingbar.RatingBar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RatingDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RatingDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingDialogFragment extends DialogFragment {

    private TextView textViewTitle;
    private TextView textViewRate;
    private TextView textViewCancel;
    private RatingBarView ratingBar;
    private Controller controller;
    private BkaraService bkaraService;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String OBJECT_RATING = "param1";
    private static final String IS_SONG = "param2";

    // TODO: Rename and change types of parameters
    private Song song;
    private Record record;
    private boolean isSong;
    private int rateValue = 0;

    private OnFragmentInteractionListener mListener;

    public RatingDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param obj Parameter 1.
     * @param isSong Parameter 1.
     * @return A new instance of fragment RatingDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RatingDialogFragment newInstance(Object obj, boolean isSong) {
        RatingDialogFragment fragment = new RatingDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_SONG, isSong);
        if (isSong)
            args.putParcelable(OBJECT_RATING, (Song) obj);
        else
            args.putParcelable(OBJECT_RATING, (Record) obj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isSong = getArguments().getBoolean(IS_SONG);
            if (isSong)
                song = getArguments().getParcelable(OBJECT_RATING);
            else
                record = getArguments().getParcelable(OBJECT_RATING);
        }

        controller = (Controller) getActivity().getApplicationContext();
        bkaraService = BkaraService.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_rating_dialog, container, false);

        textViewTitle = (TextView) v.findViewById(R.id.frag_rating_record_textview_title);
        textViewRate = (TextView) v.findViewById(R.id.frag_rating_record_textview_rate);
        textViewCancel = (TextView) v.findViewById(R.id.frag_rating_record_textview_cancel);
        ratingBar = (RatingBarView) v.findViewById(R.id.frag_rating_record_ratingbar);

        if (isSong)
            textViewTitle.setText("Rate " + song.getTitle());
        else
            textViewTitle.setText("Rate " + record.getUser().getUserName() + "'s " + "RECORD");

        ratingBar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(int RatingCount) {
                rateValue = RatingCount;
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        textViewRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rateValue != 0 && controller.isLogin()) {
                    if (isSong) {
                        RatingSong ratingSong = new RatingSong();
                        ratingSong.setUser(controller.getCurrUser());
                        ratingSong.setSong(song);
                        ratingSong.setRateValue(rateValue);
                        bkaraService.RateSong(ratingSong);
                    }
                    else {
                        RatingRecord ratingRecord = new RatingRecord();
                        ratingRecord.setUser(controller.getCurrUser());
                        ratingRecord.setRecord(record);
                        ratingRecord.setRateValue(rateValue);
                        bkaraService.RateRecord(ratingRecord);
                    }
                }
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
