package com.example.wizard.popularmovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.wizard.popularmovie.Models.Movies;

import java.util.ArrayList;
import java.util.List;


public class FavoriteDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite.db";

    private static final int DATABASE_VERSION = 1;

    public static final String LOGTAG = "FAVORITE";

    SQLiteOpenHelper dbhandler;
    SQLiteDatabase db;

    public FavoriteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        Log.i(LOGTAG, "Database Opened");
        db = dbhandler.getWritableDatabase();
    }

    public void close(){
        Log.i(LOGTAG, "Database Closed");
        dbhandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteMovies.FavoriteEntry.TABLE_NAME + " (" +
                FavoriteMovies.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteMovies.FavoriteEntry.COLUMN_MOVIEID + " INTEGER, " +
                FavoriteMovies.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteMovies.FavoriteEntry.COLUMN_USERRATING + " REAL NOT NULL, " +
                FavoriteMovies.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteMovies.FavoriteEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovies.FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }

    public void addFavorite(Movies movie){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoriteMovies.FavoriteEntry.COLUMN_MOVIEID, movie.getId());
        values.put(FavoriteMovies.FavoriteEntry.COLUMN_TITLE, movie.getTitle());
        values.put(FavoriteMovies.FavoriteEntry.COLUMN_USERRATING, movie.getVoteAverage());
        values.put(FavoriteMovies.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(FavoriteMovies.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, movie.getOverview());
        db.insert(FavoriteMovies.FavoriteEntry.TABLE_NAME, null, values);
        Log.d("DB_INSERTION","SUCCESSFUL");
        db.close();
    }

    public void deleteFavorite(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavoriteMovies.FavoriteEntry.TABLE_NAME, FavoriteMovies.FavoriteEntry.COLUMN_MOVIEID+ "=" + id, null);
    }

    public List<Movies> getAllFavorite(){
        String[] columns = {
                FavoriteMovies.FavoriteEntry._ID,
                FavoriteMovies.FavoriteEntry.COLUMN_MOVIEID,
                FavoriteMovies.FavoriteEntry.COLUMN_TITLE,
                FavoriteMovies.FavoriteEntry.COLUMN_USERRATING,
                FavoriteMovies.FavoriteEntry.COLUMN_POSTER_PATH,
                FavoriteMovies.FavoriteEntry.COLUMN_PLOT_SYNOPSIS

        };
        String sortOrder =
                FavoriteMovies.FavoriteEntry._ID + " ASC";
        List<Movies> favoriteList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FavoriteMovies.FavoriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if (cursor.moveToFirst()){
            do {
                Movies movie = new Movies();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoriteMovies.FavoriteEntry.COLUMN_MOVIEID))));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteMovies.FavoriteEntry.COLUMN_TITLE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(FavoriteMovies.FavoriteEntry.COLUMN_USERRATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteMovies.FavoriteEntry.COLUMN_POSTER_PATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoriteMovies.FavoriteEntry.COLUMN_PLOT_SYNOPSIS)));

                favoriteList.add(movie);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return favoriteList;
    }

}