package com.example.tristanokeefe.running.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.TextView;
import android.os.Handler;



import com.example.tristanokeefe.running.MyBoundService;
import com.example.tristanokeefe.running.R;

import static android.content.ContentValues.TAG;
import static com.example.tristanokeefe.running.R.id.main_distance;
import static com.example.tristanokeefe.running.R.id.main_speed;

public class MainActivity extends Activity {

    private Button stop_button;
    private Button start_button;
    private MyBoundService.MyBinder myService = null;
    //static Context mContext;
    Handler handler;
    String total;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    int Hours, Seconds, Minutes, MilliSeconds ;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler() ;


        this.stop_button = findViewById(R.id.stop_log_button);
        this.start_button = findViewById(R.id.start_log_button);
        final TextView main_time = findViewById(R.id.main_time);
        //final TextView main_distance = findViewById(main_distance);
        //final TextView main_speed = findViewById(main_speed);

        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission() == true) {
                    startService(new Intent(MainActivity.this, MyBoundService.class));
                    bindService(new Intent(MainActivity.this, MyBoundService.class), serviceConnection, Context.BIND_AUTO_CREATE);
                    sendNotificationStart();
                    StartTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    startLogView();
                }
            }
        });
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Stop Button", "");

                myService.stop();
                Log.d("service.stop", "");
                //endGPS();

                sendNotificationStop();
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;

                main_time.setText("00:00:00");
                startActivity(new Intent(MainActivity.this, ViewSessionDataActivity.class));
                //TODO CREATE MOVE TO ANOTHER ACTIVITY TO VIEW RESULTS, FIXES PROBLEM WITH NO SERVICE CONNECTION
                //stopService(new Intent(MainActivity.this, MyBoundService.class));
            }
        });



        /*
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);

                reset.setEnabled(true);

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;

                main_time.setText("00:00:00");


            }
        });
        */




        Log.d("test", "onCreate");
    }

    private ServiceConnection serviceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.d("g53mdp", "MainActivity onServiceConnected");
            myService = (MyBoundService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d("g53mdp", "MainActivity onServiceDisconnected");
            myService = null;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MyBoundService.class));
        Log.i(TAG, "onPause, done");
    }
    public void onResume() {
        super.onResume();
        startService(new Intent(this, MyBoundService.class));
        // TODO INTEGRATE SERVICE OR CONTENT PROVIDER TO KEEP RUNNING IN BACKGROUND
    }


    public void endGPS() {

        if(serviceConnection!=null) {
            myService.stop();
        }
        /*if(serviceConnection!=null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        */
        //If ^ removed, onLogEnd() is never called.
        //stopService(new Intent(MainActivity.this, MyBoundService.class));
        //TODO DO I NEED THIS^

    }


    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    /**
     * method for creating and sending notificaiton
     */
    public void sendNotificationStart() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        //Create the intent for when the user taps the notification

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

        //location icon for notification
        mBuilder.setSmallIcon(R.drawable.ic_stat_location_on);

        //set notification details
        mBuilder.setContentTitle("Logging in progress");
        mBuilder.setContentText("");
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());
        //TODO PUT NOTIFICATION IN SERVICE???

    }

    public void sendNotificationStop() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        //Create the intent for when the user taps the notification

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

        //music icon for notification
        mBuilder.setSmallIcon(R.drawable.ic_stat_location_on);


        //set notification details
        mBuilder.setContentTitle("Logging stopped");
        mBuilder.setContentText("");
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());

    }

    public Runnable runnable = new Runnable() {

        public void run() {

            final TextView main_time = findViewById(R.id.main_time);

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Hours = Minutes / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);
            if (Hours >= 1) {
                 total = ("" + Hours + ":" + Minutes + ":"
                        + String.format("%02d", Seconds) + ":"
                        + String.format("%03d", MilliSeconds));
            }
            else {
                 total = ("" + Minutes + ":"
                        + String.format("%02d", Seconds) + ":"
                        + String.format("%03d", MilliSeconds));
            }

            main_time.setText(total);


            handler.postDelayed(this, 0);
        }

    };

    public void startLogView() {
        final TextView main_distance = findViewById(R.id.main_distance);
        final TextView main_speed = findViewById(R.id.main_speed);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("onRecieve", "recieved");
                        String distance_new = intent.getStringExtra(MyBoundService.EXTRA_DISTANCE) + "Km";
                        String time_new = intent.getStringExtra(MyBoundService.EXTRA_TIME);
                        //String distance_new = intent.getStringExtra(MyBoundService.EXTRA_DISTANCE);
                        String speed_new = intent.getStringExtra(MyBoundService.EXTRA_SPEED) + "M/s";
                        //main_time.setText(time_new);
                        main_distance.setText(distance_new);
                        main_speed.setText(speed_new);

                    }
                }, new IntentFilter(MyBoundService.ACTION_LOCATION_BROADCAST)
        );
    }




    /*
    public List getAllMemos() {
        List memos = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * From memo ORDER BY date DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long time = cursor.getLong(0);
            String text = cursor.getString(1);
            memos.add(new Memo(time, text));
            cursor.moveToNext();
        }
        cursor.close();
        return memos;
    }
*/

    // TODO ADD ON RESUME, ON DELETE ETC
}
