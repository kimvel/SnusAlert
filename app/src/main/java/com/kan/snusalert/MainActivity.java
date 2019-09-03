package com.kan.snusalert;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    // the countdown itself
    public CountDownTimer timer;

    // 3 hours in milliseconds
    int waitTimeInMilliSeconds = 10800000;

    //progress in progresBar
    int actualProgress = 0;

    // get FXML elements
    ConstraintLayout constraintLayout;
    ImageButton snusButton;
    TextView counter;
    RadioGroup radioGroup;
    ProgressBar progress;
    Switch aSwitch;
    RadioButton rb;

    Handler handler = new Handler ();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        constraintLayout = findViewById (R.id.conLayout);
        snusButton = findViewById (R.id.snusButton);
        counter = findViewById (R.id.counter);
        radioGroup = findViewById (R.id.radioGroup);
        progress = findViewById (R.id.progressBar);
        aSwitch = findViewById (R.id.snusMode);
        rb = findViewById (R.id.radioButton);

        radioGroup.setOnCheckedChangeListener (this);

        snusButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                snusButton.setEnabled(false);
                startTimer ();
                /*runProgressBar ();*/
            }
        });
    }

    @Override
    public void onCheckedChanged (RadioGroup radioGroup, int i) {
        if (i == 2131165281){
            waitTimeInMilliSeconds = 3600000;
        } else if(i == 2131165282){
            waitTimeInMilliSeconds = 7200000;
        } else if(i == 2131165283){
            waitTimeInMilliSeconds = 10800000;
        } else if(i == 2131165284){
            waitTimeInMilliSeconds = 14400000;
        }
        radioGroup.setEnabled (false);
        }

    public void startTimer (){
        //Countdown timer for button
        timer = new CountDownTimer (waitTimeInMilliSeconds, 1000) {
            @Override
            public void onTick (long millisUntilFinished) {
                snusButton = findViewById (R.id.snusButton);
                counter = findViewById (R.id.counter);

                // counts the seconds left, used for visual information in the app
                @SuppressLint("DefaultLocale") String secondsToText = (String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                counter.setText ("Tid til neste snus: " + secondsToText);
                snusButton.setImageResource (R.drawable.nosnus);
            }

            @Override
            public void onFinish () {
                // When Countdown is finished
                snusButton.setVisibility (View.VISIBLE);
                counter.setText ("");
                snusButton.setImageResource (R.drawable.snus);
                snusButton.setEnabled (true);
                alertBox ();
            }
        }.start ();
    }

    public void alertBox(){
        // Notification when button is clicked by the other app
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "kim_vel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLights (0xff00ff00 , 300,100)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Tid for snus!")
                .setContentText(":-D");

        assert notificationManager != null;
        notificationManager.notify(1, notificationBuilder.build());
    }

    public void darkMode(View view){
        boolean switchState = aSwitch.isChecked ();
        if (switchState){
            constraintLayout.setBackgroundColor (Color.parseColor ("#242424"));
            counter.setTextColor (Color.WHITE);
            aSwitch.setTextColor (Color.WHITE);
            Toast.makeText (this, "SPLOSH", Toast.LENGTH_SHORT).show ();
        } else {
            constraintLayout.setBackgroundColor (Color.WHITE);
            counter.setTextColor (Color.BLACK);
            aSwitch.setTextColor (Color.BLACK);
        }
    }

    public void runProgressBar(){
        progress.setVisibility (View.VISIBLE);

        new Thread (new Runnable () {
            @Override
            public void run () {
                while (actualProgress < 100){
                    actualProgress++;
                    SystemClock.sleep (30000);
                    if (actualProgress == 100){
                        progress.setVisibility (View.INVISIBLE);
                    }
                    handler.post (new Runnable () {
                        @Override
                        public void run () {
                            progress.setProgress (actualProgress);
                        }
                    });
                }
            }
        }).start ();
    }
}


// TODO: save progress when app is not open
// TODO: progressbar
// TODO: popup window, for image selector
// TODO: snus-mode

