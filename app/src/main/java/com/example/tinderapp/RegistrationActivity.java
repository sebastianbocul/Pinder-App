package com.example.tinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinderapp.Tags.TagsManagerActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegister;

    private EditText mEmail, mPassword, mName,mRepeatPassword;

    private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private EditText date;
    private boolean dateValid = false;
    private int dd,mm,yyyy;

    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mRepeatPassword = (EditText) findViewById(R.id.repeatpassword);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        title = findViewById(R.id.title);
        date = (EditText) findViewById(R.id.date);
        mAuth = FirebaseAuth.getInstance();
        mName = (EditText) findViewById(R.id.name);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        date.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            String clean;
            String cleanC;

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
                            year = (year < 1900) ? 1900 : (year > 2019) ? 2019 : year;
                            cal.set(Calendar.YEAR, year);
                            // ^ first set year for the line below to work correctly
                            //with leap years - otherwise, date e.g. 29/02/2012
                            //would be automatically corrected to 28/02/2012
                            dateValid = true;

                            day = (day < 1) ? 1 : (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;


                            dd = day;
                            mm = mon;
                            yyyy = year;


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
                    Toast.makeText(RegistrationActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
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
                try {
                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String repeatpassword = mRepeatPassword.getText().toString();
                final RadioButton radioButton = (RadioButton) findViewById(selectedId);

                if(!dateValid==true) {
                    Toast.makeText(RegistrationActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mRadioGroup.getCheckedRadioButtonId()==-1){
                    Toast.makeText(RegistrationActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(email.isEmpty() || password.isEmpty() || repeatpassword.isEmpty() || name.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()<6){
                    Toast.makeText(RegistrationActivity.this,"Password too short. Minimum length is 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(repeatpassword)){
                    Toast.makeText(RegistrationActivity.this,"Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "sign_up_error", Toast.LENGTH_SHORT).show();
                            } else {
                                String userId = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                String dateOfBirth = date.getText().toString();

                                Map tagsMap = new HashMap<>();
                                Map tagInfo = new HashMap<>();
                                Map userInfo = new HashMap<>();

                                tagInfo.put("minAge","18");
                                tagInfo.put("maxAge","99");
                                tagInfo.put("maxDistance","100");
                                if(radioButton.getText().toString().equals("Male")){
                                    tagInfo.put("gender","Female");
                                }
                                else  tagInfo.put("gender","Male");
                                tagsMap.put("default",tagInfo);

                                userInfo.put("name", name);
                                userInfo.put("sex", radioButton.getText().toString());
                                userInfo.put("profileImageUrl", "default");
                                userInfo.put("dateOfBirth",dateOfBirth);
                                userInfo.put("tags",tagsMap);
                                userInfo.put("showMyLocation", true);
                                currentUserDb.updateChildren(userInfo);

                                DatabaseReference tags = FirebaseDatabase.getInstance().getReference().child("Tags");
                                tags.child("default").child(userId).setValue(true);


                                Toast.makeText(RegistrationActivity.this,"Register successful!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (Exception e){
                    Log.d("myError",e.toString());
                    Toast.makeText(RegistrationActivity.this, "Fill all fields!", Toast.LENGTH_SHORT).show();
                }
                }


        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
