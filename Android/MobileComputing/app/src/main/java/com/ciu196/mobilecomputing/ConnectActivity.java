package com.ciu196.mobilecomputing;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.Duration;

import static com.ciu196.mobilecomputing.ViewAnimationService.colorTransitionAnimation;
import static com.ciu196.mobilecomputing.ViewAnimationService.fadeInAnimation;
import static com.ciu196.mobilecomputing.ViewAnimationService.fadeOutAnimation;
import static com.ciu196.mobilecomputing.ViewAnimationService.startAllAnimation;
import static com.ciu196.mobilecomputing.ViewAnimationService.translateAnimation;
import static com.ciu196.mobilecomputing.ViewAnimationService.translateToCenterInParentViewAnimation;
import static com.ciu196.mobilecomputing.ViewAnimationService.uniformScaleAnimation;
import static java.lang.Thread.sleep;


public class ConnectActivity extends AppCompatActivity {

    public enum guiMode {CONNECT, START_TO_LISTEN, CANT_LISTEN, CANT_CONNECT, LISTENING, PLAYING};

    public enum circleColor {BLUE, GRAY, RED};


    TextView pianoStatusTextView;
    TextView listenersTextView;
    TextView playerNameTextView;
    TextView durationText;
    ImageView earImage;
    Button actionButton;
    Circle circle1;
    Circle circle2;
    Circle circle3;
    Circle circle4;
    View backgroundView;
    View listenerLayout;
    FloatingActionButton fab, fab1, fab2, fab3;

    guiMode currentGuiMode = guiMode.START_TO_LISTEN;
    int currentBackgroundColor = 0;
    int currentCircleColors[] = {0, 0, 0, 0};


    boolean testFlag = false;
    boolean isShowingReaction = false;
    View.OnClickListener reactionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.fab1:
                    Toast.makeText(ConnectActivity.this, "Fab 1", Toast.LENGTH_LONG).show();
                    break;
                case R.id.fab2:
                    Toast.makeText(ConnectActivity.this, "Fab 2", Toast.LENGTH_LONG).show();
                    break;
                case R.id.fab3:
                    Toast.makeText(ConnectActivity.this, "Fab 3", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);

        //Connect UI Elements
        pianoStatusTextView = (TextView) findViewById(R.id.pianoStatusTextView);
        listenersTextView = (TextView) findViewById(R.id.listenersTextView);
        playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        durationText = (TextView) findViewById(R.id.durationText);
        actionButton = (Button) findViewById(R.id.actionButtion);
        listenerLayout = (View) findViewById(R.id.listenersLayout);
        earImage = (ImageView) findViewById(R.id.earImage);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab1.setOnClickListener(reactionListener);
        fab2.setOnClickListener(reactionListener);
        fab3.setOnClickListener(reactionListener);

        circle1 = (Circle) findViewById(R.id.circle1);
        circle2 = (Circle) findViewById(R.id.circle2);
        circle3 = (Circle) findViewById(R.id.circle3);
        circle4 = (Circle) findViewById(R.id.circle4);
        backgroundView = (View) findViewById(R.id.backgroundLayout);
        currentBackgroundColor = getResources().getColor(R.color.backgroundCreamColor);
        currentCircleColors[0] = circle1.getColor();
        currentCircleColors[1] = circle2.getColor();
        currentCircleColors[2] = circle3.getColor();
        currentCircleColors[3] = circle4.getColor();


