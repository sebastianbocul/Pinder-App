package com.pinder.app.legalInfo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.pinder.app.R;

public class LicencesDialog extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_licences, null);
        TextView rxJavaTextView = view.findViewById(R.id.rxJavaLicence);
        rxJavaTextView.setText(Html.fromHtml(getString(R.string.rx_java_licence)));
        TextView googlePlayServicesTextView = view.findViewById(R.id.googlePlayServicesLicence);
        googlePlayServicesTextView.setText(Html.fromHtml(getString(R.string.google_play_services_licence)));
        TextView retrofitTextView = view.findViewById(R.id.retrofitLicence);
        retrofitTextView.setText(Html.fromHtml(getString(R.string.retrofit_licence)));
        TextView glideTextView = view.findViewById(R.id.glideLicence);
        glideTextView.setText(Html.fromHtml(getString(R.string.glide_licence)));
        TextView swipecardsTextView = view.findViewById(R.id.swipecardsLicence);
        swipecardsTextView.setText(Html.fromHtml(getString(R.string.swipecards_licence)));
        TextView circleImageTextView = view.findViewById(R.id.circleImageViewLicecne);
        circleImageTextView.setText(Html.fromHtml(getString(R.string.circleimageview_licence)));
        TextView crystalrangeseekerTextView = view.findViewById(R.id.crystalrange_licence);
        crystalrangeseekerTextView.setText(Html.fromHtml(getString(R.string.crystalrange_licence)));
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return builder.create();
    }
}
