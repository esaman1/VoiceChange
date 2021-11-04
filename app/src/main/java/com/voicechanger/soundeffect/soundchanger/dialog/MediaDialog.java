package com.voicechanger.soundeffect.soundchanger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.voicechanger.soundeffect.soundchanger.R;

import java.io.File;

public class MediaDialog extends Dialog {
    private ImageView ivPlayAudio;
    private MediaPlayer mediaPlayer;
    private TextView tvNameAudio;
    private String url = "";
    private SeekBar sbAudio;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Boolean isCompleteAudio = false;

    public MediaDialog(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    public MediaDialog(@NonNull Context context, String url, int themeResId) {
        super(context, themeResId);
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_media);
        ivPlayAudio = findViewById(R.id.ivPlayAudio);
        sbAudio = findViewById(R.id.sbAudio);
        tvNameAudio = findViewById(R.id.tvNameAudio);


        tvNameAudio.setText(new File(url).getName());
        sbAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
                ivPlayAudio.setImageResource(R.drawable.ic_play_audio);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(sbAudio.getProgress());
                mediaPlayer.start();
                ivPlayAudio.setImageResource(R.drawable.ic_pause_audio);
                updateSeekBar();
            }
        });

        startAudio();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isCompleteAudio = true;
                sbAudio.setProgress(0);
                mediaPlayer.seekTo(0);
                ivPlayAudio.setImageResource(R.drawable.ic_play_audio);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

        ivPlayAudio.setOnClickListener(v -> {
            pauseOrResumeAudio();
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stopAudio();
            }
        });
    }

    private final Runnable mTimerRunnable = () -> {
        updateSeekBar();
    };

    private void updateSeekBar() {
        if (mediaPlayer.isPlaying()) {
            sbAudio.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(mTimerRunnable, 100);
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
    }

    private void pauseOrResumeAudio() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                ivPlayAudio.setImageResource(R.drawable.ic_play_audio);
            } else {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();
                ivPlayAudio.setImageResource(R.drawable.ic_pause_audio);
            }
            updateSeekBar();
        }
    }

    private void startAudio() {
        if (!url.isEmpty()) {
            mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(url));
            if (mediaPlayer != null){
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.setVolume(100F, 100F);
                        mediaPlayer.start();
                        isCompleteAudio = false;
                        sbAudio.setMax(mediaPlayer.getDuration());
                        updateSeekBar();
                    }
                });
            }
        }
    }

}