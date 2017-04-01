package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.rsorganiser_add_items_to_database_26022017.Data.DbHelper;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.ImageConversion;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.OrganisationsContract.OrganisationEntry;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Organisations extends ListFragment {

    ArrayList<String> organisations = new ArrayList<String>();
    ArrayList<String> organisationIds = new ArrayList<String>();
    List<byte[]> organisation_icons = new ArrayList<byte[]>();
    List<OrganisationRowItem> organisationRowItems;
    int listLength;
    ListView mylistview;

    // Database Helper that will provide us access to the database
    private DbHelper mDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(com.example.android.rsorganiser_add_items_to_database_26022017.R.layout.organisations, container, false);

        //Setup FAB to open database input form
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), OrganisationFormActivity.class);
                startActivity(intent);
            }
        });

        Context myContext = getActivity();
        mDbHelper = new DbHelper(myContext);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        organisationRowItems = new ArrayList<OrganisationRowItem>();

        //Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Define a projection that specifies which columns from the databse you will actually use for this query
        String[] projection = {
                OrganisationEntry._ID,
                OrganisationEntry.COLUMN_ORGANISATION_NAME,
                OrganisationEntry.COLUMN_ORGANISATION_ICON};

        //Perform a query on the events table
        Cursor cursor = db.query(
                OrganisationEntry.TABLE_NAME,  //Table to query
                projection,             //Columns to return
                null,                   //Columns for the where clause
                null,                   //Values for the where clause
                null,                   //Don't group the rows
                null,                   //Don't filter by row groups
                OrganisationEntry.COLUMN_ORGANISATION_NAME + " ASC");                  //Sort order

        try {

            if(cursor.moveToFirst()) {
                do {
                    byte[] currentIcon = null;
                    String currentID = cursor.getString(0);
                    String currentName = cursor.getString(1);
                    String currentIconPathString = cursor.getString(2);

                    if(currentIconPathString != null) {
                        Uri currentIconPathUri = Uri.parse(currentIconPathString);


                        try {
                            InputStream iStream = getContext().getContentResolver().openInputStream(currentIconPathUri);
                            currentIcon = ImageConversion.getBytes(iStream);

                        } catch (IOException ioe) {
                            Log.e("EventFormActivity", "<saveImageInDB> Error : " + ioe.getLocalizedMessage());
                        }
                    }

                    organisationIds.add(currentID);
                    Log.v("event id", currentID);
                    organisations.add(currentName);
                    organisation_icons.add(currentIcon);
                }while(cursor.moveToNext());
                }


        } finally {
            //Close cursor releasing all its resources and making it invalid
            cursor.close();
        }


        //organisations = getResources().getStringArray(R.array.organisations);
        //organisation_icons = getResources().obtainTypedArray(organisation_icons);

        listLength = organisations.size();
        for (int i = 0; i < listLength; i++) {
            Bitmap icon = null;
            if(organisation_icons.get(i) != null) {
                icon = ImageConversion.getImage(organisation_icons.get(i));
            }

            OrganisationRowItem item = new OrganisationRowItem(organisations.get(i),
                    icon);
            organisationRowItems.add(item);
        }

        OrganisationsAdapter adapter = new OrganisationsAdapter(getActivity(), organisationRowItems);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        int organisationPosition = Integer.parseInt(organisationIds.get(pos));
        Log.v("Click position", "" + pos);
        Log.v("organisation ID", organisationIds.get(pos));
        Intent intent = new Intent(getActivity(), OrganisationDescription.class);
        intent.putExtra("position", organisationPosition);
        startActivity(intent);
    }


    private void insertOrganisation() {

        Bitmap iconResource = BitmapFactory.decodeResource(getResources(), com.example.android.rsorganiser_add_items_to_database_26022017.R.drawable.school_logo);
        byte[] exampleIcon = ImageConversion.getImageBytes(iconResource);
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(OrganisationEntry.COLUMN_ORGANISATION_NAME, "Reading School Charity Committee");
        values.put(OrganisationEntry.COLUMN_ORGANISATION_ICON, exampleIcon);
        values.put(OrganisationEntry.COLUMN_ORGANISATION_DESCRIPTION, "Test data dummy description");

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert(OrganisationEntry.TABLE_NAME, null, values);
    }

}
