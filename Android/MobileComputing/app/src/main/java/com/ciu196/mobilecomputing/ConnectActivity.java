package com.ciu196.mobilecomputing;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);

        BroadcastService broadcastService = new BroadcastService();

        //Connect UI Elements
        final TextView pianoStatusTextView = (TextView) findViewById(R.id.pianoStatusTextView);
        final TextView pianoDetailedTextView = (TextView) findViewById(R.id.pianodetailedTextView);

        if (BroadcastService.isLive()) {
            pianoStatusTextView.setText(BroadcastService.getPlayerName() + " is playing");
            try {

                pianoDetailedTextView.setText(formatDuration(BroadcastService.getCurrentSessionDuration()));
            } catch (NotLiveException e) {
                e.printStackTrace();
            }
        }

        final TextView tvDebug = (TextView) findViewById(R.id.debugTxt);
        final int outerRadius = ((Circle) findViewById(R.id.outerCircle)).getRadius();

//        tvDebug.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                final int dur = 5;
//                new CountDownTimer(dur*1000, 100) {
//                    int delta = 100;
//                    int secondsLeft = dur;
//
//                    public void onTick(long millisUntilFinished) {
//                        if(Math.round(millisUntilFinished/1000)<=secondsLeft) {
////                        ((Circle) view).setRadius(outerRadius + delta);
//                            tvDebug.setText(String.valueOf(secondsLeft));
//                            secondsLeft--;
//                        }
//                    }
//
//                    public void onFinish() {
////                        ((Circle) view).setRadius(outerRadius);
//                    }
//
//                }.start();
//            }
//        });

        Circle circleButton = (Circle) findViewById(R.id.buttonCircle);
        ClickEffect.clickTintEffect(circleButton);
        circleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (((Circle) v).insideCircle(event)) {
                    //DO STUFF HERE!!!!
                    final Animation out = new AlphaAnimation(1.0f, 0.0f);
                    out.setDuration(1000);
                    final Animation in = new AlphaAnimation(0.0f, 1.0f);
                    in.setDuration(1000);

                    pianoStatusTextView.startAnimation(out);
                    out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            pianoStatusTextView.setText("No-one is playing");
                            pianoStatusTextView.startAnimation(in);
//                        circleButton.setColor("#d1172e"); //set no one is playing color
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                return false;
            }
        });
    }

    private String formatDuration(Duration d) {

        String hours = d.getStandardHours() + "";
        String minutes = (d.getStandardMinutes() % 60 > 9 ? d.getStandardMinutes() % 60 + "" : "0" + d.getStandardMinutes() % 60 + "");
        String seconds = (d.getStandardSeconds() % 60 > 9 ? d.getStandardSeconds() % 60 + "" : "0" + d.getStandardSeconds() % 60 + "");


        return hours + ":" + minutes + ":" + seconds;
    }
}