package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.activity.VenueDetailActivity;
import com.hooapps.pca.cvilleart.artfinder.api.model.ArtVenue;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.VenueTable;
import com.hooapps.pca.cvilleart.artfinder.util.ColorUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VenueListFragment extends BaseFragment implements
        AdapterView.OnItemClickListener {

    @InjectView(R.id.list)
    ListView listView;

    private SQLiteDatabase db;
    private ArrayList<ArtVenue> venueList;
    private VenueListAdapter adapter;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), VenueDetailActivity.class);
        intent.putExtra(C.EXT_PARSE_OBJECT_ID, venueList.get(position).parseObjectId);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_venue_list, container, false);
        ButterKnife.inject(this, root);

        db = MainApp.getDatabase();

        venueList = new ArrayList<ArtVenue>();

        loadData();
        adapter = new VenueListAdapter(this.getActivity());
        adapter.addAll(venueList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(this);

        return root;
    }

    private void loadData() {
        String[] projection = {
                VenueTable.COL_ORGANIZATION_NAME,
                VenueTable.COL_IMAGE_URL,
                VenueTable.COL_PRIMARY_CATEGORY,
                VenueTable.COL_PARSE_OBJECT_ID
        };

        Cursor c = db.query(
                VenueTable.TABLE_VENUES,
                projection,
                VenueTable.COL_IS_DELETED + " = 'NO'",
                null,
                null,
                null,
                VenueTable.COL_PRIMARY_CATEGORY + " ASC, UPPER(" + VenueTable.COL_ORGANIZATION_NAME + ") ASC");

        Log.d("VENUES", "" + c.getCount());

        ArtVenue venue;
        while (c.moveToNext()) {
            venue = new ArtVenue();
            venue.organizationName = c.getString(c.getColumnIndex(VenueTable.COL_ORGANIZATION_NAME));
            venue.imageUrl = c.getString(c.getColumnIndex(VenueTable.COL_IMAGE_URL));
            venue.primaryCategory = c.getString(c.getColumnIndex(VenueTable.COL_PRIMARY_CATEGORY));
            venue.parseObjectId = c.getString(c.getColumnIndex(VenueTable.COL_PARSE_OBJECT_ID));
            venueList.add(venue);
        }
    }

    private class VenueListAdapter extends ArrayAdapter<ArtVenue> {
        private Context context;
        private LayoutInflater inflater;

        public VenueListAdapter(Context context) {
            super(context, R.layout.venue_list_item);
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.venue_list_item, null);
                holder.headerView = (TextView) convertView.findViewById(R.id.venue_header);
                holder.nameView = (TextView) convertView.findViewById(R.id.venue_name);
                holder.imageView = (ImageView) convertView.findViewById(R.id.venue_image);
                convertView.setTag(holder);
            }

            holder.nameView.setText(getItem(position).organizationName);

            int drawableResId = ColorUtils.getVenueDrawableForCategory(getItem(position).primaryCategory);
            if (getItem(position).imageUrl != null && !getItem(position).imageUrl.isEmpty()) {
                Picasso.with(context).load(getItem(position).imageUrl)
                        .placeholder(drawableResId)
                        .resize(C.THUMB_SIZE, C.THUMB_SIZE)
                        .centerCrop()
                        .into(holder.imageView);
            } else {
                Picasso.with(context).load(drawableResId)
                        .into(holder.imageView);
            }


            holder.headerView.setText(getItem(position).primaryCategory.toUpperCase());
            if (hasHeader(position) ) {
                holder.headerView.setVisibility(View.VISIBLE);
            } else {
                holder.headerView.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    class ViewHolder {
        TextView headerView;
        TextView nameView;
        ImageView imageView;
    }

    private boolean hasHeader(int position) {
        if (position == 0) {
            return true;
        }
        ArtVenue current = venueList.get(position);
        ArtVenue previous = venueList.get(position - 1);
        return !current.primaryCategory.equalsIgnoreCase(previous.primaryCategory);
    }
}
