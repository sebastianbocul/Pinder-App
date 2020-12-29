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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
 * Use the {@link RegistrationEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class RegistrationEmailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button mRegister;
    private EditText mEmail, mPassword, mName, mRepeatPassword;
    private RadioGroup mRadioGroup;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private EditText date;
    private boolean dateValid = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @Inject
    AuthViewModel authViewModel;

    public RegistrationEmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationEmailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationEmailFragment newInstance(String param1, String param2) {
        RegistrationEmailFragment fragment = new RegistrationEmailFragment();
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
        return inflater.inflate(R.layout.fragment_registration_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent;
                    if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
                        intent = new Intent(getActivity(), MainFragmentManager.class);
                    } else {
                        intent = new Intent(getActivity(), RequestLocationPermissionActivity.class);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        mRegister = getView().findViewById(R.id.register);
        mEmail = getView().findViewById(R.id.email);
        mPassword = getView().findViewById(R.id.password);
        mRepeatPassword = getView().findViewById(R.id.repeatpassword);
        mRadioGroup = getView().findViewById(R.id.radioGroup);
        date = getView().findViewById(R.id.date);
        mAuth = FirebaseAuth.getInstance();
        mName = getView().findViewById(R.id.name);
        date.addTextChangedListener(new TextWatcher() {
            String clean;
            String cleanC;
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

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

    public void register() {
        try {
            int selectedId = mRadioGroup.getCheckedRadioButtonId();
            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();
            final String name = mName.getText().toString();
            final String repeatpassword = mRepeatPassword.getText().toString();
            final RadioButton radioButton = getView().findViewById(selectedId);
            if (!dateValid) {
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
            if (email.isEmpty() || password.isEmpty() || repeatpassword.isEmpty() || name.isEmpty()) {
                Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(getContext(), "Password too short. Minimum length is 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(repeatpassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(), "User with that email already exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
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
                        userInfo.put("sex", radioButton.getText().toString());
                        userInfo.put("profileImageUrl", "default");
                        userInfo.put("dateOfBirth", dateOfBirth);
                        userInfo.put("tags", tagsMap);
                        userInfo.put("showMyLocation", true);
                        currentUserDb.updateChildren(userInfo);
                        DatabaseReference tags = FirebaseDatabase.getInstance().getReference().child("Tags");
                        tags.child("default").child(userId).setValue(true);
                        Toast.makeText(getContext(), "Register successful!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Fill all fields!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}