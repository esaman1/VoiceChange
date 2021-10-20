package com.voicechanger.soundeffect.soundchanger;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.multidex.MultiDex;

import com.vapp.admoblibrary.AdsMultiDexApplication;
import com.vapp.admoblibrary.ads.AdmodUtils;
import com.vapp.admoblibrary.ads.AppOpenManager;
import com.vapp.admoblibrary.iap.PurchaseUtils;
import com.voicechanger.soundeffect.soundchanger.activity.SplashScreenActivity;

public class MyApplication extends AdsMultiDexApplication {
    public static final String CHANNEL_ID = "push_notification_id";
    public static Context appContext;
    boolean isShowAds = false;
    boolean isShowAdsResume = true;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotifyChannel();
        appContext = getApplicationContext();
        PurchaseUtils.getInstance().initBilling(this,getString(R.string.play_console_license));

//        if (PurchaseUtils.getInstance().isSubscriptiond(getString(R.string.premium))) {
//            isShowAds = false;
//        }else {
//            isShowAds = true;
//        }
        PurchaseUtils.getInstance().isPurchased(getString(R.string.product_id));

        AdmodUtils.getInstance().initAdmob(this, 10000,false, false);
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
