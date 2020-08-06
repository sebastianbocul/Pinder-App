package com.pinder.app.chat;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.pinder.app.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatViewHolders extends ViewHolder implements View.OnClickListener {
    public TextView mMessaage;
    public LinearLayout mContainer;
    public CircleImageView mChatImage;

    public ChatViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mChatImage = itemView.findViewById(R.id.chatImage);
        mMessaage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
    }

    @Override
    public void onClick(View view) {
    }
}
