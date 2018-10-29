package com.zzu.zk.zhiwen.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class EncryptionUtils {

    public static byte[] decryptByByte(byte[] s) {

        byte seed = 0x66;

        for (int i = 0; i < s.length; i++) {
            s[i] ^= seed;
        }

        return s;
    }


    public static String decryptByChar(String s){
        char[] array = s.toCharArray();
        for(int i=0;i<array.length;i++) //遍历字符数组
        {
            array[i]=(char)(array[i]^20000); //对每个数组元素进行异或运算
        }
        return new String(array);
    }



    public static String inputstream2String(InputStream in_st) {
        try {
            return new String(Objects.requireNonNull(inputStream2bytes(in_st)), "UTF-8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }

    }



    private static byte[] inputStream2bytes(InputStream inputStream) {
        byte[] b = new byte[2048];
        int n;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
        try {

            while ((n = inputStream.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            inputStream.close();
            bos.close();
            return decryptByByte(bos.toByteArray());
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }


}

