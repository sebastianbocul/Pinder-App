package com.pinder.app.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.pinder.app.R;
import com.pinder.app.adapters.TagsAdapter;
import com.pinder.app.models.TagsObject;
import com.pinder.app.viewmodels.TagsFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView ageRangeTextView, maxDistanceTextView;
    private CrystalRangeSeekbar ageRangeSeeker;
    private CrystalSeekbar maxDistanceSeeker;
    private TagsAdapter adapter;
    private Button addTagButton;
    private EditText tagsEditText;
    private RadioGroup mRadioGroup;
    private String ageMin, ageMax, distanceMax;
    private RecyclerView recyclerView;
    private ArrayList<TagsObject> arrayList = new ArrayList<>();
    private TagsFragmentViewModel tagsFragmentViewModel;

    public TagsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagsFragment newInstance(String param1, String param2) {
        TagsFragment fragment = new TagsFragment();
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
        return inflater.inflate(R.layout.fragment_tags, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ageRangeSeeker = getView().findViewById(R.id.ageRangeSeeker);
        ageRangeTextView = getView().findViewById(R.id.ageRangeTextView);
        maxDistanceSeeker = getView().findViewById(R.id.distanceSeeker);
        maxDistanceTextView = getView().findViewById(R.id.maxDistanceTextView);
        tagsEditText = getView().findViewById(R.id.tagsEditText);
        addTagButton = getView().findViewById(R.id.addButton);
        mRadioGroup = getView().findViewById(R.id.radioGroup);
        maxDistanceSeeker.setMinStartValue(100);
        maxDistanceSeeker.apply();
        recyclerView = getView().findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        tagsFragmentViewModel = new ViewModelProvider(this).get(TagsFragmentViewModel.class);
        adapter = new TagsAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        fillTagsAdapter();
        ageRangeSeeker.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ageRangeTextView.setText("Age range: " + minValue.toString() + " - " + maxValue.toString());
                ageMin = minValue.toString();
                ageMax = maxValue.toString();
            }
        });
        maxDistanceSeeker.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue) {
                if (minValue.intValue() == 1000) {
                    minValue = 100000;
                    maxDistanceTextView.setText("Max distance: " + "∞");
                } else {
                    maxDistanceTextView.setText("Max distance: " + minValue + " km");
                }
                distanceMax = minValue.toString();
            }
        });
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTagButtonFunction();
            }
        });
        adapter.setOnItemClickListener(new TagsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
        adapter.setOnItemClickListener(new TagsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void addTagButtonFunction() {
        if (tagsEditText.getText().toString().length() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "Fill tag name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity().getApplicationContext(), "Choose gender to find", Toast.LENGTH_SHORT).show();
            return;
        }
        for (TagsObject tmo : arrayList) {
            if (tmo.getTagName().equals(tagsEditText.getText().toString().toLowerCase())) {
                Toast.makeText(getActivity().getApplicationContext(), "Duplicate tag", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = getView().findViewById(selectedId);
        String tagName = tagsEditText.getText().toString().toLowerCase();
        String gender = radioButton.getText().toString();
        String mAgeMax = ageMax;
        String mAgeMin = ageMin;
        String mDistance = distanceMax;
        TagsObject tag = new TagsObject(tagName, gender, mAgeMin, mAgeMax, mDistance);
        tagsFragmentViewModel.addTag(tag);
        Toast.makeText(getContext(), "Tag added!", Toast.LENGTH_SHORT).show();
    }

    private void fillTagsAdapter() {
        tagsFragmentViewModel.getAllTags().observe(getActivity(), new Observer<List<TagsObject>>() {
            @Override
            public void onChanged(List<TagsObject> tagsObjects) {
                arrayList.clear();
                arrayList.addAll(tagsObjects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void removeItem(int position) {
        TagsObject tagToDel = adapter.getItem(position);
        tagsFragmentViewModel.removeTag(tagToDel);
    }
}
