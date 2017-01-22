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
        start.setColorFilter(getResources().getColor(R.color.colorAccent));
        pauseButton.setEnabled(true);
        pauseButton.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        stop.setEnabled(true);
        stop.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));

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

        timerView.setText(getResources().getString(R.string.timer_zero));
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

        timer.cancel();

        pause = false; //if cancelled from pause position

        start.setEnabled(true);
        start.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        pauseButton.setEnabled(false);
        pauseButton.setColorFilter(getResources().getColor(R.color.colorAccent));
        stop.setEnabled(false);
        stop.setColorFilter(getResources().getColor(R.color.colorAccent));
        pauseButton.setEnabled(false);

    }

    /**
     * Displays elapsed time as a formatted string
     */
    public void updateTime() {

        long milliseconds = (elapsed / 10) % 100;
        long seconds = (elapsed / 1000) % 60;
        long minutes = (elapsed / (1000 * 60)) % 60;
        long hours = (elapsed / (1000 * 60 * 60));

        timerView.setText(String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds));
    }
}

