package com.voicechanger.soundeffect.soundchanger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.voicechanger.soundeffect.soundchanger.R;

public class LoadingDialog extends Dialog {
    private TextView tvCancel;
    private ImageView ivLoading;


    public LoadingDialog(@NonNull Context context ) {
        super(context);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        tvCancel = findViewById(R.id.tvCancel);
        ivLoading = findViewById(R.id.ivLoading);
        tvCancel.setOnClickListener(v -> {
            dismiss();
        });
        Glide.with(getContext()).load(R.drawable.img_loading).into(ivLoading);
    }

}