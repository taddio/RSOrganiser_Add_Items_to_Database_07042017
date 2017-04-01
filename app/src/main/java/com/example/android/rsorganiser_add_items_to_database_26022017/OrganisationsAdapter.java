package com.example.android.rsorganiser_add_items_to_database_26022017;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class OrganisationsAdapter extends BaseAdapter {

    Context context;
    List<OrganisationRowItem> organisationRowItems;

    OrganisationsAdapter(Context context, List<OrganisationRowItem> organisationRowItems) {
        this.context = context;
        this.organisationRowItems = organisationRowItems;
    }

    @Override
    public int getCount() {
        String rowItemsSize = Integer.toString(organisationRowItems.size());
        return organisationRowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return organisationRowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        String logPosition = Integer.toString(organisationRowItems.indexOf(getItem(position)));
        Log.v("Row Position", logPosition);
        return organisationRowItems.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView organisation_icon;
        TextView organisations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        OrganisationsAdapter.ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(com.example.android.rsorganiser_add_items_to_database_26022017.R.layout.organisation_list_item, null);
            holder = new OrganisationsAdapter.ViewHolder();

            holder.organisations = (TextView) convertView
                    .findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.organisations);
            holder.organisation_icon = (ImageView) convertView
                    .findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.organisation_icon);

            convertView.setTag(holder);
        } else {
            holder = (OrganisationsAdapter.ViewHolder) convertView.getTag();
        }

        OrganisationRowItem row_pos = organisationRowItems.get(position);

        if(row_pos.getOrganisation_icon() == null) {
            holder.organisation_icon.setImageResource(R.drawable.school_logo);
        }
        else if(row_pos.getOrganisation_icon() != null){
            holder.organisation_icon.setImageBitmap(row_pos.getOrganisation_icon());
        }

        holder.organisations.setText(row_pos.getOrganisations());

        return convertView;
    }
}

