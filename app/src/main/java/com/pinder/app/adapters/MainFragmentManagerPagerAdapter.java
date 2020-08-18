package com.pinder.app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pinder.app.R;
import com.pinder.app.ui.MainFragment;
import com.pinder.app.ui.MatchesFragment;
import com.pinder.app.ui.PopularTagsFragment;
import com.pinder.app.ui.ProfileFragment;
import com.pinder.app.ui.SettingsFragment;
import com.pinder.app.ui.TagsFragment;
import com.pinder.app.ui.TagsManagerFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    int mNoOfTabs;

    public MainPagerAdapter(@NonNull FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//        Fragment selectedFragment = null;
//        switch (position) {
//            case R.id.nav_settings:
//                selectedFragment = new SettingsFragment();
//                break;
//            case R.id.nav_tags:
//                selectedFragment = new TagsManagerFragment();
//                break;
//            case R.id.nav_main:
//                selectedFragment = new MainFragment();
//                break;
//            case R.id.nav_matches:
//                selectedFragment = new MatchesFragment();
//                break;
//            case R.id.nav_profile:
//                selectedFragment = new ProfileFragment();
//                break;
//        }
//        return selectedFragment;
//    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SettingsFragment();
            case 1:
                return new TagsManagerFragment();
            case 2:
                return new MainFragment();
            case 3:
                return new MatchesFragment();
            case 4:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
