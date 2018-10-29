package com.zzu.zk.zhiwen.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.app.ZhiWen;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.StorageUtils;

import java.util.Random;

public class Main extends AppCompatActivity {
    ImageView welcom_bg;
    public static  String file_path = null;
    @SuppressLint("HandlerLeak")
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    startActivity(new Intent(Main.this,Home.class));
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Window window = getWindow();
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        setContentView(R.layout.main);
        welcom_bg = findViewById(R.id.welcom_bg);
//        int i = new Random().nextInt(3-1)+1;
//        if(i==0){
//            welcom_bg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page1));
//        }else if(i==1){
//            welcom_bg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page2));
//        }else {
//            welcom_bg.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page3));
//        }

        if(TextUtils.isEmpty(SharePreferenceUtils.getString(this, Cons.FIRST_COME))){
            startActivity(new Intent(this,Splash.class));
            finish();
        }else {
            Message msg = Message.obtain();
            msg.what= 0;
            handler.sendMessageDelayed(msg,2000);
        }

    }


}
