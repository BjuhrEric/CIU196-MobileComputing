package com.ciu196.mobilecomputing;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ciu196.mobilecomputing.animations.ViewAnimationService;
import com.ciu196.mobilecomputing.common.Reaction;
import com.ciu196.mobilecomputing.common.requests.ServerResponseType;
import com.ciu196.mobilecomputing.io.BroadcastService;
import com.ciu196.mobilecomputing.io.NotLiveException;
import com.ciu196.mobilecomputing.io.OnlineBroadcastService;
import com.ciu196.mobilecomputing.io.RequestDoneListener;
import com.ciu196.mobilecomputing.io.ServerConnection;
import com.ciu196.mobilecomputing.io.StatusUpdateListener;
import com.ciu196.mobilecomputing.reactions.ReactionListener;
import com.ciu196.mobilecomputing.reactions.ReactionService;

import org.joda.time.Duration;

import java.util.Random;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import static android.support.design.widget.FloatingActionButton.INVISIBLE;
import static android.support.design.widget.FloatingActionButton.SIZE_MINI;
import static android.support.design.widget.FloatingActionButton.SIZE_NORMAL;
import static android.support.design.widget.FloatingActionButton.VISIBLE;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.addAnimator;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.addFadeInAnimation;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.addFadeOutAnimation;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.addFadeWithScaleAnimation;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.addInstantOperation;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.getColorTransitionAnimator;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.getFadeAnimator;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.getTranslateToCenterInParentViewAnimator;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.getTranslationAnimatorReset;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.getUniformScaleAnimator;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.getUniformScaleAnimatorReset;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.startAllAnimations;
import static com.ciu196.mobilecomputing.animations.ViewAnimationService.startAnimations;


public class ConnectActivity extends AppCompatActivity implements ReactionListener, StatusUpdateListener {

    @Override
    public void onReactionReceived(Reaction reaction) {
        animateReaction(reaction);
    }

    @Override
    public void onBroadcastStarted() {
        System.out.print("Broadcast started");
        if (!broadcasting) {
            //Somebody else started broadcasting!
            System.out.print(" by somebody else");
            if (BroadcastService.closeEnough()) {
                switchGui(guiMode.START_TO_LISTEN);
            } else {
                switchGui(guiMode.CANT_LISTEN);
            }
        }
        System.out.println();
    }

    @Override
    public void onBroadcastEnded() {
        broadcasting = false; //If we were broadcasting, we no longer are...

        if (BroadcastService.closeEnough()) {
            switchGui(guiMode.CONNECT);
        } else {
            switchGui(guiMode.CANT_CONNECT);
        }
    }

    @Override
    public void onNumberOfListenersChanged() {
        addInstantOperation(listenersTextView, () -> listenersTextView.setText(OnlineBroadcastService.getInstance().getNumberOfListeners() + ""));
        startAnimations(listenersTextView);
    }

    public enum guiMode {CONNECT, START_TO_LISTEN, CANT_LISTEN, CANT_CONNECT, LISTENING, PLAYING}

    ;

    public enum circleColor {BLUE, GRAY, RED}

    ;

    final int STATUS_FADE_DURATION = 475;
    final int NAME_FADE_DURATION = 400;
    final int LISTENER_FADE_DURATION = 500;
    final int EAR_FADE_DURATION = 525;
    final int ACTION_BUTTON_FADE_DURATION = 550;
    final int ERROR_FADE_DURATION = 575;
    final int DURATION_TEXT_FADE_DURATION = 600;

    RelativeLayout rel;
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
    View errorView;
    TextView errorText;
    FloatingActionButton fab, fab1, fab2, fab3;
    OnlineBroadcastService obs;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private boolean isReturningFromListening;
    private boolean broadcasting = false; //TODO Set this value when starting the broadcast

    guiMode currentGuiMode = guiMode.START_TO_LISTEN;
    int currentBackgroundColor = 0;
    int currentCircleColors[] = {0, 0, 0, 0};

    String resultName;


    boolean testFlag = false;
    boolean isShowingReactions = false;
    float density;
    LinearLayout.LayoutParams miniFabLp, normalFabLp;

