#### 简介

hermit，是一款支持多种编程语言使用的轻量级（目前仅2.78MB）安卓操作软件。支持快速的点击、滑动、读取与设置剪切板、模拟输入、寻找控件并点击，支持可视化布局范围分析。  

它是一个安卓软件，运行之后提供HTTP API，对API 的操作将转化为对手机的操作，具体请看:  

#### hermit的工作方式  

![](https://www.lookcos.cn/wp-content/uploads/2021/01/2021012804240032.png)

#### 简单的安装工作：  
1. 下载并安装hermit  
2. 打开hermit并根据提示给予相关权限
3. 使用`pyhermit`或其他语言直接对API进行操作即可。

运行端口: 9999

1. 局域网内设备可通过 内网ip:9999打开，进行相关操作。    
2. 模拟器用户，需要做一次端口映射，打开终端执行如下操作：   

```bash
# 通过adb 连接模拟器
adb connect 127.0.0.1:7555
# 将模拟器的9999端口转发到本机的 9999端口  
adb forward tcp:9999 tcp:9999
# 看到返回 9999， 就是成功了。这个时候，我们打开 127.0.0.1:9999 就能访问了
```

![](https://www.lookcos.cn/wp-content/uploads/2021/01/2021013111254293.png)

### 开始使用  

1. 如果你使用`python3`，我极力推荐你使用`pyhermit`,它是对hermit API的封装，安装即可使用，无需关心HTTP API。

   你可以从`pyhermit`的[Release](https://github.com/LookCos/hermit-py/releases)下载，亦可以点击这个 [国内链接下载](https://www.lookcos.cn/wp-ext/hermit.zip)

2. 若不幸的是，你不会使用`python`，那么也不要灰心，参考[HTTP API 详细说明](https://github.com/LookCos/hermit/wiki/HTTP-API-%E6%96%87%E6%A1%A3)，使用你擅长的语言，也能获得不错的使用体验。



#### 其他重要功能： 

目前还实现了布局范围可视化分析器，能帮助我们查看界面中的控件信息，便于点击等操作。 

**详情见Wiki**

![](https://www.lookcos.cn/wp-content/uploads/2021/01/2021013111483088.png)

#### 未来  

目前点击某个具体的坐标和滑动，需要root权限才能有较好的体验。  目前想到了一种无需root权限，也不需要adb的方案，正在开发，敬请期待，多谢支持。
