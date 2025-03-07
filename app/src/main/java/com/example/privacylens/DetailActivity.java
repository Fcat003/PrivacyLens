package com.example.privacylens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DetailActivity extends AppCompatActivity {

    private Context context;
    private static final String TAG = "DetailActivity";
    private TextView tvText; // 用于显示推荐信息
    private TextView tvAppInfo; // 用于显示应用名称和包名

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        // 修复：赋值 context
        context = this;

        tvText = findViewById(R.id.tvText);
        tvAppInfo = findViewById(R.id.tvAppInfo);

        // 获取传递过来的包名
        Intent intent = getIntent();
        String packageName = intent.getStringExtra("packageName");
        if (packageName != null) {
            // 从 JSON 文件中查找对应数据
            JSONObject appData = getDataForPackage(packageName);
            if (appData != null) {
                try {
                    String appName = appData.getString("appName");
                    JSONArray privacyCount_miniApp = appData.getJSONArray("privacyCount_miniApp");
                    JSONArray privacyCount_app = appData.getJSONArray("privacyCount_app");
                    setPrivacyCount(privacyCount_app, privacyCount_miniApp);
                    String recommend = appData.getString("recommend");
                    JSONArray score = appData.getJSONArray("score");
                    setScore(score);
                    // 显示推荐信息
                    String displayText = "本应用推荐使用  " + recommend;
                    tvText.setText(displayText);

                    // 获取应用图标并更新 UI
                    PackageManager pm = getPackageManager();
                    try {
                        Drawable icon = pm.getApplicationIcon(packageName);
                        setAppInfo(icon, appName, packageName);
                    } catch (PackageManager.NameNotFoundException e) {
                        // 如果找不到图标，设置默认图标
                        // 例如：setAppInfo(defaultIcon, appName, packageName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvText.setText("数据解析错误");
                }
            } else {
                tvText.setText("未找到对应数据");
            }
        }
    }

    /**
     * 从 assets/data.json 文件中读取数据，根据 packageName 查找对应应用数据
     */
    private JSONObject getDataForPackage(String packageName) {
        String jsonStr = readJsonFromAssets("data.json");
        if (jsonStr == null) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            // 遍历 JSON 数组，查找包名匹配的对象
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (packageName.equals(obj.getString("packageName"))) {
                    return obj;
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
        return null;
    }

    /**
     * 从 assets 文件夹中读取 JSON 文件内容
     */
    private String readJsonFromAssets(String fileName) {
        AssetManager assetManager = getAssets();
        StringBuilder sb = new StringBuilder();
        try (InputStream is = assetManager.open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file", e);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    private void setAppInfo(Drawable icon, String appName, String packageName) {
        ImageView ivIcon = findViewById(R.id.ivIcon);
        TextView tvAppInfo = findViewById(R.id.tvAppInfo);
        ivIcon.setImageDrawable(icon);
        tvAppInfo.setText(appName + "\n" + packageName);
    }

    private void setScore(JSONArray score) throws JSONException {
        TextView tvApp4 = findViewById(R.id.tvApp_4);
        TextView tvMiniApp4 = findViewById(R.id.tvMiniApp_4);
        StringBuilder sbApp = new StringBuilder();
        StringBuilder sbMiniApp = new StringBuilder();
        sbApp.append(score.getInt(0)).append("分");
        sbMiniApp.append(score.getInt(1)).append("分");
        tvApp4.setText(sbApp.toString());
        tvMiniApp4.setText(sbMiniApp.toString());
    }

    private void setPrivacyCount(JSONArray privacyCount_app, JSONArray privacyCount_miniApp) {
        // 获取控件引用
        TextView tvApp1 = findViewById(R.id.tvApp_1);
        TextView tvApp2 = findViewById(R.id.tvApp_2);
        TextView tvApp3 = findViewById(R.id.tvApp_3);
        TextView tvMiniApp1 = findViewById(R.id.tvMiniApp_1);
        TextView tvMiniApp2 = findViewById(R.id.tvMiniApp_2);
        TextView tvMiniApp3 = findViewById(R.id.tvMiniApp_3);

        try {
            // privacyCount_app：前4个数据到 tvApp_1
            StringBuilder sbApp1 = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                int value = privacyCount_app.getInt(i);
                sbApp1.append(value == 0 ? "-" : value + "个");
                if (i < 3) {
                    sbApp1.append("\n");
                }
            }
            tvApp1.setText(sbApp1.toString());

            // privacyCount_app：接下来6个数据到 tvApp_2
            StringBuilder sbApp2 = new StringBuilder();
            for (int i = 4; i < 10; i++) {
                int value = privacyCount_app.getInt(i);
                sbApp2.append(value == 0 ? "-" : value + "个");
                if (i < 9) {
                    sbApp2.append("\n");
                }
            }
            tvApp2.setText(sbApp2.toString());

            // privacyCount_app：接下来2个数据到 tvApp_3
            StringBuilder sbApp3 = new StringBuilder();
            for (int i = 10; i < 12; i++) {
                int value = privacyCount_app.getInt(i);
                sbApp3.append(value == 0 ? "-" : value + "个");
                if (i < 11) {
                    sbApp3.append("\n");
                }
            }
            tvApp3.setText(sbApp3.toString());

            // privacyCount_miniApp：前4个数据到 tvMiniApp_1
            StringBuilder sbMini1 = new StringBuilder();
            for (int i = 0; i < 4; i++) {
                int value = privacyCount_miniApp.getInt(i);
                sbMini1.append(value == 0 ? "-" : value + "个");
                if (i < 3) {
                    sbMini1.append("\n");
                }
            }
            tvMiniApp1.setText(sbMini1.toString());

            // privacyCount_miniApp：中间6个数据到 tvMiniApp_2
            StringBuilder sbMini2 = new StringBuilder();
            for (int i = 4; i < 10; i++) {
                int value = privacyCount_miniApp.getInt(i);
                sbMini2.append(value == 0 ? "-" : value + "个");
                if (i < 9) {
                    sbMini2.append("\n");
                }
            }
            tvMiniApp2.setText(sbMini2.toString());

            // privacyCount_miniApp：最后2个数据到 tvMiniApp_3
            StringBuilder sbMini3 = new StringBuilder();
            for (int i = 10; i < 12; i++) {
                int value = privacyCount_miniApp.getInt(i);
                sbMini3.append(value == 0 ? "-" : value + "个");
                if (i < 11) {
                    sbMini3.append("\n");
                }
            }
            tvMiniApp3.setText(sbMini3.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