    View.OnClickListener fabReactionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                case R.id.fab1:
                case R.id.fab2:
                case R.id.fab3:
                    Reaction reaction = getReactionFromId(id);
                    animateReaction(reaction);
                    ServerConnection.getInstance().sendReaction(reaction);
                    break;
                default:
                    break;
            }

        }
    };

    public Reaction getReactionFromId(int fabId) {
        ImageView imageView = new ImageView(ConnectActivity.this);
        switch (fabId) {
            case R.id.fab1:
                return Reaction.THUMBS_UP;
            case R.id.fab2:
                return Reaction.HAPPY;
            case R.id.fab3:
                return Reaction.HEART;
            default:
                return null;
        }
    }

    public void animateReaction(Reaction reaction) {
        ImageView view = ReactionService.getReactionImageView(this, reaction);
        rel.addView(view);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        Random random = new Random();
        int x = 150 + random.nextInt(width - 300);
        int y = height - random.nextInt(height / 4) - 100;
        view.setX(x);
        view.setY(y);
        view.setVisibility(VISIBLE);
        view.setElevation(4);

        int duration = 3000;
        AnimatorSet animation = new AnimatorSet();
        animation
                .play(ViewAnimationService.getFadeAnimator(view, duration, 1, 0))
                .with(ViewAnimationService.getTranslationAnimator(view, duration, ViewAnimationService.Axis.Y, -1 * height))
                .with(ViewAnimationService.getElevationTransitionAnimator(view, duration, 32f))
                .with(ViewAnimationService.getWiggleAnimator(view, 1000, -15f, 15f))
                .with(ViewAnimationService.getUniformScaleAnimator(view, duration, 4f));


        //ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha",  1f, 0f);
        //fadeOut.setDuration(2000);

        //final AnimatorSet mAnimationSet = new AnimatorSet();

        //mAnimationSet.play(fadeIn).after(fadeOut);

        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rel.removeView(view);
            }
        });
        animation.setInterpolator(new PowerInterpolator(2.5f));
        animation.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_activity);

        //Online Broadcast Service
        obs = OnlineBroadcastService.getInstance();
        obs.addStatusUpdateListener(this);
        ReactionService.addReactionListener(this);

        //Connect UI Elements
        rel = (RelativeLayout) (RelativeLayout) findViewById(R.id.backgroundLayout);
        pianoStatusTextView = (TextView) findViewById(R.id.pianoStatusTextView);
        listenersTextView = (TextView) findViewById(R.id.listenersTextView);
        playerNameTextView = (TextView) findViewById(R.id.playerNameTextView);
        durationText = (TextView) findViewById(R.id.durationText);
        actionButton = (Button) findViewById(R.id.actionButtion);
        listenerLayout = (View) findViewById(R.id.listenersLayout);
        earImage = (ImageView) findViewById(R.id.earImage);
        errorView = (View) findViewById(R.id.errorView);
        errorText = (TextView) findViewById(R.id.errorText);

        density = getResources().getDisplayMetrics().density;
        int miniFabWidth = (int) (36 * getResources().getSystem().getDisplayMetrics().density);
        int normalFabWidth = (int) (56 * getResources().getSystem().getDisplayMetrics().density);
        int layout_padding16 = (int) (16 * getResources().getSystem().getDisplayMetrics().density);
        int layout_padding36 = (int) (36 * getResources().getSystem().getDisplayMetrics().density);
        int layout_padding8 = (int) (8 * getResources().getSystem().getDisplayMetrics().density);
        miniFabLp = new LinearLayout.LayoutParams(miniFabWidth, miniFabWidth, Gravity.CENTER_HORIZONTAL);
        miniFabLp.gravity = Gravity.CENTER_HORIZONTAL;
        miniFabLp.setMargins(layout_padding16, layout_padding16, layout_padding16, layout_padding8); //todo
        normalFabLp = new LinearLayout.LayoutParams(normalFabWidth, normalFabWidth, Gravity.CENTER_HORIZONTAL);
        normalFabLp.gravity = Gravity.CENTER_HORIZONTAL;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab1.setOnClickListener(fabReactionListener);
        fab2.setOnClickListener(fabReactionListener);
        fab3.setOnClickListener(fabReactionListener);


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


        if (OnlineBroadcastService.getInstance().isLive()) {
            if (BroadcastService.closeEnough()) {
                switchGui(guiMode.START_TO_LISTEN);
            } else {
                switchGui(guiMode.CANT_LISTEN);
            }
        } else {
            if (BroadcastService.closeEnough()) {
                switchGui(guiMode.CONNECT);
            } else {
                switchGui(guiMode.CANT_CONNECT);
            }
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentGuiMode == guiMode.START_TO_LISTEN) {
                    ServerConnection.getInstance().startListen((RequestDoneListener) response -> {
                        if (response.getType().equals(ServerResponseType.REQUEST_ACCEPTED))
                            new Handler(Looper.getMainLooper()).post(()->switchGui(guiMode.LISTENING));
                    });
                } else if (currentGuiMode == guiMode.LISTENING) {
                    ServerConnection.getInstance().stopListen();
                    switchGui(guiMode.START_TO_LISTEN);
                } else if (currentGuiMode == guiMode.CONNECT) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Enter desired name");

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.name_dialog, null);
                    builder.setView(dialogView);


                    builder.setPositiveButton("CONNECT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO create RequestDoneListener
                            resultName = ((EditText) dialogView.findViewById(R.id.name)).getText().toString();
                            ServerConnection.getInstance().startBroadcast(resultName, (RequestDoneListener) response -> {
                                if (response.getType().equals(ServerResponseType.REQUEST_ACCEPTED)) {
                                    broadcasting = true;
                                    new Handler(Looper.getMainLooper()).post(() -> switchGui(guiMode.PLAYING));
                                }
                            });
                            Log.d("Broadcast", "Starting broadcast");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();


                } else if (currentGuiMode == guiMode.PLAYING) {
                    ServerConnection.getInstance().stopBroadcast();
                    //switchGui(guiMode.CONNECT);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentGuiMode == guiMode.START_TO_LISTEN) {

                    Intent i = new Intent(ConnectActivity.this, VolumeRangeActivity.class);
                    startActivity(i);
                } else if(currentGuiMode == guiMode.PLAYING){
                    Intent i = new Intent(ConnectActivity.this, ChangeVolumeRangeActivity.class);
                    startActivity(i);
                } else if (currentGuiMode == guiMode.LISTENING) {

                    if (isShowingReactions) {
                        //hide reaction alternatives
                        fab.hide();
                        fab.setSize(SIZE_NORMAL);
                        fab.setLayoutParams(normalFabLp);
                        fab.setImageResource(R.drawable.ic_tag_faces_white_24dp);
                        fab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.fabColor)));
                        fab.show();

                        fab1.setClickable(false);
                        fab2.setClickable(false);
                        fab3.setClickable(false);
                        fab1.hide();
                        fab2.hide();
                        fab3.hide();

                        isShowingReactions = false;
                    } else {
                        //show reaction alternatives
                        fab.hide();
                        fab.setSize(SIZE_MINI);
                        fab.setLayoutParams(miniFabLp);
                        fab.setImageResource(R.drawable.ic_close_black_24dp);
                        fab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.disabledGrey)));
                        fab.show();


                        fab1.setClickable(true);
                        fab2.setClickable(true);
                        fab3.setClickable(true);
                        fab1.show();
                        fab2.show();
                        fab3.show();

                        fab1.setSize(SIZE_NORMAL);
                        fab2.setSize(SIZE_NORMAL);
                        fab3.setSize(SIZE_NORMAL);


                        isShowingReactions = true;
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

            changeBackgroundColor(getColor(R.color.backgroundCreamColor));
            setCircleColor(circleColor.GRAY);

            addFadeInAnimation(listenersTextView, LISTENER_FADE_DURATION);
            addInstantOperation(listenersTextView, () -> listenersTextView.setText(OnlineBroadcastService.getInstance().getNumberOfListeners() + ""));

            addInstantOperation(playerNameTextView, () -> playerNameTextView.setTextColor(getColor(R.color.actionBlueColor)));
            addInstantOperation(playerNameTextView, () -> playerNameTextView.setText(OnlineBroadcastService.getInstance().getBroadcasterName()));

            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setText("is playing"));
            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setTextColor(getColor(R.color.grayTextColor)));

            addFadeInAnimation(actionButton, ACTION_BUTTON_FADE_DURATION);
            addInstantOperation(actionButton, () -> actionButton.setText("Start Listening"));
            addInstantOperation(actionButton, () -> actionButton.setBackground(getDrawable(R.drawable.rounded_button_blue)));

            addFadeInAnimation(earImage, EAR_FADE_DURATION);
            earImage.setImageResource(R.drawable.ic_hearing_white_24dp);
            addInstantOperation(earImage, () -> earImage.setImageTintList(ColorStateList.valueOf(getColor(R.color.grayTextColor))));

            if(!isReturningFromListening) {
                addFadeInAnimation(playerNameTextView, NAME_FADE_DURATION);
                addFadeInAnimation(pianoStatusTextView, STATUS_FADE_DURATION);
            }else{
                AnimatorSet animset = new AnimatorSet();
                animset
                        .play(getTranslationAnimatorReset(pianoStatusTextView, 1500, ViewAnimationService.Axis.X, "pianoStatusTextView"))
                        .with(getTranslationAnimatorReset(playerNameTextView, 1500, ViewAnimationService.Axis.X, "playerNameTextView"))
                        .with(getTranslationAnimatorReset(playerNameTextView, 1500, ViewAnimationService.Axis.Y, "playerNameTextView"))
                        .with(getUniformScaleAnimatorReset(playerNameTextView, 1500, 1.3f))
                        .with(getTranslationAnimatorReset(listenerLayout, 1100, ViewAnimationService.Axis.Y, "listenerLayout"))
                        .with(getFadeAnimator(pianoStatusTextView, STATUS_FADE_DURATION, 0, 1))
                        .with(getFadeAnimator(playerNameTextView, NAME_FADE_DURATION, 0, 1));
                addAnimator(pianoStatusTextView,animset);
            }


            fab.setSize(SIZE_NORMAL);
            fab.setLayoutParams(normalFabLp);
            addInstantOperation(fab,
                    () -> fab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.actionBlueColor))),
                    () -> fab.setImageResource(R.drawable.ic_map_white_24dp)
            );
            addFadeWithScaleAnimation(fab, 400, 1, 0, 1);

            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab1.hide();
            fab2.hide();
            fab3.hide();

            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    try {

                        durationText.setText(formatDuration(OnlineBroadcastService.getInstance().getCurrentSessionDuration()));
                    } catch (NotLiveException e) {
                        e.printStackTrace();
                    }

                    handler.postDelayed(this, 1000);
                }
            }, 0);

        } else if (m == guiMode.LISTENING) {
            currentGuiMode = guiMode.LISTENING;

            changeBackgroundColor(getColor(R.color.backgroundBlueColor));
            setCircleColor(circleColor.BLUE);

            addInstantOperation(playerNameTextView, () -> playerNameTextView.setTextColor(getColor(R.color.actionBlueColor)));
            addInstantOperation(playerNameTextView, () -> playerNameTextView.setText(OnlineBroadcastService.getInstance().getBroadcasterName()));

            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setText("Currently listening to"));
            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setTextColor(getColor(R.color.grayTextColor)));

            addInstantOperation(actionButton, () -> actionButton.setText("Stop listening"));
            addInstantOperation(actionButton, () -> actionButton.setBackground(getDrawable(R.drawable.rounded_button_blue)));

            addFadeInAnimation(listenersTextView, LISTENER_FADE_DURATION);
            addInstantOperation(listenersTextView, () -> listenersTextView.setText(OnlineBroadcastService.getInstance().getNumberOfListeners() + ""));

            earImage.setImageResource(R.drawable.ic_hearing_white_24dp);
            addFadeInAnimation(earImage, EAR_FADE_DURATION);
            addInstantOperation(earImage, () -> earImage.setImageTintList(ColorStateList.valueOf(getColor(R.color.grayTextColor))));


            fab.setSize(SIZE_NORMAL);
            fab.setLayoutParams(normalFabLp);

            addInstantOperation(fab,
                    () -> fab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.fabColor))),
                    () -> fab.setImageResource(R.drawable.ic_tag_faces_white_24dp)
            );
            addFadeWithScaleAnimation(fab, 400, 1, 0, 1);

            AnimatorSet animset = new AnimatorSet();
            animset
                    .play(getTranslateToCenterInParentViewAnimator(pianoStatusTextView, 1500, ViewAnimationService.Axis.X, "pianoStatusTextView"))
                    .with(getTranslateToCenterInParentViewAnimator(playerNameTextView, 1500, ViewAnimationService.Axis.X, "playerNameTextView"))
                    .with(getTranslateToCenterInParentViewAnimator(playerNameTextView, 1500, ViewAnimationService.Axis.Y, "playerNameTextView"))
                    .with(getUniformScaleAnimator(playerNameTextView, 1500, 1.3f))
                    .with(getTranslateToCenterInParentViewAnimator(listenerLayout, 1500, ViewAnimationService.Axis.Y, "listenerLayout"))
                    .with(getFadeAnimator(playerNameTextView, NAME_FADE_DURATION, 0, 1))
                    .with(getFadeAnimator(pianoStatusTextView, STATUS_FADE_DURATION*3, 0, 1))
                    .with(getFadeAnimator(actionButton, ACTION_BUTTON_FADE_DURATION,0, 1));
            addAnimator(pianoStatusTextView, animset);
