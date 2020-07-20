package com.pinder.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pinder.app.R;

public class MainFragmentMenager extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_menager);
//        if (findViewById(R.id.fragment_layout) != null) {
//
//            // However, if we're being restored from a previous state,
//            // then we don't need to do anything and should return or else
//            // we could end up with overlapping fragments.
//            if (savedInstanceState != null) {
//                return;
//            }
//
//            // Create an instance of Fragment1
//            MainFragment firstFragment = new MainFragment();
//
//            // In case this activity was started with special instructions from an Intent,
//            // pass the Intent's extras to the fragment as arguments
//            firstFragment.setArguments(getIntent().getExtras());
//
//            // Add the fragment to the 'fragment_container' FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_layout, firstFragment).commit();
//        }
//        final ViewPager viewPager = findViewById(R.id.pager);
//        MatchesFragment matchesFrag = new MatchesFragment();
//        viewPager.setAdapter(adapter);
//        viewPager.setCurrentItem(matchesFrag);
//        ProfileFragment textFragment = (ProfileFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.profile_fragment);
    }
//    @Override
//    public void onBackPressed() {
////        Fragment profileFragment = new ProfileFragment();
////        FragmentManager fm = getSupportFragmentManager();
////        FragmentTransaction transaction = fm.beginTransaction();
////        transaction.replace(R.id.main_fragment,profileFragment);
////        transaction.commit();
//
//
//        // Create an instance of Fragment1
//        ProfileFragment firstFragment = new ProfileFragment();
//
//        // In case this activity was started with special instructions from an Intent,
//        // pass the Intent's extras to the fragment as arguments
//        firstFragment.setArguments(getIntent().getExtras());
//
//        // Add the fragment to the 'fragment_container' FrameLayout
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_layout, firstFragment).commit();
//
//   //     setContentView(R.layout.fragment_profile);
////        Intent startMain = new Intent(Intent.ACTION_MAIN);
////        startMain.addCategory(Intent.CATEGORY_HOME);
////        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        startActivity(startMain);
//    }

    public void onBack(View view) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

}
