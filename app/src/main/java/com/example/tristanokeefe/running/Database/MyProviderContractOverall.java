package com.example.tristanokeefe.running.Database;

import android.net.Uri;

/**
 * Created by tristanokeefe on 03/01/2018.
 */

public class MyProviderContractOverall {

    public static final String AUTHORITY = "com.example.tristanokeefe.running.Database.MyProviderOverall";

    public static final Uri OVERALL_URI = Uri.parse("content://"+AUTHORITY+"/overall");
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");

    public static final String _ID = "_id";

    public static final String date = "date";
    public static final String distance = "distance";
    public static final String time = "time";
    public static final String speed = "speed";







// TODO TEST THIS

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyProvider.data.text";
}
