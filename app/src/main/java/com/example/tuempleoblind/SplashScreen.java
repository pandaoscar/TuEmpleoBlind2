package com.example.tuempleoblind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {
    LottieAnimationView lottie;
    TextView tx_animation1;
    TextView tx_animation2;
    TextView tx_animation3;
    RelativeLayout animationTop;
    RelativeLayout animationBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        lottie=findViewById(R.id.lottie);
        lottie.animate().rotation(360).setDuration(500).setStartDelay(900)
                .withEndAction(() -> lottie.animate().translationY(-2000).setDuration(500).setStartDelay(500));
        lottie.animate().scaleX(0.5f).scaleY(0.5f).setDuration(500).setStartDelay(900);

        tx_animation1=findViewById(R.id.text_animation1);
        tx_animation1.animate().translationY(-2000).setDuration(500).setStartDelay(1900);

        tx_animation2=findViewById(R.id.text_animation2);
        tx_animation2.animate().translationY(2000).setDuration(500).setStartDelay(1900);

        tx_animation3=findViewById(R.id.text_animation3);
        tx_animation3.animate().translationY(2000).setDuration(500).setStartDelay(1900);

        animationTop = findViewById(R.id.animation_top);
        animationTop.animate().translationY(-2000).setDuration(500).setStartDelay(1900);

        animationBottom = findViewById(R.id.animation_bottom);
        animationBottom.animate().translationY(2000).setDuration(500).setStartDelay(1900);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i= new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();

            }
        }, 2500);
    }
}