package com.zzu.zk.zhiwen.app;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONArray;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.goyourfly.multi_picture.ImageLoader;

import com.goyourfly.multi_picture.MultiPictureView;
import com.mylhyl.circledialog.res.values.CircleDimen;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.utils.PushHandler;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;

public class ZhiWen extends Application {
    public static Context c = null;
    public static  String file_path = null;
    static {
        CircleDimen.DIALOG_RADIUS = 20;

//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);//矢量图兼容

    }

    @Override
    public void onCreate() {
        c = this;
        file_path =   getFilesDir().getAbsolutePath();
        if(SharePreferenceUtils.getInteger(this, Cons.IS_LOGIN)!=-2){
            KlbertjCache.uid = SharePreferenceUtils.getInteger(this, Cons.IS_LOGIN);
        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.NAME))){
            KlbertjCache.name = SharePreferenceUtils.getString(this, Cons.NAME);
        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.BG_PATH))){
            KlbertjCache.bg = SharePreferenceUtils.getString(this, Cons.BG_PATH);
        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.AVATOR_PATH))){
            KlbertjCache.avator_path = SharePreferenceUtils.getString(this, Cons.AVATOR_PATH);
        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.COLLEGE))){
            KlbertjCache.college = SharePreferenceUtils.getString(this, Cons.COLLEGE);
        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.MAJOR))){
            KlbertjCache.major = SharePreferenceUtils.getString(this, Cons.MAJOR);

        }
        if(SharePreferenceUtils.getInteger(this, Cons.COUNT_OF_MY_QUESTIONS)!=-2){
            KlbertjCache.my_q_num = SharePreferenceUtils.getInteger(this, Cons.COUNT_OF_MY_QUESTIONS);

        }
        if(SharePreferenceUtils.getInteger(this, Cons.DISCUSS_NUM)!=-2){
            KlbertjCache.discuss_num = SharePreferenceUtils.getInteger(this, Cons.DISCUSS_NUM);

        }
        if(SharePreferenceUtils.getInteger(this, Cons.FANS_NUM)!=-2){
            KlbertjCache.fans_num = SharePreferenceUtils.getInteger(this, Cons.FANS_NUM);

        }
        if(SharePreferenceUtils.getInteger(this, Cons.NUMS_OF_COLLECTION_QUESTIONS)!=-2){
            KlbertjCache.coll_q_num = SharePreferenceUtils.getInteger(this, Cons.NUMS_OF_COLLECTION_QUESTIONS);

        }
        if(SharePreferenceUtils.getInteger(this, Cons.ATTENTION_PEOPLE_NUM)!=-2){
            KlbertjCache.att_p_num = SharePreferenceUtils.getInteger(this, Cons.ATTENTION_PEOPLE_NUM);

        }
        if(SharePreferenceUtils.getInteger(this, Cons.SCORE)!=-2){
            KlbertjCache.score = SharePreferenceUtils.getInteger(this, Cons.SCORE);

        }

        if(!"".equals(SharePreferenceUtils.getString(this, Cons.NEW_PRO))){
            JSONArray array = JSONArray.parseArray(SharePreferenceUtils.getString(this, Cons.NEW_PRO));
            List<String> list = KlbertjCache.notification2.get("新的问题");
            if(array!=null&&!array.isEmpty()){
                for(int i = 0;i<array.size();i++){
                    list.add(array.getString(i));
                }
            }
            KlbertjCache.notification2.put("新的问题",list);

        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.NEW_REPLY))){
            JSONArray array = JSONArray.parseArray(SharePreferenceUtils.getString(this, Cons.NEW_PRO));
            List<String> list = KlbertjCache.notification2.get("新的回复");
            if(array!=null&&!array.isEmpty()){
                for(int i = 0;i<array.size();i++){
                    list.add(array.getString(i));
                }
            }
            KlbertjCache.notification2.put("新的回复",list);

        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.HAS_ATT))){
            JSONArray array = JSONArray.parseArray(SharePreferenceUtils.getString(this, Cons.HAS_ATT));
            List<String> list = KlbertjCache.notification2.get("有人关注");
            if(array!=null&&!array.isEmpty()){
                for(int i = 0;i<array.size();i++){
                    list.add(array.getString(i));
                }
            }
            KlbertjCache.notification2.put("有人关注",list);

        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.HAS_APPLY))){
            JSONArray array = JSONArray.parseArray(SharePreferenceUtils.getString(this, Cons.HAS_APPLY));
            List<String> list = KlbertjCache.notification2.get("你被采纳");
            if(array!=null&&!array.isEmpty()){
                for(int i = 0;i<array.size();i++){
                    list.add(array.getString(i));
                }
            }
            KlbertjCache.notification2.put("你被采纳",list);

        } if(!"".equals(SharePreferenceUtils.getString(this, Cons.CANCEL_ATT))){
            JSONArray array = JSONArray.parseArray(SharePreferenceUtils.getString(this, Cons.CANCEL_ATT));
            List<String> list = KlbertjCache.notification2.get("取消关注");
            if(array!=null&&!array.isEmpty()){
                for(int i = 0;i<array.size();i++){
                    list.add(array.getString(i));
                }
            }
            KlbertjCache.notification2.put("取消关注",list);

        } if(!"".equals(SharePreferenceUtils.getString(this, Cons.COLL))){
            JSONArray array = JSONArray.parseArray(SharePreferenceUtils.getString(this, Cons.COLL));
            List<String> list = KlbertjCache.notification2.get("收藏问题");
            if(array!=null&&!array.isEmpty()){
                for(int i = 0;i<array.size();i++){
                    list.add(array.getString(i));
                }
            }
            KlbertjCache.notification2.put("收藏问题",list);

        }
        if(!"".equals(SharePreferenceUtils.getString(this, Cons.CANCLE_COLL))){
            JSONArray array = JSONArray.parseArray(SharePreferenceUtils.getString(this, Cons.CANCLE_COLL));
            List<String> list = KlbertjCache.notification2.get("取消收藏问题");
            if(array!=null&&!array.isEmpty()){
                for(int i = 0;i<array.size();i++){
                    list.add(array.getString(i));
                }
            }
            KlbertjCache.notification2.put("取消收藏问题",list);

        }






//        if(!"".equals(SharePreferenceUtils.getString(this,Cons.BANNER))){
//            JSONArray jsonArray = JSONArray.parseArray(SharePreferenceUtils.getString(this,Cons.BANNER));
//            List<String> list = new ArrayList<>();
//            for(int i = 0;i<jsonArray.size();i++){
//                list.add(Cons.PICS_BASE_URL+Cons.BANNER_PICS_PATH_IN_SERVER+"/"+jsonArray.getString(i));
//            }
//            KlbertjCache.banner = list;
//        }
        super.onCreate();
        StorageUtils.initial_storage();
        MultiPictureView.setImageLoader(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, Uri uri) {
                Glide.with(imageView.getContext())
                        .load(uri)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.drawable.ic_loading)
                                .error(R.drawable.ic_loadingerr)

                        .override(150,150))
                        .into(imageView);
            }
        });

        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(configuration);
        if(KlbertjCache.uid!=-2){
            new PushHandler().start();
        }


    }

    public static String getFileDir(){
        return file_path;
    }
    public static Context getContext(){
        return c;
    }

}
