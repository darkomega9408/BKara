package com.beast.bkara.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.ToggleButton;


import com.beast.bkara.Controller;
import com.beast.bkara.model.Record;
import com.beast.bkara.util.bkararestful.BkaraService;
import com.beast.bkara.viewmodel.RecordViewModel;
import com.devbrackets.android.exomedia.EMAudioPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Darka on 4/26/2016.
 */
public class RecordExoPlayerHandler {
    private EMAudioPlayer mediaPlayer;
    private Context mContext;
    private Handler threadHandler;
    private List<ToggleButton> toggleButtonList;
    private List<SeekBar> seekBarList;
    private List<Record> recordList;
    private UpdateSeekBarThread updateSeekBarThread;
    private boolean isRemote;
    private Controller controller;

    private class UpdateSeekBarThread implements Runnable {
        public SeekBar sbar;
        public boolean isRepeat = false;

        public void run() {
            if (mediaPlayer != null) {
                int currentPosition = (int) mediaPlayer.getCurrentPosition();
                sbar.setProgress(currentPosition);
                if (isRepeat)
                    threadHandler.postDelayed(this, 500);
            }
        }
    }

    public RecordExoPlayerHandler(Context context, boolean isRemote) {
        this.mContext = context;
        this.isRemote = isRemote;
        this.controller = (Controller) context.getApplicationContext();
        toggleButtonList = new ArrayList<ToggleButton>();
        seekBarList = new ArrayList<SeekBar>();
        recordList = new ArrayList<Record>();
        updateSeekBarThread = new UpdateSeekBarThread();
        threadHandler = new Handler();

    }

    public void AddRecordInfo(final ToggleButton toggleButton, final SeekBar seekBar, final ProgressBar progressBar, final Record record) {

        seekBar.setEnabled(false);

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    SeekBar sbar = (SeekBar) view;
                    int currentProgress = sbar.getProgress();
                    if (currentProgress != 0) {
                        mediaPlayer.seekTo(currentProgress);
                    }
                }
                return false;
            }

            ;

        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isPlay) {

                final int itemIndex = toggleButtonList.indexOf((ToggleButton) compoundButton);
                final SeekBar sbar = seekBarList.get(itemIndex);
                int currentProgress = seekBarList.get(itemIndex).getProgress();

                if (isPlay) {
                    sbar.setEnabled(true);

                    for (int i = 0; i < seekBarList.size(); i++) {
                        if (i != itemIndex) {
                            seekBarList.get(i).setProgress(0);
                            seekBarList.get(i).setEnabled(false);
                            toggleButtonList.get(i).setChecked(false);
                        }
                    }
                    if (currentProgress == 0) {
                        if (mediaPlayer != null) {
                            updateSeekBarThread.isRepeat = false;
                            mediaPlayer.release();
                        }

                        mediaPlayer = new EMAudioPlayer(mContext);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                toggleButton.setChecked(false);
                                mediaPlayer.reset();
                                sbar.setEnabled(false);
                            }
                        });
                        if (isRemote) {

                            toggleButton.setVisibility(View.INVISIBLE);
                            seekBar.setVisibility(View.INVISIBLE);

                            if (progressBar != null)
                                progressBar.setVisibility(View.VISIBLE);

                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            try {
                                mediaPlayer.setDataSource(mContext, Uri.parse(recordList.get(itemIndex).getStream_link()));
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        toggleButton.setVisibility(View.VISIBLE);
                                        seekBar.setVisibility(View.VISIBLE);

                                        for (int i = 0; i < seekBarList.size(); i++)
                                            if (i != itemIndex) {
                                                toggleButtonList.get(i).setVisibility(View.VISIBLE);
                                                seekBarList.get(i).setVisibility(View.VISIBLE);
                                            }

                                        if (progressBar != null)
                                            progressBar.setVisibility(View.GONE);

                                        sbar.setMax((int)mediaPlayer.getDuration());

                                        if (controller.isLogin()) {
                                            BkaraService.getInstance().SendNoti(controller.getCurrUser().getUserId(),
                                                    record.getUser().getUserId(),
                                                    controller.getCurrUser().getUserName() + " is listening to your's record");
                                        }
                                    }
                                });
                                for (int i = 0; i < seekBarList.size(); i++)
                                    if (i != itemIndex) {
                                        toggleButtonList.get(i).setVisibility(View.INVISIBLE);
                                        seekBarList.get(i).setVisibility(View.INVISIBLE);
                                    }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            mediaPlayer.setDataSource(mContext, Uri.parse(recordList.get(itemIndex).getStream_link()));
                            Log.d("LOCAL PLAY DURATION", "" + (int)mediaPlayer.getDuration());
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    Log.d("LOCAL PLAY DURATION", "" + (int)mediaPlayer.getDuration());
                                    sbar.setMax((int) mediaPlayer.getDuration());
                                }
                            });
                        }
                        mediaPlayer.prepareAsync();
                        mediaPlayer.start();
                    } else {
                        mediaPlayer.start();
                    }
                    updateSeekBarThread.sbar = sbar;
                    updateSeekBarThread.isRepeat = true;
                    Log.d("MEDIA PLAYER", "THREAD BEGIN");
                    threadHandler.postDelayed(updateSeekBarThread, 500);
                } else {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                }
            }
        });

        toggleButtonList.add(toggleButton);
        seekBarList.add(seekBar);
        recordList.add(record);
    }

    public void StopMediaPlayer() {
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = null;
        for (int i = 0; i < seekBarList.size(); i++) {
            seekBarList.get(i).setProgress(0);
            toggleButtonList.get(i).setChecked(false);
        }
    }
}
