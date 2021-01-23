package com.pinder.app.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pinder.app.ui.MainFragment;
import com.pinder.app.ui.MatchesFragment;
import com.pinder.app.ui.ProfileFragment;
import com.pinder.app.ui.SettingsFragment;
import com.pinder.app.ui.TagsManagerFragment;

public class MainFragmentManagerPagerAdapter extends FragmentStatePagerAdapter {
    int mNoOfTabs;
    Bundle extras;

    public MainFragmentManagerPagerAdapter(@NonNull FragmentManager fm, int NumberOfTabs, Bundle extras) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
        this.extras = extras;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new SettingsFragment();
                break;
            case 1:
                fragment = new ProfileFragment();
                break;
            case 2:
                fragment = new MainFragment();
                fragment.setArguments(extras);
                break;
            case 3:
                fragment = new MatchesFragment();
                fragment.setArguments(extras);
                break;
            case 4:
                fragment = new TagsManagerFragment();
                break;
        }
        extras = null;
        return fragment;
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
