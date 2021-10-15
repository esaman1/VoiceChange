package com.voicechanger.soundeffect.soundchanger.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.voicechanger.soundeffect.soundchanger.R;

public class ConfirmSaveAudioDialog extends Dialog {
    private TextView tvTitle;
    private EditText edtContent;
    private TextView tvSkip;
    private TextView tvOke;
    private CallBackConfirmSaveDialogListener callBackConfirmSaveDialogListener;

    public ConfirmSaveAudioDialog(@NonNull Context context , CallBackConfirmSaveDialogListener callBackDialogConfirmListener) {
        super(context);
        this.callBackConfirmSaveDialogListener = callBackDialogConfirmListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_confirm_save_dialog);
        tvTitle = findViewById(R.id.tvTitle);
        edtContent = findViewById(R.id.etContent);
        tvSkip = findViewById(R.id.tvSkip);
        tvOke = findViewById(R.id.tvOke);
        if(callBackConfirmSaveDialogListener != null){
            tvSkip.setOnClickListener(v -> {
                callBackConfirmSaveDialogListener.onSelectSkip();
            });
            tvOke.setOnClickListener(v ->{
                if(edtContent.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.text_please_enter_name), Toast.LENGTH_SHORT).show();
                } else {
                    callBackConfirmSaveDialogListener.onSelectOk(edtContent.getText().toString());
                }
            });
        }

    }

    public interface CallBackConfirmSaveDialogListener {
        void onSelectSkip();
        void onSelectOk(String name);
    }

}
