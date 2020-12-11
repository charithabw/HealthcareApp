//this is the start window (splash window)
package com.example.healthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashWindow extends AppCompatActivity {
TextView h;
Animation blink_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_window);

        h = (TextView)findViewById(R.id.txtH);
        blink_anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blinkeffect);
        h.startAnimation(blink_anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashWindow.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
