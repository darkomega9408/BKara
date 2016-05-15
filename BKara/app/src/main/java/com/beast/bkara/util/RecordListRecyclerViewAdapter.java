package com.beast.bkara.util;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.beast.bkara.Controller;
import com.beast.bkara.R;
import com.beast.bkara.dialogfragments.RatingDialogFragment;
import com.beast.bkara.model.Record;
import com.beast.bkara.viewmodel.RecordViewModel;
import com.devbrackets.android.exomedia.EMAudioPlayer;
import com.devbrackets.android.exomedia.EMVideoView;
import com.devbrackets.android.exomedia.exoplayer.EMExoPlayer;
import com.devbrackets.android.exomedia.listener.ExoPlayerListener;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.bindingcollectionadapter.BindingRecyclerViewAdapter;
import me.tatarka.bindingcollectionadapter.ItemViewArg;

/**
 * Created by Darka on 4/17/2016.
 */
public class RecordListRecyclerViewAdapter<T> extends BindingRecyclerViewAdapter<T> {

    private RecordPlayerHandler recordPlayerHandler;
    private boolean isShowUsername;
    private Context mContext;

    public RecordListRecyclerViewAdapter(@NonNull ItemViewArg<T> arg, Context context, boolean isShowUsername) {
        super(arg);
        recordPlayerHandler = new RecordPlayerHandler(context, true);
        this.isShowUsername = isShowUsername;
        this.mContext = context;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recordPlayerHandler.StopMediaPlayer();
        Log.d("RECYCLERVIEW ADAPTER", "DETACHED");
    }



    @Override
    public ViewDataBinding onCreateBinding(LayoutInflater inflater, @LayoutRes int layoutId, ViewGroup viewGroup) {
        ViewDataBinding binding = super.onCreateBinding(inflater, layoutId, viewGroup);
        Log.d("BINDING ADAPTER", "created binding: " + binding);
        return binding;
    }

    @Override
    public void onBindBinding(ViewDataBinding binding, int bindingVariable, @LayoutRes int layoutId, int position, T item) {
        super.onBindBinding(binding, bindingVariable, layoutId, position, item);
        Log.d("BINDING ADAPTER", "bound binding: " + binding + " at position: " + position);
        final Record record = (Record) item;
        View v = binding.getRoot();

        ToggleButton toggleButton = (ToggleButton) v.findViewById(R.id.record_item_btnPlay);
        SeekBar seekBar = (SeekBar) v.findViewById(R.id.record_item_seekBar);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.record_item_progressbar);


        if (isShowUsername) {
            TextView textViewSongname = (TextView) v.findViewById(R.id.record_item_textview_songname);
            textViewSongname.setVisibility(View.GONE);
        }
        else {

            TextView textViewUsername = (TextView) v.findViewById(R.id.record_item_textview_username);
            textViewUsername.setVisibility(View.GONE);
            Log.d("BINDING ADAPTER", "GONE USERNAME");
        }

        recordPlayerHandler.AddRecordInfo(toggleButton, seekBar, progressBar, record);
    }
}
