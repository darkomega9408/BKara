package com.beast.bkara.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beast.bkara.MainActivity;
import com.beast.bkara.R;
import com.beast.bkara.model.Song;
import com.beast.bkara.model.supportmodel.HotSong;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.viewmodel.SongViewModel;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private SliderLayout sliderLayout;
    private ArrayList<Song> songListNew;
    private ArrayList<Song> songListHot;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    // Add image in resources to a slider
    private void addImageToSlider(SliderLayout sl, int imageID) {
        if (sliderLayout != null) {
            DefaultSliderView dsv = new DefaultSliderView(getContext());
            dsv.image(imageID);
            sl.addSlider(dsv);
        }
    }

    // Add image url on internet to a slider
    private void addImageToSlider(SliderLayout sl, String imageURL) {
        if (sliderLayout != null) {
            DefaultSliderView dsv = new DefaultSliderView(getContext());
            dsv.image(imageURL);
            dsv.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    ((MainActivity) getActivity()).displayCustomFragment(SongsFragment.newInstance(SongsFragment.HOT_TAB_POSITION), "Songs");
                }
            });
            sl.addSlider(dsv);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        songListNew = new ArrayList<Song>();
        songListHot = new ArrayList<Song>();
    }

    private void setUpImageNew(View imgView, View progressBar, List<Song> songList, int position) {
        if (songList.size() > position) {
            ImageLoader.getInstance().displayImage(songList.get(position).getPoster(), (ImageView) imgView);
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_home, container, false);
        sliderLayout = (SliderLayout) v.findViewById(R.id.sliderHot);
        final ProgressBar hotProgressBar = (ProgressBar) v.findViewById(R.id.frag_home_progressbar_hot);

        //Load poster for hot songs
        BkaraService.getInstance().GetSongListHot(new Callback<List<HotSong>>() {
            @Override
            public void onResponse(Call<List<HotSong>> call, Response<List<HotSong>> response) {
                hotProgressBar.setVisibility(View.GONE);
                List<HotSong> hotSongList = response.body();
                if (hotSongList != null) {
                    for (int i=0; i < hotSongList.size(); i++)
                        addImageToSlider(sliderLayout, hotSongList.get(i).getHotPoster());
                }
            }

            @Override
            public void onFailure(Call<List<HotSong>> call, Throwable t) {

            }
        });

        //Load poster for new songs
        BkaraService.getInstance().GetSongList(BkaraService.WhichList.NEW, new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.body() != null) {
                    songListNew.clear();
                    songListNew.addAll(response.body());
                }
                setUpImageNew(v.findViewById(R.id.frag_home_image_new0), v.findViewById(R.id.frag_home_progressbar_new0), songListNew, 0);
                setUpImageNew(v.findViewById(R.id.frag_home_image_new1), v.findViewById(R.id.frag_home_progressbar_new1), songListNew, 1);
                setUpImageNew(v.findViewById(R.id.frag_home_image_new2), v.findViewById(R.id.frag_home_progressbar_new2), songListNew, 2);
                setUpImageNew(v.findViewById(R.id.frag_home_image_new3), v.findViewById(R.id.frag_home_progressbar_new3), songListNew, 3);
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStop() {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onResume() {
        sliderLayout.startAutoCycle();
        super.onResume();
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
