package com.ciu196.mobilecomputing.sounddetection;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.ciu196.mobilecomputing.Circle;
import com.ciu196.mobilecomputing.R;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class PitchAnimationActivity {

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

    Thread audioThread = null;
    private int base = 1;
    private int changeColor = 0;

    public void processPitch(float pitchInHz) {

        pitchText.setText("" + pitchInHz);
        noteText.setText("...");

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

        if (changeColor > 2)
            changeColor = 0;

        if (changeColor > 0)
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

    private void ChangeColor(int colorSwitch) {
        switch (colorSwitch) {
            case 1:
//                circle1.setColor(getResources().getColor(R.color.circle1RedColor));
//                circle2.setColor(getResources().getColor(R.color.circle2RedColor));
//                circle3.setColor(getResources().getColor(R.color.circle3RedColor));
//                circle4.setColor(getResources().getColor(R.color.circle4RedColor));
                break;
            case 2:
//                circle1.setColor(getResources().getColor(R.color.circle1BlueColor));
//                circle2.setColor(getResources().getColor(R.color.circle2BlueColor));
//                circle3.setColor(getResources().getColor(R.color.circle3BlueColor));
//                circle4.setColor(getResources().getColor(R.color.circle4BlueColor));
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
    private int oldTime;
    private double ampInDbOld;

//    public void processAMP(double ampInDb) {
//        if (circle2Radius == 0) {
////            circle1Radius = ((Circle) findViewById(R.id.circle1)).getRadius();
////            circle2Radius = ((Circle) findViewById(R.id.circle2)).getRadius();
////            circle3Radius = ((Circle) findViewById(R.id.circle3)).getRadius();
////            circle4Radius = ((Circle) findViewById(R.id.circle4)).getRadius();
//        }
//
//        dbText.setText("db: " + (int) ampInDb);
//
//        deltaTime = (int) System.currentTimeMillis() - oldTime;
//        double deltaAmp = ampInDb - ampInDbOld;
//
//        if (deltaTime > 500 && (deltaAmp > 3 || deltaAmp < -3)) {
//            oldTime = (int) System.currentTimeMillis();
//            ampInDbOld = ampInDb;
//
//            if (ampInDb > -190) {
//
//                final int delta = 150;
//                final int dur = 4 * delta + delta;
//                new CountDownTimer(dur, delta) {
//                    int tick = 0;
//                    int msLeft = dur - delta;
//
//                    public void onTick(long millisUntilFinished) {
//                        if (Math.round(millisUntilFinished / delta) <= msLeft) {
//                            switch (tick) {
//                                case 1:
//                                    circle1.setRadius2(circle1Radius + 20);
//                                    break;
//                                case 2:
//                                    circle1.setRadius(circle1Radius);
//                                    circle2.setRadius2(circle2Radius + 20);
//                                    break;
//                                case 3:
//                                    circle2.setRadius(circle2Radius);
//                                    circle3.setRadius2(circle3Radius + 20);
//                                    break;
//                                case 4:
//                                    circle3.setRadius(circle3Radius);
//                                    circle4.setRadius2(circle4Radius + 20);
//                                    break;
//                            }
//                            tick++;
//                            msLeft = msLeft - delta;
//                        }
//                    }
//
//                    public void onFinish() {
//                        circle4.setRadius2(circle4Radius);
//                    }
//
//                }.start();
//            }
//        }
//    }

//    public void  AudioDispatcher(){
//            be.tarsos.dsp.AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
//            PitchDetectionHandler pdh = new PitchDetectionHandler() {
//                @Override
//                public void handlePitch(PitchDetectionResult res, AudioEvent e) {
//                    final float pitchInHz = res.getPitch();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            processPitch(pitchInHz);
//                        }
//                    });
//                }
//            };
//            AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
//            dispatcher.addAudioProcessor(pitchProcessor);
//
//
//            AMPDetectionHandler adh = new AMPDetectionHandler() {
//                @Override
//                public void handleBuffer(double currentSPL, AudioEvent audioEvent) {
//                    final double dbFloat = currentSPL;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            processAMP(dbFloat);
//                        }
//                    });
//                }
//
//            };
//            AMP ampDetector = new AMP(adh);
//            dispatcher.addAudioProcessor(ampDetector);
//
//            audioThread = new Thread(dispatcher, "Audio Thread");
//            audioThread.start();
//    }

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

    private void terminate(){
        audioThread.interrupt();
        audioThread = null;
    }

}