package com.lxk.classloaderdemo.test;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashSet;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by cuixiaoxiao on 2018/5/8.
 */
public class FixDexUtils {
    private static HashSet<File> loadedDex = new HashSet<>();

    static {
        loadedDex.clear();
    }

    /**
     * 加载修复文件
     *
     * @param context
     */
    public static void loadFixedDex(Context context) {
        if (null == context) {
            return;
        }
        //遍历所有的修复的dex
        File fileDir = context.getDir(MyConstants.DEX_DIR, Context.MODE_PRIVATE);
        File[] listFiles = fileDir.listFiles();
        for (File file : listFiles) {
            if (file.getName().startsWith("classes") || file.getName().endsWith(".dex")) {
                loadedDex.add(file);//先将补丁文件放到一个集合里，然后再进行合并
            }
        }
        //dex合并之前的dex
        doDexInject(context, fileDir, loadedDex);
    }

    /**
     * 将项目的dex和已经修复的dex合并，并赋值给类加载器，进行热修复
     *
     * @param appContext
     * @param filesDir
     * @param loadedDex
     */
    private static void doDexInject(final Context appContext, File filesDir, HashSet<File> loadedDex) {
        try {
            String optiondexDir = filesDir.getAbsolutePath() + File.separator + "opt_dex";
            File fopt = new File(optiondexDir);
            if (!fopt.exists()) {
                fopt.mkdir();
            }
            //1.加载应用程序的dex
            PathClassLoader pathLoader = (PathClassLoader) appContext.getClassLoader();
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
                Object dexElements = conbineArray(mDexElementsList, pathDexElementsList);
                //重写给PathList里面的Element[] dexElements赋值
                Object pathList = getPathList(pathLoader);
                setFiled(pathList, pathList.getClass(), "dexElements", dexElements);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 得到类加载器的pathList
     *
     * @param baseDexClassLoader
     * @return
     * @throws Exception
     */
    private static Object getPathList(Object baseDexClassLoader) throws Exception {
        return getFiled(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    /**
     * 通过反射给指定类的指定属性赋值
     *
     * @param obj
     * @param cl
     * @param field
     * @param value
     * @throws Exception
     */
    private static void setFiled(Object obj, Class<?> cl, String field, Object value) throws Exception {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        localField.set(obj, value);
    }

    /**
     * 通过反射调用指定类的方法
     *
     * @param obj
     * @param cl
     * @param field
     * @return
     * @throws Exception
     */
    private static Object getFiled(Object obj, Class<?> cl, String field) throws Exception {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    /**
     * 得到dexElements
     *
     * @param obj
     * @return
     * @throws Exception
     */
    private static Object getDexElements(Object obj) throws Exception {
        return getFiled(obj, obj.getClass(), "dexElements");
    }

    /**
     * 合并两个数组
     *
     * @param arrayLbs
     * @param arrayRhs
     * @return
     */
    private static Object conbineArray(Object arrayLbs, Object arrayRhs) {
        Class<?> localClass = arrayLbs.getClass().getComponentType();
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