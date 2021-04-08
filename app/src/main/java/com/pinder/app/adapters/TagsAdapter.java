package com.pinder.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pinder.app.R;
import com.pinder.app.models.TagsObject;

import java.util.ArrayList;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagsViewHolder> {
    private final ArrayList<TagsObject> mTagsManagerObject;
    private OnItemClickListener mListener;

    public TagsAdapter(ArrayList<TagsObject> exampleList) {
        mTagsManagerObject = exampleList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public TagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tags_manager, parent, false);
        TagsViewHolder evh = new TagsViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(TagsViewHolder holder, int position) {
        TagsObject currentItem = mTagsManagerObject.get(position);
        holder.tagName.setText("#" + currentItem.getTagName());
        holder.gender.setText(currentItem.getGender());
        if (currentItem.getmDistance().equals("100000")) {
            holder.distance.setText("∞");
        } else {
            holder.distance.setText(currentItem.getmDistance());
        }
        holder.tagAge.setText(currentItem.getmAgeMin() + "-" + mTagsManagerObject.get(position).getmAgeMax());
    }

    @Override
    public int getItemCount() {
        return mTagsManagerObject.size();
    }

    public TagsObject getItem(int position) {
        return mTagsManagerObject.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public static class TagsViewHolder extends RecyclerView.ViewHolder {
        private final TextView tagName;
        private final TextView gender;
        private final TextView tagAge;
        private final TextView distance;
        private final ImageView mDeleteImage;

        public TagsViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            tagName = itemView.findViewById(R.id.tag);
            gender = itemView.findViewById(R.id.tag_gender);
            distance = itemView.findViewById(R.id.tag_distance);
            tagAge = itemView.findViewById(R.id.tag_age);
            mDeleteImage = itemView.findViewById(R.id.tag_delete);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}