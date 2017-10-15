package com.ciu196.mobilecomputing;

//http://www.doepiccoding.com/blog/?p=195

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AudioMeter extends Activity {

    private static final int sampleRate = 8000;
    private AudioRecord audio;
    private int bufferSize;
    private double lastLevel = 0;
    private Thread thread;
    private static final int SAMPLE_DELAY = 75;
    private ImageView mouthImage;

    TextView pitchText;
    TextView noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch2_activity);

        mouthImage = (ImageView) findViewById(R.id.mounthHolder);
        pitchText = (TextView) findViewById(R.id.pitch);
        noteText = (TextView) findViewById(R.id.note);
        mouthImage.setKeepScreenOn(true);

        try {
            bufferSize = AudioRecord
                    .getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
        } catch (Exception e) {
            android.util.Log.e("TrackingFlow", "Exception", e);
        }
    }

    protected void onResume() {
        super.onResume();
        audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        audio.startRecording();
        thread = new Thread(new Runnable() {
            public void run() {
                while (thread != null && !thread.isInterrupted()) {
                    //Let's make the thread sleep for a the approximate sampling time
                    try {
                        Thread.sleep(SAMPLE_DELAY);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
//                    readAudioBuffer();//After this call we can get the last value assigned to the lastLevel variable
                    final double amp = getAmplitude();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
//                            if(lastLevel > 0 && lastLevel <= 15){
////                                pitchText.setText("pitch: low: " + lastLevel);
////                                pitchText.setText("low: " + (int)lastLevel + " amp2: "+ (int)amp);
//                                pitchText.setText("low: " + (int)lastLevel);
//
//                            }else if(lastLevel > 15 && lastLevel <= 35){
//                                pitchText.setText("med: " + (int)lastLevel);
//
//                            }else if(lastLevel > 35 && lastLevel <= 50){
//                                pitchText.setText("medhigh: " + (int)lastLevel);
//                            }
//                            if(lastLevel > 50){
//                                pitchText.setText("high: " + (int)lastLevel);
//                            }
                            if (amp > -50 && amp <= -35) {
                                pitchText.setText(" low: " + (int) amp);

                            }else if(amp > -35 && amp <= -20) {
                                pitchText.setText(" mid: " + (int) amp);
                            }else if(amp > -35 && amp <= 0) {
                                pitchText.setText(" high: " + (int) amp);
                            }else if(amp > 0) {
                                pitchText.setText(" very high: " + (int) amp);
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    /**
     * Functionality that gets the sound level out of the sample
     */
    private void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult = 1;

            if (audio != null) {

                // Sense the voice...
                bufferReadResult = audio.read(buffer, 0, bufferSize);
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++) {
                    sumLevel += buffer[i];
                }
                lastLevel = Math.abs((sumLevel / bufferReadResult));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getAmplitude() {
        short[] buffer = new short[bufferSize];
        audio.read(buffer, 0, bufferSize);
        int max = 0;
        double db = 0;
        for (short s : buffer) {
            if (Math.abs(s) > max) {
                max = Math.abs(s);
                db = 20.0 * Math.log10(max / 32767.0);
            }
        }
        return db;
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
        thread = null;
        try {
            if (audio != null) {
                audio.stop();
                audio.release();
                audio = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
