package com.iconoir.settings;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IconListAdapter extends BaseAdapter {
    List<String> iconList;
    Map<String, String> iconTargetMap;
    MainActivity context;
    Boolean showAll;
    PackageManager packageManager;
    int currentIconPos = -1;
    List<Integer> hiddenPositions;

    public IconListAdapter(MainActivity context, Map<String, String> iconTargetMap) {
        super();
        this.packageManager = context.getPackageManager();
        this.context = context;
        this.iconList = new ArrayList<String>();
        this.iconTargetMap = iconTargetMap;
        this.iconList.addAll(iconTargetMap.keySet());
        Collections.sort(this.iconList);
        updateHiddenPositions();
    }

    public void updateHiddenPositions() {

        this.hiddenPositions = new ArrayList<>();
        for (int i = 0; i < this.iconList.size(); i++) {
            if (!isPackageInstalled(this.iconList.get(i))) {
                this.hiddenPositions.add(i);
            }
        }
        notifyDataSetChanged();
    }

    private boolean isPackageInstalled(String targetPackage) {
        try {
            PackageInfo info=packageManager.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    private PackageInfo getPackageInfo(String targetPackage) {
        try {
            return packageManager.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public void setShowAll(Boolean showAll) {
        this.showAll = showAll;
        notifyDataSetChanged();
    }

    public void updateIconPackageMap(String iconPkg, String targetPkg) {
        iconTargetMap.put(iconPkg, targetPkg);
        notifyDataSetChanged();
    }

    public void updateIconPackageMap(String targetPkg) {
        iconTargetMap.put(getItem(currentIconPos), targetPkg);
        currentIconPos = -1;
        notifyDataSetChanged();
    }

    public int getCount() {
        if (showAll) {
            return iconList.size();
        } else {
            return iconList.size() - hiddenPositions.size();
        }
    }

    public String getItem(int position) {
        return iconList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        RelativeLayout row;
        TextView text;
        ImageView icon;
        ImageView arrow;
        ImageView target;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Shared view
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_icon, null);
            holder = new ViewHolder();
            holder.row = (RelativeLayout) convertView.findViewById(R.id.row);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            holder.target = (ImageView) convertView.findViewById(R.id.target);
            holder.text = (TextView) convertView.findViewById(R.id.target_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Item data
        if (!showAll) {
            for (int hiddenIndex : hiddenPositions) {
                if (hiddenIndex <= position) {
                    position = position + 1;
                }
            }
        }
        String iconPkg = getItem(position);

        holder.text.setTag(position);
        int drawableId = getDrawableId(iconPkg);
        holder.icon.setImageDrawable(context.getResources().getDrawable(drawableId));
        if (isPackageInstalled(iconPkg)) {
            String targetPkg = iconTargetMap.get(iconPkg);
            PackageInfo targetInfo = getPackageInfo(targetPkg);
            if (targetInfo != null) {
                holder.target.setImageDrawable(packageManager.getApplicationIcon(targetInfo.applicationInfo));
                holder.arrow.setVisibility(View.VISIBLE);
                holder.target.setVisibility(View.VISIBLE);
            } else {
                holder.arrow.setVisibility(View.INVISIBLE);
                holder.target.setVisibility(View.INVISIBLE);
            }
            holder.text.setText(iconTargetMap.get(iconPkg));
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentIconPos = (Integer)v.getTag();
                    Intent i = new Intent(context, TargetActivity.class);
                    context.startActivityForResult(i, 1);
                }
            });
        } else {
            holder.arrow.setVisibility(View.INVISIBLE);
            holder.target.setVisibility(View.INVISIBLE);
            holder.text.setText("NOT INSTALLED");
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, IconInstallActivity.class);
                    context.startActivity(i);
                }
            });
        }
        Animation anim = AnimationUtils.loadAnimation(
                context, android.R.anim.slide_in_left
        );
        anim.setDuration(300);
        convertView.startAnimation(anim);
        return convertView;
    }

    public static int getDrawableId(String iconPkg) {
        String parsed = iconPkg.replace(".", "_");
        try {
            Class c = R.drawable.class;
            Field field = c.getDeclaredField(parsed);
            return field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
