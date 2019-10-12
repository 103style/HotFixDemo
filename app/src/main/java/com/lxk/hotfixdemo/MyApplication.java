package com.lxk.hotfixdemo;

import android.content.Context;

import androidx.multidex.MultiDexApplication;


/**
 * @author https://github.com/103style
 * @date 2019/9/19 13:46
 */
public class MyApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        new FixDemo().loadFixedDex(base);
    }
}
