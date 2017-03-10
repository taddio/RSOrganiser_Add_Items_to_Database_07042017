package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.graphics.Bitmap;

/**
 * Created by Tads on 29-Jan-17.
 */

public class OrganisationRowItem {
    private Bitmap organisation_icon;
    private String organisations;

    public OrganisationRowItem(String organisations, Bitmap organisation_icon) {

        this.organisations = organisations;
        this.organisation_icon = organisation_icon;
    }

    public String getOrganisations() {
        return organisations;
    }

    public void setOrganisations(String organisations) {
        this.organisations = organisations;
    }

    public Bitmap getOrganisation_icon() {return organisation_icon;
    }

    public void setOrganisation_icon_id(int organisation_icon_id) {
        this.organisation_icon = organisation_icon;
    }

}