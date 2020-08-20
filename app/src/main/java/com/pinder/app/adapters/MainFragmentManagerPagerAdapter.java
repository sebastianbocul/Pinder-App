package com.pinder.app.adapters;

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

    public MainFragmentManagerPagerAdapter(@NonNull FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SettingsFragment();
            case 1:
                return new ProfileFragment();
            case 2:
                return new MainFragment();
            case 3:
                return new MatchesFragment();
            case 4:
                return new TagsManagerFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
