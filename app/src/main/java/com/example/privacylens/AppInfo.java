package com.example.privacylens;

public class AppInfo {
    private String appName;

    private String packageName;

    private String recommend; //推荐使用的端：app端,小程序端

    private int[] privacyCount_app;
    private int[] privacyCount_miniApp;

    private int[] score;

    public AppInfo(String appName, String packageName){
        this.appName = appName;
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }
}
