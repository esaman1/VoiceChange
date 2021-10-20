package com.voicechanger.soundeffect.soundchanger.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.vapp.admoblibrary.ads.AdCallback;
import com.vapp.admoblibrary.ads.AdmodUtils;
import com.vapp.admoblibrary.ads.NativeAdCallback;
import com.voicechanger.soundeffect.soundchanger.R;
import com.voicechanger.soundeffect.soundchanger.adclass.BaseActivity;
import com.voicechanger.soundeffect.soundchanger.androidaudiorecorder.AndroidAudioRecorder;
import com.voicechanger.soundeffect.soundchanger.androidaudiorecorder.model.AudioChannel;
import com.voicechanger.soundeffect.soundchanger.androidaudiorecorder.model.AudioSampleRate;
import com.voicechanger.soundeffect.soundchanger.androidaudiorecorder.model.AudioSource;
import com.voicechanger.soundeffect.soundchanger.constants.IVoiceChangerConstants;
import com.voicechanger.soundeffect.soundchanger.utils.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQ_CODE = 1;
    //    private ImageView iv_start, iv_creation;
    private TextView tvStart;
    private TextView tvCreation;
    private Uri urishare;
    private Dialog dialog;
    int color;
    //public static String recordFileManagerIml.genFilePath();
    Shimmer shimmer;
    ShimmerTextView myShimmerTextView, myShimmerTextView1;
    //private RecordFileManagerImpl recordFileManagerIml = new RecordFileManagerImpl(MyApplication.appContext);
    private String PATH_AUDIO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main1);


//        myShimmerTextView = findViewById(R.id.shimmer_tv);
//        myShimmerTextView1 = findViewById(R.id.shimmer_tv1);
//        shimmer = new Shimmer();
//        shimmer.start(myShimmerTextView);
//        shimmer.start(myShimmerTextView1);

        //String file = Environment.getExternalStorageDirectory().toString();
        onCreateAudioPath();
        if (Build.VERSION.SDK_INT >= 23) {
            checkMultiplePermissions();
        }

        AdmodUtils.getInstance().loadNativeAdsWithLayout(this,
                getString(R.string.test_ads_admob_native_id),
                findViewById(R.id.nativeAds), R.layout.ad_template_medium,new NativeAdCallback() {
                    @Override
                    public void onNativeAdLoaded() {

                    }

                    @Override
                    public void onAdFail() {

                    }
                });

        inIT();
//        exitDialog();
    }

    private void inIT() {

//        checkPermission();
        tvStart = findViewById(R.id.tvStart);
        tvCreation = findViewById(R.id.tvCreation);

        tvStart.setOnClickListener(this);
        tvCreation.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvStart:
                recordAudio();
                break;

            case R.id.tvCreation:
                AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(this, getString(R.string.test_ads_admob_inter_id), 0,
                        new AdCallback() {
                            @Override
                            public void onAdClosed() {
                                startActivity(new Intent(MainActivity.this, MyStudioActivity.class));
                            }

                            @Override
                            public void onAdFail() {
                                startActivity(new Intent(MainActivity.this, MyStudioActivity.class));
                            }
                        }, true);
                break;

        }
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 0 && i2 == -1) {
            if (StringUtils.isEmptyString(PATH_AUDIO)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(PATH_AUDIO);
            } else {
                Intent intent2 = new Intent(this, EffectActivity.class);
                intent2.putExtra(IVoiceChangerConstants.KEY_PATH_AUDIO, PATH_AUDIO);
                startActivity(intent2);
                onCreateAudioPath();

            }
        }
    }

    private void checkMultiplePermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionsNeeded = new ArrayList();
            List<String> permissionsList = new ArrayList();
            if (!addPermission(permissionsList, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                permissionsNeeded.add("Write Storage");
            }
            if (!addPermission(permissionsList, "android.permission.READ_EXTERNAL_STORAGE")) {
                permissionsNeeded.add("Read Storage");
            }
            if (!addPermission(permissionsList, "android.permission.RECORD_AUDIO")) {
                permissionsNeeded.add("Recoer Audio");
            }
            if (permissionsList.size() > 0) {
                requestPermissions((String[]) permissionsList.toArray(new String[permissionsList.size()]), REQ_CODE);
                return;
            }
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }
        return true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE:
                Map<String, Integer> perms = new HashMap();
                perms.put("android.permission.WRITE_EXTERNAL_STORAGE", Integer.valueOf(0));
                perms.put("android.permission.READ_EXTERNAL_STORAGE", Integer.valueOf(0));
                perms.put("android.permission.RECORD_AUDIO", Integer.valueOf(0));
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], Integer.valueOf(grantResults[i]));
                }

                if (((Integer) perms.get("android.permission.READ_EXTERNAL_STORAGE")).intValue() == PackageManager.PERMISSION_GRANTED
                        && ((Integer) perms.get("android.permission.WRITE_EXTERNAL_STORAGE")).intValue() == PackageManager.PERMISSION_GRANTED
                        && ((Integer) perms.get("android.permission.RECORD_AUDIO")).intValue() == PackageManager.PERMISSION_GRANTED) {
                    break;
                } else if (Build.VERSION.SDK_INT >= 23) {
                    Toast.makeText(getApplicationContext(), "My App cannot run without Storage Permissions.\nRelaunch My App or allow permissions in Applications Settings", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                } else {
                    break;
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
        }
    }


