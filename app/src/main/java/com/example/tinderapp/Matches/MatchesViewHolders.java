package com.example.tinderapp.Matches;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tinderapp.Chat.ChatActivity;
import com.example.tinderapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;


public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchName,mLastMessage;
    public CircularImageView mMatchImage;
    String mMatchUserId;
    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mLastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
        mMatchImage = (CircularImageView) itemView.findViewById(R.id.MatchImage);

    }

    @Override
    public void onClick(View view){
        try {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            Bundle b = new Bundle();
            b.putString("matchId", mMatchUserId);
            intent.putExtras(b);
            view.getContext().startActivity(intent);
        }catch (Exception e){
            Toast.makeText(view.getContext(),"Oops something went wrong",Toast.LENGTH_SHORT).show();
        }
    }
}
