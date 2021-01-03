package com.pinder.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pinder.app.R;
import com.pinder.app.models.ChatObject;
import com.pinder.app.models.TagsObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolders> {
    private List<ChatObject> chatList;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private OnItemClickListener mListener;

    public ChatAdapter(List<ChatObject> matchesList, Context context) {
        this.chatList = matchesList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, null, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, null, false);
        }
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ChatViewHolders(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder, int position) {
        holder.mMessaage.setText(chatList.get(position).getMessage());
        if (chatList.get(position).getCurrentUser()) {
            holder.mChatImage.setVisibility(View.GONE);
        } else {
            if (position != chatList.size() - 1) {
                if (chatList.get(position + 1).getCurrentUser()) {
                    holder.mChatImage.setVisibility(View.VISIBLE);
                } else {
                    holder.mChatImage.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.mChatImage.setVisibility(View.VISIBLE);
            }
            if (chatList.get(position).getProfileImageUrl().equals("default")) {
                Glide.with(context).load(R.drawable.ic_profile_hq).into(holder.mChatImage);
            } else {
                Glide.with(context).load(chatList.get(position).getProfileImageUrl()).into(holder.mChatImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }

    public ChatObject getItem(int position) {
        return chatList.get(position);
    }

    public interface OnItemClickListener {
        void onProfileClick(int position);
        void onMessageClick(int position);
    }

    public class ChatViewHolders extends RecyclerView.ViewHolder {
        public TextView mMessaage;
        public LinearLayout mContainer;
        public CircleImageView mChatImage;

        public ChatViewHolders(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            mChatImage = itemView.findViewById(R.id.chatImage);
            mMessaage = itemView.findViewById(R.id.message);
            mContainer = itemView.findViewById(R.id.container);

            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onMessageClick(position);
                        }
                    }
                }
            });
            mChatImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onProfileClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getCurrentUser()) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}

