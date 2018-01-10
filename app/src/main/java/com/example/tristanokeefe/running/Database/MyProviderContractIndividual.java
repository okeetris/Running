package com.example.tristanokeefe.running.Database;

import android.net.Uri;

/**
 * Created by tristanokeefe on 03/01/2018.
 */

public class MyProviderContractIndividual {

    public static final String AUTHORITY = "com.example.tristanokeefe.running.Database.MyProviderIndividual";

    public static final Uri INDIVIDUAL_URI = Uri.parse("content://"+AUTHORITY+"/individual");
    public static final Uri ALL_URI = Uri.parse("content://"+AUTHORITY+"/");

    public static final String _ID = "_id";

    public static final String longitude = "longitude";
    public static final String latitude = "latitude";
    public static final String speedMS = "speedMS";
    public static final String time = "time";


// TODO TEST THIS

    public static final String CONTENT_TYPE_SINGLE = "vnd.android.cursor.item/MyProvider.data.text";
    public static final String CONTENT_TYPE_MULTIPLE = "vnd.android.cursor.dir/MyProvider.data.text";
}
