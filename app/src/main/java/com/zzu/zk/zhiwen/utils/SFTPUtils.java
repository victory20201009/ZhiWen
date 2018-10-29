package com.zzu.zk.zhiwen.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.zzu.zk.zhiwen.constant.Cons;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SFTPUtils {
    private static Session session = null;
    private static ChannelSftp sftp = null;

    private static void login() {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession("root", "112.74.53.233", 22);
            session.setPassword(EncryptionUtils.decryptByChar(Cons.SFTP));
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;

        } catch (JSchException e) {

            e.printStackTrace();
        }

    }

    private static void logout() {

        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }

    }


    /**
     * @param basePath     存储根目录
     * @param directory    个人目录
     * @param sftpFileName 存储在服务器的文件名
     * @param input        本地文件输入流
     * @throws SftpException
     */
    public static void upload(String basePath, String directory, String sftpFileName, InputStream input) throws SftpException {
        login();
        try {
            sftp.cd(basePath);
            sftp.cd(directory);
        } catch (SftpException e) {
            // 目录不存在，则创建文件夹
            String[] dirs = directory.split("/");
            String tempPath = basePath;
            for (String dir : dirs) {
                if (null == dir || "".equals(dir))
                    continue;
                tempPath += "/" + dir;
                try {
                    sftp.cd(tempPath);
                } catch (SftpException ex) {
                    sftp.mkdir(tempPath);
                    sftp.cd(tempPath);
                }
            }
        }
        sftp.put(input, sftpFileName); // 上传文件
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logout();
    }

    /**
     * @param directory  被删除文件所在服务器的目录
     * @param deleteFile 被删除文件名称
     * @throws SftpException
     */
    public static void delete(String directory, String deleteFile) throws SftpException {
        login();
        sftp.cd(directory);
        sftp.rm(deleteFile);
        logout();
    }

    /**
     * @param directory    要下载的文件所在的服务器路径
     * @param downloadFile 要下载的文件名称
     * @return 返回文件的字节数组 用于图片
     * @throws SftpException
     * @throws IOException
     */
    public static byte[] download(String directory, String downloadFile) throws SftpException, IOException {
        login();
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        InputStream is = sftp.get(downloadFile);

        byte[] fileData = IOUtils.toByteArray(is);
        is.close();
        logout();
        return fileData;
    }

    /**
     * @param directory    要下载的文件所在的服务器路径
     * @param downloadFile 要下载的文件名称
     * @param saveFile     存放在本地的绝对路径 带名称
     * @throws SftpException
     * @throws FileNotFoundException
     */
    public static void download(String directory, String downloadFile, String saveFile)
            throws SftpException, FileNotFoundException {
        login();
        if (directory != null && !"".equals(directory)) {
            sftp.cd(directory);
        }
        File file = new File(saveFile);
//        if(downloadFile==null){
//            Log.i("fdsfds","downloadFile==null");
//        }else {
//            Log.i("fdsfds","downloadFile!=null");
//        }
        sftp.get(downloadFile, new FileOutputStream(file));
        logout();
    }


    public static void upLoadPics(Context context, Map<Uri,String> uri_name , String pic_path) {

        for(Uri uri:uri_name.keySet()){
            try {
                InputStream is = context.getContentResolver().openInputStream(uri);
                upload(Cons.PICS_ROOT_PATH_IN_SERVER,pic_path,uri_name.get(uri),is);
            } catch (FileNotFoundException | SftpException e) {
                e.printStackTrace();
            }


        }

    }

    public static void upLoadAvator(Context context, Uri uri,String path,String name) {
            try {
                InputStream is = context.getContentResolver().openInputStream(uri);
                upload(Cons.PICS_ROOT_PATH_IN_SERVER,path,name,is);
            } catch (FileNotFoundException | SftpException e) {
                Log.i("upLoadAvator","upLoadAvator 出错upLoadAvator 出错upLoadAvator 出错upLoadAvator 出错upLoadAvator 出错upLoadAvator 出错upLoadAvator 出错");
                e.printStackTrace();
            }

    }

    public static void deleteAvator(Context context,String path, String name) {
        try {
            delete(Cons.PICS_ROOT_PATH_IN_SERVER+path,name);
            Log.i("dddfsf",Cons.PICS_ROOT_PATH_IN_SERVER+path+"==============="+name);
        } catch (SftpException e) {
            Log.i("deleteAvator","deleteAvator 出错deleteAvator 出错deleteAvator 出错deleteAvator 出错deleteAvator 出错deleteAvator 出错");
            e.printStackTrace();
        }

    }
    public static void deletePics(Context context, List<String> pics , String pic_path) {

        for(String pic:pics){
            try {

                delete(Cons.PICS_ROOT_PATH_IN_SERVER+pic_path,pic);
            } catch ( SftpException e) {
                e.printStackTrace();
            }


        }

    }
    public static void deleteFiles(Context context, List<String> pics ) {

        for(String pic:pics){
            try {

                delete(Cons.FILES_ROOT_PATH_IN_SERVER,pic);
            } catch ( SftpException e) {
                e.printStackTrace();
            }


        }

    }

    public static void uploadFiles(Map<String,String> pics ) {

        for(String pic:pics.keySet()){
            try {

                InputStream is = new FileInputStream(new File(pic));
                upload(Cons.ROOT_PATH_IN_SERVER,"/files",pics.get(pic),is);
            } catch ( SftpException | FileNotFoundException e) {
                e.printStackTrace();
            }


        }

    }

}

