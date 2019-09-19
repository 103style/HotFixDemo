package com.lxk.hotfixdemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lxk.hotfixdemo.test.MyConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author https://github.com/103style
 * @date 2019/9/19 13:34
 */
public class MainActivity extends AppCompatActivity {


    private TextView bugTv;
    private int i = 10;
    private int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bugTv = findViewById(R.id.bug);

        bugTv.setText("Bug： " + i + " / " + a);

        findViewById(R.id.bug).setOnClickListener(v -> bugMethod());
        findViewById(R.id.move).setOnClickListener(v -> moveDex("classes2.dex"));
    }

    private void bugMethod() {
        Toast.makeText(this, "res = " + i / a, Toast.LENGTH_SHORT).show();
    }


    private void moveDex(String name) {
        //目录 data/data/packageName/odex
        File fileDir = getDir(MyConstants.DEX_DIR, Context.MODE_PRIVATE);
        AssetManager am = getResources().getAssets();
        try {
            InputStream is = am.open(name);
            String filePath = fileDir.getAbsolutePath() + File.separator + name;
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream os = new FileOutputStream(filePath);
            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.close();
            is.close();

            //粘贴完文件
            File f = new File(filePath);
            if (f.exists()) {
                //文件从sk卡赋值到应用运行目录下，成功则toast提示
                Toast.makeText(this, "dex移动成功,请重启应用", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
