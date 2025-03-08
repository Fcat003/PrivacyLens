package com.example.privacylens;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAppList;
    private MyAdapter adapter;
    private FloatingActionButton fab;
    private static final String TAG = "MainActivity";
    private ActivityResultLauncher<Intent> filePickerLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 处理系统窗口内边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 注册文件选择器结果回调
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            // 从 data 中获取文件 URI
                            android.net.Uri uri = data.getData();
                            try {
                                String newJson = readTextFromUri(uri);
                                if (validateJson(newJson)) {
                                    saveJsonToInternalStorage(newJson);
                                    Toast.makeText(this, "文件更新成功", Toast.LENGTH_SHORT).show();
                                    recreate();
                                } else {
                                    Toast.makeText(this, "文件格式不符合要求", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "读取文件失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        // 初始化 RecyclerView
        rvAppList = findViewById(R.id.rvAppList);
        rvAppList.setLayoutManager(new LinearLayoutManager(this));

        // 读取 JSON 数据和过滤出已安装的应用
        List<AppInfo> appList = loadAppsFromJson();
        if (appList.isEmpty()) {
            Toast.makeText(this, "没有符合条件的应用", Toast.LENGTH_SHORT).show();
        }

        // 创建适配器并设置给 RecyclerView
        adapter = new MyAdapter(this, appList);
        rvAppList.setAdapter(adapter);

        // 初始化 FloatingActionButton 点击事件
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("选择新的配置文件")
                    .setMessage("选择新的配置文件(json)，更新数据")
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("选择文件", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/json");  // 或 "*/*"
                        filePickerLauncher.launch(intent);
                    })
                    .show();
        });
    }

    private String readTextFromUri(android.net.Uri uri) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private boolean validateJson(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            if (jsonArray.length() == 0) return false;
            JSONObject firstObj = jsonArray.getJSONObject(0);
            return firstObj.has("packageName") && firstObj.has("appName") &&
                    firstObj.has("privacyCount_app") && firstObj.has("privacyCount_miniApp") &&
                    firstObj.has("score") && firstObj.has("actions") && firstObj.has("actionCount_app") && firstObj.has("actionCount_miniApp");
        } catch (JSONException e) {
            return false;
        }
    }

    private void saveJsonToInternalStorage(String json) {
        try (java.io.FileOutputStream fos = openFileOutput("data.json", MODE_PRIVATE)) {
            fos.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 从 assets/data.json 读取数据，并筛选出手机上已安装且在 JSON 中存在的应用
     */
    private List<AppInfo> loadAppsFromJson() {
        List<AppInfo> list = new ArrayList<>();
        String jsonStr = readJsonFromFile("data.json");
        if (jsonStr == null) {
            jsonStr = readJsonFromAssets("data.json");
        }
        if (jsonStr == null) return list;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            PackageManager pm = getPackageManager();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String pkg = obj.getString("packageName");
                try {
                    pm.getPackageInfo(pkg, 0);
                    String appName = obj.getString("appName");
                    AppInfo info = new AppInfo(appName, pkg);
                    list.add(info);
                } catch (PackageManager.NameNotFoundException e) {
                    // 跳过未安装的应用
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON解析错误", e);
        }
        return list;
    }

    private String readJsonFromFile(String fileName) {
        try (java.io.FileInputStream fis = openFileInput(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }



    /**
     * 从 assets 中读取 JSON 文件
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
            Log.e(TAG, "读取 JSON 文件失败", e);
        }
        return null;
    }
}
