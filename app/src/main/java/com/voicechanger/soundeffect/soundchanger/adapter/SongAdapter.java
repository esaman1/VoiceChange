package com.voicechanger.soundeffect.soundchanger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.voicechanger.soundeffect.soundchanger.R;
import com.voicechanger.soundeffect.soundchanger.activity.EffectActivity;
import com.voicechanger.soundeffect.soundchanger.constants.IVoiceChangerConstants;
import com.voicechanger.soundeffect.soundchanger.model.Song;
import com.voicechanger.soundeffect.soundchanger.utils.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {
    public static String sName;
    public static String spath;
    public Context context;
    String destinationPath;
    public ArrayList<Song> songList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivMore;
        public ImageView ivPlay;
        public TextView title;

        @SuppressLint("WrongConstant")
        public MyViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.tvName);
            this.ivPlay = (ImageView) view.findViewById(R.id.ivPlay);
            this.ivMore = (ImageView) view.findViewById(R.id.ivMore);
            this.ivMore.setVisibility(8);
        }
    }

    public SongAdapter(ArrayList<Song> arrayList, Context context) {
        this.songList = arrayList;
        this.context = context;
    }

    public int getItemCount() {
        return this.songList.size();
    }

    public void onBindViewHolder(MyViewHolder myViewHolder, final int i) {
        myViewHolder.title.setText(((Song) this.songList.get(i)).getName());
        myViewHolder.ivPlay.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                File file = new File(((Song) SongAdapter.this.songList.get(i)).getPath());
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(268435456);
                intent.setDataAndType(Uri.fromFile(file), "audio/*");
                SongAdapter.this.context.startActivity(intent);
            }
        });
        myViewHolder.title.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SongAdapter.sName = ((Song) SongAdapter.this.songList.get(i)).getName();
                SongAdapter.spath = ((Song) SongAdapter.this.songList.get(i)).getPath();
                StringBuilder stringBuilder;
                if (StringUtils.isEmptyString(SongAdapter.spath)) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("");
                    stringBuilder.append(SongAdapter.this.destinationPath);
                    return;
                }
                File file = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC + File.separator + "CallVoiceChanger");
                if (!file.exists()) {
                    file.mkdirs();
                }
                String filePath = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                SongAdapter songAdapter = SongAdapter.this;
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(file);
                stringBuilder3.append("/Audio-");
                stringBuilder3.append(filePath);
                stringBuilder3.append(IVoiceChangerConstants.AUDIO_RECORDER_FILE_EXT_MP3);
                songAdapter.destinationPath = stringBuilder3.toString();

                stringBuilder = new StringBuilder();
                stringBuilder.append(SongAdapter.sName);
                stringBuilder.append("=");
                stringBuilder.append(SongAdapter.this.destinationPath);
                Intent intent = new Intent(SongAdapter.this.context, EffectActivity.class);
                intent.putExtra(IVoiceChangerConstants.KEY_PATH_AUDIO, SongAdapter.this.destinationPath);
                SongAdapter.this.context.startActivity(intent);
            }
        });
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_list_row, viewGroup, false));
    }
}
