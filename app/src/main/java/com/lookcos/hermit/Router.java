package com.lookcos.hermit;


import android.os.Build;
import android.os.Environment;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;

import com.yanzhenjie.andserver.annotation.Controller;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PutMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.framework.website.AssetsWebsite;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
public class Router {
    /**
     * 首页测试
     */
    @GetMapping("/")
    String index() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "Hermit is ok!");
        return result.toString();
    }
    /**
    * 获取当前布局
    */
    @GetMapping("/data/nodes")
    String infoNodes() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "ok!");

        NodeInfo ni = new NodeInfo();
        result.put("count", ni.getNodes().size());
        result.put("data", ni.getNodes());
        //result.put("data", ni.getNodes());
        return result.toJSONString();
    }
    /*
    * 检查是否有root权限
    * */
    @GetMapping("/check/root")
    String isRoot() throws JSONException {
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", ShellUtils.checkRootPermission());
        return result.toString();
    }
    /*
    * 查看手机端安装的所有app包名:
    * */
    @GetMapping("/data/packages")
    String infoPackages() throws JSONException {
        JSONObject result = new JSONObject();
        ShellUtils.CommandResult shellResult = ShellUtils.execCommand("pm list packages", false);
        if(shellResult.result==0){
            result.put("code", 0);
            result.put("msg", "ok");
            ArrayList<String> packages = new ArrayList<String>(Arrays.asList(shellResult.successMsg.split("package:")));
            ArrayList<String> list2 = new ArrayList<String>();
            list2.add(packages.get(0));
            packages.removeAll(list2);
            result.put("count", packages.size());
            result.put("data", packages);
        }else {
            result.put("code", -1);
            result.put("msg", "error");
            result.put("data", "[]");
        }
        return result.toString();
    }
    @GetMapping("/shell/start")
    String shellStart(@RequestParam("packageName") String packageName) throws JSONException {
        /**
         * 启动某个app
         * @param: packageName 也即是包名
         * */
        ShellUtils.checkRootPermission();
        String command = "monkey -p " + packageName + " -v -v -v 1";
        ShellUtils.CommandResult shellResult = ShellUtils.execCommand(command, true);
        JSONObject result = new JSONObject();
        if(shellResult.result==0){
            result.put("code", 0);
            result.put("msg", "ok");
        }else {
            result.put("code", -1);
            result.put("msg", "请检查root权限授予情况");
        }
        return result.toString();
    }
    @GetMapping("/shell/keyevent")
    String inputKeyEvent(@RequestParam("keycode") String keycode) throws JSONException {
        String command = "input keyevent "+ keycode;
        ShellUtils.CommandResult shellResult = ShellUtils.execCommand(command, true);
        JSONObject result = new JSONObject();
        if(shellResult.result==0){
            result.put("code", 0);
            result.put("msg", "ok");
        }else {
            result.put("code", -1);
            result.put("msg", shellResult.errorMsg);
        }
        return result.toString();
    }

    @GetMapping("/shell/tap")
    String inputTap(@RequestParam("x") String x, @RequestParam("y") String y) throws JSONException {
        /*
        * 点击操作
        * @param：x,y坐标值
        * */
        String command = "input tap " + x + " " + y;
        ShellUtils.CommandResult shellResult = ShellUtils.execCommand(command, true);
        JSONObject result = new JSONObject();
        if(shellResult.result==0){
            result.put("code", 0);
            result.put("msg", "ok");
        }else {
            result.put("code", -1);
            result.put("msg", shellResult.errorMsg);
        }
        return result.toString();
    }
    
    @GetMapping("/shell/longtap")
    String inputTap(@RequestParam("x") String x, @RequestParam("y") String y, @RequestParam("duration") String duration) throws JSONException {
        /*
        * 长按操作
        * @param：x,y坐标值
        * @param: duration 长按持续时间 单位ms
        * */
        String command = "input swipe " + x + " " + y + " " + x + " " + y + " " + duration;
        ShellUtils.CommandResult shellResult = ShellUtils.execCommand(command, true);
        JSONObject result = new JSONObject();
        if(shellResult.result==0){
            result.put("code", 0);
            result.put("msg", "ok");
        }else {
            result.put("code", -1);
            result.put("msg", shellResult.errorMsg);
        }
        return result.toString();
    }
    
    @GetMapping("/shell/swipe")
    String inputSwipe(@RequestParam("x1") String x1,
                      @RequestParam("y1") String y1,
                      @RequestParam("x2") String x2,
                      @RequestParam("y2") String y2) throws JSONException {
        /*
         * 滑动操作
         * @param：从（x1,y1）滑动到（x2, y2）
         * */
        String command = "input swipe "+ x1 + " " + y1 + " " + x2 + " " + y2;
        ShellUtils.CommandResult shellResult = ShellUtils.execCommand(command, true);
        JSONObject result = new JSONObject();
        if(shellResult.result==0){
            result.put("code", 0);
            result.put("msg", "ok");
        }else {
            result.put("code", -1);
            result.put("msg", shellResult.errorMsg);
        }
        return result.toString();
    }

    @GetMapping("/data/cliBoard")
    String infoCliBoard() throws JSONException {
        JSONObject result = new JSONObject();

        String rs = MainActivity.getCliBoardText();
        if(rs != null){
            result.put("code", 0);
            result.put("msg", "ok");
            result.put("data", rs);
        }else {
            result.put("code", -1);
            result.put("msg", "可能是剪贴板暂时没有内容");
        }
        return result.toString();
    }

    @PutMapping("/data/cliBoard")
    String setCliBoard(@RequestParam("content") String content) throws JSONException{
        MainActivity.setCliBoardText(content);
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg","ok");
        result.put("data", content);
        return result.toString();
    }

    @GetMapping("/click/text")
    String clickByText(@RequestParam("obj") String textName) throws JSONException {
        JSONObject result = new JSONObject();
        boolean r = AccessbilityCore.clickTextViewByText(textName);
        if(r){
            result.put("code", 0);
            result.put("msg", "ok");
        }
        else {
            result.put("code", -1);
            result.put("msg", "没有找到text为：" + textName + "的可点击对象");
        }
        return result.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @GetMapping("/click/id")
    String clickById(@RequestParam("obj") String viewID) throws JSONException {
        JSONObject result = new JSONObject();
        boolean r = AccessbilityCore.clickTextViewByID(viewID);
        if(r){
            result.put("code", 0);
            result.put("msg", "ok");
        }
        else {
            result.put("code", -1);
            result.put("msg", "没有找到id为：" + viewID + "的可点击对象");
        }
        return result.toString();
    }
    @GetMapping("/click/desc")
    String clickByDesc(@RequestParam("obj") String desc) throws JSONException {
        JSONObject result = new JSONObject();
        boolean r = AccessbilityCore.clickViewByDesc(desc);
        if(r){
            result.put("code", 0);
            result.put("msg", "ok");
        }
        else {
            result.put("code", -1);
            result.put("msg", "没有找到content-desc为：" + desc + "的可点击对象");
        }
        return result.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @GetMapping("/action")
    String perAction(@RequestParam("obj") String obj) throws JSONException {
        JSONObject result = new JSONObject();
        int action = Utils.getActionType(obj);

        if (action != 0 && AccessbilityCore.getmAccessibilityService() != null){
            result.put("code", 0);
            result.put("msg", "ok");
            AccessbilityCore.performAction(action);
        } else {
            result.put("code", -1);
            result.put("msg", "操作失败，请检查无障碍权限是否开启，指令是否正确");
        }
        return result.toString();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @GetMapping("/input")
    String inputById(@RequestParam("by") String by,
                     @RequestParam("obj") String obj,
                     @RequestParam("text") String text) throws JSONException {
        JSONObject result = new JSONObject();
        AccessibilityNodeInfo aNode = null;
        if(by.equals("id")){
            aNode = AccessbilityCore.findViewByID(obj);
        }else if(by.equals("text")){
            aNode = AccessbilityCore.findViewByText(obj);
        }else if(by.equals("desc")){
            aNode = AccessbilityCore.findViewByDes(obj);
        }else {
            result.put("code", -1);
            result.put("msg", "请从id、text和desc中选一个");
            return result.toJSONString();
        }

        AccessbilityCore.inputTextByNode(aNode, text);
        result.put("code", 0);
        result.put("msg", "操作成功");
        return result.toString();
    }

    @GetMapping("/data/screen")
    String screenInfo() throws JSONException {
        JSONObject result = new JSONObject();
        int height = MainActivity.getScreenHeight();
        int width = MainActivity.getScreenWidth();
        JSONObject screenInfo = new JSONObject();
        screenInfo.put("height", height);
        screenInfo.put("width", width);
        result.put("code", 0);
        result.put("msg", "ok");
        result.put("data", screenInfo);
        return result.toString();
    }

    @GetMapping("/img/screen")
    public FileBody imgScreen() {
        ShellUtils.execCommand("screencap -p /sdcard/Pictures/hermit.png", true);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File("/storage/emulated/0/Pictures", "hermit.png");
        FileBody body = new FileBody(file);
        return body;
    }
    @GetMapping("/tap")
    public String info() throws IOException {
       Utils.tapTest();
       return "ok";
    }
}
@Controller
class PageController {

    @GetMapping(path = "/viewer")
    public String index() {
        // Equivalent to [return "/index"].
        return "forward:/index.html";
    }
}
