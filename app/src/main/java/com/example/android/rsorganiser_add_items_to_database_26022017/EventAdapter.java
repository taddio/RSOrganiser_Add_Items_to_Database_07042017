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

public class EventAdapter extends BaseAdapter {

    Context context;
    List<RowItem> rowItems;

    EventAdapter(Context context, List<RowItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        String rowItemsSize = Integer.toString(rowItems.size());
        Log.v("Row Item Size", rowItemsSize);
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        String logPosition = Integer.toString(rowItems.indexOf(getItem(position)));
        Log.v("Row Position", logPosition);
        return rowItems.indexOf(getItem(position));
    }

    /* private view holder class */
    private class ViewHolder {
        ImageView event_icon;
        TextView event_name;
        TextView organisation;
        TextView time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(com.example.android.rsorganiser_add_items_to_database_26022017.R.layout.list_item, null);
            holder = new ViewHolder();

            holder.event_name = (TextView) convertView
                    .findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.event_name);
            holder.event_icon = (ImageView) convertView
                    .findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.event_icon);
            holder.organisation = (TextView) convertView
                    .findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.organisation);
            holder.time = (TextView) convertView
                    .findViewById(com.example.android.rsorganiser_add_items_to_database_26022017.R.id.time);


            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        RowItem row_pos = rowItems.get(position);

        holder.event_icon.setImageBitmap(row_pos.getEvent_icon());
        holder.event_name.setText(row_pos.getEvent_name());
        holder.organisation.setText(row_pos.getOrganisation());
        holder.time.setText(row_pos.getTime());

        return convertView;
    }
}
