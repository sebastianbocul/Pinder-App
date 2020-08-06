package com.pinder.app.matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.pinder.app.chat.ChatActivity;
import com.pinder.app.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchName, mLastMessage;
    public CircularImageView mMatchImage;
    String mMatchUserId;

    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mLastMessage = itemView.findViewById(R.id.lastMessage);
        mMatchName = itemView.findViewById(R.id.MatchName);
        mMatchImage = itemView.findViewById(R.id.MatchImage);
    }

    @Override
    public void onClick(View view) {
        try {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            Bundle b = new Bundle();
            b.putString("matchId", mMatchUserId);
            intent.putExtras(b);
            view.getContext().startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Oops something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
