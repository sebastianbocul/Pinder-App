package com.example.tinderapp.Images;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.tinderapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageAdapter extends PagerAdapter {
    private Context mContext;
    private int[] mImageIds = new int[] {R.drawable.ic_launcher_background,R.drawable.pop_bg,R.drawable.ic_launcher_foreground};
    private Map mImages2 = new HashMap<>();
    private ArrayList mImages;
    public ImageAdapter(Context context, ArrayList imagesUrls){
        mContext= context;
        mImages = imagesUrls;
    }


    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Integer i = position;
        String imageUrl = mImages.get(i).toString();
        if(mImages.get(i).equals("default")){
            Glide.with(mContext).load(R.mipmap.ic_launcher).into(imageView);
        }else {
            Uri myUri = Uri.parse(imageUrl);
            Glide.with(mContext).load(imageUrl).into(imageView);
            imageView.setImageURI(myUri);
        }


        container.addView(imageView,0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
       container.removeView((ImageView) object);
    }
}
