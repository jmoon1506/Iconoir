package com.iconoir.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.CustomViewHolder> {
    List<String> iconList;
    Map<String, String> iconTargetMap;
    MainActivity context;
    Boolean showAll = false;
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

    public String updateIconPackageMap(String targetPkg) {
        String iconPkg = getItem(currentIconPos);
        iconTargetMap.put(iconPkg, targetPkg);
        currentIconPos = -1;
        notifyDataSetChanged();
        return iconPkg;
    }

    @Override
    public int getItemCount() {
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
        return position;
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
        String iconPkg = getItem(position);

        holder.target_text.setTag(position);
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
            holder.target_text.setText(getPackageLabel(iconTargetMap.get(iconPkg)));
            holder.target_text.setTextColor(Color.WHITE);
            holder.target_text.setOnClickListener(new View.OnClickListener() {
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
            holder.target_text.setText(R.string.notInstalled);
            holder.target_text.setTextColor(Color.GRAY);
            holder.target_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, IconInstallActivity.class);
                    context.startActivity(i);
                }
            });
        }
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

    class CustomViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout listItem;
        ImageView icon;
        ImageView arrow;
        ImageView target;
        TextView target_text;

        public CustomViewHolder(View view) {
            super(view);
            this.listItem = (RelativeLayout) view.findViewById(R.id.listItem);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            this.arrow = (ImageView) view.findViewById(R.id.arrow);
            this.target = (ImageView) view.findViewById(R.id.target);
            this.target_text = (TextView) view.findViewById(R.id.target_text);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_icon, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    public String getPackageLabel(String pkgName) {
        try {
            ApplicationInfo app = packageManager.getApplicationInfo(pkgName, 0);
            return (String) packageManager.getApplicationLabel(app);
        } catch (PackageManager.NameNotFoundException e) {
            return pkgName;
        }
    }
}
