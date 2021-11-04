package com.voicechanger.soundeffect.soundchanger.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy.Builder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.voicechanger.soundeffect.soundchanger.Common;
import com.voicechanger.soundeffect.soundchanger.R;
import com.voicechanger.soundeffect.soundchanger.adapter.MyStudioAdapter;
import com.voicechanger.soundeffect.soundchanger.constants.IVoiceChangerConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MyStudioActivity extends AppCompatActivity implements MyStudioAdapter.ListenerClickPopupMenu {
    ImageView ivBack;
    RecyclerView.LayoutManager layoutManager;
    private RelativeLayout ll_Ad_Progress;
    ArrayList<String> myStudio;
    MyStudioAdapter myStudioAdapter;
    RecyclerView my_recycler_view_my;
    private LinearLayout native_ad_container;
    String path;
    ActivityResultLauncher<Intent> tapTranslateLauncher;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_my_studio);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        StrictMode.setVmPolicy(new Builder().build());
        this.my_recycler_view_my = (RecyclerView) findViewById(R.id.my_recycler_view_my);
        this.ivBack = (ImageView) findViewById(R.id.ivBack);
        this.ivBack.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MyStudioActivity.this.onBackPressed();
            }
        });
        setupListAudio();
        tapTranslateLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                setupListAudio();
        });
    }

    private void setupListAudio() {
        int i = 0;
        this.myStudio = new ArrayList();
        File file = getExternalFilesDir(Environment.DIRECTORY_MUSIC + File.separator + "CallVoiceChanger");
        if (file.exists() && file.listFiles() != null) {
            File[] listFiles = file.listFiles();
            int length = listFiles.length;
            while (i < length) {
                File file2 = listFiles[i];
                if (file2.isFile()) {
                    this.path = file2.getName();
                }
                if (this.path.contains(IVoiceChangerConstants.AUDIO_RECORDER_FILE_EXT_MP3)
                        || this.path.contains(IVoiceChangerConstants.AUDIO_RECORDER_FILE_EXT_WAV)
                        || this.path.contains(IVoiceChangerConstants.AUDIO_RECORDER_FILE_EXT_M4A)) {
                    this.myStudio.add(this.path);
                }
                i++;
            }
        }
        this.layoutManager = new LinearLayoutManager(this);
        this.myStudioAdapter = new MyStudioAdapter(this.myStudio, this);
        myStudioAdapter.setListenerClickPopupMenu(this);
        this.my_recycler_view_my.setLayoutManager(this.layoutManager);
        this.my_recycler_view_my.setAdapter(this.myStudioAdapter);
    }

    @Override
    public void onSelectCut(String filePath) {
        Intent intent = new Intent(MyStudioActivity.this, TrimAudioActivity.class);
        intent.putExtra(Common.EXTRA_AUDIO_URI, filePath);
        tapTranslateLauncher.launch(intent);

    }

    @Override
    public void onSelectEditEffect(String filePath) {
        Intent intent = new Intent(this, EffectActivity.class);
        intent.putExtra(IVoiceChangerConstants.KEY_PATH_AUDIO, filePath);
        startActivity(intent);
    }

    @Override
    public void onSelectShare(File file) {
        if (file.exists() && file.isFile()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("audio/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(Intent.createChooser(intent, "Share Via"));

        }
    }

    @Override
    public void onSelectDelete(String filePath) {

    }

    @Override
    public void onSelectRingtone(File file) {
        if (file.isFile()) {
            Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onSelectNotification(File file) {
        if (file.isFile()) {
            Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
            startActivity(intent);
        }
    }
}
