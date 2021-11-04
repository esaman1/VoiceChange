package com.voicechanger.soundeffect.soundchanger.androidaudiorecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.vapp.admoblibrary.ads.AdCallback;
import com.vapp.admoblibrary.ads.AdmodUtils;
import com.voicechanger.soundeffect.soundchanger.R;
import com.voicechanger.soundeffect.soundchanger.activity.MainActivity;
import com.voicechanger.soundeffect.soundchanger.androidaudiorecorder.model.AudioChannel;
import com.voicechanger.soundeffect.soundchanger.androidaudiorecorder.model.AudioSampleRate;
import com.voicechanger.soundeffect.soundchanger.androidaudiorecorder.model.AudioSource;
import com.voicechanger.soundeffect.soundchanger.services.RecordingService;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.OmRecorder;
import omrecorder.PullTransport.Default;
import omrecorder.PullTransport.OnAudioChunkPulledListener;
import omrecorder.Recorder;

public class AudioRecorderActivity extends AppCompatActivity implements OnCompletionListener, OnAudioChunkPulledListener {
    private boolean autoStart;
    private AudioChannel channel;
    private int color;
    //private RelativeLayout contentLayout;
    private String filePath;
    private boolean isRecording;
    private boolean keepDisplayOn;
    private ImageView playView;
    private ImageView btn_ok,ib_cancel;
    private MediaPlayer player;
    private int playerSecondsElapsed;
    private ImageView recordView;
    private Recorder recorder;
    private int recorderSecondsElapsed;
    private ImageView restartView;
    private AudioSampleRate sampleRate;
//    private MenuItem saveMenuItem;
    private AudioSource source;
    //private TextView statusView;
    private Timer timer;
    private TextView timerView;
    private VisualizerHandler visualizerHandler;
    private File file;
    private RecordingReceiver receiver;

    public static final String START_PAUSE = AudioRecorderActivity.class.getCanonicalName() + ".START_PAUSE";
    public static final String PAUSE_BUTTON = AudioRecorderActivity.class.getCanonicalName() + ".PAUSE_BUTTON";
    public static final String START_RECORDING = RecordingService.class.getCanonicalName() + ".START_RECORDING";
    public static final String STOP_RECORDING = RecordingService.class.getCanonicalName() + ".STOP_RECORDING";

