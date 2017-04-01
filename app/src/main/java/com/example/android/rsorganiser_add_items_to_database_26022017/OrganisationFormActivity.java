package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.rsorganiser_add_items_to_database_26022017.Data.DbHelper;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.ImageConversion;
import com.example.android.rsorganiser_add_items_to_database_26022017.Data.OrganisationsContract;

import java.io.IOException;
import java.io.InputStream;

public class OrganisationFormActivity extends AppCompatActivity{

    private EditText mNameEditText;

    private EditText mDescriptionEditText;

    private Button mImageButton;

    private DbHelper mDbHelper;

    byte[] imageInputData;

    String imageInputPath;

    private static final int SELECT_PICTURE = 100;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organisation_form);

        mNameEditText = (EditText) findViewById(R.id.edit_organisation_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_organisation_description);
        mImageButton = (Button) findViewById(R.id.edit_image_button);
        mDbHelper = new DbHelper(this);

        setupImageButton();
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

                    imageInputPath = selectedImageUri.toString();
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

    private void insertOrganisation() {

        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_NAME, nameString);
        values.put(OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_ICON, imageInputPath);
        values.put(OrganisationsContract.OrganisationEntry.COLUMN_ORGANISATION_DESCRIPTION, descriptionString);

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        long newRowId = db.insert(OrganisationsContract.OrganisationEntry.TABLE_NAME, null, values);
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
                // Save event to database
                insertOrganisation();
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
}
