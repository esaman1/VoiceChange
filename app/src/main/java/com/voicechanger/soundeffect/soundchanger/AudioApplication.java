package com.voicechanger.soundeffect.soundchanger;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.github.axet.androidlibrary.app.NotificationManagerCompat;
import com.github.axet.androidlibrary.widgets.NotificationChannelCompat;
import com.vapp.admoblibrary.AdsMultiDexApplication;
import com.vapp.admoblibrary.ads.AdmodUtils;
import com.vapp.admoblibrary.ads.AppOpenManager;
import com.vapp.admoblibrary.iap.PurchaseUtils;
import com.voicechanger.soundeffect.soundchanger.activity.SplashScreenActivity;
import com.voicerecorder.axet.audiolibrary.app.MainApplication;

public class AudioApplication extends MainApplication {
    public static final String CHANNEL_ID = "push_notification_id";
    public static Context appContext;
    boolean isShowAds = false;
    boolean isShowAdsResume = false;
    public static final String PREFERENCE_NEXT = "next";
    public NotificationChannelCompat channelStatus;
    public static final String PREFERENCE_TARGET = "target";

    public static AudioApplication from(Context context) {
        return (AudioApplication) MainApplication.from(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        PurchaseUtils.getInstance().initBilling(this,getString(R.string.play_console_license));
        PurchaseUtils.getInstance().isPurchased(getString(R.string.product_id));

        channelStatus = new NotificationChannelCompat(this, "status", "Status", NotificationManagerCompat.IMPORTANCE_LOW);

        AdmodUtils.getInstance().initAdmob(this, 10000,true, isShowAds);
        if (isShowAdsResume) {
            AppOpenManager.getInstance().init(this, getString(R.string.test_ads_admob_app_open));
            AppOpenManager.getInstance().disableAppResumeWithActivity(SplashScreenActivity.class);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void createNotifyChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "PushNotification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
