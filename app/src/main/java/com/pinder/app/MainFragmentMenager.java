package com.pinder.app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pinder.app.ui.MainFragment;
import com.pinder.app.ui.MatchesFragment;
import com.pinder.app.ui.ProfileFragment;
import com.pinder.app.ui.SettingsFragment;
import com.pinder.app.ui.TagsManagerFragment;

public class MainFragmentMenager extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_menager);
        int fragmentContainer = R.id.fragment_container;
        getSupportFragmentManager().beginTransaction().replace(fragmentContainer, new MainFragment()).commit();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_settings:
                    selectedFragment = new SettingsFragment();
                    break;
                case R.id.nav_tags:
                    selectedFragment = new TagsManagerFragment();
                    break;
                case R.id.nav_main:
                    selectedFragment = new MainFragment();
                    break;
                case R.id.nav_matches:
                    selectedFragment = new MatchesFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}

   

