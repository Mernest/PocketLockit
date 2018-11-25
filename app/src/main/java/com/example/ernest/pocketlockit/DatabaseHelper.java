package com.example.ernest.pocketlockit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.constraint.Constraints.TAG;
import static com.example.ernest.pocketlockit.Config.DATABASE_NAME;


public class DatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = "DatabaseHelper";

    private Context context = null;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {

        // Create tables SQL execution
        String CREATE_LOG_TABLE = "CREATE TABLE " + Config.TABLE_LOG + "("
                + Config.COLUMN_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_LOG_TIME + " TEXT NOT NULL, "
                + Config.COLUMN_LOG_TAG + " TEXT NOT NULL "
                + ")";

        Log.d(TAG,"Table create SQL: " + CREATE_LOG_TABLE);

        db.execSQL(CREATE_LOG_TABLE);

        Log.d(TAG,"DB created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_LOG);

        // Create tables again
        onCreate(db);
    }

    public long insertLogItem(LogItem logItem){

        long id = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_LOG_TIME,logItem.getTime());
        contentValues.put(Config.COLUMN_LOG_TAG, logItem.getTag());


        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_LOG, null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return id;
    }

    public List<LogItem> getAllLogItems(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_LOG, null, null, null, null, null, null, null);

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<LogItem> logList = new ArrayList<>();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_LOG_ID));
                        String time = cursor.getString(cursor.getColumnIndex(Config.COLUMN_LOG_TIME));
                        String tag = cursor.getString(cursor.getColumnIndex(Config.COLUMN_LOG_TAG));

                        logList.add(new LogItem(id,time,tag));
                    }   while (cursor.moveToNext());

                    return logList;
                }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    public boolean deleteLog(){
        boolean deleteStatus = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {

            sqLiteDatabase.delete(Config.TABLE_LOG, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_LOG);

            if(count==0)
                deleteStatus = true;

        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deleteStatus;
    }

}
