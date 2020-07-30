package com.pinder.app;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.pinder.app.Matches.MatchesFragment;
import com.pinder.app.Profile.ProfileFragment;
import com.pinder.app.Settings.SettingsFragment;
import com.pinder.app.Tags.TagsManagerFragment;

public class MainFragmentMenager extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_menager);
        int fragmentContainer = R.id.fragment_container;
        getSupportFragmentManager().beginTransaction().replace(fragmentContainer, new MainFragment()).commit();
        ImageView main = findViewById(R.id.main);
        ImageView tags = findViewById(R.id.tags);
        ImageView profile = findViewById(R.id.profile);
        ImageView settings = findViewById(R.id.settings);
        ImageView matches = findViewById(R.id.matches);
        main.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer, new MainFragment()).commit();
        });
        tags.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer, new TagsManagerFragment()).commit();
        });
        profile.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer, new ProfileFragment()).commit();
        });
        settings.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer, new SettingsFragment()).commit();
        });
        matches.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer, new MatchesFragment()).commit();
        });
    }


   
}
