package com.voicechanger.soundeffect.soundchanger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.vapp.admoblibrary.ads.AdCallback;
import com.vapp.admoblibrary.ads.AdmodUtils;
import com.voicechanger.soundeffect.soundchanger.Common;
import com.voicechanger.soundeffect.soundchanger.R;

public class AppIntroActivity extends AppCompatActivity {
    private ImageView ivNext;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_intro);
        ivNext = findViewById(R.id.ivNext);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager2);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        ivNext.setOnClickListener(v -> {
            if (tabLayout.getSelectedTabPosition() < 2) {
                tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition() + 1));
            } else {
                startMainActivity();
            }
        });

        viewPager2.setAdapter(new ViewPager2Adapter(this));
        dotsIndicator.setViewPager2(viewPager2);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            // Disable touch for Tab Layout
            tab.view.setClickable(false);
        }).attach();
    }

    private void startMainActivity() {
        AdmodUtils.getInstance().loadAndShowAdInterstitialWithCallback(this, getString(R.string.test_ads_admob_inter_id), 0,
                new AdCallback() {
                    @Override
                    public void onAdClosed() {
                        startActivity(new Intent(AppIntroActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onAdFail() {
                        startActivity(new Intent(AppIntroActivity.this, MainActivity.class));
                        finish();
                    }
                }, true);
    }

    public static class ViewPager2Adapter extends FragmentStateAdapter {
        public ViewPager2Adapter(FragmentActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                case 2:
                default:
                    return FragmentIntro1.newInstance(position);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    @Override
    public void onBackPressed() {
        if (tabLayout.getSelectedTabPosition() > 0) {
            tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition() - 1));
        } else {
            finish();
        }
    }
}