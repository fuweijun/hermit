package com.lookcos.hermit;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.IOException;

/*
* 或许这里可以写上一句话
* */
public class Utils {
    private static String[] PERMISSIONS_CAMERA_AND_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public static int getActionType(String ac){
        int action = 0;
        if(ac.equals("back")){
            action = 1; // 按下系统返回键
        }else if (ac.equals("home")) {
            action = 2; // 按下home键
        }else if (ac.equals("recents")) {
            action = 3; // 最近任务
        }else if (ac.equals("noticefications")) {
            action = 4; // 显示通知
        }else if (ac.equals("quick_settings")) {
            action = 5; // 下拉栏快速设置
        }else if (ac.equals("power")) {
            action = 6; // 长按电源键
        }else if (ac.equals("split_screen")) {
            action = 7; // 分屏
        }else if (ac.equals("lock_screen")) {
            action = 8; // 锁屏(安卓9.0适用)
        }else if (ac.equals("screen_shot")) {
            action = 9; // 截屏(安卓9.0适用)
        }
        return action;
    }
    public static void tapTest() throws IOException {
        Runtime.getRuntime().exec("input tap 100 200");
    }
    // 申请存储相关权限
    public static boolean isGrantExternalRW(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int storagePermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            //检测是否有权限，如果没有权限，就需要申请
            if (storagePermission != PackageManager.PERMISSION_GRANTED ||
                    cameraPermission != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                activity.requestPermissions(PERMISSIONS_CAMERA_AND_STORAGE, requestCode);
                //返回false。说明没有授权
                return false;
            }
        }
        //说明已经授权
        return true;
    }

}
