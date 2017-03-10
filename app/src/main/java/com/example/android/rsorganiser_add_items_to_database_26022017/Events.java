package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.rsorganiser_add_items_to_database_26022017.Data.DbHelper;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.EventsContract.EventEntry;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.ImageConversion;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.OrganisationsContract.OrganisationEntry;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.rsorganiser_add_items_to_database_26022017.Data.EventsContract.EventEntry.COLUMN_EVENT_NAME;

public class Events extends ListFragment {

    ArrayList<String> event_names = new ArrayList<String>();
    List<byte[]> event_icons = new ArrayList<byte[]>();
    ArrayList<String> organisations = new ArrayList<String>();
    ArrayList<String> datetime = new ArrayList<String>();
    List<RowItem> rowItems;
    int listLength;
    ListView mylistview;

    // Database Helper that will provide us access to the database
    private DbHelper mDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(com.example.android.rsorganiser_add_items_to_database_26022017.R.layout.events, container, false);

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
                null);    //Sort order

        try {
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_EVENT_NAME);
            int dateColumnIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_DATE);
            int iconColumnIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_ICON);
            int organisationColumnIndex = cursor.getColumnIndex(OrganisationEntry.COLUMN_ORGANISATION_NAME);
            

            while(cursor.moveToNext()) {
                String currentName = cursor.getString(nameColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                String currentOrganisation = cursor.getString(organisationColumnIndex);
                byte[] currentIcon = cursor.getBlob(iconColumnIndex);


                event_names.add(currentName);
                datetime.add(currentDate);
                organisations.add(currentOrganisation);
                event_icons.add(currentIcon);
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
            Log.v("Counter", Integer.toString(i));
            Bitmap icon = ImageConversion.getImage(event_icons.get(i));

            RowItem item = new RowItem(event_names.get(i),
                    icon, organisations.get(i),
                    datetime.get(i));
            rowItems.add(item);
        }

        EventAdapter adapter = new EventAdapter(getActivity(), rowItems);
        setListAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(com.example.android.rsorganiser_add_items_to_database_26022017.R.menu.menu_main, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
