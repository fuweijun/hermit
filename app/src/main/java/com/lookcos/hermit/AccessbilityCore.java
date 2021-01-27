package com.lookcos.hermit;


import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.accessibilityservice.AccessibilityService;


import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;

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

    /**
     * 根据text查找并点击该节点
     *
     * @param text
     */
    public static boolean clickTextViewByText(String text) {
        boolean res = false;
        AccessibilityNodeInfo accessibilityNodeInfo = activeNodeInfo;
        if (accessibilityNodeInfo == null)
        {
            return res;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
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
    public static boolean clickTextViewByID(String id) {
        boolean res = false;
        AccessibilityNodeInfo accessibilityNodeInfo = activeNodeInfo;
        if (accessibilityNodeInfo == null)
        {
            return res;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty())
        {
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