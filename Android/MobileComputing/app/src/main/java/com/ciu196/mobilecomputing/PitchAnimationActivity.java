package com.ciu196.mobilecomputing;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

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

        circle1.setColor(getResources().getColor(R.color.circle1BlueColor));
        circle2.setColor(getResources().getColor(R.color.circle2BlueColor));
        circle3.setColor(getResources().getColor(R.color.circle3BlueColor));
        circle4.setColor(getResources().getColor(R.color.circle4BlueColor));


        aBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circle1.setColor(getResources().getColor(R.color.circle1BlueColor));
            }
        });

//        circle1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), AudioMeter.class);
//                startActivity(i);
//            }
//        });

        circle2 = (Circle) findViewById(R.id.circle2);
        circle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                circle2Radius = ((Circle) findViewById(R.id.circle2)).getRadius();

                final int outerColor = ((Circle) findViewById(R.id.circle2)).getColor();
                final int dur = 800 + 100;
                new CountDownTimer(dur, 100) {
                    int tick = 0;
                    int ratio = 1;
                    int secondsLeft = ratio * dur - 100;

                    public void onTick(long millisUntilFinished) {
                        if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {

                            switch (tick) {
                                case 1:
                                    String hexColor = String.format("#%06X", (0xFFFFFF & circle1.getColor()));
                                    int toColor = Color.parseColor("#59"+ hexColor.substring(1,hexColor.length()));
                                    ViewAnimationService.colorTransitionAnimation(circle1, 200, circle1.getColor(), toColor);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 2:
                                    String hexColor2 = String.format("#%06X", (0xFFFFFF & circle2.getColor()));
                                    int toColor2 = Color.parseColor("#59"+ hexColor2.substring(1,hexColor2.length()));
                                    ViewAnimationService.colorTransitionAnimation(circle2, 200, circle2.getColor(), toColor2);
//                                    circle1.setAlpha(0.8f);
                                    break;
                                case 3:
                                    String hexColor3 = String.format("#%06X", (0xFFFFFF & circle3.getColor()));
                                    int toColor3 = Color.parseColor("#59"+ hexColor3.substring(1,hexColor3.length()));
                                    ViewAnimationService.colorTransitionAnimation(circle3, 200, circle3.getColor(), toColor3);
//
                                    break;
                                case 4:
                                    String hexColor4 = String.format("#%06X", (0xFFFFFF & circle4.getColor()));
                                    int toColor4 = Color.parseColor("#59"+ hexColor4.substring(1,hexColor4.length()));
                                    ViewAnimationService.colorTransitionAnimation(circle4, 200, circle4.getColor(), toColor4);
/
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
                        circle1.clearAnimation();
                        circle2.clearAnimation();
                        circle3.clearAnimation();
                        circle4.clearAnimation();
//                        ViewAnimationService.colorTransitionAnimation(circle1, 100, circle1.getColor(), getResources().getColor(R.color.circle1BlueColor));
//                        ViewAnimationService.colorTransitionAnimation(circle2, 100, circle2.getColor(), getResources().getColor(R.color.circle2BlueColor));
//                        ViewAnimationService.colorTransitionAnimation(circle3, 100, circle3.getColor(), getResources().getColor(R.color.circle3BlueColor));
//                        ViewAnimationService.colorTransitionAnimation(circle4, 100, circle4.getColor(), getResources().getColor(R.color.circle4BlueColor));
                        circle1.setColor(getResources().getColor(R.color.circle1BlueColor));
                        circle2.setColor(getResources().getColor(R.color.circle2BlueColor));
                        circle3.setColor(getResources().getColor(R.color.circle3BlueColor));
                        circle4.setColor(getResources().getColor(R.color.circle4BlueColor));
//                        circle1.setAlpha(1);
//                        circle1.set
//                        circle2.setAlpha(1);
//                        circle3.setAlpha(1);
//                        circle4.setAlpha(1);
                    }

                }.start();
            }
        });
    }

    protected void onResume() {
        super.onResume();
//        getPitchFromMic();
//        getAmpFromMic();
        service = Executors.newFixedThreadPool(1);
        service.submit(new Pitch());
    }

    public void processPitch(float pitchInHz) {
        if (circle2Radius == 0) {
            circle1Radius = ((Circle) findViewById(R.id.circle1)).getRadius() - 20;
            circle2Radius = ((Circle) findViewById(R.id.circle2)).getRadius() - 20;
        }

        pitchText.setText("" + pitchInHz);
//        if (pitchInHz == -1)
//            circle2.resetRadius();
//        else
        if (pitchInHz <= 100) {
            circle1.setRadius(circle1Radius + (int) Math.round(pitchInHz));
            circle2.setRadius(circle2Radius + (int) Math.round(pitchInHz));
        } else if (pitchInHz > 100 && pitchInHz < 1000) {
            circle1.setRadius(circle1Radius + (int) Math.round(pitchInHz) / 10);
            circle2.setRadius(circle2Radius + (int) Math.round(pitchInHz) / 10);
        }


        if (pitchInHz >= 110 && pitchInHz < 123.47) {
            //A
            noteText.setText("A");
        } else if (pitchInHz >= 123.47 && pitchInHz < 130.81) {
            //B
            noteText.setText("B");
        } else if (pitchInHz >= 130.81 && pitchInHz < 146.83) {
            //C
            noteText.setText("C");
        } else if (pitchInHz >= 146.83 && pitchInHz < 164.81) {
            //D
            noteText.setText("D");
        } else if (pitchInHz >= 164.81 && pitchInHz <= 174.61) {
            //E
            noteText.setText("E");
        } else if (pitchInHz >= 174.61 && pitchInHz < 185) {
            //F
            noteText.setText("F");
        } else if (pitchInHz >= 185 && pitchInHz < 196) {
            //G
            noteText.setText("G");
        }


    }

    private CountDownTimer circleTimer;

    public void processAMP(double ampInDb) {
        dbText.setText("db: " + (int) ampInDb);

        if (ampInDb == -81) {
            if(circleTimer != null)
               circleTimer.cancel();

            final int dur = 4000 + 1000;
            circleTimer = new CountDownTimer(dur, 100) {
                int circleCount = 1;
                int ratio = 10;
                int secondsLeft = ratio * dur - 1;

                public void onTick(long millisUntilFinished) {
                    if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {

                        switch (circleCount) {
                            case 0:
                                circle1.setAlpha(0.8f);
                                break;
                            case 1:
                                circle2.setAlpha(0.8f);
                                break;
                            case 3:
                                circle3.setAlpha(0.8f);
                                break;
                            case 4:
                                circle4.setAlpha(0.8f);
                                break;
                        }

                        circleCount++;
                        secondsLeft--;
                    }
                }

                public void onFinish() {
                    circle1.setColor(getResources().getColor(R.color.circle1BlueColor));
                    circle2.setColor(getResources().getColor(R.color.circle2BlueColor));
                    circle3.setColor(getResources().getColor(R.color.circle3BlueColor));
                    circle4.setColor(getResources().getColor(R.color.circle4BlueColor));
                }

            }.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioThread.interrupt();
        audioThread = null;
        service.shutdown();
    }


    public class Pitch implements Callable<Object> {
        @Override
        public Object call() throws Exception {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
//        new AndroidFFMPEGLocator(this);
//        String path = "android.resource://" + getPackageName() + "/" + R.raw.testaudio;
//        File mp3 = new File(path);
//        AudioDispatcher dispatcher =  AudioDispatcherFactory.fromPipe(path, 44100, 4096, 0);
//
//        AudioDispatcher dispatcher =  AudioDispatcherFactory.fromPipe("testaudio.mp3", 44100, 4096, 0);
            PitchDetectionHandler pdh = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                    final float pitchInHz = res.getPitch();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //processPitch(pitchInHz);
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