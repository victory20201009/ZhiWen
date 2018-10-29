package com.zzu.zk.zhiwen.cache;

import android.graphics.Bitmap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzu.zk.zhiwen.beans.ProblemDigest;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KlbertjCache {

    public static Integer  uid  =-2;
    public static String  name  ="";
    public static String  avator_path  ="";
    public static String  bg  ="";

    public static Integer  my_q_num  =-2;
    public static  Integer discuss_num = -2;
    public static  Integer fans_num = -2;
    public static  Integer coll_q_num = -2;
    public static  Integer att_p_num = -2;
    public static  Integer score = -2;
    public static String  college  ="";
    public static String  major  ="";
    public static JSONArray hot_ques = null;
    public static List<ProblemDigest> index_ques_first_page = null;
    public static List<ProblemDigest> index_ques_second_page = null;
    public static List<ProblemDigest> index_ques_third_page = null;
    public static List<ProblemDigest> index_ques_forth_page = null;

    public static List<ProblemDigest> cate_ques_first_page = null;
    public static List<ProblemDigest> cate_ques_second_page = null;
    public static List<ProblemDigest> cate_ques_third_page = null;
    public static List<ProblemDigest> cate_ques_forth_page = null;
    public static List<String> banner = null;
    public static String my_pro_list = "";
    public static String my_coll_list = "";
    public static String my_att_list = "";
    public static LinkedHashMap<Integer ,String> prodetail = new LinkedHashMap<>();
    public static int max_prodetail_size = 512;
    public static Map<String,List<String>> notification = new HashMap<String,List<String>>(){{
        put("新的问题",new ArrayList<String>());
        put("新的回复",new ArrayList<String>());
        put("有人关注",new ArrayList<String>());
        put("你被采纳",new ArrayList<String>());
        put("取消关注",new ArrayList<String>());
        put("收藏问题",new ArrayList<String>());
        put("取消收藏问题",new ArrayList<String>());
    }};
    public static Map<String,List<String>> notification2 = new HashMap<String,List<String>>(){{
        put("新的问题",new ArrayList<String>());
        put("新的回复",new ArrayList<String>());
        put("有人关注",new ArrayList<String>());
        put("你被采纳",new ArrayList<String>());
        put("取消关注",new ArrayList<String>());
        put("收藏问题",new ArrayList<String>());
        put("取消收藏问题",new ArrayList<String>());
    }};
    public static int[] event_num = new int[7];
}
