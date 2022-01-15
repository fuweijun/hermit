[中文README](./README_CN.md)  

Doc: [https://www.lookcos.cn/docs/hermit](https://www.lookcos.cn/docs/hermit)

## Introduction

Hermit, a software for Android automation testing, runs on Android by receiving restful API requests, which are then translated into operations on the device.  
It supports a range of operations such as quick taps, swipes, reading and setting the clipboard (Chinese language support), simulating input, finding controls and clicking on them, and supports visual layout analysis.
Less than 3Mib in size and runs on port 9999 by default.

## Accessibility based features

- Coordinate click
- Coordinate swipe
- Clicking based on control ids (if there are multiple identical ids, click based on order)
- Click based on control text (as above, clickable in order)
- Click based on control content-desc
- View screenshots
- Analog input function (Chinese language support)
- Ten global functions (back, press home key, recent tasks, show notifications, quick settings, long press on power button, split screen, lock screen, screenshot)
- Real-time access to interface control details

## Root-based features

- Coordinate tapping
- Coordinate swipe
- Screenshot
- Request and determine root access
- Send any keyevent
- Long press on coordinates
- Launch app based on package name
- Get device information capability
- Get and set clipboard content (Chinese language support)
- Device screen length and width
- CPU max, min, real time frequency
- CPU name
- Total amount of memory, available
- Total amount of storage, amount available
- Name of all installed packages
- Device language, system version number, phone model, phone manufacturer

## Interactive layout visualisation

Similar to Android Studio's LayoutInspector, you can analyse the level and scope of the interface and get information about the controls on the interface to facilitate operations such as clicking and swiping.
Controls can be selected with the mouse (see LayoutInspector for details) to see the corresponding information:

- bounds (clickable range)
- boundsInParent
- checked
- class
- clickable
- package
- resource-id (control id)
- scrollable
- text (control text)
- content-desc (control description)  

![screencapture3.png](https://lookcos.cn/usr/uploads/2021/02/3890288493.png)

## How it works

![](https://www.lookcos.cn/usr/uploads/2021/01/2021012804240032.png)

### Caution

The diagram may be slightly over-drawn, but in short: Hermit, once installed and running, provides HTTP APIs which are translated into operations on the phone by calling the appropriate APIs. As a result, most languages can be supported. It is because it is based on the restful API that it is considerably more extensible, and a Python module based on its API has been produced to enable the ease of operation.
