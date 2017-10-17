package com.ciu196.mobilecomputing;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.beatroot.Event;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AndroidFFMPEGLocator;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.BeatRootSpectralFluxOnsetDetector;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.fft.FFT;

import static junit.framework.Assert.assertEquals;

public class PitchAnimationActivity extends AppCompatActivity {

    TextView pitchText;
    TextView noteText;
    TextView dbText;
    Circle circle1;
    Circle circle2;
    Circle circle3;
    Circle circle4;
    int circle1Radius;
    int circle2Radius;
    int circle3Radius;
    int circle4Radius;
    private final static int capRadius = 80;

    Thread audioThread = null;

    ExecutorService service;
    AMP ampDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitchanimation_activity);

        pitchText = (TextView) findViewById(R.id.pitch);
        noteText = (TextView) findViewById(R.id.note);
        dbText = (TextView) findViewById(R.id.db);

        circle1 = (Circle) findViewById(R.id.circle1);
        circle2 = (Circle) findViewById(R.id.circle2);
        circle3 = (Circle) findViewById(R.id.circle3);
        circle4 = (Circle) findViewById(R.id.circle4);
        Button aBtn = (Button) findViewById(R.id.actionButtion);

        aBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(circle1, "alpha", 1f, .3f);
                fadeOut.setDuration(300);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(circle1, "alpha", .3f, 1f);
                fadeIn.setDuration(300);

                final AnimatorSet mAnimationSet = new AnimatorSet();

                mAnimationSet.play(fadeIn).after(fadeOut);

                mAnimationSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
