package com.voicechanger.soundeffect.soundchanger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.voicechanger.soundeffect.soundchanger.dialog.ConfirmDialog;
import com.voicechanger.soundeffect.soundchanger.R;
import com.voicechanger.soundeffect.soundchanger.dialog.MediaDialog;

import java.io.File;
import java.util.ArrayList;

public class MyStudioAdapter extends RecyclerView.Adapter<MyStudioAdapter.MyViewHolder> {
    public Context context;
    public ArrayList<String> myList;
    String pth;
    private ConfirmDialog confirmDialog;
    private ListenerClickPopupMenu listenerClickPopupMenu;

    public void setListenerClickPopupMenu(ListenerClickPopupMenu listenerClickPopupMenu) {
        this.listenerClickPopupMenu = listenerClickPopupMenu;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMore;
        public LinearLayout ll;
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.tvName);
            this.ll = (LinearLayout) view.findViewById(R.id.llView);
            this.ivMore = (ImageView) view.findViewById(R.id.ivMore);
            File file = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC + File.separator + "CallVoiceChanger");
            MyStudioAdapter.this.pth = file + "";
        }
    }

    public MyStudioAdapter(ArrayList<String> arrayList, Context context) {
        this.myList = arrayList;
        this.context = context;
    }

    public void updateAdapter(ArrayList<String> arrayList){
        this.myList = arrayList;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.myList.size();
    }


    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.title.setText((CharSequence) this.myList.get(myViewHolder.getAdapterPosition()));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MyStudioAdapter.this.pth);
        stringBuilder.append("/");
        stringBuilder.append((String) MyStudioAdapter.this.myList.get(myViewHolder.getAdapterPosition()));
        File file = new File(stringBuilder.toString());
        myViewHolder.ll.setOnClickListener(new OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                MediaDialog mediaDialog = new MediaDialog(context, stringBuilder.toString());
                mediaDialog.setCancelable(true);
                mediaDialog.show();
                Window window = mediaDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            }
        });

        myViewHolder.ivMore.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                popupDisplay(myViewHolder.getAdapterPosition(), myViewHolder, file);

            }
        });
    }

    public PopupWindow popupDisplay(int position, MyViewHolder myViewHolder, File file) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.popup_custom_menu, null);
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // inflate your layout or dynamically add view

        TextView tvCut = view.findViewById(R.id.tvCut);
        TextView tvEditEffect = view.findViewById(R.id.tvEditEffect);
        TextView tvShare = view.findViewById(R.id.tvShare);
        TextView tvDelete = view.findViewById(R.id.tvDelete);
        TextView tvRingtone = view.findViewById(R.id.tvRingtone);
        TextView tvNotification = view.findViewById(R.id.tvNotification);

        tvCut.setOnClickListener((OnClickListener) v -> {
            if(listenerClickPopupMenu != null){
                listenerClickPopupMenu.onSelectCut(file.getPath());
            }
            popupWindow.dismiss();
        });

        tvEditEffect.setOnClickListener(v -> {
            if(listenerClickPopupMenu != null){
                listenerClickPopupMenu.onSelectEditEffect(file.getPath());
            }
        });

        tvShare.setOnClickListener(v -> {
            if(listenerClickPopupMenu != null){
                listenerClickPopupMenu.onSelectShare(file);
            }
            popupWindow.dismiss();
        });
        tvDelete.setOnClickListener(v -> {
            confirmDialog = new ConfirmDialog(context, new ConfirmDialog.CallBackConfirmDialogListener() {
                @Override
                public void onSelectOk() {
                    if (file.exists()) {
                        file.delete();
                        Toast.makeText(context, R.string.text_delete_success, Toast.LENGTH_SHORT).show();
                    }
                    myList.remove(position);
                    notifyDataSetChanged();
                    confirmDialog.dismiss();
                }

                @Override
                public void onSelectCancel() {
                    confirmDialog.dismiss();
                }
            }, context.getResources().getString(R.string.text_delete), context.getResources().getString(R.string.text_confirm_delete));
            confirmDialog.show();
            Window window = confirmDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.dismiss();
        });
        tvRingtone.setOnClickListener(v -> {
            if(listenerClickPopupMenu != null){
                listenerClickPopupMenu.onSelectRingtone(file);
            }
            popupWindow.dismiss();
        });
        tvNotification.setOnClickListener(v -> {
            if (listenerClickPopupMenu != null) {
                listenerClickPopupMenu.onSelectNotification(file);
            }
            popupWindow.dismiss();
        });

        int[] values = new int[2];
        myViewHolder.ivMore.getLocationInWindow(values);
        int positionOfIcon = values[1];
        System.out.println("Position Y:" + positionOfIcon);

        //Get the height of 2/3rd of the height of the screen
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int height = (displayMetrics.heightPixels * 2) / 3;
        System.out.println("Height:" + height);

        //If the position of menu icon is in the bottom 2/3rd part of the screen then we provide menu height as offset  but in negative as we want to open our menu to the top
        if (positionOfIcon > height) {
            popupWindow.showAsDropDown(myViewHolder.ivMore, 0, -450);
        } else {
            popupWindow.showAsDropDown(myViewHolder.ivMore, 0, 0);
        }
        return popupWindow;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_list_row, viewGroup, false));
    }

    public interface ListenerClickPopupMenu {
        void onSelectCut(String filePath);
        void onSelectEditEffect(String filePath);
        void onSelectShare(File file);
        void onSelectDelete(String filePath);
        void onSelectRingtone(File file);
        void onSelectNotification(File file);
    }
}
