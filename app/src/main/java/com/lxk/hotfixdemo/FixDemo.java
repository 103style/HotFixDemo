package com.lxk.hotfixdemo;

import android.content.Context;

import com.lxk.hotfixdemo.test.MyConstants;
import com.lxk.hotfixdemo.utils.ReflectUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.HashSet;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

/**
 * @author https://github.com/103style
 * @date 2019/9/19 13:52
 */
public class FixDemo {
    public void loadFixedDex(Context context) {
        if (null == context) {
            return;
        }
        //遍历所有的修复的dex
        File fileDir = context.getDir(MyConstants.DEX_DIR, Context.MODE_PRIVATE);
        File[] listFiles = fileDir.listFiles();
        if (listFiles == null) {
            return;
        }

        HashSet<File> loadedDex = new HashSet<>();
        for (File file : listFiles) {
            if (file.getName().startsWith("classes") || file.getName().endsWith(".dex")) {
                //先将补丁文件放到一个集合里，然后再进行合并
                loadedDex.add(file);
            }
        }
        //dex合并之前的dex
        doDexInject(context, fileDir, loadedDex);
    }


    private void doDexInject(Context appContext, File filesDir, HashSet<File> loadedDex) {
        try {
            String filesDirPath = filesDir.getAbsolutePath() + File.separator + "opt_dex";
            File fopt = new File(filesDirPath);
            if (!fopt.exists()) {
                fopt.mkdir();
            }
            //1.加载应用程序的dex
            BaseDexClassLoader pathLoader = (BaseDexClassLoader) appContext.getClassLoader();
            for (File dex : loadedDex) {
                //2.加载指定的修复的dex文件
                DexClassLoader classLoader = new DexClassLoader(
                        dex.getAbsolutePath(),
                        fopt.getAbsolutePath(),
                        null,
                        pathLoader);
                //3.合并
                Object dexObj = getPathList(classLoader);
                Object pathObj = getPathList(pathLoader);
                Object mDexElementsList = getDexElements(dexObj);
                Object pathDexElementsList = getDexElements(pathObj);
                //将两个list合并为一个
                Object dexElements = combineArray(mDexElementsList, pathDexElementsList);
                //重写给PathList里面的Element[] dexElements赋值
                Object pathList = getPathList(pathLoader);
                ReflectUtils.setClassFiled(pathList, pathList.getClass(), "dexElements", dexElements);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getPathList(ClassLoader classLoader) {
        try {
            return ReflectUtils.getDeclaredField(classLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object getDexElements(Object obj) {
        return ReflectUtils.getClassField(obj, "dexElements");
    }

    /**
     * 合并数组
     */
    private Object combineArray(Object arrayLbs, Object arrayRhs) {
        Class localClass = arrayLbs.getClass().getComponentType();
        int i = Array.getLength(arrayLbs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLbs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }

}