//                        mAnimationSet.start();
                    }
                });
                mAnimationSet.start();
            }
        });

        circle2 = (Circle) findViewById(R.id.circle2);
        circle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int dur = 400 + 100;
                new CountDownTimer(dur, 100) {
                    int tick = 0;
                    int ratio = 1;
                    int secondsLeft = ratio * dur - 100;

                    public void onTick(long millisUntilFinished) {
                        if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {

                            switch (tick) {
                                case 1:
                                    String hexColor = String.format("#%06X", (0xFFFFFF & circle1.getColor()));
                                    int toColor = Color.parseColor("#59" + hexColor.substring(1, hexColor.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle1, 200, circle1.getColor(), toColor);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 2:
                                    String hexColor2 = String.format("#%06X", (0xFFFFFF & circle2.getColor()));
                                    int toColor2 = Color.parseColor("#59" + hexColor2.substring(1, hexColor2.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle2, 200, circle2.getColor(), toColor2);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 3:
                                    String hexColor3 = String.format("#%06X", (0xFFFFFF & circle3.getColor()));
                                    int toColor3 = Color.parseColor("#59" + hexColor3.substring(1, hexColor3.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle3, 200, circle3.getColor(), toColor3);
//
                                    break;
                                case 4:
                                    String hexColor4 = String.format("#%06X", (0xFFFFFF & circle4.getColor()));
                                    int toColor4 = Color.parseColor("#59" + hexColor4.substring(1, hexColor4.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle4, 200, circle4.getColor(), toColor4);
//                                    circle4.setAlpha(0.8f);
                                    break;
                            }
                            tick++;
                            System.out.println(tick);
                            secondsLeft--;
                        }
                    }

                    public void onFinish() {
                        System.out.println("Onfinish()");
                    }

                }.start();
            }
        });


        ///STACKOVERFLOW
//        new AndroidFFMPEGLocator(this);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                File externalStorage = Environment.getExternalStorageDirectory();
//                File sourceFile = new File(externalStorage.getAbsolutePath() , "/440.mp3");
//
//                Uri uri = Uri.parse("android.resource://"+getPackageName()+"/raw/testaudio");
//                String path = uri.getPath(); // "file:///mnt/sdcard/FileName.mp3"
////                InputStream fIn = getBaseContext().getResources().openRawResource(R.raw.testaudio);
//
//                final int bufferSize = 4096;
//                final int fftSize = bufferSize / 2;
//                final int sampleRate = 44100;
//
//                be.tarsos.dsp.AudioDispatcher audioDispatcher;
//                audioDispatcher = AudioDispatcherFactory.fromPipe(sourceFile.getAbsolutePath(), sampleRate, bufferSize, 0);
////                audioDispatcher = AudioDispatcherFactory.fromPipe(uri.getPath(), sampleRate, bufferSize, 0);
//                audioDispatcher.addAudioProcessor(new AudioProcessor() {
//
//                    FFT fft = new FFT(bufferSize);
//                    final float[] amplitudes = new float[fftSize];
//
//                    @Override
//                    public boolean process(AudioEvent audioEvent) {
//                        float[] audioBuffer = audioEvent.getFloatBuffer();
//                        fft.forwardTransform(audioBuffer);
//                        fft.modulus(audioBuffer, amplitudes);
//
//                        for (int i = 0; i < amplitudes.length; i++) {
//                            System.out.println(String.format("Amplitude at %3d Hz: %8.3f", (int) fft.binToHz(i, sampleRate), amplitudes[i]));
//                        }
//
//                        return true;
//                    }
//
//                    @Override
//                    public void processingFinished() {
//
//                    }
//                });
//                audioDispatcher.run();
//            }
//        }).start();

    }

    private CountDownTimer circleTimer;
    private int base = 1;
    private int changeColor = 0;

    public void processPitch(float pitchInHz) {
//        if (circle2Radius == 0) {
//            circle1Radius = ((Circle) findViewById(R.id.circle1)).getRadius() - 20;
//            circle2Radius = ((Circle) findViewById(R.id.circle2)).getRadius() - 20;
//            circle3Radius = ((Circle) findViewById(R.id.circle3)).getRadius() - 20;
//            circle4Radius = ((Circle) findViewById(R.id.circle4)).getRadius() - 20;
//        }

        pitchText.setText("" + pitchInHz);
        noteText.setText("...");

//        if (pitchInHz <= 100) {
//            circle1.setRadius(circle1Radius + (int) Math.round(pitchInHz));
//            circle2.setRadius(circle2Radius + (int) Math.round(pitchInHz));
//            circle3.setRadius(circle3Radius + (int) Math.round(pitchInHz));
////            circle4.setRadius(circle4Radius + (int) Math.round(pitchInHz));
//        } else if (pitchInHz > 100 && pitchInHz < 1000) {
//            circle1.setRadius(circle1Radius + (int) Math.round(pitchInHz) / 10);
//            circle2.setRadius(circle2Radius + (int) Math.round(pitchInHz) / 10);
//            circle3.setRadius(circle3Radius + (int) Math.round(pitchInHz) / 5);
////            circle4.setRadius(circle4Radius + (int) Math.round(pitchInHz) / 10);
//        }

        if ((int) Math.floor(pitchInHz / 110) <= 1) {
            base = 1;
        } else if ((int) Math.floor(pitchInHz / 220) <= 1) {
            base = 2;
        } else if ((int) Math.floor(pitchInHz / 440) <= 1) {
            base = 3;
        } else if ((int) Math.floor(pitchInHz / 880) <= 1) {
            base = 4;
        } else if ((int) Math.floor(pitchInHz / 1760) <= 1) {
            base = 5;
        } else if ((int) Math.floor(pitchInHz / 3520) <= 1) {
            base = 6;
        } else if ((int) Math.floor(pitchInHz / 7040) <= 1) {
            base = 7;
        }

        if(changeColor>2)
            changeColor=0;

        if(changeColor>0)
            ChangeColor(changeColor);

        //System.out.println("base " + base + " pithz: " + pitchInHz + " pitchAndBase: " + 110 * powerN(base - 1));

        if (pitchInHz >= 110 * powerN(base - 1) && pitchInHz < 123.47 * powerN(base - 1)) {
            //A
            noteText.setText("A");
            changeColor++;
        } else if (pitchInHz >= 123.47 * powerN(base - 1) && pitchInHz < 130.81 * powerN(base - 1)) {
            //B
            noteText.setText("B");
            changeColor++;
        } else if (pitchInHz >= 130.81 * powerN(base - 1) && pitchInHz < 146.83 * powerN(base - 1)) {
            //C
            noteText.setText("C");
            changeColor++;
        } else if (pitchInHz >= 146.83 * powerN(base - 1) && pitchInHz < 164.81 * powerN(base - 1)) {
            //D
            noteText.setText("D");
            changeColor++;
        } else if (pitchInHz >= 164.81 * powerN(base - 1) && pitchInHz <= 174.61 * powerN(base - 1)) {
            //E
            noteText.setText("E");
            changeColor++;
        } else if (pitchInHz >= 174.61 * powerN(base - 1) && pitchInHz < 185 * powerN(base - 1)) {
            //F
            noteText.setText("F");
            changeColor++;
        } else if (pitchInHz >= 185 * powerN(base - 1) && pitchInHz < 196 * powerN(base - 1)) {
            //G
            noteText.setText("G");
            changeColor++;
        }
    }

    private void ChangeColor(int colorSwitch){
        switch (colorSwitch){
            case 1 :
                circle1.setColor(getResources().getColor(R.color.circle1RedColor));
                circle2.setColor(getResources().getColor(R.color.circle2RedColor));
                circle3.setColor(getResources().getColor(R.color.circle3RedColor));
                circle4.setColor(getResources().getColor(R.color.circle4RedColor));
                break;
            case 2 :
                circle1.setColor(getResources().getColor(R.color.circle1BlueColor));
                circle2.setColor(getResources().getColor(R.color.circle2BlueColor));
                circle3.setColor(getResources().getColor(R.color.circle3BlueColor));
                circle4.setColor(getResources().getColor(R.color.circle4BlueColor));
                break;
        }
    }

    public static double powerN(int n) {
        int base = 2;
        if (n < 0) {
            System.out.println("this is the n: " + n);
            throw new IllegalArgumentException("Illegal Power Argument");
        }
        if (n == 0) {
            return 1;
        } else {
            return base * powerN(n - 1);
        }
    }

    private int deltaTime;
    private int deltaTime2;
    private int oldTime;
    private int oldTime2;
    private double ampInDbOld;
    private CountDownTimer idleAnimation;
    private CountDownTimer idleAnimation2;

    public void processAMP(double ampInDb) {
        if (circle2Radius == 0) {
            circle1Radius = ((Circle) findViewById(R.id.circle1)).getRadius();
            circle2Radius = ((Circle) findViewById(R.id.circle2)).getRadius();
            circle3Radius = ((Circle) findViewById(R.id.circle3)).getRadius();
            circle4Radius = ((Circle) findViewById(R.id.circle4)).getRadius();
        }

        dbText.setText("db: " + (int) ampInDb);

        deltaTime = (int) System.currentTimeMillis() - oldTime;
        deltaTime2 = (int) System.currentTimeMillis() - oldTime2;
        double deltaAmp = ampInDb - ampInDbOld;

        if (deltaTime > 500) {
            oldTime = (int) System.currentTimeMillis();
            ampInDbOld = ampInDb;

            if (ampInDb > -77) {
            } else if (ampInDb > -81) {
            } else if (ampInDb > -86) {
            } else if (ampInDb > -90) {
            } else if (ampInDb > -190) {
//                circle4.setRadius(circle4Radius + -(int) Math.round(ampInDb) + 2);
//                circle3.setRadius(circle3Radius + -(int) Math.round(ampInDb) + 8);
//                circle2.setRadius(circle2Radius + -(int) Math.round(ampInDb) + 5);
//                circle1.setRadius(circle1Radius + -(int) Math.round(ampInDb) + 10);

            }
        } else if (deltaTime2 > 500 && (deltaAmp > 3 || deltaAmp < -3)) {
            oldTime2 = (int) System.currentTimeMillis();
            ampInDbOld = ampInDb;
//                if (ampInDb > -190) {
//                    final int dur = 400 + 100;
//                    new CountDownTimer(dur, 100) {
//                        int tick = 0;
//                        int ratio = 1;
//                        int secondsLeft = ratio * dur - 100;
//
//                        public void onTick(long millisUntilFinished) {
//                            if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {
//
//                                switch (tick) {
//                                    case 1:
//                                        String hexColor = String.format("#%06X", (0xFFFFFF & circle1.getColor()));
//                                        int toColor = Color.parseColor("#59" + hexColor.substring(1, hexColor.length()));
//                                        ViewAnimationService.colorTransitionAndBackAnimation(circle1, 200, circle1.getColor(), toColor);
////                                    circle1.setAlpha(0.8f);
//                                        break;
//                                    case 2:
//                                        String hexColor2 = String.format("#%06X", (0xFFFFFF & circle2.getColor()));
//                                        int toColor2 = Color.parseColor("#59" + hexColor2.substring(1, hexColor2.length()));
//                                        ViewAnimationService.colorTransitionAndBackAnimation(circle2, 200, circle2.getColor(), toColor2);
////                                    circle1.setAlpha(0.8f);
//                                        break;
//                                    case 3:
//                                        String hexColor3 = String.format("#%06X", (0xFFFFFF & circle3.getColor()));
//                                        int toColor3 = Color.parseColor("#59" + hexColor3.substring(1, hexColor3.length()));
//                                        ViewAnimationService.colorTransitionAndBackAnimation(circle3, 200, circle3.getColor(), toColor3);
////
//                                        break;
//                                    case 4:
//                                        String hexColor4 = String.format("#%06X", (0xFFFFFF & circle4.getColor()));
//                                        int toColor4 = Color.parseColor("#80" + hexColor4.substring(1, hexColor4.length()));
//                                        ViewAnimationService.colorTransitionAndBackAnimation(circle4, 200, circle4.getColor(), toColor4);
////                                    circle4.setAlpha(0.8f);
//                                        break;
//                                }
//                                tick++;
//                                secondsLeft--;
//                            }
//                        }
//
//                        public void onFinish() {
//                        }
//
//                    }.start();
//                }
//            circle1.setRadius(circle1Radius);
//            circle2.setRadius(circle2Radius);
//            circle3.setRadius(circle3Radius);
//            circle4.setRadius(circle4Radius);
            if (ampInDb > -190) {

                final int delta = 150;
                final int dur = 4*delta + delta;
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
                            msLeft = msLeft-delta;
                        }
                    }

                    public void onFinish() {
                        circle4.setRadius2(circle4Radius);
                    }

                }.start();
            }
        }

        /*
        if (deltaTime > 500) {
            if (ampInDb > -70) {
                oldTime = (int) System.currentTimeMillis();
                ampInDbOld = ampInDb;
                final int dur = 400 + 100;
                new CountDownTimer(dur, 100) {
                    int tick = 0;
                    int ratio = 1;
                    int secondsLeft = ratio * dur - 100;

                    public void onTick(long millisUntilFinished) {
                        if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {

                            switch (tick) {
                                case 1:
                                    String hexColor = String.format("#%06X", (0xFFFFFF & circle1.getColor()));
                                    int toColor = Color.parseColor("#59" + hexColor.substring(1, hexColor.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle1, 200, circle1.getColor(), toColor);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 2:
                                    String hexColor2 = String.format("#%06X", (0xFFFFFF & circle2.getColor()));
                                    int toColor2 = Color.parseColor("#59" + hexColor2.substring(1, hexColor2.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle2, 200, circle2.getColor(), toColor2);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 3:
                                    String hexColor3 = String.format("#%06X", (0xFFFFFF & circle3.getColor()));
                                    int toColor3 = Color.parseColor("#59" + hexColor3.substring(1, hexColor3.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle3, 200, circle3.getColor(), toColor3);
//
                                    break;
                                case 4:
                                    String hexColor4 = String.format("#%06X", (0xFFFFFF & circle4.getColor()));
                                    int toColor4 = Color.parseColor("#80" + hexColor4.substring(1, hexColor4.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle4, 200, circle4.getColor(), toColor4);
//                                    circle4.setAlpha(0.8f);
                                    break;
                            }
                            tick++;
                            secondsLeft--;
                        }
                    }

                    public void onFinish() {
                    }

                }.start();
            } else if (ampInDb > -75) {
                oldTime = (int) System.currentTimeMillis();
                ampInDbOld = ampInDb;
                final int dur = 400 + 100;
                new CountDownTimer(dur, 100) {
                    int tick = 0;
                    int ratio = 1;
                    int secondsLeft = ratio * dur - 100;

                    public void onTick(long millisUntilFinished) {
                        if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {

                            switch (tick) {
                                case 1:
                                    String hexColor = String.format("#%06X", (0xFFFFFF & circle1.getColor()));
                                    int toColor = Color.parseColor("#59" + hexColor.substring(1, hexColor.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle1, 200, circle1.getColor(), toColor);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 2:
                                    String hexColor2 = String.format("#%06X", (0xFFFFFF & circle2.getColor()));
                                    int toColor2 = Color.parseColor("#59" + hexColor2.substring(1, hexColor2.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle2, 200, circle2.getColor(), toColor2);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 3:
                                    String hexColor3 = String.format("#%06X", (0xFFFFFF & circle3.getColor()));
                                    int toColor3 = Color.parseColor("#59" + hexColor3.substring(1, hexColor3.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle3, 200, circle3.getColor(), toColor3);
//
                                    break;
                            }
                            tick++;
                            secondsLeft--;
                        }
                    }

                    public void onFinish() {
                    }

                }.start();
            } else if (ampInDb > -80) {
                oldTime = (int) System.currentTimeMillis();
                ampInDbOld = ampInDb;
                final int dur = 400 + 100;
                new CountDownTimer(dur, 100) {
                    int tick = 0;
                    int ratio = 1;
                    int secondsLeft = ratio * dur - 100;

                    public void onTick(long millisUntilFinished) {
                        if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {

                            switch (tick) {
                                case 1:
                                    String hexColor = String.format("#%06X", (0xFFFFFF & circle1.getColor()));
                                    int toColor = Color.parseColor("#59" + hexColor.substring(1, hexColor.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle1, 200, circle1.getColor(), toColor);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 2:
                                    String hexColor2 = String.format("#%06X", (0xFFFFFF & circle2.getColor()));
                                    int toColor2 = Color.parseColor("#59" + hexColor2.substring(1, hexColor2.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle2, 200, circle2.getColor(), toColor2);
//                                    circle1.setAlpha(0.8f);
                                    break;
                            }
                            tick++;
                            secondsLeft--;
                        }
                    }

                    public void onFinish() {
                    }

                }.start();
            } else if (ampInDb > -90) {
                oldTime = (int) System.currentTimeMillis();
                ampInDbOld = ampInDb;
                final int dur = 400 + 100;
                new CountDownTimer(dur, 100) {
                    int tick = 0;
                    int ratio = 1;
                    int secondsLeft = ratio * dur - 100;

                    public void onTick(long millisUntilFinished) {
                        if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {

                            switch (tick) {
                                case 1:
                                    String hexColor = String.format("#%06X", (0xFFFFFF & circle1.getColor()));
                                    int toColor = Color.parseColor("#59" + hexColor.substring(1, hexColor.length()));
                                    ViewAnimationService.colorTransitionAndBackAnimation(circle1, 200, circle1.getColor(), toColor);
//                                    circle1.setAlpha(0.8f);
                                    break;
                            }
                            tick++;
                            secondsLeft--;
                        }
                    }

                    public void onFinish() {
                    }

                }.start();
            }
        }
        */
    }

    public class AudioDispatcher implements Callable<Object> {
        @Override
        public Object call() throws Exception {
            be.tarsos.dsp.AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
//
//            Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/testaudio");
//            String path = uri.getPath(); // "file:///mnt/sdcard/FileName.mp3"
//            File file = new File(new URI(path));
//
////            InputStream fIn = getBaseContext().getResources().openRawResource(R.raw.testaudio);
//            be.tarsos.dsp.AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe( file.getPath(),44100,5000,2500);
//            dispatcher.addAudioProcessor(new AndroidAudioPlayer(dispatcher.getFormat(),5000, AudioManager.STREAM_MUSIC));
//            dispatcher.run();
//
            PitchDetectionHandler pdh = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                    final float pitchInHz = res.getPitch();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            processPitch(pitchInHz);
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

            ampDetector = new AMP(adh);
            dispatcher.addAudioProcessor(ampDetector);

            double threshold = 8;
            double sensitivity = 20;

            PercussionOnsetDetector mPercussionDetector = new PercussionOnsetDetector(22050, 1024,
                    new OnsetHandler() {
                        @Override
                        public void handleOnset(double v, double v1) {
                            System.out.println("Clap Detected");
                        }
                    }, sensitivity, threshold);

            dispatcher.addAudioProcessor(mPercussionDetector);

            audioThread = new Thread(dispatcher, "Audio Thread");
            audioThread.start();
            return null;
        }
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

    protected void onResume() {
        super.onResume();
//        getPitchFromMic();
//        getAmpFromMic();
        service = Executors.newFixedThreadPool(1);
        service.submit(new AudioDispatcher());
    }

    @Override
    protected void onPause() {
        super.onPause();
//        audioThread.interrupt();
//        audioThread = null;
//        service.shutdown();
    }
}

// circle2.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(final View view) {
//        circle2Radius = ((Circle) findViewById(R.id.circle2)).getRadius();
//
//final int outerColor = ((Circle) findViewById(R.id.circle2)).getColor();
//final int dur = 5 + 1;
//        new CountDownTimer(dur * 1000, 100) {
//        int delta = 1;
//        int tick = 1;
//        int ratio = 10;
//        int secondsLeft = ratio * dur - 1;
//
//public void onTick(long millisUntilFinished) {
//        if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {
//        System.out.println("math: " + Math.round(millisUntilFinished / 500));
//        System.out.println("secondsleft: " + secondsLeft);
//        ((Circle) view).setRadius(circle2Radius + delta * tick);
//        ((Circle) view).setColor("#d1172e");
//        tick++;
//        secondsLeft--;
//        }
//        }
//
//public void onFinish() {
//        ((Circle) view).resetRadius();
//        ((Circle) view).setColor(outerColor);
//        }
//
//        }.start();
//        }
//        });