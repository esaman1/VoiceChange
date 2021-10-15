package com.vapp.admoblibrary;

import android.app.Application;

import com.vapp.admoblibrary.ads.AdmodUtils;
import com.vapp.admoblibrary.ads.AppOpenManager;

import java.util.List;

public abstract class AdsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AdmodUtils.getInstance().initAdmob(this, 10000,false, true);
        if(enableAdsResume()) {
            AppOpenManager.getInstance().init(this, getOpenAppAdId());
        }
    }

    public abstract boolean enableAdsResume();

    public abstract String getOpenAppAdId();
}
