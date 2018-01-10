package com.example.tristanokeefe.running.Activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.Date;
import java.util.Calendar;

import com.example.tristanokeefe.running.Database.MyProviderContractIndividual;
import com.example.tristanokeefe.running.Database.MyProviderContractOverall;
import com.example.tristanokeefe.running.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by tristanokeefe on 07/01/2018.
 */

public class ViewSessionDataActivity extends Activity{

    SimpleCursorAdapter dataAdapter1;
    TextView distance_view;
    TextView time_view;
    TextView speed_view;
    String distance;
    String time;
    String speed;
    Button save_button;
    Button delete_button;
    Date date;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session_data);
        Date currentTime = Calendar.getInstance().getTime();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyy 'at' hh:mm aaa");

        this.distance_view = findViewById(R.id.distance);
        this.speed_view = findViewById(R.id.speed);
        this.time_view = findViewById(R.id.time);
        this.save_button = findViewById(R.id.save_button);
        this.delete_button = findViewById(R.id.delete_button);

        this.date = new Date();

        //getContentResolver().registerContentObserver(
        //        MyProviderContractOverall.ALL_URI, true, new ViewSessionDataActivity.MyObserver(new Handler()));

        Intent intent = getIntent();
        distance = intent.getStringExtra("DISTANCE");
        final String time = intent.getStringExtra("TIME");
        speed = intent.getStringExtra("SPEED");
        Log.d("distance =", "" + distance);
        Log.d("time1 =", "" + time);
        Log.d("speed =", "" + speed);
        //final long time1 = Long.parseLong(time);
        //final long time1 = 10000;
        final String date = dateFormat.format(currentTime);



        Log.d("distance","" + distance);
        Log.d("time","" + time);
        Log.d("speed","" + speed);
        Log.d("time", "" + date);
        distance_view.setText(distance + "Km");
        speed_view.setText(speed + "m/s");
        time_view.setText(getDurationBreakdown(time));

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues newValues = new ContentValues();
                newValues.put(MyProviderContractOverall.date, date);
                newValues.put(MyProviderContractOverall.distance, distance + "km");
                newValues.put(MyProviderContractOverall.time, getDurationBreakdown(time));
                newValues.put(MyProviderContractOverall.speed, speed + "M/s");
                getContentResolver().insert(MyProviderContractOverall.OVERALL_URI, newValues);
                startActivity(new Intent(ViewSessionDataActivity.this, ViewDataActivity.class));
            }
        });
        //TODO LOOK AT LSCAN TO SEE HOW TO VIEW ABOVE BY DATE DESCENDING.

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewSessionDataActivity.this, WelcomeActivity.class));
            }
        });
    }
    /**
     * obtain data from database
     *
    /*
    public void queryContentProvider() {

        String[] projection = new String[]{
                MyProviderContractOverall.date,
                MyProviderContractOverall.distance,
                MyProviderContractOverall.time,
                MyProviderContractOverall.speed
        };

        String colsToDisplay [] = new String[] {
                MyProviderContractOverall.date,
                MyProviderContractOverall.distance,
                MyProviderContractOverall.time,
                MyProviderContractOverall.speed
        };

        //Log.d("test", "longitude=" + MyProviderContractOverall.longitude);
        //Log.d("test", "latitude=" + MyProviderContractOverall.latitude);
        //Log.d("test", "speedMS=" + MyProviderContractOverall.speedMS);
        //Log.d("test", "time=" + MyProviderContractOverall.time);

        int[] colResIds = new int[] {
                R.id.longitude,
                R.id.latitude,
                R.id.speedms,
                R.id.time
        };

        Cursor cursor = getContentResolver().query(MyProviderContractOverall.OVERALL_URI, projection, null, null, null);

        //populate list view with data
        dataAdapter1 = new SimpleCursorAdapter(
                this,
                R.layout.layout_list_item_2,
                cursor,
                colsToDisplay,
                colResIds,
                0);

        ListView listView = findViewById(R.id.ListView);
        listView.setAdapter(dataAdapter1);

    };

    class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }
        @Override
        public void onChange(boolean selfChange, Uri uri) {

            queryContentProvider();



        }


    }

    */
    public static String getDurationBreakdown(String millis1)
    {
        long millis = Long.parseLong(millis1);
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (hours != 0){
            sb.append(hours);
            sb.append(" Hours ");
            sb.append(minutes);
            sb.append(" Minutes ");
            sb.append(seconds);
            sb.append(" Seconds");
        }
        else if (minutes != 0){
            sb.append(minutes);
            sb.append(":");
            sb.append(seconds);
            //sb.append(" Seconds");
        }
        else if (seconds != 0){
            sb.append(seconds);
            sb.append("s");
        }


        return(sb.toString());
    }
}
