package com.iconoir.settings;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.CustomViewHolder> {
    List<IconInfo> iconList;
    Map<String, String> iconTargetMap;
//    Map<String, Boolean> iconReleaseMap;
    MainActivity context;
    Boolean showAll = false;
    PackageManager packageManager;
    int currentIconPos = -1;
    List<Integer> hiddenPositions;

    public IconListAdapter(MainActivity context) {
        super();
        this.context = context;

        packageManager = context.packageManager;
        iconList = new ArrayList<IconInfo>();
        iconList.add(new IconInfo(IconState.INSTALLED));
        iconList.add(new IconInfo(IconState.NOT_INSTALLED));
        iconList.add(new IconInfo(IconState.NOT_RELEASED));
        String[] iconPkgList = context.getResources().getStringArray(R.array.iconoirPackages);
        for (String iconPkg : iconPkgList) {
            String targetPkg = context.pref.getString(iconPkg, "");
            iconList.add(new IconInfo(iconPkg, targetPkg));
        }
        updateHiddenPositions();
    }

    public List<IconInfo> sortIconList(List<IconInfo> inputList) {
        List<IconInfo> installed = new ArrayList<IconInfo>();
        List<IconInfo> notInstalled = new ArrayList<IconInfo>();
        List<IconInfo> notReleased = new ArrayList<IconInfo>();
        List<IconInfo> outputList = new ArrayList<IconInfo>();
        for (IconInfo item : inputList) {
            switch (item.state) {
                case HEADER:
                    switch (item.headerType) {
                        case INSTALLED:
                            installed.add(item); break;
                        case NOT_INSTALLED:
                            notInstalled.add(item); break;
                        case NOT_RELEASED:
                            notReleased.add(item); break;
                    } break;
                case INSTALLED:
                    installed.add(item); break;
                case NOT_INSTALLED:
                    notInstalled.add(item); break;
                case NOT_RELEASED:
                    notReleased.add(item); break;
            }
        }
        Collections.sort(installed, new IconInfoComparator());
        Collections.sort(notInstalled, new IconInfoComparator());
        Collections.sort(notReleased, new IconInfoComparator());
        outputList.addAll(installed);
        outputList.addAll(notInstalled);
        outputList.addAll(notReleased);
        return outputList;
    }

    public class IconInfoComparator implements Comparator<IconInfo> {
        @Override
        public int compare(IconInfo info1, IconInfo info2) {
            if (info1.state == IconState.HEADER) return -1; // header is always higher
            if (info2.state == IconState.HEADER) return 1;
            return info1.iconPkg.compareTo(info2.iconPkg);
        }
    };

    public enum IconState {
        INSTALLED, NOT_INSTALLED, NOT_RELEASED, HEADER
    }

    class IconInfo {
        String iconPkg;
        String targetPkg;
        ApplicationInfo targetInfo;
        IconState state = IconState.NOT_INSTALLED;

        IconState headerType = IconState.INSTALLED;
        String headerTitle;

        public IconInfo(String iconPkg, String targetPkg) {
            this.iconPkg = iconPkg;
            setTarget(targetPkg);
            setState();
        }

        public IconInfo(IconState headerType) {
            this.state = IconState.HEADER;
            this.headerType = headerType;
            switch (headerType) {
                case INSTALLED:
                    this.headerTitle = context.getResources().getString(R.string.installed);
                    break;
                case NOT_INSTALLED:
                    this.headerTitle = context.getResources().getString(R.string.notInstalled);
                    break;
                case NOT_RELEASED:
                    this.headerTitle = context.getResources().getString(R.string.notReleased);
                    break;
            }
        }

        public ApplicationInfo setTarget(String targetPkg) {
            this.targetPkg = targetPkg;
            try {
                targetInfo = packageManager.getPackageInfo(targetPkg, PackageManager.GET_META_DATA).applicationInfo;
            } catch (PackageManager.NameNotFoundException e) {
                targetInfo = null;
            }
            return targetInfo;
        }

        public IconState setState() {
            if (state != IconState.HEADER) {
                try {
                    PackageInfo info = packageManager.getPackageInfo(iconPkg, PackageManager.GET_META_DATA);
                    state = IconState.INSTALLED;
                } catch (PackageManager.NameNotFoundException e) {
                    if (false) {
                        state = IconState.NOT_RELEASED;
                    } else {
                        state = IconState.NOT_INSTALLED;
                    }
                }
            }
            return state;
        }
    }



    public void updateHiddenPositions() {
        iconList = sortIconList(iconList);
        hiddenPositions = new ArrayList<>();
        for (int i = 0; i < iconList.size(); i++) {
            IconState state = iconList.get(i).setState();
            if (state != IconState.INSTALLED) {
                hiddenPositions.add(i);
            }
        }
        notifyDataSetChanged();
    }



//    private boolean isPackageInstalled(String targetPackage) {
//        try {
//            PackageInfo info=packageManager.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
//        } catch (PackageManager.NameNotFoundException e) {
//            return false;
//        }
//        return true;
//    }

//    private PackageInfo getPackageInfo(String targetPackage) {
//        try {
//            return packageManager.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
//        } catch (PackageManager.NameNotFoundException e) {
//            return null;
//        }
//    }

    public void setShowAll(Boolean showAll) {
        this.showAll = showAll;
        notifyDataSetChanged();
    }

    public String updateTarget(String targetPkg) {
        IconInfo item = getItem(currentIconPos);
        item.setTarget(targetPkg);
        currentIconPos = -1;
        notifyDataSetChanged();
        return item.iconPkg;
    }

//    public String updateIconPackageMap(String targetPkg) {
//        String iconPkg = getItem(currentIconPos);
//        iconTargetMap.put(iconPkg, targetPkg);
//        currentIconPos = -1;
//        notifyDataSetChanged();
//        return iconPkg;
//    }

    @Override
    public int getItemCount() {
        if (showAll) {
            return iconList.size();
        } else {
            return iconList.size() - hiddenPositions.size();
        }
    }

    public IconInfo getItem(int position) {
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
        IconInfo item = getItem(position);



        if (item.state == IconState.HEADER) {
            holder.icon.setVisibility(View.GONE);
            holder.arrow.setVisibility(View.GONE);
            holder.target.setVisibility(View.GONE);
            holder.text.setGravity(Gravity.CENTER | Gravity.START);
            holder.text.setTypeface(Typeface.DEFAULT_BOLD);
            holder.text.setText(item.headerTitle);
            holder.text.setTextColor(Color.WHITE);
            holder.text.setClickable(false);
            holder.text.setFocusable(false);
        } else {
            holder.icon.setVisibility(View.VISIBLE);
            holder.text.setGravity(Gravity.CENTER);
            holder.text.setTypeface(Typeface.DEFAULT);
            holder.text.setClickable(true);
            holder.text.setFocusable(true);

            String iconPkg = item.iconPkg;
            if (iconPkg != null) {
                int drawableId = getDrawableId(iconPkg);
                holder.icon.setImageDrawable(context.getResources().getDrawable(drawableId));
            }
            holder.text.setTextColor(Color.LTGRAY);

            if (item.state == IconState.INSTALLED) {
                String targetPkg = item.targetPkg;
                ApplicationInfo targetInfo = item.targetInfo;
                if (targetInfo != null) {
                    holder.target.setImageDrawable(packageManager.getApplicationIcon(targetInfo));
                    holder.arrow.setVisibility(View.VISIBLE);
                    holder.target.setVisibility(View.VISIBLE);
                    holder.text.setText(getPackageLabel(targetInfo));
                } else {
                    holder.arrow.setVisibility(View.GONE);
                    holder.target.setVisibility(View.GONE);
                    holder.text.setText("");
                }

                holder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentIconPos = (Integer) v.getTag();
                        Intent i = new Intent(context, TargetActivity.class);
                        context.startActivityForResult(i, 1);
                    }
                });
            } else {
                holder.arrow.setVisibility(View.GONE);
                holder.target.setVisibility(View.GONE);
                holder.text.setText("");
                if (item.state == IconState.NOT_RELEASED) { // NOT RELEASED
                    holder.text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                } else { // NOT INSTALLED
                    holder.text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position=(Integer)v.getTag();
                            String targetPkg = getItem(position).iconPkg;
                            Bundle bundle = new Bundle();
                            bundle.putString("iconPkg", targetPkg);

                            Intent i = new Intent(context, IconInstallActivity.class);
                            i.putExtras(bundle);
                            context.startActivity(i);
                        }
                    });
                }

            }
        }
        holder.text.setTag(position); // store position for OnClick callback
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
        TextView text;

        public CustomViewHolder(View view) {
            super(view);
            this.listItem = (RelativeLayout) view.findViewById(R.id.listItem);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            this.arrow = (ImageView) view.findViewById(R.id.arrow);
            this.target = (ImageView) view.findViewById(R.id.target);
            this.text = (TextView) view.findViewById(R.id.text);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_icon, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    public String getPackageLabel(ApplicationInfo pkgInfo) {
        try {
            return (String) packageManager.getApplicationLabel(pkgInfo);
        } catch (Exception e) {
            return "";
        }
    }


}
