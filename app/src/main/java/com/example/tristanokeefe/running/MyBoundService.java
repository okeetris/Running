package com.example.tristanokeefe.running;

/**
 * Created by tristanokeefe on 04/01/2018.
 */

import android.app.Service;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteCallbackList;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;


import com.example.tristanokeefe.running.Activity.MainActivity;
import com.example.tristanokeefe.running.Activity.ViewSessionDataActivity;
import com.example.tristanokeefe.running.Database.MyProviderContractIndividual;
import com.example.tristanokeefe.running.Database.MyProviderContractOverall;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

/**
 * bound service file.
 */
public class MyBoundService extends Service {

    static Context mContext;
    //LocationManager locationManager =
      //      (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    public static final String
            ACTION_LOCATION_BROADCAST = MyBoundService.class.getName() + "LocationBroadcast",
            EXTRA_TIME = "extra_time",
            EXTRA_DISTANCE = "extra_distance",
            EXTRA_SPEED = "extra_speed";

    private static final int
            MIN_TIME = 2000,
            MIN_DISTANCE = 1;

    public class MyLocationListener implements LocationListener {
        public Location mLastLocation;
        public Location mFirstLocation;
        private double first_long;
        private double first_lat;
        private int x = 0;
        //double avgSpeed = 0;
        double avgSpeedKm = 0;
        double speedTest = 100;
        TextView textView;


        //sendBroadcastMessage(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));

        @Override
        public void onLocationChanged(Location pCurrentLocation) {
            //Log.d("g53mdp", pCurrentLocation.getLatitude() + " " + pCurrentLocation.getLongitude());




            if (x == 0) {
                first_lat = pCurrentLocation.getLatitude();
                first_long = pCurrentLocation.getLongitude();
                this.mFirstLocation = pCurrentLocation;
            }

            //onLogEnter();

            ContentValues newValues = new ContentValues();
            newValues.put(MyProviderContractIndividual.longitude, pCurrentLocation.getLongitude());
            newValues.put(MyProviderContractIndividual.latitude, pCurrentLocation.getLatitude());
            newValues.put(MyProviderContractIndividual.speedMS, speedTest);
            newValues.put(MyProviderContractIndividual.time, pCurrentLocation.getTime());
            getContentResolver().insert(MyProviderContractIndividual.INDIVIDUAL_URI, newValues);


            //calculate manually speed
            double speed1 = 0;
            double speed = 0;
            float[] distance = new float[0];
            if (this.mLastLocation != null)
                speed1 = (distance(pCurrentLocation, mLastLocation) / (pCurrentLocation.getTime() - this.mLastLocation.getTime())*1000);
            //TODO ^ dividing by 1000, 1 second?

            //if there is speed from location
            //if (pCurrentLocation.hasSpeed())
                //get location speed
             //   speed = pCurrentLocation.getSpeed();
            //Log.d("speed from location=", "" + speed);
            Log.d("send location broadcast", "sent from changed");
            sendBroadcastMessage(mFirstLocation,pCurrentLocation);

            this.mLastLocation = pCurrentLocation;
            x ++;

            Log.d("data", " calculated speed= " + speed1 + " provider= " + pCurrentLocation.getProvider() + " time=  " + pCurrentLocation.getTime());

        }



        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // information about the signal, i.e. number of satellites
            Log.d("g53mdp", "onStatusChanged: " + provider + " " + status);
        }
        @Override
        public void onProviderEnabled(String provider) {
            // the user enabled (for example) the GPS
            Log.d("g53mdp", "onProviderEnabled: " + provider);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // the user disabled (for example) the GPS
            Log.d("g53mdp", "onProviderDisabled: " + provider);
        }





        public void onLogEnd(){
            //Location location;
            x = 0;
            double avgSpeed = getSpeed(mFirstLocation, mLastLocation);
            avgSpeedKm = (distance(mLastLocation, mFirstLocation) / (mLastLocation.getTime() - this.mFirstLocation.getTime()));
            Log.d("avg speed=", "" + avgSpeed);
            Log.d("avg speedKm=", "" + avgSpeedKm);

            Float distance = mFirstLocation.distanceTo(mLastLocation);
            Log.d("distanceto", distance.toString());

            Long time  = mLastLocation.getTime() - mFirstLocation.getTime();
            String time1 = time.toString();
            Log.d("time", time1);



            Intent i = new Intent();
            i.setClass(MyBoundService.this, ViewSessionDataActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            i.putExtra("DISTANCE", convertDistance(distance));
            Log.d("distance string", convertDistance(distance));
            Log.d("time string", time1);
            i.putExtra("TIME", time1);

            i.putExtra("SPEED", formatSpeed(avgSpeed));
            startActivity(i);


        }
    }

    MyLocationListener locationListener = new MyLocationListener();