//            addAnimator(playerNameTextView,getUniformScaleAnimator(playerNameTextView, 500, 1.3f));

            audioDispatcher();

        } else if (m == guiMode.CANT_LISTEN) {
            currentGuiMode = guiMode.CANT_LISTEN;

            setCircleColor(circleColor.GRAY);
            changeBackgroundColor(getColor(R.color.backgroundCreamColor));

            addInstantOperation(durationText, () -> durationText.setVisibility(INVISIBLE));

            addInstantOperation(errorText, () -> errorText.setText("You are too far away to listen to the piano."));
            addInstantOperation(errorView, () -> errorView.setVisibility(VISIBLE));
            addFadeInAnimation(errorView, ERROR_FADE_DURATION);

            addInstantOperation(playerNameTextView, () -> playerNameTextView.setTextColor(getColor(R.color.actionBlueColor)));
            addInstantOperation(playerNameTextView, () -> playerNameTextView.setText(OnlineBroadcastService.getInstance().getBroadcasterName()));
            addFadeInAnimation(playerNameTextView, NAME_FADE_DURATION);

            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setTextColor(getColor(R.color.grayTextColor)));
            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setText("is playing"));
            addFadeInAnimation(pianoStatusTextView, STATUS_FADE_DURATION);

            addInstantOperation(listenersTextView, () -> listenersTextView.setText(OnlineBroadcastService.getInstance().getNumberOfListeners() + ""));
            addFadeInAnimation(listenersTextView, LISTENER_FADE_DURATION);

            addInstantOperation(earImage, () -> earImage.setImageTintList(ColorStateList.valueOf(getColor(R.color.grayTextColor))));
            addFadeInAnimation(earImage, EAR_FADE_DURATION);

            addInstantOperation(actionButton, () -> actionButton.setEnabled(false));
            addInstantOperation(actionButton, () -> actionButton.setText("Start to listen"));
            addInstantOperation(actionButton, () -> actionButton.setBackground(getDrawable(R.drawable.rounded_button_gray)));
            addFadeInAnimation(actionButton, ACTION_BUTTON_FADE_DURATION);


        } else if (m == guiMode.CANT_CONNECT) {
            currentGuiMode = guiMode.CANT_CONNECT;
            setCircleColor(circleColor.GRAY);
            changeBackgroundColor(getColor(R.color.backgroundGrayColor));

            addInstantOperation(durationText, () -> durationText.setVisibility(INVISIBLE));

            addInstantOperation(errorText, () -> errorText.setText("You are too far away to connect to the piano."));
            addInstantOperation(errorView, () -> errorView.setVisibility(VISIBLE));
            addFadeInAnimation(errorView, ERROR_FADE_DURATION);


            addInstantOperation(playerNameTextView, () -> playerNameTextView.setTextColor(getColor(R.color.actionRedColor)));
            addInstantOperation(playerNameTextView, () -> playerNameTextView.setText(OnlineBroadcastService.getInstance().getBroadcasterName()));
            addFadeInAnimation(playerNameTextView, NAME_FADE_DURATION);

            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setTextColor(getColor(R.color.whiteColor)));
            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setText("is playing"));
            addFadeInAnimation(pianoStatusTextView, STATUS_FADE_DURATION);

            addInstantOperation(listenersTextView, () -> listenersTextView.setText(OnlineBroadcastService.getInstance().getNumberOfListeners() + ""));
            addFadeInAnimation(listenersTextView, LISTENER_FADE_DURATION);

            addInstantOperation(earImage, () -> earImage.setImageTintList(ColorStateList.valueOf(getColor(R.color.grayTextColor))));
            addFadeInAnimation(earImage, EAR_FADE_DURATION);

            addInstantOperation(actionButton, () -> actionButton.setEnabled(false));
            addInstantOperation(actionButton, () -> actionButton.setText("Connect to piano"));
            addInstantOperation(actionButton, () -> actionButton.setBackground(getDrawable(R.drawable.rounded_button_gray)));
            addFadeInAnimation(actionButton, ACTION_BUTTON_FADE_DURATION);


        } else if (m == guiMode.CONNECT) {
            currentGuiMode = guiMode.CONNECT;

            changeBackgroundColor(getColor(R.color.backgroundGrayColor));
            setCircleColor(circleColor.GRAY);
            durationText.setVisibility(View.INVISIBLE);

            addInstantOperation(playerNameTextView, () -> playerNameTextView.setTextColor(getColor(R.color.actionRedColor)));
            addInstantOperation(playerNameTextView, () -> playerNameTextView.setText(OnlineBroadcastService.getInstance().getBroadcasterName()));
            addFadeInAnimation(playerNameTextView, NAME_FADE_DURATION);

            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setTextColor(getColor(R.color.whiteColor)));
            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setText("is playing"));
            addFadeInAnimation(pianoStatusTextView, STATUS_FADE_DURATION);

            addInstantOperation(actionButton, () -> actionButton.setText("Connect to piano"));
            addInstantOperation(actionButton, () -> actionButton.setBackground(getDrawable(R.drawable.rounded_button_red)));
            addFadeInAnimation(actionButton, ACTION_BUTTON_FADE_DURATION);

            addInstantOperation(listenersTextView, () -> listenersTextView.setText(OnlineBroadcastService.getInstance().getNumberOfListeners() + ""));
            addFadeInAnimation(listenersTextView, LISTENER_FADE_DURATION);

            addInstantOperation(earImage, () -> earImage.setImageTintList(ColorStateList.valueOf(getColor(R.color.grayTextColor))));
            addFadeInAnimation(earImage, EAR_FADE_DURATION);


        } else if (m == guiMode.PLAYING) {
            currentGuiMode = guiMode.PLAYING;

            audioDispatcher();
            changeBackgroundColor(getColor(R.color.backgroundRedColor));
            setCircleColor(circleColor.RED);

            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    try {

                        durationText.setText(formatDuration(OnlineBroadcastService.getInstance().getCurrentSessionDuration()));
                    } catch (NotLiveException e) {
                        e.printStackTrace();
                    }

                    handler.postDelayed(this, 1000);
                }
            }, 0);

            addFadeInAnimation(durationText, DURATION_TEXT_FADE_DURATION);

            addInstantOperation(playerNameTextView, () -> playerNameTextView.setTextColor(getColor(R.color.actionRedColor)));
            addInstantOperation(playerNameTextView, () -> playerNameTextView.setText("You"));
            addFadeInAnimation(playerNameTextView, NAME_FADE_DURATION);

            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setTextColor(getColor(R.color.whiteColor)));
            addInstantOperation(pianoStatusTextView, () -> pianoStatusTextView.setText("are playing"));
            addFadeInAnimation(pianoStatusTextView, STATUS_FADE_DURATION);

            addInstantOperation(actionButton, () -> actionButton.setText("Stop playing"));
            addInstantOperation(actionButton, () -> actionButton.setBackground(getDrawable(R.drawable.rounded_button_red)));
            addFadeInAnimation(actionButton, ACTION_BUTTON_FADE_DURATION);

            addInstantOperation(listenersTextView, () -> listenersTextView.setText(OnlineBroadcastService.getInstance().getNumberOfListeners() + ""));
            addFadeInAnimation(listenersTextView, LISTENER_FADE_DURATION);

            addInstantOperation(earImage, () -> earImage.setImageTintList(ColorStateList.valueOf(getColor(R.color.grayTextColor))));
            addFadeInAnimation(earImage, EAR_FADE_DURATION);

            fab.setSize(SIZE_NORMAL);
            fab.setLayoutParams(normalFabLp);

            addInstantOperation(fab,
                    () -> fab.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.fabColor))),
                    () -> fab.setImageResource(R.drawable.ic_map_white_24dp)
            );
            addFadeWithScaleAnimation(fab, 400, 1, 0, 1);
        }
        startAllAnimations();
    }

    private void teardownCurrentGui() {

        //Common teardowns
        addFadeOutAnimation(playerNameTextView, NAME_FADE_DURATION / 2);
        addFadeOutAnimation(pianoStatusTextView, STATUS_FADE_DURATION / 2);
        addFadeOutAnimation(actionButton, ACTION_BUTTON_FADE_DURATION / 2);
        addFadeOutAnimation(earImage, EAR_FADE_DURATION / 2);
        addFadeOutAnimation(listenersTextView, LISTENER_FADE_DURATION / 2);
        addInstantOperation(errorView, () -> errorView.setVisibility(INVISIBLE));
        isReturningFromListening =false;

        if (currentGuiMode == guiMode.START_TO_LISTEN) {
            addFadeWithScaleAnimation(fab, 400, 0, 1, 0);

        } else if (currentGuiMode == guiMode.LISTENING) {

            terminateAudioDispatcher();
            isShowingReactions = false;
            addFadeWithScaleAnimation(fab, 400, 0, 1, 0);
            isReturningFromListening = true;

        } else if (currentGuiMode == guiMode.CONNECT) {
            durationText.setVisibility(View.VISIBLE);

        } else if (currentGuiMode == guiMode.PLAYING) {
            addFadeOutAnimation(durationText, DURATION_TEXT_FADE_DURATION / 2);
            terminateAudioDispatcher();

        } else if (currentGuiMode == guiMode.CANT_CONNECT) {
            addInstantOperation(actionButton, () -> actionButton.setEnabled(true));
            addFadeOutAnimation(errorView, ERROR_FADE_DURATION / 2);

        } else if (currentGuiMode == guiMode.CANT_LISTEN) {
            addInstantOperation(actionButton, () -> actionButton.setEnabled(true));
            addFadeOutAnimation(errorView, ERROR_FADE_DURATION / 2);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show();
                    audioDispatcher();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }

    }

    Thread audioThread = null;
    private int deltaTime;
    private int oldTime;
    private double ampInDbOld;
    private int circle1Radius;
    private int circle2Radius;
    private int circle3Radius;
    private int circle4Radius;
    private be.tarsos.dsp.AudioDispatcher dispatcher;
    private final int RECORD_AUDIO_REQUEST_CODE = 1337;

    private void audioDispatcher() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_REQUEST_CODE);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "returning...", Toast.LENGTH_LONG).show();
            return;
        }

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);


        AMPDetectionHandler adh = new AMPDetectionHandler() {
            @Override
            public void handleBuffer(double currentSPL, AudioEvent audioEvent) {
                final double dbFloat = currentSPL;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processAMP(dbFloat);
                    }
                });
            }

        };
        ConnectActivity.AMP ampDetector = new ConnectActivity.AMP(adh);
        dispatcher.addAudioProcessor(ampDetector);

        audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }


    public interface AMPDetectionHandler {
        void handleBuffer(double currentSPL, AudioEvent audioEvent);
    }

    public class AMP implements AudioProcessor {
        private final AMPDetectionHandler handler;

        public AMP(AMPDetectionHandler handler) {
            this.handler = handler;
        }

        double currentSPL = 0;

        public boolean process(AudioEvent audioEvent) {
            currentSPL = soundPressureLevel(audioEvent.getFloatBuffer());
            this.handler.handleBuffer(currentSPL, audioEvent);
            return true;
        }

        /**
         * Returns the dBSPL for a buffer.
         *
         * @param buffer The buffer with audio information.
         * @return The dBSPL level for the buffer.
         */
        private double soundPressureLevel(final float[] buffer) {
            double value = Math.pow(localEnergy(buffer), 0.5);
            value = value / buffer.length;
            return linearToDecibel(value);
        }

        /**
         * Calculates the local (linear) energy of an audio buffer.
         *
         * @param buffer The audio buffer.
         * @return The local (linear) energy of an audio buffer.
         */
        private double localEnergy(final float[] buffer) {
            double power = 0.0D;
            for (float element : buffer) {
                power += element * element;
            }
            return power;
        }

        /**
         * Converts a linear to a dB value.
         *
         * @param value The value to convert.
         * @return The converted value.
         */
        private double linearToDecibel(final double value) {
            return 20.0 * Math.log10(value);
        }

        public void processingFinished() {
        }
    }

    private void processAMP(double ampInDb) {
        if (circle2Radius == 0) {
            circle1Radius = ((Circle) findViewById(R.id.circle1)).getRadius();
            circle2Radius = ((Circle) findViewById(R.id.circle2)).getRadius();
            circle3Radius = ((Circle) findViewById(R.id.circle3)).getRadius();
            circle4Radius = ((Circle) findViewById(R.id.circle4)).getRadius();
        }

        deltaTime = (int) System.currentTimeMillis() - oldTime;
        double deltaAmp = ampInDb - ampInDbOld;

        if (deltaTime > 500 && (deltaAmp > 3 || deltaAmp < -3)) {
            oldTime = (int) System.currentTimeMillis();
            ampInDbOld = ampInDb;

            if (ampInDb > -190) {

                final int delta = 150;
                final int dur = 4 * delta + delta;
                new CountDownTimer(dur, delta) {
                    int tick = 0;
                    int msLeft = dur - delta;

                    public void onTick(long millisUntilFinished) {
                        if (Math.round(millisUntilFinished / delta) <= msLeft) {
                            switch (tick) {
                                case 1:
                                    circle1.setRadius2(circle1Radius + 20);
                                    break;
                                case 2:
                                    circle1.setRadius(circle1Radius);
                                    circle2.setRadius2(circle2Radius + 20);
                                    break;
                                case 3:
                                    circle2.setRadius(circle2Radius);
                                    circle3.setRadius2(circle3Radius + 20);
                                    break;
                                case 4:
                                    circle3.setRadius(circle3Radius);
                                    circle4.setRadius2(circle4Radius + 20);
                                    break;
                            }
                            tick++;
                            msLeft = msLeft - delta;
                        }
                    }

                    public void onFinish() {
                        circle4.setRadius2(circle4Radius);
                    }

                }.start();
            }
        }
    }

    private void terminateAudioDispatcher() {
        if (audioThread != null) {
            dispatcher.stop();
            audioThread.interrupt();
            audioThread = null;
        }
    }

    private void changeBackgroundColor(int newColor) {
        addAnimator(backgroundView, getColorTransitionAnimator(backgroundView, 750, currentBackgroundColor, newColor));
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

        } else if (c == circleColor.GRAY) {
            colorTo1 = getResources().getColor(R.color.circle1GrayColor);
            colorTo2 = getResources().getColor(R.color.circle2GrayColor);
            colorTo3 = getResources().getColor(R.color.circle3GrayColor);
            colorTo4 = getResources().getColor(R.color.circle4GrayColor);

        }

        addAnimator(circle1, getColorTransitionAnimator(circle1, 250, colorFrom1, colorTo1));
        addAnimator(circle2, getColorTransitionAnimator(circle2, 450, colorFrom2, colorTo2));
        addAnimator(circle3, getColorTransitionAnimator(circle3, 600, colorFrom3, colorTo3));
        addAnimator(circle4, getColorTransitionAnimator(circle4, 700, colorFrom4, colorTo4));

        currentCircleColors[0] = colorTo1;
        currentCircleColors[1] = colorTo2;
        currentCircleColors[2] = colorTo3;
        currentCircleColors[3] = colorTo4;


    }


}