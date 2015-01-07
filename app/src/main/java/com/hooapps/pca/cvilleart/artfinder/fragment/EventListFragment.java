package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.hooapps.pca.cvilleart.artfinder.Datastore;
import com.hooapps.pca.cvilleart.artfinder.MainApp;
import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.activity.EventDetailActivity;
import com.hooapps.pca.cvilleart.artfinder.api.model.Event;
import com.hooapps.pca.cvilleart.artfinder.constants.C;
import com.hooapps.pca.cvilleart.artfinder.data.EventTable;
import com.hooapps.pca.cvilleart.artfinder.util.ColorUtils;
import com.hooapps.pca.cvilleart.artfinder.util.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EventListFragment extends BaseFragment implements
        AdapterView.OnItemClickListener,
        EventFilterDialogFragment.EventFilterDialogListener {

    private Datastore datastore;
    private SQLiteDatabase db;
    private List<Event> eventList;
    private EventListAdapter adapter;
    private boolean[] checkedItems;
    private String[] categoryArr;

    @InjectView(R.id.list)
    ListView listView;
    @InjectView(R.id.error_title)
    TextView noEventTitleView;
    @InjectView(R.id.error_message)
    TextView noEventMessageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        datastore = Datastore.getInstance();
        checkedItems = datastore.getEventFilterItems();
        categoryArr = getResources().getStringArray(R.array.event_filter_categories);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {

            Bundle args = new Bundle();
            args.putBooleanArray(C.EXT_CHECKED_ITEMS, checkedItems.clone());

            DialogFragment dialog = new EventFilterDialogFragment();
            dialog.setArguments(args);
            dialog.setTargetFragment(this, 0);
            dialog.show(getFragmentManager(), "EventFilerDialogFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_list, container, false);
        ButterKnife.inject(this, rootView);

        db = MainApp.getDatabase();
        eventList = new ArrayList<Event>();

        loadData();
        adapter = new EventListAdapter(getActivity());
        adapter.setData(eventList);
        filterList();
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(this);

        if (eventList.size() == 0) {
            noEventTitleView.setVisibility(View.VISIBLE);
            noEventMessageView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            noEventTitleView.setVisibility(View.GONE);
            noEventMessageView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FlurryAgent.logEvent(getString(R.string.flurry_event));

        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        intent.putExtra(C.EXT_EVENT_ID, adapter.getItem(position).id);
        startActivity(intent);
    }

    private void loadData() {
        String[] projection = {
                EventTable.COL_EVENT_ID,
                EventTable.COL_CATEGORY,
                EventTable.COL_SUMMARY,
                EventTable.COL_START_TIME,
                EventTable.COL_IS_ALL_DAY
        };

        Cursor c = db.query(
                EventTable.TABLE_EVENTS,
                projection,
                null,
                null,
                null,
                null,
                EventTable.COL_START_TIME + " ASC");

        Event event;
        while (c.moveToNext()) {
            event = new Event();
            event.id = c.getString(c.getColumnIndex(EventTable.COL_EVENT_ID));
            event.category = c.getString(c.getColumnIndex(EventTable.COL_CATEGORY));
            event.summary = c.getString(c.getColumnIndex(EventTable.COL_SUMMARY));
            event.unixStart = c.getLong(c.getColumnIndex(EventTable.COL_START_TIME));
            event.isAllDay = (0!=c.getInt(c.getColumnIndex(EventTable.COL_IS_ALL_DAY)));
            eventList.add(event);
        }
    }

    @Override
    public void onFilterDialogPositiveClick(DialogFragment dialog, boolean[] checkedItems) {
        this.checkedItems = checkedItems;
        datastore.saveEventFilterItems(checkedItems);
        filterList();
    }

    @Override
    public void onFilterDialogNegativeClick(DialogFragment dialog) {
    }

    private void filterList() {
        String categoryString = "";
        for (int idx = 0; idx < checkedItems.length; idx++) {
            if (checkedItems[idx]) {
                categoryString += categoryArr[idx] + " ";
            }
        }
        adapter.getFilter().filter(categoryString);
    }

    private class EventListAdapter extends ArrayAdapter<Event> {
        private LayoutInflater inflater;

        private List<Event> originalList;
        private List<Event> filteredList;
        private Filter categoryFilter;

        public EventListAdapter(Context context) {
            super(context, R.layout.event_list_item);
            inflater = LayoutInflater.from(context);
            originalList = new ArrayList<Event>();
            filteredList = new ArrayList<Event>();
        }

        @Override
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
                holder.hourView = (TextView) convertView.findViewById(R.id.hour);
                holder.minuteView = (TextView) convertView.findViewById(R.id.minute);
                holder.timePeriodView = (TextView) convertView.findViewById(R.id.time_period);
                holder.timeContainerView = (RelativeLayout) convertView.findViewById(R.id.time_container);
                convertView.setTag(holder);
            }


            Event event = getItem(position);
            holder.nameView.setText(event.summary);
            holder.categoryView.setText(event.category);

            Calendar c = Calendar.getInstance();
            if (event.isAllDay) {
                // Display 'All Day' instead of time for all day events
                holder.timePeriodView.setVisibility(View.GONE);
                holder.hourView.setText("All");
                holder.minuteView.setText("Day");
            } else {
                // Convert the unixStart time to hours and minutes
                c.setTimeInMillis(event.unixStart);
                int hours = c.get(Calendar.HOUR_OF_DAY);
                int minutes = c.get(Calendar.MINUTE);
                int formattedHours = hours;

                if (hours > 12) {
                    formattedHours = hours % 12;
                }
                holder.hourView.setText(String.format("% 2d", formattedHours));
                holder.minuteView.setText(String.format("%02d", minutes));

                holder.timePeriodView.setVisibility(View.VISIBLE);
                if (hours / 12 == 0) {
                    holder.timePeriodView.setText("AM");
                } else {
                    holder.timePeriodView.setText("PM");
                }
            }

            int drawableResId = ColorUtils.getColorDrawableForCategory(event.category);
            holder.timeContainerView.setBackgroundResource(drawableResId);

            if (hasHeader(position)) {
                c = Calendar.getInstance();
                c.setTimeInMillis(event.unixStart);
                String dateString = TimeUtils.createDateString(c);
                holder.headerView.setText(dateString);
                holder.headerView.setVisibility(View.VISIBLE);
            } else {
                holder.headerView.setVisibility(View.GONE);
            }
            return convertView;
        }

        @Override
        public Event getItem(int pos) {
            return filteredList.get(pos);
        }

        @Override
        public int getCount() {
            return filteredList.size();
        }

        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public Filter getFilter() {
            if (categoryFilter == null) {
                categoryFilter = new EventFilter();
            }
            return categoryFilter;
        }

        public void setData(List<Event> list) {
            this.originalList = list;
            this.filteredList = list;
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

        class EventFilter extends Filter {

            // The charSequence is string with all desired categories separated by commas
            // Thus, charSequence.contain(event.category) will return true if the category
            // is one of the desired categories
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                String compText = charSequence.toString().toLowerCase();

                if (compText.isEmpty()) {
                    results.values = originalList;
                    results.count = originalList.size();
                    for (int i = 0; i < checkedItems.length; i++) {
                        checkedItems[i] = true;
                    }
                } else {
                    List<Event> nList = new ArrayList<Event>();

                    for (Event event : originalList) {
                        if (compText.contains(event.category.toLowerCase())) {
                            nList.add(event);
                        }
                    }
                    results.values = nList;
                    results.count = nList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                filteredList = (List<Event>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    class ViewHolder {
        TextView headerView;
        TextView nameView;
        TextView categoryView;
        TextView hourView;
        TextView minuteView;
        TextView timePeriodView;
        RelativeLayout timeContainerView;
    }
}
