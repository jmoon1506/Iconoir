package com.iconoir.settings;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TargetListAdapter extends RecyclerView.Adapter<TargetListAdapter.CustomViewHolder> {
    Map<String, PackageInfo> packageMap;
    List<TargetInfo> targetList;
    TargetActivity context;
    PackageManager packageManager;
    Boolean showAll = false;
//    List<Integer> hiddenPositions;

    public TargetListAdapter(TargetActivity context, Map<String, PackageInfo> labelInfoMap) {
        super();
        this.packageManager = context.getPackageManager();
        this.context = context;
        targetList = new ArrayList<>();
        for (String label : labelInfoMap.keySet()) {
            targetList.add(new TargetInfo(label, labelInfoMap.get(label)));
        }
        targetList.add(new TargetInfo("", null));
        Collections.sort(targetList, new TargetInfoComparator());
    }

    class TargetInfo {
        PackageInfo pkgInfo;
        String pkgLabel;

        public TargetInfo(String pkgLabel, PackageInfo pkgInfo) {
            this.pkgInfo = pkgInfo;
            this.pkgLabel = pkgLabel;
        }
    }

    public class TargetInfoComparator implements Comparator<TargetInfo> {
        @Override
        public int compare(TargetInfo info1, TargetInfo info2) {
            if (info1.pkgLabel.isEmpty()) return 1; // empty label should go at end of list
            if (info2.pkgLabel.isEmpty()) return -1;
            return info1.pkgLabel.compareTo(info2.pkgLabel);
        }
    };

    public void setShowAll(Boolean showAll) {
        this.showAll = showAll;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return targetList.size();
    }

    public TargetInfo getItem(int position) {
        return targetList.get(position);
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
                    PackageInfo pkgInfo = getItem(position).pkgInfo;
                    String targetPkg = pkgInfo == null ? "" : pkgInfo.packageName;
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
        final TargetInfo item = getItem(position);
        holder.text.setTag(position);
        holder.text.setText(item.pkgLabel);
        if (item.pkgInfo != null) {
            holder.icon.setImageDrawable(packageManager.getApplicationIcon(item.pkgInfo.applicationInfo));
        } else {
            holder.icon.setImageDrawable(null);
        }
    }
}