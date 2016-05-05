package com.highsparrow.sffilminglocations.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.highsparrow.sffilminglocations.models.FilmingLocation;

import java.util.ArrayList;

/**
 * Created by High Sparrow on 05-05-2016.
 */
public class DBQueryHelper extends ContentProvider {

    private DBOpenHelper dbOpenHelper;

    @Override
    public boolean onCreate() {
        dbOpenHelper = new DBOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        Cursor cursor = builder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return SFFilmingLocationsContract.FilmingLocationEntry.CONTENT_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        sqLiteDatabase.insert(SFFilmingLocationsContract.FilmingLocationEntry.TABLE_NAME, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        int retCount = 0;
        sqLiteDatabase.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = sqLiteDatabase.insert(SFFilmingLocationsContract.FilmingLocationEntry.TABLE_NAME, null, value);
                if (id != -1)
                    retCount++;
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return retCount;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return dbOpenHelper.getWritableDatabase().delete(SFFilmingLocationsContract.FilmingLocationEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return dbOpenHelper.getWritableDatabase().update(SFFilmingLocationsContract.FilmingLocationEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public static FilmingLocation populateFilmingLocationFromCursor(Cursor cursor){
        FilmingLocation filmingLocation = new FilmingLocation();
        filmingLocation.setFirstActor(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.FIRST_ACTOR)));
        filmingLocation.setSecondActor(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.SECOND_ACTOR)));
        filmingLocation.setThirdActor(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.THIRD_ACTOR)));
        filmingLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.LATITUDE)));
        filmingLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.LONGITUDE)));
        filmingLocation.setLocations(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.LOCATIONS)));
        filmingLocation.setDirector(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.DIRECTOR)));
        filmingLocation.setDistributor(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.DISTRIBUTOR)));
        filmingLocation.setFunFacts(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.FUN_FACTS)));
        filmingLocation.setProductionCompany(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.PRODUCTION_COMPANY)));
        filmingLocation.setReleaseYear(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.RELEASE_YEAR)));
        filmingLocation.setTitle(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.TITLE)));
        filmingLocation.setWriter(cursor.getString(cursor.getColumnIndex(SFFilmingLocationsContract.FilmingLocationEntry.WRITER)));
        return filmingLocation;
    }

    public static ContentValues putDataInContentValues(FilmingLocation filmingLocation) {
        ContentValues value = new ContentValues();
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.FIRST_ACTOR, filmingLocation.getFirstActor());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.SECOND_ACTOR, filmingLocation.getSecondActor());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.THIRD_ACTOR, filmingLocation.getThirdActor());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.LATITUDE, filmingLocation.getLatitude());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.LONGITUDE, filmingLocation.getLongitude());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.LOCATIONS, filmingLocation.getLocations());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.DIRECTOR, filmingLocation.getDirector());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.DISTRIBUTOR, filmingLocation.getDistributor());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.FUN_FACTS, filmingLocation.getFunFacts());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.PRODUCTION_COMPANY, filmingLocation.getProductionCompany());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.RELEASE_YEAR, filmingLocation.getReleaseYear());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.TITLE, filmingLocation.getTitle());
        value.put(SFFilmingLocationsContract.FilmingLocationEntry.WRITER, filmingLocation.getWriter());
        return value;
    }
}
