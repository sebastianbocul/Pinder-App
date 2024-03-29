package com.pinder.app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pinder.app.ui.PopularTagsFragment;
import com.pinder.app.ui.TagsFragment;

public class TagsManagerPagerAdapter extends FragmentStatePagerAdapter {
    int mNoOfTabs;

    public TagsManagerPagerAdapter(@NonNull FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TagsFragment tagsManagerFragment = new TagsFragment();
                return tagsManagerFragment;
            case 1:
                PopularTagsFragment popularTagsFragment = new PopularTagsFragment();
                return popularTagsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
