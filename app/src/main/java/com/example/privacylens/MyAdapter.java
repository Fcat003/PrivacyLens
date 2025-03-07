package com.example.privacylens;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<AppInfo> appList;

    public MyAdapter(Context context, List<AppInfo> appList) {
        this.context = context;
        this.appList = appList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AppInfo appInfo = appList.get(position);
        holder.tvAppName.setText(appInfo.getAppName());
        holder.tvPackageName.setText(appInfo.getPackageName());

        // 通过 PackageManager 获取应用图标
        PackageManager pm = context.getPackageManager();
        try {
            Drawable icon = pm.getApplicationIcon(appInfo.getPackageName());
            holder.ivAppIcon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            // 如果找不到图标，设置默认图标
            holder.ivAppIcon.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // 设置点击事件，传递包名给 DetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("packageName", appInfo.getPackageName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppName, tvPackageName;
        ImageView ivAppIcon, ivArrow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppName = itemView.findViewById(R.id.tvAppName);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
            ivArrow = itemView.findViewById(R.id.ivArrow);
        }
    }
}
