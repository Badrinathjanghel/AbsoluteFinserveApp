package com.absolute.bnj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Splash extends Activity {
    private static final String TAG = "Splash";
    LottieAnimationView lottie_graph;
    String fbtoken;
    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Log.d("TAG", "onCreate: ");
        initVars();
    }


    private void initVars() {
        session = new Session(this);

        TextView version = findViewById(R.id.version);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/poppins_regular.ttf");
        version.setTypeface(typeface);

        TextView promo_text = findViewById(R.id.promo_text);
        promo_text.setTypeface(typeface);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.d(TAG, "onComplete: exception "+ task.getException());
                    return;
                }
                fbtoken=task.getResult();
                session.setFirebaseToken(fbtoken);
                Log.d(TAG, "onCreate: fb token "+fbtoken);

            }
        });

//        lottie_graph = findViewById(R.id.lottie_graph);
//
//        lottie_graph.playAnimation();
//
//        lottie_graph.addAnimatorListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                //animation completed
//                startActivity(new Intent(Splash.this, IntroSlider.class));
//                finish();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });

//        lottie_graph
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, IntroSlider.class));

                finish();
            }
        }, 1000);
    }
}