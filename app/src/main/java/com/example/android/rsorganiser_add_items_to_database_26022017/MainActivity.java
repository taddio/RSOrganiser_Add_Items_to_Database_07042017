package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    // Declaring Your View and Variables
    private Toolbar toolbar;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout tabs;
    private CharSequence Titles[] = {"Events", "Organisations", "Map"};
    int Numboftabs = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.rsorganiser_add_items_to_database_26022017.R.layout.activity_main);

        // Creating The Toolbar and setting it as the Toolbar for the activity

        toolbar = (Toolbar) findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.toolbar);
        setSupportActionBar(toolbar);

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(com.example.android.rsorganiser_add_items_to_database_26022017.R.drawable.school_logo_title_bar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles
        // fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles,
                Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setViewPager(pager);
    }
}
