package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.graphics.Bitmap;

public class RowItem {

    private String event_name;
    private Bitmap event_icon;
    private String organisation;
    private String time;

    public RowItem(String event_name, Bitmap event_icon, String organisation,
                   String time) {

        this.event_name = event_name;
        this.event_icon = event_icon;
        this.organisation = organisation;
        this.time = time;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public Bitmap getEvent_icon() {
        return event_icon;
    }

    public void setEvent_icon_id(int event_icon_id) {
        this.event_icon = event_icon;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
