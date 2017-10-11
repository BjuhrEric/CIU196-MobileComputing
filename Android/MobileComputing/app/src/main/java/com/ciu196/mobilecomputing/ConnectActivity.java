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




public class ConnectActivity extends AppCompatActivity {

    public enum guiMode{CONNECT, LISTEN, CANT_LISTEN, CANT_CONNECT,LISTENING, PLAYING };

    TextView pianoStatusTextView;
    TextView pianoDetailedTextView;
    TextView playerNameTextView;
    Button actionButton;
    Circle innerCircle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);

        BroadcastService broadcastService = new BroadcastService();

        //Connect UI Elements
        pianoStatusTextView = (TextView) findViewById(R.id.pianoStatusTextView);
        pianoDetailedTextView = (TextView) findViewById(R.id.pianodetailedTextView);
        playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        actionButton = (Button) findViewById(R.id.actionButtion);
        innerCircle = (Circle) findViewById(R.id.buttonCircle);



        if (BroadcastService.isLive()) {
            if(BroadcastService.closeEnough()){
                switchGui(guiMode.LISTEN);
            }
            else {
                switchGui(guiMode.CANT_LISTEN);
            }
        }else{
            switchGui(guiMode.CONNECT);
        }


        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchGui(guiMode.LISTENING);
            }
        });


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
    private void switchGui(guiMode m){

        if(m == guiMode.LISTEN){

            playerNameTextView.setText(BroadcastService.getPlayerName());
            pianoStatusTextView.setText("is playing");
            actionButton.setText("Start Listening");
            innerCircle.setColor(R.color.listenBlueColor);
            try {

                pianoDetailedTextView.setText(formatDuration(BroadcastService.getCurrentSessionDuration()));
            } catch (NotLiveException e) {
                e.printStackTrace();
            }
        }
        else if(m == guiMode.CANT_CONNECT){
            playerNameTextView.setText(BroadcastService.getPlayerName());
            actionButton.setEnabled(false);
            actionButton.setText("Connect");

        }
        else if(m == guiMode.CONNECT){
            playerNameTextView.setText(BroadcastService.getPlayerName() + " is playing");
            actionButton.setText("Connect");

        }
        else {
            if (m == guiMode.LISTENING) {
                final Animation out = new AlphaAnimation(1.0f, 0.0f);
                out.setDuration(500);
                actionButton.startAnimation(out);
                out.setAnimationListener(new Animation.AnimationListener() {
                                             @Override
                                             public void onAnimationStart(Animation animation) {

                                             }

                                             @Override
                                             public void onAnimationEnd(Animation animation) {
                                                    actionButton.setVisibility(View.INVISIBLE);
                                             }

                                             @Override
                                             public void onAnimationRepeat(Animation animation) {

                                             }
                                         }
                );

                pianoStatusTextView.setText("Currently listening to");
                playerNameTextView.setText(BroadcastService.getPlayerName());


            } else if (m == guiMode.PLAYING) {
                pianoStatusTextView.setText(BroadcastService.getPlayerName() + " is playing");
                actionButton.setText("Connect");

            }
        }

    }
}