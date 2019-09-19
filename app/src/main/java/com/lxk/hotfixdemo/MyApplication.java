package com.lxk.hotfixdemo;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

import com.lxk.hotfixdemo.test.FixDexUtils;

/**
 * @author https://github.com/103style
 * @date 2019/9/19 13:46
 */
public class MyApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
       FixDexUtils.loadFixedDex(base);
    }
}