    public void stop(){

        LocationManager locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Log.d("TAG", "onStop");
        super.onDestroy();
        try
        {
            locationManager.removeUpdates(locationListener);
            locationManager=null;
            locationListener.onLogEnd();
            //onLogEnd();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public class MyBinder extends Binder implements IInterface {
        @Override
        public IBinder asBinder() {
            return this;
        }

        public void stop()
        {
            MyBoundService.this.stop();
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.d("g53mdp", "service onBind");
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("TAG", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        LocationManager locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5, // minimum time interval between updates
                    1, // minimum distance between updates, in metres
                    locationListener);
            Log.d("g53mdp", locationManager.getAllProviders().toString());
        } catch(SecurityException e) {
            Log.d("g53mdp", e.toString());
        }
    }

    @Override
    public void onDestroy()
    {
        LocationManager locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Log.d("TAG", "onDestroy");
        super.onDestroy();
        try
        {
            locationManager.removeUpdates(locationListener);
            locationManager=null;
            locationListener.onLogEnd();
            //onLogEnd();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d("g53mdp", "service onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d("g53mdp", "service onUnbind");
        locationListener.onLogEnd();
        return super.onUnbind(intent);
    }

    public String convertDistance(float distance){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String finalDistance;

        float newDistance = (distance/1000);
        Log.d("round", "" + df.format(newDistance));
        finalDistance = Float.toString(newDistance);

        return df.format(newDistance);
    }

    public String convertSpeed(double speed){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        String new_speed = Double.toString(speed);

        return df.format(new_speed);
    }

    public String formatSpeed(Double speed){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        String avgSpeed =  df.format(speed);

        return avgSpeed;

    }

    public static String getDurationBreakdown(long millis)
    {
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


    public long getTime(Location mFirstLocation, Location pCurrentLocation){
        long timeTaken = pCurrentLocation.getTime() - mFirstLocation.getTime();

        return timeTaken;
    }

    public double getSpeed(Location mFirstLocation, Location pCurrentLocation){

        double avgSpeed = (distance(pCurrentLocation, mFirstLocation) / (pCurrentLocation.getTime() - mFirstLocation.getTime())*1000);
        return avgSpeed;
    }

    private Double distance(Location one, Location two) {
        int R = 6371000;
        Double dLat = toRad(two.getLatitude() - one.getLatitude());
        Double dLon = toRad(two.getLongitude() - one.getLongitude());
        Double lat1 = toRad(one.getLatitude());
        Double lat2 = toRad(two.getLatitude());
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double d = R * c;
        return d;
    }
    private double toRad(Double d) {
        return d * Math.PI / 180;
    }


    public void sendBroadcastMessage(Location mFirstLocation, Location pCurrentLocation) {
        if (pCurrentLocation != null) {
            String time_new;
            Log.d("sendBroadcastMessage", "sending");
            Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
            time_new = getDurationBreakdown(getTime(mFirstLocation, pCurrentLocation));


            Log.d("thread update:", "" + getDurationBreakdown(getTime(mFirstLocation, pCurrentLocation)));
            Log.d("thread update:", "" + time_new);


            intent.putExtra(EXTRA_TIME, time_new);
            intent.putExtra(EXTRA_DISTANCE, convertDistance(mFirstLocation.distanceTo(pCurrentLocation)));
            intent.putExtra(EXTRA_SPEED, formatSpeed(getSpeed(mFirstLocation, pCurrentLocation)));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    public void onLogEnd(){
        //Location location;
        locationListener.x = 0;
        double avgSpeed = getSpeed(locationListener.mFirstLocation, locationListener.mLastLocation);
        locationListener.avgSpeedKm = (distance(locationListener.mLastLocation, locationListener.mFirstLocation) / (locationListener.mLastLocation.getTime() - this.locationListener.mFirstLocation.getTime()));
        Log.d("avg speed=", "" + avgSpeed);
        Log.d("avg speedKm=", "" + locationListener.avgSpeedKm);

        Float distance = locationListener.mFirstLocation.distanceTo(locationListener.mLastLocation);
        Log.d("distanceto", distance.toString());

        Long time  = locationListener.mLastLocation.getTime() - locationListener.mFirstLocation.getTime();
        String time1 = time.toString();
        Log.d("time", time1);



        Intent i = new Intent();
        Bundle mBundle = new Bundle();
        i.setClass(MyBoundService.this, ViewSessionDataActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mBundle.putString("DISTANCE", convertDistance(distance));
        Log.d("distance string", convertDistance(distance));
        Log.d("time string", time1);
        mBundle.putLong("TIME", time);

        mBundle.putString("SPEED", formatSpeed(avgSpeed));
        i.putExtras(mBundle);
        startActivity(i);


    }
}