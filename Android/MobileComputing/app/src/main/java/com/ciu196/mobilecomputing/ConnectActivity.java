package com.ciu196.mobilecomputing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        if(BroadcastService.isLive()){
            pianoStatusTextView.setText(BroadcastService.getPlayerName() + " is playing");
            try {

                pianoDetailedTextView.setText(formatDuration(BroadcastService.getCurrentSessionDuration()));
            } catch (NotLiveException e) {
                e.printStackTrace();
            }
        }


         final Circle actionButton =  (Circle) findViewById(R.id.actionButton);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DO STUFF HERE!!!!
                Toast.makeText(v.getContext(), "ActionClicked",Toast.LENGTH_LONG).show();
                actionButton.setColor(999999);
                Animation out = new AlphaAnimation(1.0f, 0.0f);
                out.setDuration(2000);
                pianoStatusTextView.startAnimation(out);


            }
        });
    }
    private String formatDuration(Duration d){

        String hours = d.getStandardHours()+"";
        String minutes = (d.getStandardMinutes()%60 > 9 ? d.getStandardMinutes()%60+"" :"0" + d.getStandardMinutes()%60+"");
        String seconds = (d.getStandardSeconds()%60 > 9 ? d.getStandardSeconds()%60+"" :"0" + d.getStandardSeconds()%60+"");


        return hours + ":" + minutes + ":" + seconds;
    }
}