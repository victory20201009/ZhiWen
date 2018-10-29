package com.zzu.zk.zhiwen.utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.app.ZhiWen;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

import static android.support.v4.app.NotificationCompat.*;

public class PushHandler extends Thread {

int id = 1;
   private static OnDataReceiveListener onDataReceiveListener = null;
    private static Activity activity = null;
    public static OutputStream out = null;
    @Override
    public void run() {
        BufferedReader reader = null;
        Socket socket = null;


    try {
        socket = new Socket("39.108.98.219", 20000);
         out = socket.getOutputStream();
        String s = String.valueOf(KlbertjCache.uid + 100000).substring(1);
        out.write(("C0" + s).getBytes());
        out.flush();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String lineString = "";
        NotificationManager notifyManager = (NotificationManager) ZhiWen.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        while (true) {


            lineString = reader.readLine();
            if(lineString!=null){
                lineString = lineString.substring(6);
                Log.i("cdf",lineString);
                JSONObject noti = JSONObject.parseObject(lineString);

                String title = "";
                String con = "";
                if (noti != null) {
                    con = noti.getString("con");
                    final String type = noti.getString("tpye");
                    if(onDataReceiveListener!=null&&activity!=null){
                        final String finalCon = lineString;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onDataReceiveListener.OnDataReceive(type, finalCon);
                            }
                        });

                    }else {
                        List<String> list = null;
                        switch (type) {
                            case "new_question":
                                title = "新的问题";
                                list = KlbertjCache.notification.get("新的问题");
                                break;
                            case "reply":
                                title = "新的回复";
                                list = KlbertjCache.notification.get("新的回复");
                                break;
                            case "atten":
                                title = "有人关注";
                                list = KlbertjCache.notification.get("有人关注");
                                break;
                            case "apply":
                                title = "你被采纳";
                                list = KlbertjCache.notification.get("你被采纳");
                                break;
                            case "unatten":
                                title = "取消关注";
                                list = KlbertjCache.notification.get("取消关注");
                                break;
                            case "coll":
                                title = "收藏问题";
                                list = KlbertjCache.notification.get("收藏问题");
                                break;
                            case "uncoll":
                                title = "取消收藏问题";
                                list = KlbertjCache.notification.get("取消收藏问题");
                                break;
                        }

                        Objects.requireNonNull(list).add(lineString);
                        KlbertjCache.notification.put(title,list);
                    }

                }

                //实例化NotificationCompat.Builde并设置相关属性
                Builder builder = new NotificationCompat.Builder(ZhiWen.getContext(),"tgrgtredf")
                        //设置小图标
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //设置通知标题
                        .setContentTitle(title)
                        //设置通知内容
                        .setContentText(con)
                        //设置通知时间，默认为系统发出通知的时间，通常不用设置
                        .setWhen(System.currentTimeMillis());
                //通过builder.build()方法生成Notification对象,并发送通知,id=1
                Objects.requireNonNull(notifyManager).notify(id++, builder.build());
            }else{
                break;
            }


        }
    } catch (IOException e) {
        e.printStackTrace();
    }



    try {
        if(reader!=null){
        reader.close();
        }
        if(socket!=null){
            socket.close();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }



    }


    public interface OnDataReceiveListener{
        void OnDataReceive(String type,String con);
    }
    public  static  void setOnDataReceiveListener(OnDataReceiveListener onDataReceiveListene, Activity a){
        onDataReceiveListener = onDataReceiveListene;
        activity = a;
    }
}
