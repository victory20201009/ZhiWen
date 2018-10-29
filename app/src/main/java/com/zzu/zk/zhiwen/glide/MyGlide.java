package com.zzu.zk.zhiwen.glide;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.zzu.zk.zhiwen.R;

@GlideModule
public class MyGlide {
    public static RequestOptions getRequestOptions(){
      return   new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)  .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_loadingerr) .diskCacheStrategy(DiskCacheStrategy.ALL).skipMemoryCache(false);
    }

}
