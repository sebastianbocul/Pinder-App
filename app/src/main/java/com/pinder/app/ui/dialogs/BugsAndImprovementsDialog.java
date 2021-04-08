package com.pinder.app.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pinder.app.R;

public class BugsAndImprovementsDialog extends AppCompatDialogFragment {
    private final String myId;
    private EditText editText;

    public BugsAndImprovementsDialog(String myId) {
        this.myId = myId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("BugsAndImprovements").push();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bugs_improvements, null);
        editText = view.findViewById(R.id.report_text);
        builder.setView(view)
                .setMessage("Please write a message")
                .setCancelable(false)
                .setTitle("Bugs and improvements")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = "";
                        if (editText != null) {
                            message = editText.getText().toString().trim();
                        }
                        Toast.makeText(getContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                        long millis = System.currentTimeMillis();
                        java.sql.Date date = new java.sql.Date(millis);
                        reference.child("reportedBy").setValue(myId);
                        reference.child("message").setValue(message);
                        reference.child("date").setValue(date.toString());
                    }
                });
        return builder.create();
    }
}
