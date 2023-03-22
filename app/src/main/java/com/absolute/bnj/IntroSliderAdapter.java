package com.absolute.bnj;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

public class IntroSliderAdapter extends PagerAdapter {

    Context mContext ;
    List<IntroSliderItem> mListScreen;

    public IntroSliderAdapter(Context mContext, List<IntroSliderItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_intro_slider_screen,null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/poppins_regular.ttf");
        title.setTypeface(typeface);

        TextView description = layoutScreen.findViewById(R.id.intro_description);
        description.setTypeface(typeface);

        title.setText(mListScreen.get(position).getTitle());
//        title.setText("Title of the page and working for you");
        description.setText(mListScreen.get(position).getDescription());

        Glide.with(mContext).load(mListScreen.get(position).getScreenImg()).into(imgSlide);


//        imgSlide.setImageResource(mListScreen.get(position).getScreenImg());

        container.addView(layoutScreen);

        return layoutScreen;





    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }
}
