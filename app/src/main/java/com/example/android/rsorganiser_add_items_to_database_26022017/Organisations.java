package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.rsorganiser_add_items_to_database_26022017.Data.DbHelper;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.ImageConversion;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.OrganisationsContract;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.OrganisationsContract.OrganisationEntry;

import java.util.ArrayList;
import java.util.List;

public class Organisations extends ListFragment {

    ArrayList<String> organisations = new ArrayList<String>();
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

        Context myContext = getActivity();
        mDbHelper = new DbHelper(myContext);
        insertOrganisation();

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
                OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_NAME,
                OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_ICON};

        //Perform a query on the events table
        Cursor cursor = db.query(
                OrganisationEntry.TABLE_NAME,  //Table to query
                projection,             //Columns to return
                null,                   //Columns for the where clause
                null,                   //Values for the where clause
                null,                   //Don't group the rows
                null,                   //Don't filter by row groups
                null);                  //Sort order

        try {
            int nameColumnIndex = cursor.getColumnIndex(OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_NAME);
            int iconColumnIndex = cursor.getColumnIndex(OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_ICON);

            while(cursor.moveToNext()) {
                String currentName = cursor.getString(nameColumnIndex);
                byte[] currentIcon = cursor.getBlob(iconColumnIndex);


                organisations.add(currentName);
                organisation_icons.add(currentIcon);
            }

        } finally {
            //Close cursor releasing all its resources and making it invalid
            cursor.close();
        }

        //organisations = getResources().getStringArray(R.array.organisations);
        //organisation_icons = getResources().obtainTypedArray(organisation_icons);

        listLength = organisations.size();
        for (int i = 0; i < listLength; i++) {
            Bitmap icon = ImageConversion.getImage(organisation_icons.get(i));

            OrganisationRowItem item = new OrganisationRowItem(organisations.get(i),
                    icon);
            organisationRowItems.add(item);
        }

        OrganisationsAdapter adapter = new OrganisationsAdapter(getActivity(), organisationRowItems);
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
