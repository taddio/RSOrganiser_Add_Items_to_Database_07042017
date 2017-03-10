package com.example.android.rsorganiser_add_items_to_database_26022017.Data;

import android.provider.BaseColumns;

/**
 * API Contract for the Organiser app.
 */
public final class EventsContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private EventsContract() {}

    /**
     * Inner class that defines constant values for the events database table.
     * Each entry in the table represents a single event.
     */
    public static final class EventEntry implements BaseColumns {

        /** Name of database table for evemts */
        public final static String TABLE_NAME = "events";

        public final static String FOREIGN_TABLE_NAME = "organisations";

        /**
         * Unique ID number for the event (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the event.
         *
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_NAME ="name";

        /**
         * Icon for event.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_EVENT_ICON = "icon";

        /**
         * Foreign Key to find Organisation name
         *
         * Type: INTEGER
         */
        public final static String COLUMN_ORGANISATION_ID = "OrganisationID";

        /**
         * Date and time of event.
         *
         * Type: TEXT
         */
        public final static String COLUMN_EVENT_DATE = "date";

        public final static String COLUMN_EVENT_DESCRIPTION = "description";
    }

}
