package com.pinder.app.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pinder.app.R;
import com.pinder.app.adapters.PopularTagsAdapter;
import com.pinder.app.models.PopularTagsObject;
import com.pinder.app.util.Resource;
import com.pinder.app.viewmodels.PopularTagsViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopularTagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class PopularTagsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private static final String TAG = "PopularTagsFragment";
    @Inject
    public PopularTagsViewModel popularTagsViewModel;

    public PopularTagsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopularTagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularTagsFragment newInstance(String param1, String param2) {
        PopularTagsFragment fragment = new PopularTagsFragment();
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
        return inflater.inflate(R.layout.fragment_popular_tags, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView popularTagsRecyclerView = getView().findViewById(R.id.popularTagsRecyclerView);
        ArrayList<PopularTagsObject> arrayList = new ArrayList<>();
        popularTagsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        PopularTagsAdapter tagsPopularAdapter = new PopularTagsAdapter(getContext(), arrayList);
        popularTagsRecyclerView.setAdapter(tagsPopularAdapter);
        popularTagsViewModel.getAllPopularTags().observe(getActivity(), new Observer<Resource<List<PopularTagsObject>>>() {
            @Override
            public void onChanged(Resource<List<PopularTagsObject>> popularTagsObjects) {
                if (popularTagsObjects != null) {
                    switch (popularTagsObjects.status) {
                        case LOADING:
                            if (popularTagsObjects.data != null) {
                                Log.d(TAG, "onChangd LADING: " + popularTagsObjects.data);
                                arrayList.clear();
                                arrayList.addAll(popularTagsObjects.data);
                                tagsPopularAdapter.notifyDataSetChanged();
                            }
                            break;
                        case SUCCESS:
                            if (popularTagsObjects.data != null) {
                                Log.d(TAG, "onChanged: SUCCESS" + popularTagsObjects.data);
                                arrayList.clear();
                                arrayList.addAll(popularTagsObjects.data);
                                tagsPopularAdapter.notifyDataSetChanged();
                            }
                            break;
                        case ERROR:
                            Log.e(TAG, "Failed loading tags ");
                    }
                }
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
