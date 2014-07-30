package com.hooapps.pca.cvilleart.artfinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hooapps.pca.cvilleart.artfinder.R;

import java.util.List;

public class NavDrawerAdapter extends BaseAdapter {

    private Context context;
    private List<String> items;
    private LayoutInflater inflater;
    private ViewHolder holder;

    public NavDrawerAdapter(Context context, List<String> items) {
        super();
        this.context = context;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        public TextView textView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.nav_drawer_text_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.nav_item_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.textView.setText(items.get(position));
        return view;
    }
}