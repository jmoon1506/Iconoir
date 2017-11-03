package com.iconoir.settings;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LauncherListAdapter extends BaseAdapter {
    List<LauncherInfo> launcherList;
    Activity context;

    public LauncherListAdapter(Activity context, List<LauncherInfo> launcherList) {
        super();
        this.context = context;
        this.launcherList = launcherList;
    }

    public int getCount() {
        return launcherList.size();
    }

    public Object getItem(int position) {
        return launcherList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return convertView;
    }
}
