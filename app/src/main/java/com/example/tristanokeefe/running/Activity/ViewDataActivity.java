package com.example.tristanokeefe.running.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.tristanokeefe.running.Database.DBHelper;
import com.example.tristanokeefe.running.Database.MyProviderContractOverall;
import com.example.tristanokeefe.running.MyBoundService;
import com.example.tristanokeefe.running.R;

/**
 * Created by tristanokeefe on 05/01/2018.
 */

public class ViewDataActivity extends Activity {

    SimpleCursorAdapter dataAdapter1;
    private Button delete_button;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    ListView listView;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        queryContentProvider();
        //this.database = dbHelper.getWritableDatabase();





        getContentResolver().registerContentObserver(
                MyProviderContractOverall.ALL_URI, true, new ViewDataActivity.MyObserver(new Handler()));


    }

    /**
     * obtain data from database
     */
    public void queryContentProvider() {

        String[] projection = new String[] {
                MyProviderContractOverall._ID,
                MyProviderContractOverall.date,
                MyProviderContractOverall.distance,
                MyProviderContractOverall.speed,
                MyProviderContractOverall.time
        };

        String colsToDisplay [] = new String[] {
                MyProviderContractOverall.date,
                MyProviderContractOverall.distance,
                MyProviderContractOverall.speed,
                MyProviderContractOverall.time
        };

        Log.d("test", "date=" + MyProviderContractOverall.date);
        Log.d("test", "distance=" + MyProviderContractOverall.distance);
        Log.d("test", "speed=" + MyProviderContractOverall.speed);
        Log.d("test", "time=" + MyProviderContractOverall.time);

        int[] colResIds = new int[] {
                R.id.date,
                R.id.distance,
                R.id.speed,
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

        //this.delete_button = findViewById(R.id.delete_button);
        /*
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    };*/
    /*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String[] projection = new String[] {
                MyProviderContractOverall._ID,
                MyProviderContractOverall.date,
                MyProviderContractOverall.distance,
                MyProviderContractOverall.speed,
                MyProviderContractOverall.time
        };

        getContentResolver().delete(MyProviderContractOverall.OVERALL_URI,
                MyProviderContractOverall._ID + " = " + id, null);
        Cursor newCursor = getContentResolver().query(MyProviderContractOverall.OVERALL_URI, projection, null, null, null);

        dataAdapter1.changeCursor(newCursor);
        super.onListItemClick(l, v, position, id);
    }


    public void onDelete() {


        getContentResolver().delete(MyProviderContractOverall.OVERALL_URI,MyProviderContractOverall._ID + "=" + id, null);
        */
    }
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

}
