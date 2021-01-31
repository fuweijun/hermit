package com.lookcos.hermit;


import java.io.IOException;

/*
* 或许这里可以写上一句话
* */
public class Utils {
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
}
