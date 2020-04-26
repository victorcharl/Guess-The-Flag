package com.victorcharl.guessthepicture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView app_logo;
    TextView app_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        app_logo = (ImageView)findViewById(R.id.app_logo);
        app_title = (TextView) findViewById(R.id.app_title);
        displayImage();

        Thread th = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent mainActivity = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
            }
        };
        th.start();
    }

    private void displayImage(){
        String imageLocation = "https://1.bp.blogspot.com/-Os_G7fBordU/W1dFCdFA3AI/AAAAAAAAp-I/NLfM0h9Nvw42dtkvBlgzC_1_QFmKHBEVgCLcBGAs/s1600/world_flags_globe_1.gif";

        DownloadImage task=new DownloadImage();
        Bitmap downloadedImage;
        try {
            downloadedImage=task.execute(imageLocation).get();
            //https://codinginfinite.com/basic-android-animation-example/
            app_logo.setImageBitmap(downloadedImage);
            app_logo.setAlpha(0.0f);
            app_logo.animate().alpha(1.0f).rotation(385f).setDuration(2000);
            app_title.setScaleX(2);
            app_title.setScaleY(2);
            app_title.animate().scaleY(1).scaleX(1).setDuration(1000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
