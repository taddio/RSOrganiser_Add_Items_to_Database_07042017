package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.rsorganiser_add_items_to_database_26022017.Data.DbHelper;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.EventsContract.EventEntry;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.ImageConversion;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.OrganisationsContract.OrganisationEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import static com.example.android.rsorganiser_add_items_to_database_26022017.R.layout.events;

public class Events extends ListFragment {

    ArrayList<String> event_names = new ArrayList<String>();
    List<byte[]> event_icons = new ArrayList<byte[]>();
    ArrayList<String> organisations = new ArrayList<String>();
    ArrayList<String> datetime = new ArrayList<String>();
    ArrayList<String> eventIds = new ArrayList<String>();
    List<RowItem> rowItems;
    int listLength;
    ListView mylistview;
    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 125;
    private boolean shouldRefreshOnResume = false;
    boolean itemClicked;
    int itemPosition;
    ListView list;
    View view;
    long ID;
    Handler myHandler;

    // Database Helper that will provide us access to the database
    private DbHelper mDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(events, container, false);

        //Setup FAB to open database input form
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EventFormActivity.class);
                startActivity(intent);
            }
        });

        Context myContext = getActivity();
        mDbHelper = new DbHelper(myContext);
        //insertEvent();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rowItems = new ArrayList<RowItem>();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }

        //Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder
                .setTables(EventEntry.TABLE_NAME
                        + " INNER JOIN "
                        + OrganisationEntry.TABLE_NAME
                        + " ON "
                        + EventEntry.COLUMN_ORGANISATION_ID
                        + " = "
                        + (OrganisationEntry.TABLE_NAME + "." + OrganisationEntry._ID));

        //Define a projection that specifies which columns from the databse you will actually use for this query
        String[] projection = {
                EventEntry.TABLE_NAME + "." + EventEntry._ID,
                EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_NAME,
                EventEntry.COLUMN_EVENT_DATE,
                EventEntry.TABLE_NAME + "." + EventEntry.COLUMN_EVENT_ICON,
                OrganisationEntry.TABLE_NAME + "." + OrganisationEntry.COLUMN_ORGANISATION_NAME};

        //Perform a query on the events table
        Cursor cursor = queryBuilder.query(
                db,                             // Table to query
                projection,                     //Columns to return
                null,                           //Columns for the where clause
                null,                           //Values for the where clause
                null,                           //Don't group the rows
                null,                           //Don't filter by row groups
                EventEntry.COLUMN_EVENT_DATE + " ASC");    //Sort order

        try {

            if(cursor.moveToFirst()) {
                do {
                    byte[] currentIcon = null;
                    String currentID = cursor.getString(0);
                    String currentName = cursor.getString(1);
                    String currentDate = cursor.getString(2);
                    String currentIconPathString = cursor.getString(3);
                    String currentOrganisation = cursor.getString(4);

                    if(currentIconPathString != null) {
                        Uri currentIconPathUri = Uri.parse(currentIconPathString);


                        try {
                            InputStream iStream = getContext().getContentResolver().openInputStream(currentIconPathUri);
                            currentIcon = ImageConversion.getBytes(iStream);

                        } catch (IOException ioe) {
                            Log.e("EventFormActivity", "<saveImageInDB> Error : " + ioe.getLocalizedMessage());
                        }
                    }

                    eventIds.add(currentID);
                    Log.v("event id", currentID);
                    event_names.add(currentName);
                    datetime.add(currentDate);
                    organisations.add(currentOrganisation);
                    event_icons.add(currentIcon);
                }while(cursor.moveToNext());
            }

        } finally {
            //Close cursor releasing all its resources and making it invalid
            cursor.close();
        }

        //event_names = getResources().getStringArray(R.array.event_names);
        //event_icons = getResources().obtainTypedArray(R.array.event_icons);
        //organisations = getResources().getStringArray(R.array.organisations);
        //time = getResources().getStringArray(R.array.time);

        listLength = event_names.size();
        for (int i = 0; i < listLength; i++) {

            Bitmap icon = null;

            if(event_icons.get(i) != null){
                icon = ImageConversion.getImage(event_icons.get(i));
            }

            RowItem item = new RowItem(event_names.get(i),
                    icon, organisations.get(i),
                    datetime.get(i));
            rowItems.add(item);
        }

        EventAdapter adapter = new EventAdapter(getActivity(), rowItems);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        int eventPosition = Integer.parseInt(eventIds.get(pos));
        Log.v("Click position", "" + pos);
        Log.v("event ID", eventIds.get(pos));
        Intent intent = new Intent(getActivity(), EventDescription.class);
        intent.putExtra("position", eventPosition);
        startActivity(intent);
    }



    /** private void insertEvent() {

        Bitmap iconResource = BitmapFactory.decodeResource(getResources(), com.example.android.rsorganiser_add_items_to_database_26022017.R.drawable.school_logo);
        byte[] exampleIcon = ImageConversion.getImageBytes(iconResource);
        String LogExampleIcon = exampleIcon.toString();
        Log.d("Byte", LogExampleIcon);
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, "Charity Committee Meeting");
        values.put(EventEntry.COLUMN_EVENT_ICON, exampleIcon);
        values.put(EventEntry.COLUMN_EVENT_DATE, "2017-02-18 12:50");
        values.put(EventEntry.COLUMN_EVENT_DESCRIPTION, "Test data dummy description");
        values.put(EventEntry.COLUMN_ORGANISATION_ID, 1);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        long newRowId = db.insert(EventEntry.TABLE_NAME, null, values);
    } **/
}
