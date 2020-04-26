package com.example.tinderapp.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.tinderapp.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards> {

    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> items){
        super(context,resourceId,items);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        cards card_item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item,parent,false);

        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView tags = (TextView) convertView.findViewById(R.id.tags);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(card_item.getName());
        tags.setText(card_item.getTags());
        switch(card_item.getProfileImageUrl()){
            case "default":
                Glide.with(convertView.getContext()).load(R.mipmap.ic_launcher).into(image);
                break;
            default:
                Glide.with(image).clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }



        return  convertView;

    }
}
