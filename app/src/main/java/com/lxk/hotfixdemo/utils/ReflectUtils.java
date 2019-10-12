package com.lxk.hotfixdemo.utils;

import java.lang.reflect.Field;

/**
 * @author https://github.com/103style
 * @date 2019/9/18 11:14
 */
public class ReflectUtils {

    private static final String TAG = "ReflectUtils";


    /**
     * 通过反射获取变量属性
     *
     * @param obj  类实例
     * @param name 变量名
     */
    public static Object getClassField(Object obj, String name) {
        LogUtils.d(TAG, "getClassField(Object obj, String name)");
        if (obj == null) {
            LogUtils.e(TAG, "obj is null ");
            return null;
        }
        LogUtils.d(TAG, "Object class name  = " + obj.getClass().getName() + ", name = " + name);
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取类实例的私有属性值
     *
     * @param obj   类实例
     * @param field 私有属性
     */
    public static Object getClassFieldValue(Object obj, Field field) {
        LogUtils.d(TAG, "getClassFieldValue(Object obj, Field field)");
        if (obj == null) {
            LogUtils.e(TAG, "obj is null ");
            return null;
        }
        LogUtils.d(TAG, "field name = " + field.getName() + ",Object class name  = " + obj.getClass().getName());
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改类实例的私有属性值
     *
     * @param obj   类实例
     * @param cl    类名
     * @param field 私有属性
     * @param value 新值
     */
    public static boolean setClassFiled(Object obj, Class cl, String field, Object value) {
        LogUtils.d(TAG, "setClassFiled(obj, cl, field, value)");
        try {
            Field localField = cl.getDeclaredField(field);
            localField.setAccessible(true);
            localField.set(obj, value);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;

    }


    /**
     * 通过反射获取类对应的私有变量
     *
     * @param obj  类实例对象
     * @param name 私有变量名
     */
    public static Object getDeclaredField(Object obj, String name) {
        LogUtils.d(TAG, "getDeclaredField(Object obj, String name)");
        return getDeclaredField(obj, obj.getClass(), name);
    }

    /**
     * 通过反射获取类对应的私有变量
     *
     * @param obj       类实例对象
     * @param className 要获取类变量的类名
     * @param name      私有变量名
     */
    public static Object getDeclaredField(Object obj, Class className, String name) {
        LogUtils.d(TAG, "getDeclaredField(Object obj, Class className, String name)");
        if (obj == null || className == null) {
            LogUtils.e(TAG, "obj or className is null");
            return null;
        }
        LogUtils.d(TAG, "className = " + className.getName() + ", name = " + name);
        Field field;
        try {
            field = className.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
