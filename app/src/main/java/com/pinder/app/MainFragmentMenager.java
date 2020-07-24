package com.pinder.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pinder.app.Matches.MatchesFragment;
import com.pinder.app.R;
import com.pinder.app.Tags.MyInterface;
import com.pinder.app.Tags.TagsManagerFragment;
import com.pinder.app.Tags.TagsObject;

import java.util.ArrayList;

public class MainFragmentMenager extends AppCompatActivity implements MyInterface {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_menager);
        int fragmentContainer = R.id.fragment_container;
        getSupportFragmentManager().beginTransaction().replace(fragmentContainer,new MainFragment()).commit();

        ImageView main = findViewById(R.id.main);
        ImageView tags = findViewById(R.id.tags);
        ImageView profile = findViewById(R.id.profile);
        ImageView settings = findViewById(R.id.settings);
        ImageView matches = findViewById(R.id.matches);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(fragmentContainer,new MainFragment()).commit();
            }
        });
        tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(fragmentContainer,new TagsManagerFragment()).commit();
            }
        });
        profile.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer,new ProfileFragment()).commit();
        });

        settings.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer,new SettingsFragment()).commit();
        });

        matches.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction().replace(fragmentContainer,new MatchesFragment()).commit();
        });
    }

    @Override
    public void doSomethingWithData(ArrayList<TagsObject> myTagsList2, ArrayList<TagsObject> removedTagList2) {
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }
}
