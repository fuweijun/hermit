package com.lookcos.hermit;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.accessibilityservice.GestureDescription;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.accessibilityservice.AccessibilityService;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;

import static android.view.Display.DEFAULT_DISPLAY;

public class AccessbilityCore extends AccessibilityService {
    public static String TAG = "HermitOut";
    public static AccessbilityCore aServer;
    private static AccessibilityEvent mAccessibilityEvent = null;
    private static AccessibilityService mAccessibilityService = null;
    public static AccessibilityNodeInfo activeNodeInfo = null;
    public static List<AccessibilityNodeInfo> listNodeInfo = new ArrayList<AccessibilityNodeInfo>();
    String regexId = ".*boundsInParent: Rect(.*?); boundsInScreen: Rect(.*?); .*";
    Pattern rID = Pattern.compile(regexId);

    JSONArray nodes = new JSONArray();
    public static JSONArray newNodes = new JSONArray();


    //初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        aServer = this;
    }

    @Override
    public void onInterrupt() {
        aServer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        aServer = null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        AccessbilityCore.setAccessibilityService(this);
    }
    /**
     * 设置数据
     *
     * @param service
     * @param
     */
    public static void setAccessibilityService(AccessibilityService service) {
        synchronized (AccessbilityCore.class)
        {
            if (service != null && mAccessibilityService == null)
            {
                mAccessibilityService = service;
            }
        }
    }
    public static AccessibilityService getmAccessibilityService() {
        return mAccessibilityService;
    }

    public static void performAction(int action){
        mAccessibilityService.performGlobalAction(action);
        return ;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void ScreenShotByAB(){

        mAccessibilityService.takeScreenshot(DEFAULT_DISPLAY, MainActivity.getContext().getMainExecutor(), new TakeScreenshotCallback() {
            @Override
            public void onSuccess(@NonNull ScreenshotResult screenshot) {
                Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshot.getHardwareBuffer(), screenshot.getColorSpace());
                File file = new File("/storage/emulated/0/Pictures", "HermitScreenShot.jpg");
                try {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int errorCode) {
            }
        });
    }

    /**
     * 模拟点击某个节点
     *
     * @param nodeInfo nodeInfo
     */
    public static boolean performViewClick(AccessibilityNodeInfo nodeInfo) {
        boolean res = false;
        if (nodeInfo == null)
        {
            return res;
        }
        while (nodeInfo != null)
        {
            if (nodeInfo.isClickable())
            {
                res = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
        return res;
    }

    //通过坐标点击具体坐标
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean clickByCoordinate(int x, int y) {
        AccessibilityService service = mAccessibilityService;
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 200)).build();
        return service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
    }

    // 根据坐标来滑动
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean swipeByCoordinate(int x0, int y0, int x1, int y1, int ms) {
        AccessibilityService service = mAccessibilityService;
        Path path = new Path();
        path.moveTo(x0, y0);
        path.lineTo(x1, y1);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 0, ms)).build();
        return service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
    }

    // 统计 text或id的控件数量
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static int countAttrNum(String attr, String val){
        AccessibilityNodeInfo accessibilityNodeInfo = activeNodeInfo;
        List<AccessibilityNodeInfo> nodeInfoList = null;
        if (attr.equals("text")){
            nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(val);
        }else {
            nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(val);
        }
        return nodeInfoList.size();
    }

    /**
     * 根据text查找并点击该节点
     *
     * @param text
     */
    public static boolean clickTextViewByText(String text, int index) {
        boolean res = false;
        AccessibilityNodeInfo accessibilityNodeInfo = activeNodeInfo;
        if (accessibilityNodeInfo == null)
        {
            return res;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            if (index < 999){
                return performViewClick(nodeInfoList.get(index));
            }

            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    res = performViewClick(nodeInfo);
                    break;
                }
            }
        }
        return res;
    }

    /**
     * 根据Id查找并点击该节点
     *
     * @param id
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean clickTextViewByID(String id, int index) {
        boolean res = false;
        AccessibilityNodeInfo accessibilityNodeInfo = activeNodeInfo;
        if (accessibilityNodeInfo == null)
        {
            return res;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            if (index < 999){
                return performViewClick(nodeInfoList.get(index));
            }
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    res = performViewClick(nodeInfo);
                    break;
                }
            }
        }
        return res;
    }
    /**
     * 根据描述查找控件
     *
     * @param des
     * @return
     */
    public static boolean clickViewByDesc(String des) {
        boolean res = false;
        if (des == null || "".equals(des))
        {
            return res;
        }
        List<AccessibilityNodeInfo> lists = listNodeInfo;
        for (AccessibilityNodeInfo node : lists)
        {
            CharSequence desc = node.getContentDescription();
            if (desc != null && des.equals(desc.toString()))
            {
                res = performViewClick(node);
                break;
            }
        }
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
        activeNodeInfo  = rowNode;
        if (rowNode == null) {
            Log.i(TAG, "noteInfo is　null");
            return;
        } else {
            // 先清空数据
            listNodeInfo.clear();
            nodes.removeAll(nodes);
            // 递归遍历添加数据
            try {
                recycle(rowNode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NodeInfo ni = new NodeInfo();
            ni.changeNodes(nodes);
        }
    }
    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    public static AccessibilityNodeInfo findViewByText(String text) {
        AccessibilityNodeInfo accessibilityNodeInfo = activeNodeInfo;
        if (accessibilityNodeInfo == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null && nodeInfo.getText() != null && nodeInfo.getText().toString().equals(text))
                {
                    return nodeInfo;


                }
            }
        }
        return null;
    }
    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo findViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = activeNodeInfo;
        if (accessibilityNodeInfo == null)
        {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList)
            {
                if (nodeInfo != null)
                {
                    return nodeInfo;
                }
            }
        }
        return null;
    }
    /**
     * 根据描述查找控件
     *
     * @param des
     * @return
     */
    public static AccessibilityNodeInfo findViewByDes(String des) {
        if (des == null || "".equals(des))
        {
            return null;
        }
        List<AccessibilityNodeInfo> lists = listNodeInfo;

        for (AccessibilityNodeInfo node : lists)
        {
            CharSequence desc = node.getContentDescription();
            if (desc != null && des.equals(desc.toString()))
            {
                return node;
            }
        }
        return null;
    }
    /*
    * 判断辅助功能是否启动
    * */
    public static boolean isStart() {
        return aServer != null;
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public static boolean inputTextByNode(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);

            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

        } else
        {
            MainActivity.setCliBoardText(text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void recycle(AccessibilityNodeInfo info) throws JSONException {
        if (info.getChildCount() == 0) {
            JSONObject infoOne = new JSONObject();
            infoOne.put("resource-id", info.getViewIdResourceName());
            infoOne.put("class", info.getClassName());
            infoOne.put("package", info.getPackageName());
            infoOne.put("text", info.getText());
            infoOne.put("content-desc", info.getContentDescription());
            infoOne.put("checked", info.isChecked());
            infoOne.put("clickable", info.isClickable());
            infoOne.put("scrollable", info.isScrollable());
            Matcher m = rID.matcher(info.toString());
            // 用正则直接匹配得到bounds
            if(m.find()){
                infoOne.put("boundsInParent", m.group(1));
                infoOne.put("bounds", m.group(2));
            }
            nodes.add(infoOne);
            listNodeInfo.add(info);
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle(info.getChild(i));
                }
            }
        }
    }
}

class NodeInfo{
    public static JSONArray nodes = new JSONArray();
    public void changeNodes(JSONArray newNodes){
        this.nodes = newNodes;
    }
    public JSONArray getNodes(){
        return nodes;
    }
}