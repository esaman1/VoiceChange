package com.vapp.admoblibrary;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.vapp.admoblibrary.ads.AdmodUtils;
import com.vapp.admoblibrary.ads.AppOpenManager;

import java.util.List;

public abstract class AdsMultiDexApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
