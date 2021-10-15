package com.voicechanger.soundeffect.soundchanger.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.voicechanger.soundeffect.soundchanger.R;

public class FragmentIntro1 extends Fragment {

    private static final String ARG_POSITION = "ARG_POSITION";

    private int position;

    private ConstraintLayout clView;
    private TextView tvContent;
    private ImageView ivLogo;

    public static FragmentIntro1 newInstance(int position) {
        FragmentIntro1 fragment = new FragmentIntro1();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro1, container, false);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clView = view.findViewById(R.id.clView);
        tvContent = view.findViewById(R.id.tvContent);
        ivLogo = view.findViewById(R.id.ivLogo);

        switch (position){
            case 0:
                clView.setBackgroundColor(view.getResources().getColor(R.color.colorA0CED9));
                tvContent.setText(view.getResources().getString(R.string.text_intro1));
                ivLogo.setImageResource(R.drawable.img_intro1);
                break;
            case 1:
                clView.setBackgroundColor(view.getResources().getColor(R.color.color8CB369));
                tvContent.setText(view.getResources().getString(R.string.text_intro2));
                ivLogo.setImageResource(R.drawable.img_intro2);
                break;
            case 2:
                clView.setBackgroundColor(view.getResources().getColor(R.color.colorE26D5C));
                tvContent.setText(view.getResources().getString(R.string.text_intro3));
                ivLogo.setImageResource(R.drawable.img_intro3);
                break;
        }
    }
}