//    public void checkPermission(){
//
//        PermissionListener permissionListener = new PermissionListener() {
//            @Override
//            public void onPermissionGranted() {
//
//            }
//
//            @Override
//            public void onPermissionDenied(List<String> deniedPermissions) {
//                Toast.makeText(MainActivity.this, "Please Allow All Permission to Use Application", Toast.LENGTH_SHORT).show();
//            }
//        };
//        TedPermission.with(MainActivity.this).setPermissionListener(permissionListener)
//                .setPermissions(Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE).check();
//    }
//    private void checkMultiplePermissions() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            List<String> permissionsNeeded = new ArrayList();
//            List<String> permissionsList = new ArrayList();
//            if (!addPermission(permissionsList, "android.permission.WRITE_EXTERNAL_STORAGE")) {
//                permissionsNeeded.add("Write Storage");
//            }
//            if (!addPermission(permissionsList, "android.permission.READ_EXTERNAL_STORAGE")) {
//                permissionsNeeded.add("Read Storage");
//            }
//            if (!addPermission(permissionsList, "android.permission.RECORD_AUDIO")) {
//                permissionsNeeded.add("Recoer Audio");
//            }
//            if (permissionsList.size() > 0) {
//                requestPermissions((String[]) permissionsList.toArray(new String[permissionsList.size()]), REQ_CODE);
//                return;
//            }
//        }
//    }

//    private boolean addPermission(List<String> permissionsList, String permission) {
//        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
//            permissionsList.add(permission);
//            if (!shouldShowRequestPermissionRationale(permission)) {
//                return false;
//            }
//        }
//        return true;
//    }

//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQ_CODE:
//                Map<String, Integer> perms = new HashMap();
//                perms.put("android.permission.WRITE_EXTERNAL_STORAGE", Integer.valueOf(0));
//                perms.put("android.permission.READ_EXTERNAL_STORAGE", Integer.valueOf(0));
//                perms.put("android.permission.RECORD_AUDIO", Integer.valueOf(0));
//                for (int i = 0; i < permissions.length; i++) {
//                    perms.put(permissions[i], Integer.valueOf(grantResults[i]));
//                }
//
//                if (((Integer) perms.get("android.permission.READ_EXTERNAL_STORAGE")).intValue() == 0
//                        && ((Integer) perms.get("android.permission.WRITE_EXTERNAL_STORAGE")).intValue() == 0
//                        && ((Integer) perms.get("android.permission.RECORD_AUDIO")).intValue() == 0) {
//                    break;
//                } else if (Build.VERSION.SDK_INT >= 23) {
//                    Toast.makeText(getApplicationContext(), "My App cannot run without Storage Permissions.\nRelaunch My App or allow permissions in Applications Settings", Toast.LENGTH_LONG).show();
//                    finish();
//                    break;
//                } else {
//                    break;
//                }
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//                return;
//        }
//    }

    public void recordAudio() {
        AndroidAudioRecorder.with((Activity) this).setFilePath(PATH_AUDIO).setColor(this.color).setRequestCode(0).setSource(AudioSource.MIC).setChannel(AudioChannel.STEREO).setSampleRate(AudioSampleRate.HZ_48000).setAutoStart(false).setKeepDisplayOn(true).record();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = (MainActivity.this).getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.customdialog, null);
        builder.setView(dialogView);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to exit?")

                .setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                        ImageView img = (ImageView)findViewById(R.id.imageofspleshscreen);
//                        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
//                        img.startAnimation(aniFade);
//                        img.setVisibility(View.VISIBLE);
//                        img.startAnimation(aniFade);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setNeutralButton("Rate us", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri1 = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Intent inrate = new Intent(Intent.ACTION_VIEW, uri1);

                try {
                    startActivity(inrate);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "You don't have Google Play installed", Toast.LENGTH_LONG).show();
                }
            }
        });
        AdLoader.Builder builder1 = new AdLoader.Builder(
                this, "ca-app-pub-3940256099942544/2247696110");
        builder1.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                TemplateView template = dialogView.findViewById(R.id.my_template);
                template.setNativeAd(unifiedNativeAd);
            }
        });
        AdLoader adLoader = builder1.build();
        adLoader.loadAd(new AdRequest.Builder().build());

        builder.create();
        builder.show();
    }

    private void onCreateAudioPath(){
        File file = getExternalFilesDir(Environment.DIRECTORY_MUSIC + File.separator + "CallVoiceChanger");
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(file);
        stringBuilder.append("/Audio-");
        stringBuilder.append(path);
        stringBuilder.append("raw");
        stringBuilder.append(IVoiceChangerConstants.AUDIO_RECORDER_FILE_EXT_MP3);
        PATH_AUDIO = stringBuilder.toString();
    }

}

