package com.example.tinderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.tinderapp.Matches.MatchesFragment;

public class MainFragmentMenager extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_menager);

//        final ViewPager viewPager = findViewById(R.id.pager);
//        MatchesFragment matchesFrag = new MatchesFragment();
//        viewPager.setAdapter(adapter);
//        viewPager.setCurrentItem(matchesFrag);
    }
}