    public static void startActivity(Context context, boolean pause) {
        Intent i = new Intent(context, AudioRecorderActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (pause)
            i.setAction(AudioRecorderActivity.START_PAUSE);
        context.startActivity(i);
    }

    private boolean isPlaying() {
        boolean z = false;
        try {
            if (!(this.player == null || !this.player.isPlaying() || this.isRecording)) {
                z = true;
            }
        } catch (Exception unused) {
            return z;
        }
        return z;
    }

    private void pauseRecording() {
        this.isRecording = false;
        this.restartView.setVisibility(View.VISIBLE);
        this.recordView.setImageResource(R.drawable.img_play);
        btn_ok.setVisibility(View.VISIBLE);
        if (this.visualizerHandler != null) {
            this.visualizerHandler.stop();
        }
        if (this.recorder != null) {
            this.recorder.pauseRecording();
        }
        stopTimer();
        Log.d("duonghq", "pauseRecording: ");
        RecordingService.startService(this, filePath, isRecording, Util.formatSeconds(playerSecondsElapsed));
    }

    private void resumeRecording() {
        this.isRecording = true;
//        this.saveMenuItem.setVisible(false);
//        this.statusView.setText(R.string.aar_recording);
//        this.statusView.setVisibility(View.VISIBLE);
        this.restartView.setVisibility(View.INVISIBLE);
        btn_ok.setVisibility(View.INVISIBLE);
//        this.playView.setVisibility(View.INVISIBLE);
        this.recordView.setImageResource(R.drawable.img_pause);
//        this.playView.setImageResource(R.drawable.ic_playyy);
        this.visualizerHandler = new VisualizerHandler();
        if (this.recorder == null) {
            this.timerView.setText("00:00:00");
            this.recorder = OmRecorder.wav(new Default(Util.getMic(this.source, this.channel, this.sampleRate), (OnAudioChunkPulledListener) this), new File(this.filePath));
        }
        this.recorder.resumeRecording();
        startTimer();
        Log.d("duonghq", "resumeRecording: ");
        RecordingService.startService(this, filePath, isRecording, Util.formatSeconds(playerSecondsElapsed));
    }

    private void selectAudio() {
        stopRecording();
        setResult(-1);
        finish();
    }

    private void startPlaying() {
        try {
            stopRecording();
            this.player = new MediaPlayer();
            this.player.setDataSource(this.filePath);
            this.player.prepare();
            this.player.start();
            this.timerView.setText("00:00:00");
//            this.statusView.setText(R.string.aar_playing);
//            this.statusView.setVisibility(View.VISIBLE);
//            this.playView.setImageResource(R.drawable.ic_stop);
            this.playerSecondsElapsed = 0;
            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        stopTimer();
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                AudioRecorderActivity.this.updateTimer();
            }
        }, 0, 1000);
    }

    private void stopPlaying() {
//        this.statusView.setText("");
//        this.statusView.setVisibility(View.INVISIBLE);
//        this.playView.setImageResource(R.drawable.ic_playyy);
        if (this.visualizerHandler != null) {
            this.visualizerHandler.stop();
        }
        if (this.player != null) {
            try {
                this.player.stop();
                this.player.reset();
            } catch (Exception unused) {
                stopTimer();
            }
        }
    }

    private void stopRecording() {
        if (this.visualizerHandler != null) {
            this.visualizerHandler.stop();
        }
        this.recorderSecondsElapsed = 0;
        if (this.recorder != null) {
            this.recorder.stopRecording();
            this.recorder = null;
        }
        stopTimer();
    }

    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
    }

    private void updateTimer() {
        runOnUiThread(new Runnable() {
            public void run() {
                TextView access$800;
                int access$700;
                if (AudioRecorderActivity.this.isRecording) {
                    AudioRecorderActivity.this.recorderSecondsElapsed = AudioRecorderActivity.this.recorderSecondsElapsed + 1;
                    access$800 = AudioRecorderActivity.this.timerView;
                    access$700 = AudioRecorderActivity.this.recorderSecondsElapsed;
                } else if (AudioRecorderActivity.this.isPlaying()) {
                    AudioRecorderActivity.this.playerSecondsElapsed = AudioRecorderActivity.this.playerSecondsElapsed + 1;
                    access$800 = AudioRecorderActivity.this.timerView;
                    access$700 = AudioRecorderActivity.this.playerSecondsElapsed;
                } else {
                    return;
                }
                access$800.setText(Util.formatSeconds(access$700));
                playerSecondsElapsed = access$700;
                Log.d("duonghq", "updateTimer: ");
                RecordingService.startService(AudioRecorderActivity.this, filePath, isRecording, Util.formatSeconds(access$700));
            }
        });
    }

    public void onAudioChunkPulled(AudioChunk audioChunk) {
        if (this.isRecording) {
            audioChunk.maxAmplitude();
        }
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        stopPlaying();
    }

    protected void onCreate(Bundle bundle) {
        boolean z;
        super.onCreate(bundle);
        setContentView((int) R.layout.aar_activity_audio_recorder);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        if (bundle != null) {
            this.filePath = bundle.getString(AndroidAudioRecorder.EXTRA_FILE_PATH);
            this.source = (AudioSource) bundle.getSerializable("source");
            this.channel = (AudioChannel) bundle.getSerializable("channel");
            this.sampleRate = (AudioSampleRate) bundle.getSerializable("sampleRate");
            this.color = bundle.getInt("color");
            this.autoStart = bundle.getBoolean("autoStart");
            z = bundle.getBoolean("keepDisplayOn");
        } else {
            this.filePath = getIntent().getStringExtra(AndroidAudioRecorder.EXTRA_FILE_PATH);
            this.source = (AudioSource) getIntent().getSerializableExtra("source");
            this.channel = (AudioChannel) getIntent().getSerializableExtra("channel");
            this.sampleRate = (AudioSampleRate) getIntent().getSerializableExtra("sampleRate");
            this.color = getIntent().getIntExtra("color", ViewCompat.MEASURED_STATE_MASK);
            this.autoStart = getIntent().getBooleanExtra("autoStart", false);
            z = getIntent().getBooleanExtra("keepDisplayOn", false);
        }
        file = new File(filePath);
        this.keepDisplayOn = z;
        if (this.keepDisplayOn) {
            getWindow().addFlags(128);
        }
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setElevation(0.0f);
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ba4374")));
//
//            getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.aar_ic_clear));
//        }
        //this.contentLayout = (RelativeLayout) findViewById(R.id.content);
//        this.statusView = (TextView) findViewById(R.id.status);
        this.timerView = (TextView) findViewById(R.id.timer);
        this.restartView = (ImageView) findViewById(R.id.ivRestart);
        this.recordView = (ImageView) findViewById(R.id.record);
        this.btn_ok=(ImageView)findViewById(R.id.btn_ok);
        this.ib_cancel=(ImageView)findViewById(R.id.ib_cancel);
        receiver = new RecordingReceiver();
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(AudioRecorderActivity.this, getString(R.string.test_ads_admob_inter_id), 0,
                        new AdCallback() {
                            @Override
                            public void onAdClosed() {
                                selectAudio();
                            }

                            @Override
                            public void onAdFail() {
                                finish();
                            }
                        }, true);
            }
        });
        ib_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(AudioRecorderActivity.this, getString(R.string.test_ads_admob_inter_id), 0,
                        new AdCallback() {
                            @Override
                            public void onAdClosed() {
                                if (file.exists()) {
                                    file.delete();
                                }
                                finish();
                            }

                            @Override
                            public void onAdFail() {
                                if (file.exists()) {
                                    file.delete();
                                }
                                finish();
                            }
                        }, true);
            }
        });
