package com.example.android.simplestopwatch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A very basic stopwatch for Android
 */
public class MainActivity extends AppCompatActivity {

    private long startTime;
    private long endTime;
    private long elapsed;
    private int count;
    private Timer timer;
    private TextView timerView;
    private ImageButton start, stop, pauseButton;
    private Object pauseLock;
    private boolean pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerView = (TextView) findViewById(R.id.timer);
        start = (ImageButton) findViewById(R.id.start);
        stop = (ImageButton) findViewById(R.id.stop);
        pauseButton = (ImageButton) findViewById(R.id.pause);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                if(!pause){
                    pauseTimer();
                }
                else{
                    resumeTimer();
                }
            }
        });

        pauseLock = new Object();
        pause = false;

    }

    /**
     * Starts the stopwatch
     *
     * @param view
     */
    public void startTimer(View view) {

        start.setEnabled(false);
        pauseButton.setEnabled(true);
        stop.setEnabled(true);


        timer = new Timer();

        startTime = 0;
        endTime = 0;
        elapsed = 0;
        count = 0;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                synchronized (pauseLock) {
                    while (pause) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                    {
                            if (count > 0) {
                                elapsed++;
                            } else {
                                count++;
                            }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTime();
                            }
                        });
                    }
                }

        }, 0, 1);
    }

    /**
     * Pauses the stopwatch
     */
    public void pauseTimer(){

      synchronized (pauseLock){
          pause = true;
      }
    }

    /**
     * Resumes the stopwatch
     */
    public void resumeTimer(){
        synchronized (pauseLock){
            pause = false;
            pauseLock.notifyAll();
        }
    }

    /**
     * Cancels the stopwatch
     *
     * @param view
     */
    public void cancelTimer(View view){
        if(!timer.equals(null)){
            timer.cancel();
        }

        start.setEnabled(true);
        stop.setEnabled(false);
        pauseButton.setEnabled(false);
        timerView.setText(String.format("%d:%02d:%02d", 0, 0, 0));
    }

    /**
     * Displays elapsed time as a formatted string
     */
    public void updateTime() {

        long milliseconds = (elapsed / 10) % 100;
        long seconds = (elapsed / 1000) % 60;
        long minutes = (elapsed / (1000 * 60)) % 60;
        long hours = (elapsed / (1000 * 60 * 60));

        timerView.setText(String.format("%d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds));
    }
}

