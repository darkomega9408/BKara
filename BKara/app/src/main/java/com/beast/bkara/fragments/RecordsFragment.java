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
import android.widget.TextView;

import com.beast.bkara.Controller;
import com.beast.bkara.MainActivity;
import com.beast.bkara.R;
import com.beast.bkara.databinding.FragmentRecordsBinding;
import com.beast.bkara.dialogfragments.RatingDialogFragment;
import com.beast.bkara.model.Song;
import com.beast.bkara.util.ItemClickSupport;
import com.beast.bkara.util.RecordListRecyclerViewAdapter;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.viewmodel.RecordViewModel;

import me.tatarka.bindingcollectionadapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter.ItemViewArg;
import me.tatarka.bindingcollectionadapter.factories.BindingRecyclerViewAdapterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordsFragment extends Fragment {

    private RecordViewModel recordVm;
    private FragmentRecordsBinding binding;
    private RecyclerView rvRecordList;
    private Controller controller;
    private RecyclerView.Adapter rvRecordAdapter;

    // Bingding adapter for record list recyclerview - used for handle playing multiple records
    public BindingRecyclerViewAdapterFactory mFactory = new BindingRecyclerViewAdapterFactory() {
        @Override
        public <T> BindingRecyclerViewAdapter<T> create(RecyclerView recyclerView, ItemViewArg<T> arg) {
            return new RecordListRecyclerViewAdapter<>(arg, getActivity(), isShowUser);
        }
    };

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String HISTORY_LIST = "param";
    private static final String IS_SHOW_USER = "param1";
    private static final String SONG = "param2";

    // TODO: Rename and change types of parameters
    private BkaraService.HistoryList historyList;
    private boolean isShowUser;
    private Song song;

    private OnFragmentInteractionListener mListener;

    public RecordsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isShowUser Parameter 1.
     * @param song Parameter 1.
     * @return A new instance of fragment RecordsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordsFragment newInstance(boolean isShowUser, Song song) {
        RecordsFragment fragment = new RecordsFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_SHOW_USER, isShowUser);
        args.putParcelable(SONG, song);
        fragment.setArguments(args);
        return fragment;
    }

    public static RecordsFragment newInstance(BkaraService.HistoryList param) {
        RecordsFragment fragment = new RecordsFragment();
        Bundle args = new Bundle();
        args.putSerializable(HISTORY_LIST, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isShowUser = getArguments().getBoolean(IS_SHOW_USER);
            song = getArguments().getParcelable(SONG);
            historyList = (BkaraService.HistoryList) getArguments().getSerializable(HISTORY_LIST);
        }
        controller = (Controller) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_records, container, false);
        View v = binding.getRoot();

        TextView textViewPleaseLogin = (TextView) v.findViewById(R.id.frag_records_textview_pleaselogin);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.frag_records_progressBarWaiting);
        rvRecordList = (RecyclerView) v.findViewById(R.id.frag_records_rv_recordList);

        ItemClickSupport.addTo(rvRecordList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (controller.isLogin()) {
                    RatingDialogFragment ratingDialogFragment = RatingDialogFragment.newInstance(recordVm.recordList.get(position), false);
                    ratingDialogFragment.show(getChildFragmentManager(), null);
                }
            }
        });

        if (isShowUser) {
            textViewPleaseLogin.setVisibility(View.GONE);
            recordVm = new RecordViewModel(null, song, progressBar);
        }
        else if( historyList != null ) {
            textViewPleaseLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            recordVm = new RecordViewModel(((MainActivity) getActivity()).getLstRecordsHistory());
        }
        else {
            if (controller.isLogin()) {
                textViewPleaseLogin.setVisibility(View.GONE);
                recordVm = new RecordViewModel(controller.getCurrUser(), null, progressBar);
            }
            else
                progressBar.setVisibility(View.GONE);
        }
        binding.setRecordVm(recordVm);
        binding.setRecordsFrag(this);

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
    public void onPause() {
        super.onPause();

        if (rvRecordList != null) {
            rvRecordAdapter = rvRecordList.getAdapter();
            rvRecordList.setAdapter(null);
        }
    }

    @Override
    public void onResume() {
        if (rvRecordAdapter != null)
            rvRecordList.setAdapter(rvRecordAdapter);
        super.onResume();
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
