package com.pinder.app;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pinder.app.util.StringDateToAge;
import com.pinder.app.viewmodels.AuthViewModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationExternalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class RegistrationExternalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Inject
    AuthViewModel authViewModel;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button mRegister;
    private EditText mName;
    private RadioGroup mRadioGroup;
    private boolean dateValid = false;
    private FirebaseAuth mAuth;
    private EditText date;

    public RegistrationExternalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationExternalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationExternalFragment newInstance(String param1, String param2) {
        RegistrationExternalFragment fragment = new RegistrationExternalFragment();
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
        return inflater.inflate(R.layout.fragment_registration_external, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date = getView().findViewById(R.id.date);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mRegister = getView().findViewById(R.id.register);
        mRadioGroup = getView().findViewById(R.id.radioGroup);
        mName = getView().findViewById(R.id.name);
        if (user != null) {
            if (user.getDisplayName() != null) {
                mName.setText(getFirstName(user.getDisplayName()));
            }
        }
        date.addTextChangedListener(new TextWatcher() {
            String clean;
            String cleanC;
            private String current = "";
            private final String ddmmyyyy = "DDMMYYYY";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().equals(current)) {
                        dateValid = false;
                        clean = s.toString().replaceAll("[^\\d.]", "");
                        cleanC = current.replaceAll("[^\\d.]", "");
                        int cl = clean.length();
                        int sel = cl;
                        for (int i = 2; i <= cl && i < 6; i += 2) {
                            sel++;
                        }
                        //Fix for pressing delete next to a forward slash
                        if (clean.equals(cleanC)) sel--;
                        if (clean.length() < 8) {
                            clean = clean + ddmmyyyy.substring(clean.length());
                        } else {
                            //This part makes sure that when we finish entering numbers
                            //the date is correct, fixing it otherwise
                            int day = Integer.parseInt(clean.substring(0, 2));
                            int mon = Integer.parseInt(clean.substring(2, 4));
                            int year = Integer.parseInt(clean.substring(4, 8));
                            if (mon > 12) mon = 12;
                            if (mon == 0) mon = 1;
                            cal.set(Calendar.MONTH, mon - 1);
                            year = (year < 1920) ? 1920 : (year > 2019) ? 2019 : year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012
                            dateValid = true;
                            day = (day < 1) ? 1 : (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                            clean = String.format("%02d%02d%02d", day, mon, year);
                        }
                        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                                clean.substring(2, 4),
                                clean.substring(4, 8));
                        sel = sel < 0 ? 0 : sel;
                        current = clean;
                        date.setText(current);
                        date.setSelection(sel < current.length() ? sel : current.length());
                    }
                } catch (Exception e) {
                    dateValid = false;
                    Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                    date.setText("");
                }
            }

            //set max lines in descriptions field
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        final String name = mName.getText().toString();
        if (!dateValid == true) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        int age = new StringDateToAge().stringDateToAge(date.getText().toString());
        if (age < 18) {
            Toast.makeText(getContext(), "You must be 18+", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            updateDb();
            changeActivity();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Opps something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void changeActivity() {
        Intent intent;
        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            intent = new Intent(getActivity(), MainActivity.class);
        } else {
            intent = new Intent(getActivity(), RequestLocationPermissionActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void updateDb() {
        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = getView().findViewById(selectedId);
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        String name = mName.getText().toString();
        String gender = radioButton.getText().toString();
        String dateOfBirth = date.getText().toString();
        Map tagsMap = new HashMap<>();
        Map tagInfo = new HashMap<>();
        Map userInfo = new HashMap<>();
        tagInfo.put("minAge", "18");
        tagInfo.put("maxAge", "99");
        tagInfo.put("maxDistance", "100000");
        if (radioButton.getText().toString().equals("Male")) {
            tagInfo.put("gender", "Female");
        } else tagInfo.put("gender", "Male");
        tagsMap.put("default", tagInfo);
        userInfo.put("name", name);
        userInfo.put("sex", gender);
        userInfo.put("dateOfBirth", dateOfBirth);
        userInfo.put("tags", tagsMap);
        userInfo.put("profileImageUrl", "default");
        userInfo.put("showMyLocation", true);
        mUserDatabase.updateChildren(userInfo);
        DatabaseReference tags = FirebaseDatabase.getInstance().getReference().child("Tags");
        tags.child("default").child(userId).setValue(true);
        Toast.makeText(getContext(), "Register successful!", Toast.LENGTH_SHORT).show();
    }

    private String getFirstName(String displayName) {
        String[] words = displayName.split(" ");
        return words[0];
    }
}