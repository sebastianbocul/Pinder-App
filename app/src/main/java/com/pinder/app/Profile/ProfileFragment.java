package com.pinder.app.Profile;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.pinder.app.Images.ImageAdapter;
import com.pinder.app.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private static int RESULT_LOAD_IMAGE = 1;
    ViewPager viewPager;
    private ImageView mAddImage, mDeleteImage, setDefaultButton;
    private EditText mNameField;
    private EditText descriptionEditText;
    private ProfileViewModel profileViewModel;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        descriptionEditText = getView().findViewById(R.id.description);
        mNameField = getView().findViewById(R.id.name);
        mAddImage = getView().findViewById(R.id.addImage);
        mDeleteImage = getView().findViewById(R.id.delImage);
        setDefaultButton = getView().findViewById(R.id.setDefaultButton);
        viewPager = getView().findViewById(R.id.viewPager);
        profileViewModel = new ViewModelProvider(getActivity()).get(ProfileViewModel.class);
        if (viewPager.getChildCount() != 0) {
        } else {
        }
        profileViewModel.getImages().observe(getActivity(), new Observer<ArrayList>() {
            @Override
            public void onChanged(ArrayList arrayList) {
                if (arrayList.size() == 0) {
                    viewPager.setBackgroundResource(R.drawable.profile_default);
                } else {
                    viewPager.setBackgroundColor(Color.parseColor("#fafafa"));
                }
                ImageAdapter adapter = new ImageAdapter(getContext(), arrayList);
                viewPager.setAdapter(adapter);
            }
        });
        profileViewModel.loadImages();
        profileViewModel.getName().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mNameField.setText(s);
            }
        });
        profileViewModel.getDescription().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                descriptionEditText.setText(s);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                profileViewModel.setImagePosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        setDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultPicture();
            }
        });
        mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });
        descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            //set max lines in descriptions field
            @Override
            public void afterTextChanged(Editable editable) {
                if (null != descriptionEditText.getLayout() && descriptionEditText.getLayout().getLineCount() > 5) {
                    descriptionEditText.getText().delete(descriptionEditText.getText().length() - 1, descriptionEditText.getText().length());
                }
            }
        });
    }

    private void setDefaultPicture() {
        if (viewPager.getAdapter().getCount() != 0) {
            profileViewModel.setDefault(getContext());
        } else
            Toast.makeText(getContext(), "Add images first!", Toast.LENGTH_SHORT).show();
    }

    private void deleteImage() {
        if (viewPager.getAdapter().getCount() != 0) {
            profileViewModel.deleteImage(getActivity());
        } else
            Toast.makeText(getContext(), "Add images first!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri resultUri = null;
        if (data != null) {
            resultUri = data.getData();
            Toast.makeText(getContext(), "Uploading image...", Toast.LENGTH_SHORT).show();
        }
        if (resultUri != null) {
            profileViewModel.addImage(getActivity(), resultUri);
        } else {
            return;
        }
    }

    @Override
    public void onDetach() {
        String nameEdt = mNameField.getText().toString();
        String descriptionEdt = descriptionEditText.getText().toString().trim();
        profileViewModel.saveUserInformation(nameEdt, descriptionEdt);
        super.onDetach();
    }
}
