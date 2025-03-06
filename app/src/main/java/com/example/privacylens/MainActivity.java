package com.example.privacylens;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAppList;
    private MyAdapter adapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 开启沉浸式边缘模式
        // EdgeToEdge.enable(this); // 如果你使用该方法，确保已经导入相关依赖
        setContentView(R.layout.activity_main);

        // 处理系统窗口内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 RecyclerView
        rvAppList = findViewById(R.id.rvAppList);
        rvAppList.setLayoutManager(new LinearLayoutManager(this));

        // 准备数据，这里示例使用简单的字符串列表
        List<AppInfo> appList = Arrays.asList(new AppInfo("应用一","com.package.meituan"));

        // 创建适配器并设置给 RecyclerView
        adapter = new MyAdapter(this
                ,appList);
        rvAppList.setAdapter(adapter);

        // 初始化 FloatingActionButton 并设置点击事件
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            // 示例：点击悬浮按钮时弹出一个 Toast 消息
            Toast.makeText(MainActivity.this, "悬浮按钮被点击", Toast.LENGTH_SHORT).show();
            // 你也可以在这里弹出对话框或打开文件选择器
        });
    }
}
