# HotFixDemo
HotFix demo base on Classloader

[Demo.apk Download](https://github.com/103style/HotFixDemo/blob/master/apk/demo.apk)



>转载请以链接形式标明出处： 
本文出自:[**103style的博客**](http://blog.csdn.net/lxk_1993) 

---

### 效果图
![修复之前](https://github.com/103style/HotFixDemo/tree/master/pic/bug.png)

![修复之后](https://github.com/103style/HotFixDemo/tree/master/pic/fixed.png)

---


### 实现思路

主要实现思路主要是：
* 先编写一个有 bug 的程序， 运行安装到手机。

* 修正bug之后，重新 `rebuild`, 然后找到 `app - build - intermediates - dex - debug - mergeProjectDexDebug - out - classes.dex` 移动到 修复包 下载的目录 ， 这里放在 `assets` 目录下，并重命名 `classes.dex` 为 `classes2.dex` 。

*  然后点击程序上的 `Move Dex`, 将修正bug之后的dex包 移动到 `android/data/packagename/` 目录下，`在这里目录才有加载dex权限`。

* 然后重启程序，在继承自 `MultiDexApplication` 的 `Application` 中加载对应的 `dex` 文件，获取对应的`dexElements`，然后合并到应用的`dexElements`之前。


---
