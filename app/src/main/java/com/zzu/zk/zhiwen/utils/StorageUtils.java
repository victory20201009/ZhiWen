package com.zzu.zk.zhiwen.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StatFs;
import android.util.Log;

import com.zzu.zk.zhiwen.constant.Cons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageUtils {
    public static void initial_storage() {
        File f = new File(Cons.ROOTDir);
        if (!f.exists()) {
            f.mkdir();
        }
        f = new File(Cons.ROOTDir + Cons.TEMP);
        if (!f.exists()) {
            f.mkdir();
        }
        f = new File(Cons.ROOTDir + Cons.AVATOR);
        if (!f.exists()) {
            f.mkdir();
        }
        f = new File(Cons.ROOTDir + Cons.PERSONAL_BG);
        if (!f.exists()) {
            f.mkdir();
        }
        f = new File(Cons.ROOTDir + Cons.ATTACHMENTS);
        if (!f.exists()) {
            f.mkdir();
        }
    }


//    public static boolean check_banner(String[] paths) {
//        File f = new File(Cons.ROOTDir + Cons.BANNER);
//        if (!f.exists()) {
//            f.mkdir();
//            return false;
//        }else {
//            for (String path : paths) {
//                f = new File(Cons.ROOTDir + Cons.BANNER + path);
//                if (!f.exists()) {
//                    return false;
//                }
//            }
//            return true;
//        }
//
//    }
//    public static void save_banner(String[] paths,byte[] b1,byte[] b2,byte[]b3) {
//        File f = new File(Cons.ROOTDir + Cons.BANNER);
//        if (!f.exists()) {
//            f.mkdir();
//        }else {
//            clearDir(f);
//        }
//        FileOutputStream fos =null;
//
//            try {
//                fos = new FileOutputStream(Cons.ROOTDir + Cons.BANNER+paths[0]);
//                fos.write(b1);
//                fos.close();
//                fos = new FileOutputStream(Cons.ROOTDir + Cons.BANNER+paths[1]);
//                fos.write(b2);
//                fos.close();
//                fos = new FileOutputStream(Cons.ROOTDir + Cons.BANNER+paths[2]);
//                fos.write(b3);
//                fos.close();
//                fos = null;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//    }

//    public static List<Bitmap> get_banner() {
//        File f = new File(Cons.ROOTDir + Cons.BANNER);
//        if (!f.exists()) {
//            f.mkdir();
//        }
//        List<Bitmap> list = new ArrayList<>();
//
//        for(String name :Cons.BANNER_NAMES){
//            try {
//                list.add(BitmapFactory.decodeStream(new FileInputStream(Cons.ROOTDir+Cons.BANNER+name))) ;
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//        return list;
//    }


    public static Uri get_temp_photo_uri() {
        //先检查
        initial_storage();
        String file_name = System.currentTimeMillis() + ".jpg";
        File f = new File(Cons.ROOTDir + Cons.TEMP, file_name);
        return Uri.fromFile(f);
    }


    public static Uri get_avator_photo_uri() {
        //先检查
        initial_storage();
        String file_name = System.currentTimeMillis() + ".jpg";
        File f = new File(Cons.ROOTDir + Cons.AVATOR, file_name);
        return Uri.fromFile(f);
    }

    public static Uri get_bg_photo_uri() {
        //先检查
        initial_storage();
        String file_name = System.currentTimeMillis() + ".jpg";
        File f = new File(Cons.ROOTDir + Cons.PERSONAL_BG, file_name);
        return Uri.fromFile(f);
    }

    public static boolean is_disk_enough() {
        //先检查
        initial_storage();
        StatFs statFs = new StatFs(Cons.ROOTDir);
        long availableBlocksLong = statFs.getAvailableBlocksLong();
        long blockSizeLong = statFs.getBlockSizeLong();

//        Log.i("fdsfsfdsfds",availableBlocksLong*blockSizeLong+"");
        if (availableBlocksLong * blockSizeLong / 1024 > 60.0) {
            return true;
        }
        return false;

    }




    private static void clearDir(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    clearDir(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }

    }


}
