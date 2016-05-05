package com.highsparrow.sffilminglocations.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by High Sparrow on 05-05-2016.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sffilminglocations.db";
    private static final int DATABASE_VERSION = 1;

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_FILMING_LOCATIONS_TABLE_QUERY = "CREATE TABLE "+ SFFilmingLocationsContract.FilmingLocationEntry.TABLE_NAME +
                " (" + SFFilmingLocationsContract.FilmingLocationEntry._ID + " INTEGER PRIMARY KEY, " +
                SFFilmingLocationsContract.FilmingLocationEntry.FIRST_ACTOR + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.SECOND_ACTOR + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.THIRD_ACTOR + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.LATITUDE + " REAL, " +
                SFFilmingLocationsContract.FilmingLocationEntry.LONGITUDE + " REAL, " +
                SFFilmingLocationsContract.FilmingLocationEntry.DIRECTOR + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.DISTRIBUTOR + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.FUN_FACTS + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.LOCATIONS + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.PRODUCTION_COMPANY + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.WRITER + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.TITLE + " TEXT, " +
                SFFilmingLocationsContract.FilmingLocationEntry.RELEASE_YEAR + " TEXT);";

        db.execSQL(CREATE_FILMING_LOCATIONS_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
