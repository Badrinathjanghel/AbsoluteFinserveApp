package com.absolute.bnj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntroSlider extends Activity {
    private static final String TAG = "IntroSlider";

    Context context = this;
    private ViewPager screenPager;
    IntroSliderAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    Button btnGetStarted;
    Animation btnAnim ;
    TextView tvSkip;
    Session session;
    ProgressBar progress_bar;

    List<IntroSliderItem> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // make the activity on full screen

        session = new Session(this);

        // when this activity is about to be launch we need to check if its openened before or not
        //session.setIntroSliderStatus(false);

        if (session.getIntroSliderStatus()==true) {
            startLoginActivity();
        }
        setContentView(R.layout.activity_intro_slider);

        // hide the action bar

//        getSupportActionBar().hide();

        // ini views
        progress_bar = findViewById(R.id.progress_bar);
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);

        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/poppins_regular.ttf");
        btnGetStarted.setTypeface(typeface);
        tvSkip.setTypeface(typeface);



        fetchIntroSliders();

        // next button click Listner
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size()-1) { // when we rech to the last screen
                    // TODO : show the GETSTARTED Button and hide the indicator and the next button
                    loadLastScreen();
                }else{
                    loadOtherScreens();
                }



            }
        });

        // tablayout add change listener


        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {

                    loadLastScreen();
                }else{
                    loadOtherScreens();
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open main activity
                session.setIntroSliderStatus(true);
//                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(mainActivity);
//                finish();

                startLoginActivity();


            }
        });

        // skip button click listener

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });



    }

    private void startLoginActivity() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class );
        startActivity(mainActivity);
        finish();
    }

    private void fetchIntroSliders() {
        progress_bar.setVisibility(View.VISIBLE);
        StringRequest sr = new StringRequest(Request.Method.POST, Constants.FETCH_INTRO_SLIDER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress_bar.setVisibility(View.GONE);
                Log.d(TAG, "onResponseUrl: " + Constants.FETCH_INTRO_SLIDER_URL);
                Log.d(TAG, "onResponse: " + response);
                try {
                    JSONObject resObj = new JSONObject(response);
                    if(resObj.getString("status").equals("success")){
                        mList.clear();

                        JSONArray dataArray = resObj.getJSONArray("data");
                        if(dataArray.length()>0) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);

                                Log.d(TAG, "onResponse: " + dataObject.toString());
                                // fill list screen

                                mList.add(new IntroSliderItem( dataObject.getString("title"),dataObject.getString("description"),dataObject.getString("image")));
//                                mList.add(new IntroSliderItem("Share Anywhere","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.social_intro));
//                                mList.add(new IntroSliderItem("Earn Coins","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.earn_intro));
//                                mList.add(new IntroSliderItem("Refer & Earn","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.refer_intro));
                            }

                            // setup viewpager
                            screenPager =findViewById(R.id.screen_viewpager);
                            introViewPagerAdapter = new IntroSliderAdapter(context,mList);
                            screenPager.setAdapter(introViewPagerAdapter);
                            // setup tablayout with viewpager
                            tabIndicator.setupWithViewPager(screenPager);

                        }else{
                            startLoginActivity();
                            Log.d(TAG, "onResponse: error fetching data");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
                progress_bar.setVisibility(View.GONE);
                startLoginActivity();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
//                params.put("mobile", session.getMobile());
//                params.put("userid", session.getUserid());
//                params.put("token", session.getJwtToken());
                return params;
            }
        };
        sr.setRetryPolicy( new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(sr);
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        btnGetStarted.setAnimation(btnAnim);
    }
    private void loadOtherScreens() {
//        btnNext.setVisibility(View.VISIBLE);
//        btnGetStarted.setVisibility(View.INVISIBLE);
//        tabIndicator.setVisibility(View.VISIBLE);

    }
}