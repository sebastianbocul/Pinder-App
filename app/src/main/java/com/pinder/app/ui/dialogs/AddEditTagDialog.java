package com.pinder.app.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.pinder.app.R;
import com.pinder.app.models.TagsObject;
import com.pinder.app.viewmodels.TagsFragmentViewModel;

import java.util.ArrayList;

public class AddEditTagDialog extends AppCompatDialogFragment {
    private TextView ageRangeTextView, maxDistanceTextView;
    private CrystalRangeSeekbar ageRangeSeeker;
    private CrystalSeekbar maxDistanceSeeker;
    private EditText tagsEditText;
    private RadioGroup mRadioGroup;
    private String ageMin, ageMax, distanceMax;
    private TagsFragmentViewModel tagsFragmentViewModel;
    private ArrayList<TagsObject> arrayList = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_tag, null);

        ageRangeSeeker = view.findViewById(R.id.ageRangeSeeker);
        ageRangeTextView = view.findViewById(R.id.ageRangeTextView);
        maxDistanceSeeker = view.findViewById(R.id.distanceSeeker);
        maxDistanceTextView  =view.findViewById(R.id.maxDistanceTextView);
        tagsEditText = view.findViewById(R.id.tagsEditText);
        mRadioGroup = view.findViewById(R.id.radioGroup);
        maxDistanceSeeker.setMinStartValue(100);
        maxDistanceSeeker.apply();


        tagsFragmentViewModel = new ViewModelProvider(this).get(TagsFragmentViewModel.class);

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

        String dialogTitle = "Add search tag";
        //get tag from bundle/ if clicked add button or item selected - get string from bundle
        builder.setView(view)
                .setCancelable(false)
                .setTitle(dialogTitle)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addTagButtonFunction(view);
                    }
                });
        return builder.create();
    }

    private void addTagButtonFunction(View view) {
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
        RadioButton radioButton = view.findViewById(selectedId);
        String tagName = tagsEditText.getText().toString().toLowerCase();
        String gender = radioButton.getText().toString();
        String mAgeMax = ageMax;
        String mAgeMin = ageMin;
        String mDistance = distanceMax;
        TagsObject tag = new TagsObject(tagName, gender, mAgeMin, mAgeMax, mDistance);
        tagsFragmentViewModel.addTag(tag);
        Toast.makeText(getContext(), "Tag added!", Toast.LENGTH_SHORT).show();
    }
}
