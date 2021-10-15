package com.voicechanger.soundeffect.soundchanger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.voicechanger.soundeffect.soundchanger.R;

public class ConfirmDialog extends Dialog {
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvOk;
    private TextView tvCancel;
    private CallBackConfirmDialogListener callBackConfirmDialogListener;
    private String title = "";
    private String body = "";

    public ConfirmDialog(@NonNull Context context , CallBackConfirmDialogListener callBackDialogConfirmListener, String title, String body) {
        super(context);
        this.callBackConfirmDialogListener = callBackDialogConfirmListener;
        this.title = title;
        this.body = body;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_confirm_dialog);
        tvTitle = findViewById(R.id.tvTitle);
        tvContent = findViewById(R.id.tvContent);
        tvOk = findViewById(R.id.tvOke);
        tvCancel = findViewById(R.id.tvCancel);
        tvTitle.setText(title);
        tvContent.setText(body);
        if(callBackConfirmDialogListener != null){
            tvOk.setOnClickListener(v -> {
                callBackConfirmDialogListener.onSelectOk();
            });
            tvCancel.setOnClickListener(v ->{
                callBackConfirmDialogListener.onSelectCancel();
            });
        }

    }

    public interface CallBackConfirmDialogListener {
        void onSelectOk();
        void onSelectCancel();
    }

}