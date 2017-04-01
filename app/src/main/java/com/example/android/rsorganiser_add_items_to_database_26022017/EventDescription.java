package com.example.android.rsorganiser_add_items_to_database_26022017;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.rsorganiser_add_items_to_database_26022017.Data.DbHelper;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.EventsContract;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.ImageConversion;

import java.io.IOException;
import java.io.InputStream;

public class EventDescription extends AppCompatActivity {

    private DbHelper mDbHelper;

    int eventPosition;

    private String currentID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_description);

        Bundle bundle = getIntent().getExtras();
        eventPosition = (bundle.getInt("position"));

        mDbHelper = new DbHelper(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        displayEventDescription();
    }

    private void displayEventDescription() {

        mDbHelper = new DbHelper(this);

        //Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Define a projection that specifies which columns from the databse you will actually use for this query
        String[] projection = {
                EventsContract.EventEntry._ID,
                EventsContract.EventEntry.COLUMN_EVENT_NAME,
                EventsContract.EventEntry.COLUMN_EVENT_DESCRIPTION,
                EventsContract.EventEntry.COLUMN_EVENT_ICON};

        //Perform a query on the events table
        Cursor cursor = db.query(
                EventsContract.EventEntry.TABLE_NAME,// Table to query
                projection,                         //Columns to return
                EventsContract.EventEntry._ID + "=?",      //Columns for the where clause
                new String[]{("" + eventPosition)},                            //Values for the where clause
                null,                               //Don't group the rows
                null,                               //Don't filter by row groups
                null);                              //Sort order

        TextView displayName = (TextView) findViewById(R.id.name);
        TextView displayDescription = (TextView) findViewById(R.id.description);
        ImageView displayIcon = (ImageView) findViewById(R.id.icon);

        try {
            int idColumnIndex = cursor.getColumnIndex(EventsContract.EventEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(EventsContract.EventEntry.COLUMN_EVENT_NAME);
            int iconColumnIndex = cursor.getColumnIndex(EventsContract.EventEntry.COLUMN_EVENT_ICON);
            int descriptionColumnIndex = cursor.getColumnIndex(EventsContract.EventEntry.COLUMN_EVENT_DESCRIPTION);

            cursor.moveToFirst();

            byte[] currentIconByteData;
            Bitmap currentIconBitmap = null;
            currentID = cursor.getString(idColumnIndex);
            String currentName = cursor.getString(nameColumnIndex);
            String currentIconPathString = cursor.getString(iconColumnIndex);
            String currentDescription = cursor.getString(descriptionColumnIndex);

            if (currentIconPathString != null) {
                Uri currentIconPathUri = Uri.parse(currentIconPathString);


                try {
                    InputStream iStream = this.getContentResolver().openInputStream(currentIconPathUri);
                    currentIconByteData = ImageConversion.getBytes(iStream);
                    currentIconBitmap = ImageConversion.getImage(currentIconByteData);

                } catch (IOException ioe) {
                    Log.e("EventFormActivity", "<saveImageInDB> Error : " + ioe.getLocalizedMessage());
                }
            }

            displayName.setText(currentName);
            displayDescription.setText(currentDescription);
            if (currentIconBitmap == null) {
                displayIcon.setImageResource(R.drawable.school_logo);
            } else displayIcon.setImageBitmap(currentIconBitmap);


        } finally {
            //Close cursor releasing all its resources and making it invalid
            cursor.close();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_description, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.action_delete:
                deleteEvent(currentID);

                // Exit activity
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                finish();
                return true;

            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteEvent(String id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            db.delete(EventsContract.EventEntry.TABLE_NAME, "_ID = ?", new String[] {id});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}