//        this.playView = (ImageView) findViewById(R.id.play);
        //this.restartView.setVisibility(View.INVISIBLE);
//        this.playView.setVisibility(View.INVISIBLE);
//        if (Util.isBrightColor(this.color)) {
//            ContextCompat.getDrawable(this, R.drawable.aar_ic_clear).setColorFilter(ViewCompat.MEASURED_STATE_MASK, Mode.SRC_ATOP);
//            ContextCompat.getDrawable(this, R.drawable.aar_ic_check).setColorFilter(ViewCompat.MEASURED_STATE_MASK, Mode.SRC_ATOP);
//            this.statusView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
//            this.timerView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
//            this.restartView.setColorFilter(ViewCompat.MEASURED_STATE_MASK);
//            this.recordView.setColorFilter(ViewCompat.MEASURED_STATE_MASK);
////            this.playView.setColorFilter(ViewCompat.MEASURED_STATE_MASK);
//        }
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.aar_audio_recorder, menu);
//        this.saveMenuItem = menu.findItem(R.id.action_save);
//        this.saveMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.aar_ic_check));
//        return super.onCreateOptionsMenu(menu);
//    }

    private void showAdsAndFinish() {
        AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(this, getString(R.string.test_ads_admob_inter_id), 0,
                new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        finish();
                    }

                    @Override
                    public void onAdFail() {
                        finish();
                    }
                }, true);
    }

    protected void onDestroy() {
        //restartRecording(null);
        stopTimer();
        setResult(0);
        Log.d("duonghq", "onDestroy: ");
        unregisterReceiver(receiver);
        super.onDestroy();
        RecordingService.stop(this);
    }

//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        int itemId = menuItem.getItemId();
//        if (itemId == 16908332) {
//            finish();
//        } else if (itemId == R.id.action_save) {
//            selectAudio();
//        }
//        return super.onOptionsItemSelected(menuItem);
//    }

    protected void onPause() {
        //restartRecording(null);
        super.onPause();
        Log.d("duonghq", "onPause: ");

    }

    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        if (this.autoStart && !this.isRecording) {
            toggleRecording(null);
        }
    }

    public void onResume() {
        super.onResume();
        Log.d("duonghq", "onResume: ");
        RecordingService.startService(this, filePath, isRecording, Util.formatSeconds(playerSecondsElapsed));
       registerReceiver(
                receiver,
                new IntentFilter(AudioRecorderActivity.PAUSE_BUTTON)
        );
    }

    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString("filePath", this.filePath);
        bundle.putInt("color", this.color);
        super.onSaveInstanceState(bundle);
    }

    public void restartRecording(View view) {
        btn_ok.setVisibility(View.GONE);
        if (this.isRecording) {
            stopRecording();
        } else if (isPlaying()) {
            stopPlaying();
        } else {
            this.visualizerHandler = new VisualizerHandler();
            if (this.visualizerHandler != null) {
                recorder = null;
                this.visualizerHandler.stop();
            }
        }
//        this.statusView.setVisibility(View.INVISIBLE);
//        this.restartView.setVisibility(View.INVISIBLE);
//        this.playView.setVisibility(View.INVISIBLE);
//        this.recordView.setImageResource(R.drawable.i_microphone);
        this.timerView.setText("00:00:00");
        this.recorderSecondsElapsed = 0;
        this.playerSecondsElapsed = 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(file.exists()){
            file.delete();
        }
    }

    public void togglePlaying(View view) {
        pauseRecording();
        Util.wait(100, new Runnable() {
            public void run() {
                if (AudioRecorderActivity.this.isPlaying()) {
                    AudioRecorderActivity.this.stopPlaying();
                } else {
                    AudioRecorderActivity.this.startPlaying();
                }
            }
        });
    }

    public void toggleRecording(View view) {
        stopPlaying();
        Util.wait(100, new Runnable() {
            public void run() {
                if (AudioRecorderActivity.this.isRecording) {
                    AudioRecorderActivity.this.pauseRecording();
                } else {

                    AudioRecorderActivity.this.resumeRecording();
                }
            }
        });
    }

    class RecordingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String a = intent.getAction();
            isRecording = intent.getBooleanExtra("status_recorder", false);
            Log.d("duonghq", "onReceive isRecording: " + isRecording);
            if (a == null)
                return;
            if (a.equals(PAUSE_BUTTON)) {
                    if(isRecording){
                        pauseRecording();
                    } else {
                        resumeRecording();
                    }
            }
        }
    }
}
