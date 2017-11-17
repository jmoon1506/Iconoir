package com.iconoir.settings;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TargetListAdapter extends RecyclerView.Adapter<TargetListAdapter.CustomViewHolder> {
    Map<String, PackageInfo> packageMap;
    List<String> packageList;
    TargetActivity context;
    PackageManager packageManager;

    public TargetListAdapter(TargetActivity context, List<String> packageList, Map<String, PackageInfo> packageMap) {
        super();
        this.packageManager = context.getPackageManager();
        this.context = context;
        this.packageList = packageList;
        this.packageMap = packageMap;
        Collections.sort(this.packageList);
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    public String getItem(int position) {
        return packageList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        View packageItem;
        TextView text;
        ImageView icon;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout listItem;
        ImageView icon;
        TextView text;

        public CustomViewHolder(View view) {
            super(view);
            this.listItem = (RelativeLayout) view.findViewById(R.id.listItem);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            this.text = (TextView) view.findViewById(R.id.text);
            this.text.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int position=(Integer)v.getTag();
                    String targetPkg = packageMap.get(getItem(position)).applicationInfo.packageName;
                    context.onBackPressed(targetPkg);
                }
            });
        }
    }

    @Override
    public TargetListAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_target, null);
        return new TargetListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final ApplicationInfo appInfo = packageMap.get(getItem(position)).applicationInfo;
        holder.text.setTag(position);
        holder.text.setText(packageManager.getApplicationLabel(appInfo).toString());
        holder.icon.setImageDrawable(packageManager.getApplicationIcon(appInfo));
    }
}
