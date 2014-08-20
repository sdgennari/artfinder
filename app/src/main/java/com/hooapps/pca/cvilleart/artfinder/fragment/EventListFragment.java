package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.api.model.Event;
import com.hooapps.pca.cvilleart.artfinder.data.EventTable;
import com.hooapps.pca.cvilleart.artfinder.util.ColorUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventListFragment extends BaseFragment implements
        AdapterView.OnItemClickListener {

    private SQLiteDatabase db;
    private List<Event> eventList;
    private EventListAdapter adapter;

    @InjectView(R.id.list)
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
        ButterKnife.inject(this, rootView);

        db = MainApp.getDatabase();
        eventList = new ArrayList<Event>();

        loadData();
        adapter = new EventListAdapter(getActivity());
        adapter.addAll(eventList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void loadData() {
        String[] projection = {
                EventTable.COL_EVENT_ID,
                EventTable.COL_CATEGORY,
                EventTable.COL_SUMMARY,
                EventTable.COL_START_TIME
        };

        Cursor c = db.query(
                EventTable.TABLE_EVENTS,
                projection,
                null,
                null,
                null,
                null,
                EventTable.COL_START_TIME + " ASC");

        Log.d("EVENTS", "" + c.getCount());

        Event event;
        while (c.moveToNext()) {
            event = new Event();
            event.id = c.getString(c.getColumnIndex(EventTable.COL_EVENT_ID));
            event.category = c.getString(c.getColumnIndex(EventTable.COL_CATEGORY));
            event.summary = c.getString(c.getColumnIndex(EventTable.COL_SUMMARY));
            event.unixStart = c.getLong(c.getColumnIndex(EventTable.COL_START_TIME));
            eventList.add(event);
        }
    }

    private class EventListAdapter extends ArrayAdapter<Event> {
        private Context context;
        private LayoutInflater inflater;

        public EventListAdapter(Context context) {
            super(context, R.layout.event_list_item);
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.event_list_item, null);
                holder.headerView = (TextView) convertView.findViewById(R.id.event_header);
                holder.nameView = (TextView) convertView.findViewById(R.id.event_name);
                holder.categoryView = (TextView) convertView.findViewById(R.id.event_category);
                holder.timeView = (TextView) convertView.findViewById(R.id.time);
                holder.timePeriodView = (TextView) convertView.findViewById(R.id.time_period);
                holder.timeContainerView = (RelativeLayout) convertView.findViewById(R.id.time_container);
                convertView.setTag(holder);
            }


            Event event = getItem(position);
            holder.nameView.setText(event.summary);
            holder.categoryView.setText(event.category);

            // Convert the unixStart time to hours and minutes
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(event.unixStart);
            int hours = c.get(Calendar.HOUR_OF_DAY);
            int minutes = c.get(Calendar.MINUTE);
            int formattedHours = hours;

            if (hours > 12) {
                formattedHours = hours % 12;
            }
            holder.timeView.setText(String.format("% 2d", formattedHours) + ":" + String.format("%02d", minutes));

            if (hours/12 == 0) {
                holder.timePeriodView.setText("AM");
            } else {
                holder.timePeriodView.setText("PM");
            }

            int colorResId = ColorUtils.getColorForCategory(event.category);
            holder.timeContainerView.setBackgroundColor(getResources().getColor(colorResId));

            if (hasHeader(position)) {
                holder.headerView.setText(getDateString(position));
                holder.headerView.setVisibility(View.VISIBLE);
            } else {
                holder.headerView.setVisibility(View.GONE);
            }
            return convertView;
        }

        private boolean hasHeader(int position) {
            if (position == 0) {
                return true;
            }

            Event current = getItem(position);
            Event previous = getItem(position-1);

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(current.unixStart);
            int curDay = c.get(Calendar.DAY_OF_MONTH);
            c.setTimeInMillis(previous.unixStart);
            int prevDay = c.get(Calendar.DAY_OF_MONTH);
            return prevDay != curDay;
        }

        private String getDateString(int position) {
            Event event = getItem(position);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(event.unixStart);

            String result = "";

            // Find the day of the week
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            switch (dayOfWeek) {
                case Calendar.MONDAY:
                    result += "Monday";
                    break;
                case Calendar.TUESDAY:
                    result += "Tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    result += "Wednesday";
                    break;
                case Calendar.THURSDAY:
                    result += "Thursday";
                    break;
                case Calendar.FRIDAY:
                    result += "Friday";
                    break;
                case Calendar.SATURDAY:
                    result += "Saturday";
                    break;
                case Calendar.SUNDAY:
                    result += "Sunday";
                    break;
            }

            result += ", ";

            // Find the month
            int month = c.get(Calendar.MONTH);
            switch (month) {
                case Calendar.JANUARY:
                    result += "January";
                    break;
                case Calendar.FEBRUARY:
                    result += "February";
                    break;
                case Calendar.MARCH:
                    result += "March";
                    break;
                case Calendar.APRIL:
                    result += "April";
                    break;
                case Calendar.MAY:
                    result += "May";
                    break;
                case Calendar.JUNE:
                    result += "June";
                    break;
                case Calendar.JULY:
                    result += "July";
                    break;
                case Calendar.AUGUST:
                    result += "August";
                    break;
                case Calendar.SEPTEMBER:
                    result += "September";
                    break;
                case Calendar.OCTOBER:
                    result += "October";
                    break;
                case Calendar.NOVEMBER:
                    result += "November";
                    break;
                case Calendar.DECEMBER:
                    result += "December";
                    break;
            }

            result += " ";

            // Add the date
            result += c.get(Calendar.DAY_OF_MONTH);

            return result;
        }
    }

    class ViewHolder {
        TextView headerView;
        TextView nameView;
        TextView categoryView;
        TextView timeView;
        TextView timePeriodView;
        RelativeLayout timeContainerView;
    }
}
