package com.example.privacylens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitleBar;
    private TextView tvGreenTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        // 1. 初始化控件
//        ImageView ivBack = findViewById(R.id.ivBack);
        TextView tvAppInfo = findViewById(R.id.tvAppInfo);
//        tvTitleBar = findViewById(R.id.tvTitleBar);
        tvGreenTag = findViewById(R.id.tvGreenTag);

        // 2. 获取上一个页面传递的数据
        Intent intent = getIntent();
        String appName = intent.getStringExtra("appName");
        String packageName = intent.getStringExtra("packageName");
        if (appName != null && packageName != null) {
            // 拼接显示
            tvAppInfo.setText(appName + "\n" + packageName);
        }

        // 3. 设置返回按钮点击事件
//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 返回上一个页面
//                finish();
//            }
//        });

        // 4. 其他逻辑（如修改标题文字、表格内容等）
        // ...
    }
}
