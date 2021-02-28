package com.lookcos.hermit;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
    /**
     * 获取某个目录下最新文件
     *
     * @param path
     * @return
     */
    public static File getLastFile(String path) {
        List<File> list = getFiles(path, new ArrayList<>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, (file, newFile) -> {
                if (file.lastModified() < newFile.lastModified()) {
                    return -1;
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    return 1;
                }
            });
        }
        return list.get(list.size() - 1);
    }
    /**
     *
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
}

class SystemUtil {

    // 获取当前手机系统语言。
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    // 获取当前手机系统版本号
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    // 获取手机型号
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    // 获取手机厂商
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
    // 获取总内存大小
    public static String getTotalRAM() {
        long size = 0;
        Context context = MainActivity.getContext();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        size = outInfo.totalMem;
        return Formatter.formatFileSize(context, size);
    }
    // 获取可用内存大小
    public static String getAvailableRAM() {
        Context context = MainActivity.getContext();
        long size = 0;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        size = outInfo.availMem;

        return Formatter.formatFileSize(context, size);
    }

    // 获取存储相关信息
    public static float[] getSDCardMemory() {
        float[] sdCardInfo=new float[2];
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            float bSize = sf.getBlockSize();
            float bCount = sf.getBlockCount();
            float availBlocks = sf.getAvailableBlocks();
            sdCardInfo[0] = bSize * bCount;//总大小
            sdCardInfo[1] = bSize * availBlocks;//可用大小
        }
        return sdCardInfo;
    }
    // 获取CPU 最大频率
    public static String getMaxCpuFreq() {
        String result = "0";
        ProcessBuilder cmd;
        try {
            String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "0";
        }
        return result.trim();
    }
    // 获取CPU最小频率（单位KHZ）
    public static String getMinCpuFreq() {
        String result = "0";
        ProcessBuilder cmd;
        try {
            String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "0";
        }
        return result.trim();
    }
    // 实时获取CPU当前频率（单位KHZ）
    public static String getCurCpuFreq() {
        String result = "0";
        try {
            FileReader fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    // 获取CPU名字
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