        if (BroadcastService.isLive()) {
            if (BroadcastService.closeEnough()) {
                switchGui(guiMode.START_TO_LISTEN);
            } else {
                switchGui(guiMode.CANT_LISTEN);
            }
        } else {
            switchGui(guiMode.CONNECT);
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentGuiMode == guiMode.START_TO_LISTEN){
                    switchGui(guiMode.LISTENING);
                } else if (currentGuiMode == guiMode.LISTENING){
                    switchGui(guiMode.START_TO_LISTEN);
                }else if (currentGuiMode == guiMode.CONNECT){
                    if(BroadcastService.startNewBroadcast())
                        switchGui(guiMode.PLAYING);
                }else if (currentGuiMode == guiMode.PLAYING){
                    if(BroadcastService.startNewBroadcast())
                        switchGui(guiMode.CONNECT);
                }


            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentGuiMode == guiMode.START_TO_LISTEN){

                    Intent i = new Intent(ConnectActivity.this, VolumeRangeActivity.class);
                    startActivity(i);
                }else if (currentGuiMode == guiMode.LISTENING){
                    if(isShowingReaction){
                        //hide reaction alternatives
                        //show reaction alternatives
                        fab1.setClickable(false);
                        fab2.setClickable(false);
                        fab3.setClickable(false);
                        fab1.hide();
                        fab2.hide();
                        fab3.hide();

                        isShowingReaction = false;
                    } else {
                        //show reaction alternatives
                        fab1.setClickable(true);
                        fab2.setClickable(true);
                        fab3.setClickable(true);
                        fab1.show();
                        fab2.show();
                        fab3.show();

                        isShowingReaction = true;
                    }


                }
            }
        });

    }

    private String formatDuration(Duration d) {

        String hours = d.getStandardHours() + "";
        String minutes = (d.getStandardMinutes() % 60 > 9 ? d.getStandardMinutes() % 60 + "" : "0" + d.getStandardMinutes() % 60 + "");
        String seconds = (d.getStandardSeconds() % 60 > 9 ? d.getStandardSeconds() % 60 + "" : "0" + d.getStandardSeconds() % 60 + "");


        return hours + ":" + minutes + ":" + seconds;
    }

    private void switchGui(guiMode m) {

        teardownCurrentGui();

        if (m == guiMode.START_TO_LISTEN) {
            currentGuiMode = guiMode.START_TO_LISTEN;

            actionButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_blue));
            setCircleColor(circleColor.GRAY);
            pianoStatusTextView.setTextColor(getResources().getColor(R.color.grayTextColor));
            playerNameTextView.setTextColor(getResources().getColor(R.color.actionBlueColor));
            playerNameTextView.setText(BroadcastService.getPlayerName());
            pianoStatusTextView.setText("is playing");
            actionButton.setText("Start Listening");
            listenersTextView.setText(BroadcastService.getNumberOfListeners()+"");
            earImage.setImageResource(R.drawable.ic_hearing_black_24dp);
            setCircleColor(circleColor.GRAY);
            changeBackgroundColor(getResources().getColor(R.color.backgroundCreamColor));
            fadeInAnimation(playerNameTextView, 500);
            fadeInAnimation(pianoStatusTextView, 550);
            fadeInAnimation(earImage, 400);
            fadeInAnimation(listenerLayout, 600);
            fadeInAnimation(actionButton, 1000);



            fab.setImageResource(R.drawable.ic_map_white_24dp);
            fab.show();

            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab1.hide();
            fab2.hide();
            fab3.hide();

            final Handler handler = new Handler();

            handler.postDelayed(new Runnable(){
                public void run(){
                    try {

                        durationText.setText(formatDuration(BroadcastService.getCurrentSessionDuration()));
                    } catch (NotLiveException e) {
                        e.printStackTrace();
                    }

                    handler.postDelayed(this, 1000);
                }
            }, 0);

        } else if (m == guiMode.LISTENING) {
            currentGuiMode = guiMode.LISTENING;

            changeBackgroundColor(getResources().getColor(R.color.backgroundBlueColor));
            actionButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_blue));
            setCircleColor(circleColor.BLUE);
            pianoStatusTextView.setTextColor(getResources().getColor(R.color.grayTextColor));
            playerNameTextView.setTextColor(getResources().getColor(R.color.actionBlueColor));
            pianoStatusTextView.setText("Currently listening to");
            playerNameTextView.setText(BroadcastService.getPlayerName());
            actionButton.setText("Stop listening");
            earImage.setImageResource(R.drawable.ic_hearing_white_24dp);

            fab.setImageResource(R.drawable.ic_tag_faces_white_24dp);
            fab.show();

            fadeInAnimation(earImage, 400);
            //TODO: The previous setText call doesn't update the internal position of the textView, which translateToCenterInParentView uses. E.I it dosn't work. Will have to be fixed.
            translateToCenterInParentViewAnimation(pianoStatusTextView, 500, ViewAnimationService.Axis.X);
            translateToCenterInParentViewAnimation(playerNameTextView, 500, ViewAnimationService.Axis.X);

            translateAnimation(playerNameTextView, 500, ViewAnimationService.Axis.Y, 120);
            translateAnimation(listenerLayout, 500, ViewAnimationService.Axis.Y, 130);

            uniformScaleAnimation(playerNameTextView, 500, 1.3f);

            fadeInAnimation(playerNameTextView, 500);
            fadeInAnimation(pianoStatusTextView, 550);
            fadeInAnimation(actionButton, 700);

        } else if (m == guiMode.CANT_CONNECT) {
            currentGuiMode = guiMode.CANT_CONNECT;
            setCircleColor(circleColor.GRAY);
            playerNameTextView.setText(BroadcastService.getPlayerName());
            actionButton.setEnabled(false);
            actionButton.setText("Connect");

        } else if (m == guiMode.CONNECT) {
            currentGuiMode = guiMode.CONNECT;
            changeBackgroundColor(getResources().getColor(R.color.backgroundGrayColor));
            setCircleColor(circleColor.GRAY);
            playerNameTextView.setTextColor(getResources().getColor(R.color.actionRedColor));
            playerNameTextView.setText(BroadcastService.getPlayerName());
            pianoStatusTextView.setTextColor(getResources().getColor(R.color.whiteColor));
            pianoStatusTextView.setText("is playing");
            actionButton.setText("Connect to piano");
            listenersTextView.setText("0");
            earImage.setImageResource(R.drawable.ic_hearing_white_24dp);
            actionButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_red));
            durationText.setVisibility(View.INVISIBLE);

        }  else if (m == guiMode.PLAYING) {
            currentGuiMode = guiMode.PLAYING;
            changeBackgroundColor(getResources().getColor(R.color.backgroundRedColor));
            setCircleColor(circleColor.RED);
            playerNameTextView.setTextColor(getResources().getColor(R.color.actionRedColor));
            playerNameTextView.setText("You");
            pianoStatusTextView.setTextColor(getResources().getColor(R.color.whiteColor));
            pianoStatusTextView.setText("are playing");
            actionButton.setText("Stop playing");
            listenersTextView.setText(BroadcastService.getNumberOfListeners()+"");
            earImage.setImageResource(R.drawable.ic_hearing_white_24dp);
            actionButton.setBackground(getResources().getDrawable(R.drawable.rounded_button_red));

        }
        startAllAnimation();
    }


    private void teardownCurrentGui() {
        if(currentGuiMode == guiMode.START_TO_LISTEN){
            fadeOutAnimation(playerNameTextView, 200);
            fadeOutAnimation(pianoStatusTextView, 350);
            fadeOutAnimation(actionButton, 700);
            fadeOutAnimation(earImage, 400);
            fab.hide();

        } else if(currentGuiMode == guiMode.LISTENING){
            fadeOutAnimation(playerNameTextView, 200);
            fadeOutAnimation(pianoStatusTextView, 350);
            fadeOutAnimation(actionButton, 700);
            fadeOutAnimation(earImage, 400);
            fab.hide();

        }
        else if(currentGuiMode == guiMode.CANT_CONNECT){


        }
        else if(currentGuiMode == guiMode.CONNECT){


        }
        else if(currentGuiMode == guiMode.PLAYING){


        }

    }

    private void changeBackgroundColor(int newColor) {
        colorTransitionAnimation(backgroundView, 750, currentBackgroundColor, newColor);
        currentBackgroundColor = newColor;

    }

    private void setCircleColor(circleColor c) {
        int colorFrom1 = currentCircleColors[0];
        int colorFrom2 = currentCircleColors[1];
        int colorFrom3 = currentCircleColors[2];
        int colorFrom4 = currentCircleColors[3];
        int colorTo1 = 0;
        int colorTo2 = 0;
        int colorTo3 = 0;
        int colorTo4 = 0;

        if (c == circleColor.BLUE) {
            colorTo1 = getResources().getColor(R.color.circle1BlueColor);
            colorTo2 = getResources().getColor(R.color.circle2BlueColor);
            colorTo3 = getResources().getColor(R.color.circle3BlueColor);
            colorTo4 = getResources().getColor(R.color.circle4BlueColor);
        } else if (c == circleColor.RED) {
            colorTo1 = getResources().getColor(R.color.circle1RedColor);
            colorTo2 = getResources().getColor(R.color.circle2RedColor);
            colorTo3 = getResources().getColor(R.color.circle3RedColor);
            colorTo4 = getResources().getColor(R.color.circle4RedColor);

        }else if (c == circleColor.GRAY) {
            colorTo1 = getResources().getColor(R.color.circle1GrayColor);
            colorTo2 = getResources().getColor(R.color.circle2GrayColor);
            colorTo3 = getResources().getColor(R.color.circle3GrayColor);
            colorTo4 = getResources().getColor(R.color.circle4GrayColor);

        }

        colorTransitionAnimation(circle1, 250, colorFrom1, colorTo1);
        colorTransitionAnimation(circle2, 450, colorFrom2, colorTo2);
        colorTransitionAnimation(circle3, 600, colorFrom3, colorTo3);
        colorTransitionAnimation(circle4, 700, colorFrom4, colorTo4);

        currentCircleColors[0] = colorTo1;
        currentCircleColors[1] = colorTo2;
        currentCircleColors[2] = colorTo3;
        currentCircleColors[3] = colorTo4;


    }


}