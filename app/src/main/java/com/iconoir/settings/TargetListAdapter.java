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
    List<Integer> hiddenPositions;

    public TargetListAdapter(TargetActivity context, Boolean showAll) {
        super();
        this.packageManager = context.getPackageManager();
        this.context = context;
        this.showAll = showAll;
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        targetList = new ArrayList<>();

        List<String> iconoirPkgs = Arrays.asList(context.getResources().getStringArray(R.array.iconoirPackages));
        String iconoirSettingsPkg = context.getResources().getString(R.string.iconoirSettingsPackage);
        for(PackageInfo pkgInfo : packageList) {
            if (isSystemPackage(pkgInfo) ||
                    (!iconoirPkgs.contains(pkgInfo.packageName) && !iconoirSettingsPkg.equals(pkgInfo.packageName))) {
                targetList.add(new TargetInfo(pkgInfo));
            }
        }
        Collections.sort(targetList, new TargetInfoComparator());

        List<String> validSystemPkgs = Arrays.asList(context.getResources().getStringArray(R.array.validSystemPackages));
        hiddenPositions = new ArrayList<>();
        for(int i = 0; i < targetList.size(); i++) {
            PackageInfo pkgInfo = targetList.get(i).pkgInfo;
            if(isSystemPackage(pkgInfo) && !validSystemPkgs.contains(pkgInfo.packageName)) {
                hiddenPositions.add(i);
            }
        }
    }

    class TargetInfo {
        PackageInfo pkgInfo;
        String pkgLabel;

        public TargetInfo(PackageInfo pkgInfo) {
            this.pkgInfo = pkgInfo;
            this.pkgLabel = getPkgLabel(pkgInfo);
        }

        public TargetInfo(PackageInfo pkgInfo, String pkgLabel) {
            this.pkgInfo = pkgInfo;
            this.pkgLabel = pkgLabel;
        }
    }

    public class TargetInfoComparator implements Comparator<TargetInfo> {
        @Override
        public int compare(TargetInfo info1, TargetInfo info2) {
            return info1.pkgLabel.compareTo(info2.pkgLabel);
        }
    };

    public void setShowAll(Boolean showAll) {
        this.showAll = showAll;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (showAll) {
            return targetList.size();
        } else {
            return targetList.size() - hiddenPositions.size();
        }
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
                    String targetPkg = getItem(position).pkgLabel;
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
        if (!showAll) {
            for (int hiddenIndex : hiddenPositions) {
                if (hiddenIndex <= position) {
                    position = position + 1;
                }
            }
        }
        final TargetInfo item = getItem(position);
        holder.text.setTag(position);
        holder.text.setText(item.pkgLabel);
        holder.icon.setImageDrawable(packageManager.getApplicationIcon(item.pkgInfo.applicationInfo));
    }

    private String getPkgLabel(PackageInfo pkgInfo) {
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        try {
//            final Resources res = packageManager.getResourcesForApplication(appInfo);
//            res.updateConfiguration(context.config, context.displayMetrics);
//            return res.getString(appInfo.labelRes);
            return (String) packageManager.getApplicationLabel(appInfo);
        } catch (Exception e1) {
            try {
                return String.valueOf(packageManager.getApplicationLabel(appInfo));
            } catch (Exception e2) {
                return pkgInfo.packageName;
            }
        }
    }

    public boolean isSystemPackage(PackageInfo appInfo) {
        return (appInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}