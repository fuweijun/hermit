
### 前言  
restful是一套HTTP API标准，也是Hermit所追求的。  
一般而言，接口返回均符合如下格式：

```json
{'code': 0, 'msg': 'ok', 'data': '[]'}
```
code 0: 正常 ，负数： 异常；msg: 返回的消息  ；data: 返回的数据  

### 一、操作类 API 一览
`x: int` 表示，参数名为`x`，类型为`int`  

|  路径   | 方法|参数  | 基于|说明|
|  ----  | ----  | ---- |---- | ---|
| /click  | GET |x: int, y: int|无障碍|点击坐标(x, y)|
| /swipe  | GET |x1: int, y1: int, x2: int, y2: int|无障碍|滑动，从坐标(x1, y1)滑动至(x2, y2)|
| /click/text/{val}  | GET |val: string|无障碍|点击 text=val的控件|  
| /click/text/{val}/{index}  | GET |val: string, index: int|无障碍|点击 第index-1个 text=val的控件|  
| /click/text/{val}/_count  | GET |val: string|无障碍|统计 text=val的控件数量| 
| /click/id/{val}  | GET |val: string|无障碍|点击 resource-id=val的控件|  
| /click/id/{val}/{index}  | GET |val: string, index: int|无障碍|点击 第index-1个 resource-id=val的控件|  
| /click/id/{val}/_count  | GET |val: string|无障碍|统计 resource-id=val的控件数量|
| /click/desc/{desc}  | GET |desc: string|无障碍|点击 content-desc=val的控件|  
| /image/screen  | GET |null|无障碍|获取当前屏幕截图|
| /shell/image/screen  | GET |null|root|获取当前屏幕截图|
| /shell/tap  | GET |x: int, y: int|root|点击坐标(x, y)|
| /shell/swipe  | GET |x1: int, y1: int, x2: int, y2: int|root|滑动，从坐标(x1, y1)滑动至(x2, y2)|
| /check/root  | GET |null|root|检查root权限|
| /shell/longtap  | GET |x: int, y: int, duration：int|root|长按坐标(x, y), duration: 毫秒|
| /shell/keyevent  | GET |keycode: int|root|发送keyevent|
| /shell/start  | GET |packageName: string|root|根据包名启动APP|


#### 二、模拟输入功能  
|  路径   | 方法|参数  | 基于|
|  ----  | ----  | ---- |---- | 
| /input  | GET |by: string, obj: string, text: string|无障碍|
> 说明：  
by：通过何种方式选择输入框控件,可选（text,id,desc），  
obj：by方式对应的值，  
text：要输入的内容  

#### 三、无障碍九大全局功能  
|  路径   | 方法|参数  | 基于|
|  ----  | ----  | ---- |---- | 
| /action/{key} | GET |key: int|无障碍|

> 说明：[1-9] 也即1至9分别对应（返回、按下home键、最近任务、显示通知、快速设置、长按电源键、分屏、锁屏、截屏）


#### 四、获取与设置剪切板内容   
|  路径   | 方法|参数  | 基于|说明|
|  ----  | ----  | ---- |---- | ---|
| /data/cliBoard  | GET |null|Android|获取剪切板 文本内容|  
| /data/cliBoard  | PUT |content：string|Android|设置剪切板内容|  


#### 五、获取设备信息类  
##### 1. 运行状态
|  路径   | 方法|参数  | 基于|说明|
|  ----  | ----  | ---- |---- | ---- | 
| /  | GET |null|self|查看软件运行状态|
> 返回 {"code":0,"msg":"Hermit is ok!"}

##### 2. 屏幕长宽信息
|  路径   | 方法|参数  | 基于|
|  ----  | ----  | ---- |---- | 
| /data/screen | GET |null|Android|

> 返回 {"code":0,"data":{"height":2160,"width":1080},"msg":"ok"}

##### 3. 实时界面控件信息  
|  路径   | 方法|参数  | 基于|
|  ----  | ----  | ---- |---- | 
| /data/nodes | GET |null|无障碍|  

![1.png](https://lookcos.cn/usr/uploads/2021/02/1887138612.png)  
> 此数据有删减，仅作为演示。data为数组类型，其中元素对应分别控件信息，并非所有控件具有的属性数量都一致，例如大部分控件都没有`content-desc`属性。


##### 4. 设备相关信息  
|  路径   | 方法|参数  | 基于|
|  ----  | ----  | ---- |---- | 
| /data/device | GET |null|Android|  

![设备信息](https://lookcos.cn/usr/uploads/2021/02/1399563793.png)

> 一看便知，包括设备的CPU、内存、存储、系统等相关信息。

