package com.pinder.app.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.pinder.app.R;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagsManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class TagsManagerFragment extends Fragment implements TagsFragment.OnFragmentInteractionListener, PopularTagsFragment.OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TagsManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagsManagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagsManagerFragment newInstance(String param1, String param2) {
        TagsManagerFragment fragment = new TagsManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tags_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = getActivity().findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("My tags"));
        tabLayout.addTab(tabLayout.newTab().setText("Popular tags"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        int fragmentContainer = R.id.fragment_container;
        getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainer, new TagsFragment()).commit();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(fragmentContainer, getFragment(tab.getPosition())).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public Fragment getFragment(int position) {
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
}
