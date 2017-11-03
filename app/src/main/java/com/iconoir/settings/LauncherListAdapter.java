package com.iconoir.settings;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LauncherListAdapter extends BaseAdapter {
    List<String> iconList;
    Map<String, String> launcherMap;
    Activity context;
    Boolean showAll;
    PackageManager packageManager;

    public LauncherListAdapter(Activity context, Map<String, String> launcherMap) {
        super();
        this.packageManager = context.getPackageManager();
        this.context = context;
        this.launcherMap = launcherMap;
        this.iconList = new ArrayList<String>();
        this.iconList.addAll(launcherMap.keySet());
        Collections.sort(this.iconList);
    }

    @Override
    public boolean isEnabled(int position) {
        if(showAll) {
            return true;
        }
        return false;
    }

    public boolean isPackageInstalled(String targetPackage) {
        try {
            PackageInfo info=packageManager.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public void setShowAll(Boolean showAll) {
        this.showAll = showAll;
        notifyDataSetChanged();
    }

    public int getCount() {
        return iconList.size();
    }

    public String getItem(int position) {
        return iconList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        View launcherItem;
        TextView text;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

//        if (!showAll && !isPackageInstalled(getItem(position))) {
        if (!showAll && !isPackageInstalled(getItem(position))) {
            convertView = inflater.inflate(R.layout.null_item, null);
            return convertView;
        }


//        if (convertView == null) {
        convertView = inflater.inflate(R.layout.launcher_item, null);
        holder = new ViewHolder();

        holder.launcherItem = convertView.findViewById(R.id.launcherItem);
        holder.text = (TextView) convertView.findViewById(R.id.textView);
        holder.text.setText(getItem(position));
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }

//        holder.launcherItem.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//            }
//        });

        return convertView;
    }
}
