package com.zzu.zk.zhiwen.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;

import java.util.ArrayList;

public class Splash extends AppCompatActivity {
    ViewPager mViewPager;
    LinearLayout pager_indicator;
    ArrayList<ImageView> views = new ArrayList<ImageView>();
    Button button;
    int mNum = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharePreferenceUtils.putString(this, Cons.FIRST_COME,"0");

        Window window = getWindow();
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);
        setContentView(R.layout.splash);
        mViewPager = findViewById(R.id.splash);
        pager_indicator = findViewById(R.id.pager_indicator);
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page1));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        views.add(imageView);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page2));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        views.add(imageView);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page3));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        views.add(imageView);
        mViewPager.setAdapter(new SplashAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(position==2){
                    button.setVisibility(View.VISIBLE);
                }else {
                    button.setVisibility(View.GONE);
                }


                pager_indicator.getChildAt(mNum).setEnabled(false);
                pager_indicator.getChildAt(position).setEnabled(true);
                mNum = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
         button = findViewById(R.id.start);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Splash.this,Home.class));
                finish();
            }
        });


        View view;
         for(int i = 0; i<3;i++){
             //创建底部指示器(小圆点)
             view = new View(Splash.this);
             view.setBackgroundResource(R.drawable.dot);
             view.setEnabled(false);
             //设置宽高
             LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(30, 30);
             //设置间隔
             if (i!= 0) {
                 layoutParams.leftMargin = 10;
             }
             //添加到LinearLayout
             pager_indicator.addView(view, layoutParams);
         }
        pager_indicator.getChildAt(0).setEnabled(true);


    }

    private class SplashAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //获取图片view
            ImageView imageView = new ImageView(Splash.this);
            if(position==0){
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page1));
            }else if(position==1){
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page2));
            }else {
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.page3));
            }

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            container.addView(imageView);
            //返回控件
            return imageView ;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //从容器中删除
            container.removeView((View) object);
        }
    }
}
