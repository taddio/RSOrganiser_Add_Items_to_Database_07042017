package com.example.android.rsorganiser_add_items_to_database_26022017.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.rsorganiser_add_items_to_database_26022017.Data.EventsContract.EventEntry;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.OrganisationsContract.OrganisationEntry;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "rsorganiser.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DbHelper}.
     *
     * @param context of the app
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the events table
        String SQL_CREATE_EVENTS_TABLE =  "CREATE TABLE " + EventEntry.TABLE_NAME + " ("
                + EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EventEntry.COLUMN_EVENT_NAME + " TEXT NOT NULL, "
                + EventEntry.COLUMN_EVENT_ICON + " BLOB, "
                + EventEntry.COLUMN_EVENT_DATE + " TEXT NOT NULL, "
                + EventEntry.COLUMN_EVENT_DESCRIPTION + " TEXT, "
                + EventEntry.COLUMN_ORGANISATION_ID + " INTEGER, "
                + " FOREIGN KEY ("+EventEntry.COLUMN_ORGANISATION_ID+") REFERENCES "+ EventEntry.FOREIGN_TABLE_NAME +" ("+OrganisationEntry._ID+"));";

        String SQL_CREATE_ORGANISATIONS_TABLE =  "CREATE TABLE " + OrganisationEntry.TABLE_NAME + " ("
                + OrganisationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + OrganisationEntry.COLUMN_ORGANISATION_NAME + " TEXT NOT NULL, "
                + OrganisationEntry.COLUMN_ORGANISATION_ICON + " BLOB, "
                + OrganisationEntry.COLUMN_ORGANISATION_DESCRIPTION + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_ORGANISATIONS_TABLE);
        db.execSQL(SQL_CREATE_EVENTS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}