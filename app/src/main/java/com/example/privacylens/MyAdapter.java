package com.example.privacylens;

import android.content.Context;
import android.content.Intent;

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
    private List<AppInfo> appList; // 假设你有一个 AppInfo 类，包含 appName、packageName 等

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

        // 设置应用名称和包名
        holder.tvAppName.setText(appInfo.getAppName());
        holder.tvPackageName.setText(appInfo.getPackageName());

        // 监听箭头点击事件
        holder.itemView.setOnClickListener(v -> {
            // 跳转到 DetailActivity
            Intent intent = new Intent(context, DetailActivity.class);
            // 传递数据
            intent.putExtra("appName", appInfo.getAppName());
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

