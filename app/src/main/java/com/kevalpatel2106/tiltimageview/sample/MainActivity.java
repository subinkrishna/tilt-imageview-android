package com.kevalpatel2106.tiltimageview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kevalpatel2106.tiltimageview.TiltImageView;

public class MainActivity extends AppCompatActivity {

    private TiltImageView mTiltImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTiltImageView = (TiltImageView) findViewById(R.id.tilt_image_view);
        mTiltImageView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTiltImageView.stop();
    }
}
