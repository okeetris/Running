package com.example.tristanokeefe.running.Database;

/**
 * Created by tristanokeefe on 03/01/2018.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


import com.example.tristanokeefe.running.Database.DBHelper;

/**
 *content provider for database
 */
public class MyProviderIndividual extends ContentProvider
{
    private DBHelper dbHelper = null;

    private static final UriMatcher uriMatcher;

    //URI Matcher for provider
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MyProviderContractIndividual.AUTHORITY, "individual", 1);
        uriMatcher.addURI(MyProviderContractIndividual.AUTHORITY, "individual/#", 2);

        uriMatcher.addURI(MyProviderContractIndividual.AUTHORITY, "*", 7);
    }

    @Override
    /**
     * onCreate method for activity
     */
    public boolean onCreate() {

        Log.d("g53mdp", "contentprovider oncreate");
        this.dbHelper = new DBHelper(this.getContext(), "mydb", null, 7);
        return true;
    }

    @Override
    /**
     * getType decleration for what database will return
     */
    public String getType(Uri uri) {

        String contentType;

        if (uri.getLastPathSegment()==null)
        {
            contentType = MyProviderContractIndividual.CONTENT_TYPE_MULTIPLE;
        }
        else
        {
            contentType = MyProviderContractIndividual.CONTENT_TYPE_SINGLE;
        }

        return contentType;
    }


    @Override
    /**
     * insert method for provider
     */
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri))
        {
            case 1:
                tableName = "individual";
                break;
            default:
                tableName = "individual";
                break;
        }

        long id = db.insert(tableName, null, values);
        db.close();
        Uri nu = ContentUris.withAppendedId(uri, id);

        //Log.d("g53mdp", nu.toString());

        getContext().getContentResolver().notifyChange(nu, null);

        return nu;
    }


    @Override
    /**
     * query method for provider
     */
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d("g53mdp", uri.toString() + " " + uriMatcher.match(uri));

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(uriMatcher.match(uri))
        {
            case 2:
                selection = "_ID = " + uri.getLastPathSegment();
            case 1:
                return db.query("individual", projection, selection, selectionArgs, null, null, sortOrder);
            case 4:
                selection = "_ID = " + uri.getLastPathSegment();
            case 3:
                return db.query("individual", projection, selection, selectionArgs, null, null, sortOrder);
            case 5:
                String q5 = "SELECT _id, longitude, latitude, speedMS, time FROM individual ";
                return db.rawQuery(q5, selectionArgs);
            case 6:
                String q6 = "SELECT _id, longitude, latitude, speedMS, time FROM individual WHERE _ID = " + uri.getLastPathSegment();
                return db.rawQuery(q6, selectionArgs);
            case 7:
                String q7 = "SELECT * FROM individual ";
                return db.rawQuery(q7, selectionArgs);
            default:
                return null;
        }
    }

    @Override
    //Update method
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    //Delete method
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("not implemented");
    }

}
