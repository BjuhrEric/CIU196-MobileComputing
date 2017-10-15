package com.ciu196.mobilecomputing;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
                final int dur = 5 + 1;
                new CountDownTimer(dur * 1000, 100) {
                    int delta = 1;
                    int tick = 1;
                    int ratio = 10;
                    int secondsLeft = ratio * dur - 1;

                    public void onTick(long millisUntilFinished) {
                        if (Math.round(millisUntilFinished / (1000 / ratio)) <= secondsLeft) {
                            System.out.println("math: " + Math.round(millisUntilFinished / 500));
                            System.out.println("secondsleft: " + secondsLeft);
                            ((Circle) view).setRadius(circle2Radius + delta * tick);
                            ((Circle) view).setColor("#d1172e");
                            tick++;
                            secondsLeft--;
                        }
                    }

                    public void onFinish() {
                        ((Circle) view).resetRadius();
                        ((Circle) view).setColor(outerColor);
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

    public double getAmplitude() {
//        short[] buffer = new short[bufferSize];
//        audio.read(buffer, 0, bufferSize);
//        int max = 0;
        double db = 0;
//        for (short s : buffer) {
//            if (Math.abs(s) > max) {
//                max = Math.abs(s);
//                db = 20.0 * Math.log10(max / 32767.0);
//            }
//        }
        return db;
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
                            dbText.setText("db: " + (int) dbFloat);
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


    //old
    public void getPitchFromMic() {
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
//                        processPitch(pitchInHz);
                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);
        audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }

    private void getAmpFromMic() {
//        audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
//                AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
//
//        audio.startRecording();
//        ampThread = new Thread(new Runnable() {
//            public void run() {
//                while (ampThread != null && !ampThread.isInterrupted()) {
//                    //Let's make the thread sleep for a the approximate sampling time
//                    try {
//                        Thread.sleep(SAMPLE_DELAY);
//                    } catch (InterruptedException ie) {
//                        ie.printStackTrace();
//                    }
////                    readAudioBuffer();//After this call we can get the last value assigned to the lastLevel variable
//                    final double amp = getAmplitude();
//
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
////                            if(lastLevel > 0 && lastLevel <= 15){
//////                                pitchText.setText("pitch: low: " + lastLevel);
//////                                pitchText.setText("low: " + (int)lastLevel + " amp2: "+ (int)amp);
////                                pitchText.setText("low: " + (int)lastLevel);
////
////                            }else if(lastLevel > 15 && lastLevel <= 35){
////                                pitchText.setText("med: " + (int)lastLevel);
////
////                            }else if(lastLevel > 35 && lastLevel <= 50){
////                                pitchText.setText("medhigh: " + (int)lastLevel);
////                            }
////                            if(lastLevel > 50){
////                                pitchText.setText("high: " + (int)lastLevel);
////                            }
//                            if (amp > -50 && amp <= -35) {
//                                dbText.setText(" low: " + (int) amp);
//                            } else if (amp > -35 && amp <= -20) {
//                                dbText.setText(" mid: " + (int) amp);
//                            } else if (amp > -35 && amp <= 0) {
//                                dbText.setText(" high: " + (int) amp);
//                            } else if (amp > 0) {
//                                dbText.setText(" very high: " + (int) amp);
//                            }
//                        }
//                    });
//                }
//            }
//        });
//        ampThread.start();
    }

}
