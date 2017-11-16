package com.iconoir.settings;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TargetListAdapter extends BaseAdapter {
    Map<String, PackageInfo> packageMap;
    List<String> packageList;
    TargetActivity context;
    Boolean showSystemPackages;
    PackageManager packageManager;

    public TargetListAdapter(TargetActivity context, List<String> packageList, Map<String, PackageInfo> packageMap) {
        super();
        this.packageManager = context.getPackageManager();
        this.context = context;
        this.packageList = packageList;
        this.packageMap = packageMap;
        Collections.sort(this.packageList);
    }

//    class SortPackageInfo implements Comparator<PackageInfo>
//    {
//        // Used for sorting in ascending order of
//        // roll number
//        public int compare(PackageInfo a, PackageInfo b)
//        {
//            return packageManager.getApplicationLabel(a.applicationInfo).toString().compareTo(
//                    packageManager.getApplicationLabel(b.applicationInfo).toString());
//        }
//    }

    public void setShowAll(Boolean showSystemPackages) {
        this.showSystemPackages = showSystemPackages;
        notifyDataSetChanged();
    }

    public int getCount() {
        return packageList.size();
    }

    public String getItem(int position) {
        return packageList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        View packageItem;
        TextView text;
        ImageView icon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.item_target, null);
            holder = new ViewHolder();

            holder.packageItem = convertView.findViewById(R.id.listItem);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.text.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int position=(Integer)v.getTag();
                    String targetPkg = packageMap.get(getItem(position)).applicationInfo.packageName;
                    context.onBackPressed(targetPkg);
//                    Toast.makeText(context,
//                            targetPkg, Toast.LENGTH_SHORT)
//                            .show();
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ApplicationInfo appInfo = packageMap.get(getItem(position)).applicationInfo;
        holder.text.setTag(position);
        holder.text.setText(packageManager.getApplicationLabel(appInfo).toString());
        holder.icon.setImageDrawable(packageManager.getApplicationIcon(appInfo));
        return convertView;
    }
}
