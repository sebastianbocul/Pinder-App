package com.pinder.app;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pinder.app.adapters.MainFragmentManagerPagerAdapter;
import com.pinder.app.util.HideSoftKeyboard;

import dagger.hilt.android.AndroidEntryPoint;

import static com.pinder.app.BaseApplication.*;
import static com.pinder.app.BaseApplication.LoginEnum.*;

@AndroidEntryPoint
public class MainFragmentManager extends AppCompatActivity {
    private static final String TAG = "MainFragmentManager";
    private BottomNavigationView bottomNavigationView;
    private String fromActivity = "";
    private AdView mBannerAd;
    private FrameLayout adContainerView;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_menager);
        UserStatus = LOGGED;
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_main);
//        initAd();
        initAdaptiveAd();
        final ViewPager viewPager = findViewById(R.id.pager);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("fromActivity") != null) {
                fromActivity = getIntent().getExtras().getString("fromActivity");
            }
        }
        Log.d(TAG, "  bottomNavigationView.getMaxItemCount(): " + bottomNavigationView.getMaxItemCount());
        MainFragmentManagerPagerAdapter adapter = new MainFragmentManagerPagerAdapter(getSupportFragmentManager(), bottomNavigationView.getMaxItemCount());
        viewPager.setAdapter(adapter);
        if (fromActivity.equals("chatActivity")) {
            viewPager.setCurrentItem(3);
            bottomNavigationView.setSelectedItemId(R.id.nav_matches);
        } else {
            viewPager.setCurrentItem(2);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                HideSoftKeyboard.hideKeyboard(MainFragmentManager.this);
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_settings).setChecked(true);
                        break;
                    case 1:
                        Log.d("MainFragment", "onPageSelected:  tags");
                        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
                        break;
                    case 2:
                        Log.d("MainFragment", "onPageSelected:  main");
                        bottomNavigationView.getMenu().findItem(R.id.nav_main).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.nav_matches).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.nav_tags).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_profile:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_main:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_matches:
                        viewPager.setCurrentItem(3);
                        break;
                    case R.id.nav_tags:
                        viewPager.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
    }


    public void replaceTabPage(int tabPage) {
        bottomNavigationView.setSelectedItemId(tabPage);
    }

    public void initAd(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        mBannerAd = findViewById(R.id.ad_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAd.loadAd(adRequest);
    }
    private void initAdaptiveAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) { }
        });

        adContainerView = findViewById(R.id.ad_frame);
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.test_ad));
        adContainerView.addView(adView);
        loadBanner();
    }


    private void loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

        AdSize adSize = getAdSize();
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);


        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }
}


   

