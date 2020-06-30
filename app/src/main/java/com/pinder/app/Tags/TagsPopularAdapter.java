package com.pinder.app.Tags;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pinder.app.R;

import java.util.ArrayList;

public class TagsPopularAdapter extends RecyclerView.Adapter<TagsPopularAdapter.ViewHolder> {
    private ArrayList<TagsPopularObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    TagsPopularAdapter(Context context, ArrayList<TagsPopularObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_tags_popular, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TagsPopularObject current = mData.get(position);
        String tagNameStr = current.getTagName();
        int tagPopularityStr = current.getTagPopularity();
        holder.tagName.setText(tagNameStr);
        holder.tagPopularity.setText(String.valueOf(tagPopularityStr));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // convenience method for getting data at click position
    TagsPopularObject getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tagName, tagPopularity;

        ViewHolder(View itemView) {
            super(itemView);
            tagPopularity = itemView.findViewById(R.id.tag_popularity);
            tagName = itemView.findViewById(R.id.tag_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}


