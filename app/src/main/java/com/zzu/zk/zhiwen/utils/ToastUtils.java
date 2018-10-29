package com.zzu.zk.zhiwen.utils;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;
import com.zzu.zk.zhiwen.activity.Login;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ToastUtils {
    public  static void showToast(Context c,String s){
        Toast.makeText(c,s,Toast.LENGTH_SHORT).show();
    }
    public static String getCurrentSeconds() {
        return String.valueOf(new Date().getTime());
    }

    public  static  void showloading(String s1, final String s2, final OnLoadingCancelListener onLoadingCancelListener , final FragmentActivity fa){

        final Timer timer = new Timer();
        final CircleDialog.Builder builder = new CircleDialog.Builder();
        final DialogFragment dialogFragment = builder.setCancelable(false).setCanceledOnTouchOutside(false)

                .setProgressText(s1)
                .setProgressStyle(ProgressParams.STYLE_HORIZONTAL)


                .show(fa.getSupportFragmentManager());
        TimerTask timerTask = new TimerTask() {
            final int max = 100;
            int progress = 0;
            int accerl = 0;


            @Override
            public void run() {
                accerl = RamdomUtils.ramdomNum();
                progress += accerl;
                if (progress > max) {
                    fa.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogFragment.dismiss();

                            timer.cancel();
                            onLoadingCancelListener.OnLoadingCancel();

                        }
                    });
                } else if (progress > 50) {
                    accerl = 0;
                    builder.setProgressText(s2).create();
                    builder.setProgress(max, progress).create();
                } else {
                    builder.setProgress(max, progress).create();
                }
            }
        };
        timer.schedule(timerTask, 0, 60);

    }

    public interface OnLoadingCancelListener{
       void OnLoadingCancel();
    }
}

