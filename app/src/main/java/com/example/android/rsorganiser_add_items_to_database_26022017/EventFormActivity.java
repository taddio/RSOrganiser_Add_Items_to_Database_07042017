package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.rsorganiser_add_items_to_database_26022017.Data.DbHelper;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.EventsContract;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.ImageConversion;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.OrganisationsContract;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.android.rsorganiser_add_items_to_database_26022017.Data.EventsContract.EventEntry.COLUMN_EVENT_NAME;

public class EventFormActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText mNameEditText;

    private EditText mDescriptionEditText;

    private Button mDateTimeButton;

    private Button mImageButton;

    private Spinner mOrganisationSpinner;

    private DbHelper mDbHelper;

    private ArrayList<String> organisations = new ArrayList<String>();

    byte[] imageInputData;

    int day, month, year, hour, minute;

    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    int organisationID;

    private static final int SELECT_PICTURE = 100;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_form);

        mNameEditText = (EditText) findViewById(R.id.edit_event_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_event_description);
        mDateTimeButton = (Button) findViewById(R.id.edit_datetime_button);
        mImageButton = (Button) findViewById(R.id.edit_image_button);
        mOrganisationSpinner = (Spinner) findViewById(R.id.spinner_organisations);

        setupSpinner();
        setupDateTimeButton();
        setupImageButton();
    }

    private void setupSpinner() {

        mDbHelper = new DbHelper(this);

        //Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Define a projection that specifies which columns from the databse you will actually use for this query
        String[] projection = {
                OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_NAME,
                OrganisationsContract.OrganisationEntry._ID};

        //Perform a query on the events table
        Cursor cursor = db.query(
                OrganisationsContract.OrganisationEntry.TABLE_NAME,  //Table to query
                projection,             //Columns to return
                null,                   //Columns for the where clause
                null,                   //Values for the where clause
                null,                   //Don't group the rows
                null,                   //Don't filter by row groups
                null);                  //Sort order

        try {
            int nameColumnIndex = cursor.getColumnIndex(OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_NAME);
            int idColumnIndex = cursor.getColumnIndex(OrganisationsContract.OrganisationEntry._ID);

            while (cursor.moveToNext()) {
                String currentName = cursor.getString(nameColumnIndex);
                String currentID = cursor.getString(idColumnIndex);

                organisations.add(currentName);
            }

        } finally {
            //Close cursor releasing all its resources and making it invalid
            cursor.close();
        }

        ArrayAdapter<String> organisationSpinnerAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, organisations);

        organisationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mOrganisationSpinner.setAdapter(organisationSpinnerAdapter);

        mOrganisationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String label = parent.getItemAtPosition(position).toString();
               Toast.makeText(parent.getContext(), "You selected: " + label + "at position #" + position, Toast.LENGTH_LONG).show();
               organisationID = position + 1;
               return;
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
        }

        );

    }

    private void setupDateTimeButton() {

        mDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventFormActivity.this, EventFormActivity.this,
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month + 1;
        dayFinal = dayOfMonth;

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(EventFormActivity.this, EventFormActivity.this,
                hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        Toast.makeText(this, yearFinal + "-" + monthFinal + "-" + dayFinal + " " + hourFinal + ":" + minuteFinal, Toast.LENGTH_LONG).show();

    }

    private void setupImageButton() {

        mImageButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                if (null != selectedImageUri) {

                    convertImageData(selectedImageUri);
                    }
                }
            }
        }

    void convertImageData(Uri selectedImageUri) {

        try {
            InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
            imageInputData = ImageConversion.getBytes(iStream);

        }
        catch (IOException ioe) {
            Log.e("EventFormActivity", "<saveImageInDB> Error : " + ioe.getLocalizedMessage());
        }

    }

    private void insertEvent() {

        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String datetimeString = yearFinal + "-" + monthFinal + "-" + dayFinal + " " + hourFinal + ":" + minuteFinal;

        if (imageInputData == null) {
            Bitmap iconResource = BitmapFactory.decodeResource(getResources(), com.example.android.rsorganiser_add_items_to_database_26022017.R.drawable.school_logo);
            imageInputData = ImageConversion.getImageBytes(iconResource);
        }


        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, nameString);
        values.put(EventsContract.EventEntry.COLUMN_EVENT_ICON, imageInputData);
        values.put(EventsContract.EventEntry.COLUMN_EVENT_DATE, datetimeString);
        values.put(EventsContract.EventEntry.COLUMN_EVENT_DESCRIPTION, descriptionString);
        values.put(EventsContract.EventEntry.COLUMN_ORGANISATION_ID, organisationID);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        long newRowId = db.insert(EventsContract.EventEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                insertEvent();
                // Exit activity
                finish();
                return true;

            case R.id.action_delete:
                // Do nothing for now
                return true;

            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}


