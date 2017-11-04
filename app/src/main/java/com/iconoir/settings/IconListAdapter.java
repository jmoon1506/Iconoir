package com.iconoir.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IconListAdapter extends BaseAdapter {
    List<String> iconList;
    Map<String, String> iconMap;
    Activity context;
    Boolean showAll;
    PackageManager packageManager;

    public IconListAdapter(Activity context, Map<String, String> iconMap) {
        super();
        this.packageManager = context.getPackageManager();
        this.context = context;
        this.iconMap = iconMap;
        this.iconList = new ArrayList<String>();
        this.iconList.addAll(iconMap.keySet());
        Collections.sort(this.iconList);
    }

//    @Override
//    public boolean isEnabled(int position) {
//        if(showAll) {
//            return true;
//        }
//        return false;
//    }

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
        ImageView icon;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(getItem(position),
                    PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }

        if (showAll || packageInfo != null) {
            if (convertView == null || convertView.getTag() == null) {
                convertView = inflater.inflate(R.layout.icon_item, null);
                holder = new ViewHolder();

                holder.launcherItem = convertView.findViewById(R.id.listItem);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                holder.text.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, PackagesActivity.class));
//                        TextView t = (TextView) v.findViewById(R.id.textView);
//                        Toast.makeText(context, t.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // Set data
            holder.text.setText(iconMap.get(getItem(position)));
            int drawableId = getDrawableId(getItem(position));
            Log.d("TAG", getItem(position));
            Log.d("TAG", String.valueOf(drawableId));

            holder.icon.setImageDrawable(context.getResources().getDrawable(drawableId));
//            holder.icon.setImageDrawable(context.getResources().getDrawable(getResources().);
//            if (packageInfo != null) {
//                holder.icon.setImageDrawable(packageManager.getApplicationIcon(packageInfo.applicationInfo));
//            } else {
//                holder.icon.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_info_details));
//            }
            return convertView;

        } else {
            convertView = null;
            return inflater.inflate(R.layout.null_item, null);
        }
    }

    public static int getDrawableId(String iconPkg) {
        String parsed = (iconPkg.toLowerCase()).replace(".", "_");
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
