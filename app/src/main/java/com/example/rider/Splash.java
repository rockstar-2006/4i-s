package com.example.rider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class Splash extends AppCompatActivity {
    TextView text, textbot;
    ConstraintLayout constraintLayout; // Correct type for ConstraintLayout

    Animation textAnimation, layoutAnimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textAnimation = AnimationUtils.loadAnimation(Splash.this, R.anim.fall);
        layoutAnimation = AnimationUtils.loadAnimation(Splash.this, R.anim.bottom);


        text = findViewById(R.id.text);
        textbot = findViewById(R.id.textbot);
        constraintLayout = findViewById(R.id.main);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                constraintLayout.setVisibility(View.VISIBLE);
                constraintLayout.startAnimation(layoutAnimation); // Use startAnimation

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        text.setVisibility(View.VISIBLE);
                        textbot.setVisibility(View.VISIBLE);

                        text.startAnimation(textAnimation); // Use startAnimation
                        textbot.startAnimation(textAnimation); // Use startAnimation
                    }
                }, 900);
            }
        }, 500);

        // Navigate to login activity after a delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class); // Ensure LoginActivity is the correct name
                startActivity(intent);
            }

        }, 6000);

    }
}

