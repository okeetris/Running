package com.example.tristanokeefe.running.Database;

/**
 * Created by tristanokeefe on 03/01/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper class
 */
public class DBHelper extends SQLiteOpenHelper
{
    public DBHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        Log.d("g54mdp", "DBHelper");
    }

    @Override
    /**
     * onCreate to create SQL table for database
     */
    public void onCreate(SQLiteDatabase db) {
        Log.d("g54mdp", "onCreate");

        /**
         * create table recipeBook with columns recipeTitle and recipeMain
         */
        //db.execSQL("CREATE TABLE individual(_id INTEGER PRIMARY KEY AUTOINCREMENT, recipeTitle TEXT NOT NULL, recipeMain TEXT NOT NULL);");

        db.execSQL("CREATE TABLE individual(_id INTEGER PRIMARY KEY AUTOINCREMENT, longitude TEXT NOT NULL, latitude TEXT NOT NULL, speedMS TEXT NOT NULL, time TEXT NOT NULL);");
        db.execSQL("CREATE TABLE overall(_id INTEGER PRIMARY KEY AUTOINCREMENT,date INTEGER, distance TEXT NOT NULL, time TEXT NOT NULL, speed TEXT NOT NULL);");

// TODO DELETE BELOW / TEST
        //db.execSQL("INSERT INTO individual (longitude, latitude, speedMS, time ) VALUES ('-3.1627476308', '51.4631031454', '4.35',1515175803000 );");
        //db.execSQL("INSERT INTO recipeBook (recipeTitle, recipeMain ) VALUES ('elvis', 'iuasdfaswef');");

    }

    @Override
    /**
     * onUpgrade method
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS individual");
        db.execSQL("DROP TABLE IF EXISTS overall");
        onCreate(db);
    }

}
