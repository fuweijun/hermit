hermit，是一款支持多种语言的轻量级的安卓操作软件，支持快速的点击、滑动、读取与设置剪切板、模拟输入、寻找控件并点击等。  


### hermit的工作方式  
![](https://www.lookcos.cn/wp-content/uploads/2021/01/2021012804240032.png)

### 安装方法：  
下载 APK安装包并安装，启动应用并给予权限后，hermit会向外开放HTTP API。  

默认运行端口: 9999, 局域网内设备可直接打开 内网ip:9999，进行相关操作。    
模拟器用户，需要做一次端口映射，打开终端执行如下操作：   
```bash
# 通过adb 连接模拟器
adb connect 127.0.0.1:7555
# 将模拟器的9999端口转发到本机的 9999端口  
adb forward tcp:9999 tcp:9999
# 看到返回 9999， 就是成功了。
```

### 使用方法  
本项目为hermit app，基本安装后就结束了，如果你想通过此APP操作安卓手机，
方法有二： 
1. 通过 各个语言的hermit模块来操作， 这些模块是对hermit API的封装  
2. 使用您擅长的语言，直接对api操作  

**其中，我已经将hermit api做了对应python版本的封装，如果你会使用python语言，那么下面的API说明则可以忽视，直接移步至 **  
https://github.com/LookCos/hermit-py  **从 release中下载对应的python包，进行操作。**


# 具体的接口信息如下  
## 一、获取实时界面布局信息  

|路径|方式|是否需要root|说明|
|:-----  |:-----|-----  |-----  |
|/data/nodes |GET   |否|获取当前布局信息  |

##### 返回示例 

``` 
{
    "code":0,
    "count":1,
    "data":[
        {
            "bounds":"(27, 65 - 131, 111)",
            "boundsInParent":"(0, 0 - 104, 46)",
            "checked":false,
            "class":"android.widget.TextView",
            "clickable":false,
            "package":"com.lookcos.hermit",
            "scrollable":false,
			"resource-id":"android:id/statusBarBackground",
			"content-desc":"用户头像",
            "text":"Hermit"
        }
    ],
    "msg":"ok!"
}
```

##### 返回参数说明 

|参数名|类型|说明|
|:-----  |:-----|-----                           |
|code|int   |0:正常，其他为异常 |
|msg|str   |信息 |
|count|int   |节点数统计 |
|data|list   |节点信息列表 |

##### 备注 

- data的每个item中 resource-id、content-desc、text较为重要，可以作为点击来源。

## 二、点击与滑动  

- 无障碍的方式  

|路径|方式|是否需要root|说明|
|:-----  |:-----|-----  |-----  |
|/click/id |GET   |否|通过 resource-id点击  |
|/click/text |GET   |否|通过 text点击  |
|/click/desc |GET   |否|通过 desc点击  |
它们都需要一个参数`obj`，例如 `/click/text?obj=酷安`  

- root的方式操作  

|路径|方式|是否需要root|说明|
|:-----  |:-----|-----  |-----  |
|/shell/tap |GET   |是|通过 坐标点击  |
参数 `x`与`y`, 也即点击屏幕中 （x，y） 

|路径|方式|是否需要root|说明|
|:-----  |:-----|-----  |-----  |
|/shell/swipe|GET   |是|通过 坐标滑动  |
需要四个参数 `x1`, `y1`, `x2`, `y2`，对应的操作是从坐标 (x1, y1)滑动到(x2, y2)  

## 三、剪切板与模拟输入  

|路径|方式|是否需要root|说明|
|:-----  |:-----|-----  |-----  |
|/data/cliBoard|GET   |否|获取剪切板信息|  
|/data/cliBoard|PUT   |否|设置剪切板信息|  
|/input|GET   |否|模拟输入|  
其中用`put`方法设置剪切版内容时，参数为 `content`，对应为要设置的内容。
模拟输入需要三个参数

|字段|类型|空|默认|注释|
|:----    |:-------    |:--- |-- -|------      |
|by   |str     |否 |  |  通过何种方式选择输入框控件, 可选 text、id、desc      |
|obj |str |否 |    |   对应选择控件方式的对应值  |
|text |str |否   |    |   要设置的输入框内容   |

例如 /input?by=text&obj=搜索&text=张三 ，则会自动寻找`text=搜索`的控件，并在其中输入 `张三`

## 四、全局操作  

|路径|方式|是否需要root|
|:-----  |:-----|-----  |
|/action|GET   |否|
 参数名 `obj`， 

|参数值|操作|
|:----    |:-------    |
|back  |  按下返回键   |
| home|按下home键|
|recents | 显示最近任务|
|noticefications |显示通知栏|
| quick_settings|下拉栏快速设置|
| power|长按电源键|
| lock_screen|锁屏 (安卓9.0+ 适用)|
|screen_shot |截屏(安卓9.0+ 适用)|





### 五、一些有用的 api
- 检查root权限  

|路径|方式|说明|
|:-----  |:-----|-----  |-----  |
|/check/root |GET   |检查root权限  |
- 发送一个keyevent

|路径|方式|是否需要root|说明|
|:-----  |:-----|-----  |-----  |
| /shell/keyevent |GET   |是| 发送一个keyevent|
参数名称 `keycode`，对应的值值，例如 3对应 home键，4对应返回键，
其他的映射请自行搜索，关键词： Android keyevent

- 启动一个应用  

|路径|方式|是否需要root|
|:-----  |:-----|-----  |
|/shell/start|GET   |是|

参数 `packageName`， 必须是完整的包名, 如 com.tencent.mm

- 获取屏幕的长宽  
路径为： /data/screen  
- 获取已安装的包列表 
路径为：/data/packages，需要root权限
