package com.pinder.app.adapters;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bumptech.glide.Glide;
import com.pinder.app.R;
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

//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        // If we already have this item instantiated, there is nothing
//        // to do.  This can happen when we are restoring the entire pager
//        // from its saved state, where the fragment manager has already
//        // taken care of restoring the fragments we previously had instantiated.
//        if (mFragments.size() > position) {
//            Fragment f = mFragments.get(position);
//            if (f != null) {
//                if (mCurTransaction == null) {
//                    mCurTransaction = mFragmentManager.beginTransaction();
//                }
//
//                mCurTransaction.detach(f);
//                mCurTransaction.attach(f);
//
//                return f;
//            }
//        }
//    }
}
