package com.example.tinderapp.Tags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tinderapp.R;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
public class TagsManagerAdapter extends RecyclerView.Adapter<TagsManagerAdapter.ExampleViewHolder> {
    private ArrayList<TagsManagerObject> mTagsManagerObject;
    private OnItemClickListener mListener;

    private LayoutInflater mInflater;
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        private TextView tagName,gender,tagAge,distance;
        private ImageView mDeleteImage;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            tagName= itemView.findViewById(R.id.tag);
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
                        System.out.println("Poosition");
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
    public TagsManagerAdapter(ArrayList<TagsManagerObject> exampleList) {
        mTagsManagerObject = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tags_manager, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        TagsManagerObject currentItem = mTagsManagerObject.get(position);
        holder.tagName.setText("#"+currentItem.getTagName());
        holder.gender.setText(currentItem.getGender());
        holder.distance.setText(currentItem.getmDistance());
        holder.tagAge.setText(currentItem.getmAgeMin() + "-" + mTagsManagerObject.get(position).getmAgeMax());
    }
    @Override
    public int getItemCount() {
        return mTagsManagerObject.size();
    }
}