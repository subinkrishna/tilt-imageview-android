package com.kevalpatel2106.tiltimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.kevalpatel2106.tiltimageview.sensor.Sensey;
import com.kevalpatel2106.tiltimageview.sensor.TiltDirectionDetector;

/**
 * Created by Keval Patel on 07/06/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class TiltImageView extends ImageView{
    private Context mContext;

    /**
     * Listener to detect tilt.
     */
    private TiltDirectionDetector.TiltDirectionListener mTiltDirectionListener = new TiltDirectionDetector.TiltDirectionListener() {
        @Override
        public void onTiltInAxisX(int direction) {

        }

        @Override
        public void onTiltInAxisY(int direction) {

        }

        @Override
        public void onTiltInAxisZ(int direction) {

        }
    };

    public TiltImageView(Context context) {
        super(context);
        init(context);
    }

    public TiltImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TiltImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("NewApi")
    public TiltImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }

    /**
     * Start the sensors and start tilt detection.
     */
    public void start(){
        //Initialize the sensors
        Sensey.getInstance().init(mContext);
        Sensey.getInstance().startTiltDirectionDetection(mTiltDirectionListener);
    }

    /**
     * Stop the sensor and stop tilt detection. Always call this in onDestroy().
     */
    public void stop(){
        Sensey.getInstance().stopTiltDirectionDetection(mTiltDirectionListener);
        Sensey.getInstance().stop();
    }
}
