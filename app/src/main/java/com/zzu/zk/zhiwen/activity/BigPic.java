package com.zzu.zk.zhiwen.activity;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zzu.zk.zhiwen.R;

import java.util.ArrayList;

public class BigPic extends AppCompatActivity {
    ViewPager previewViewPager;
    ArrayList<String> list;
    ArrayList<View> viewlist;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;

    private int intentTop;
    private int intentLeft;
    private int intentWidth;
    private int intentHeight;
    int pos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_pic);

        previewViewPager = findViewById(R.id.previewViewPager);
        Bundle bundle = getIntent().getExtras();
        list = bundle.getStringArrayList("urls");
        intentTop = bundle.getInt("top");
        intentLeft = bundle.getInt("left");
        intentWidth = bundle.getInt("width");
        intentHeight = bundle.getInt("hight");
        pos = bundle.getInt("pos");









        viewlist = new ArrayList<>();
        LayoutInflater layoutInflater = getLayoutInflater();

        for (int i = 0; i < list.size(); i++) {
            viewlist.add(layoutInflater.inflate(R.layout.big_pic_item, null));
        }


        previewViewPager.setAdapter(new BigPicPagerAdapter());
        previewViewPager.setCurrentItem(pos);
    }


    private class BigPicPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewlist.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }


        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = viewlist.get(position);
            final ImageView v = view.findViewById(R.id.content);

            Glide.with(BigPic.this)
                    .load(list.get(position))
                    .into(v);

            if(position==pos){
                ViewTreeObserver observer = v.getViewTreeObserver();
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {

                        v.getViewTreeObserver().removeOnPreDrawListener(this);

                        int[] screenLocation = new int[2];
                        v.getLocationOnScreen(screenLocation);
                        mLeftDelta = intentLeft - screenLocation[0];
                        mTopDelta = intentTop - screenLocation[1];

                        mWidthScale = (float) intentWidth / v.getWidth();
                        mHeightScale = (float) intentHeight / v.getHeight();



                        v.setPivotX(0);
                        v.setPivotY(0);
                        v.setScaleX(mWidthScale);
                        v.setScaleY(mHeightScale);
                        v.setTranslationX(mLeftDelta);
                        v.setTranslationY(mTopDelta);
                        TimeInterpolator sDecelerator = new DecelerateInterpolator();
                        v.animate().setDuration(150).scaleX(1).scaleY(1).
                                translationX(0).translationY(0).setInterpolator(sDecelerator).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        Matrix matrix = v.getMatrix();
                                        v.setImageMatrix(matrix);
                                        v.setScaleType(ImageView.ScaleType.MATRIX);
                                        v.setOnTouchListener(new ImageTouchListener());
                                    }
                                }

                                );
                        ObjectAnimator bgAnim = ObjectAnimator.ofInt(new ColorDrawable(ContextCompat.getColor(BigPic.this, R.color.colorBackgroundDark)), "alpha", 0, 255);
                        bgAnim.setDuration(150);
                        bgAnim.start();


                        return false;
                    }
                });

            }
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimeInterpolator sInterpolator = new AccelerateInterpolator();
                    v.animate().setDuration(150).scaleX(mWidthScale).scaleY(mHeightScale).
                            translationX(mLeftDelta).translationY(mTopDelta).setInterpolator(sInterpolator).withEndAction(new Runnable() {
                        public void run() {
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    });
                    ObjectAnimator bgAnim = ObjectAnimator.ofInt( new ColorDrawable(ContextCompat.getColor(BigPic.this, R.color.colorBackgroundDark))
                         , "alpha", 0);
                    bgAnim.setDuration(150);
                    bgAnim.start();

                }

            });

            container.addView(view);
            return view;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(viewlist.get(position));
        }
    }


public class ImageTouchListener implements View.OnTouchListener {
        private   boolean isClick   = false;
        private int  mode      = 0;// default value
    //   /**      * Drag mode      */

     private static final int     MODE_DRAG = 1;
     /**      * Zoom mode      */
     private static final int     MODE_ZOOM = 2;
     /**      * Beginning point      */
     private PointF startPoint    = new PointF();
     private Matrix matrix        = new Matrix();

     private Matrix currentMatrix = new Matrix();
     /**      * Distance between two fingers      */
     private float  startDis;
     /**      * The middle point of two fingers      */
     private PointF midPoint;
     private float values[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
     private float touchX   = 0;
     private float touchY   = 0;
     private float scaleXY  = 0;
     @Override
     public boolean onTouch(View v, MotionEvent event) {
         ImageView imageView = (ImageView) v;
         switch (event.getAction() & MotionEvent.ACTION_MASK) {
             case MotionEvent.ACTION_DOWN:
                 isClick = true;
                 mode = MODE_DRAG;
                 currentMatrix.set(imageView.getImageMatrix());
                 matrix.set(currentMatrix);
                 startPoint.set(event.getX(), event.getY());
                 touchX = event.getX();
                 touchY = event.getY();
                 if (scaleXY == 0) {
                     matrix.getValues(values);
                     scaleXY = values[0];
                 }
                 break;
                 case MotionEvent.ACTION_MOVE:
                     matrix.getValues(values);
                     if (mode == MODE_DRAG) {
                         float dx = event.getX() - startPoint.x;
                         float dy = event.getY() - startPoint.y;
                         if (dx > 15 || dx < -15 || dy > 15 || dy < -15) {
                             isClick = false;
                         }
                         float width = (v.getWidth() * values[0]) / (scaleXY);
                         float height = (v.getHeight() * values[4]) / (scaleXY);
                         float offset1 = v.getContext().getResources().getDimension(R.dimen.sliding_search_view_header_height);
                         float offset2 = v.getContext().getResources().getDimension(R.dimen.sliding_search_view_header_height) * 1.2f * values[0];
                         if ((values[2] > v.getWidth() - offset1 && touchX - event.getX() < 0)) {//move to right
                                               values[2] = v.getWidth() - offset1 + 5;
                                               if (!(values[5] > v.getHeight() - offset1 && touchY - event.getY() < 0) && !(values[5] + height < offset2 && touchY - event.getY() > 0))
                                               {
                                                   values[5] += event.getY() - touchY;
                                                   touchY = event.getY();
                                               }
                                               matrix.setValues(values);
                                               imageView.setImageMatrix(matrix);
                                               return true;
                         } else if ((values[2] + width < offset1 && touchX - event.getX() > 0)) {//move to left
                                            values[2] = offset1 - width - 5;

                                            if (!(values[5] > v.getHeight() - offset1 && touchY - event.getY() < 0) && !(values[5] + height < offset2 && touchY - event.getY() > 0)) {
                                                values[5] += event.getY() - touchY;
                                                touchY = event.getY();
                                            }
                                            matrix.setValues(values);
                                            imageView.setImageMatrix(matrix);
                                            return true;
                         }                     if ((values[5] > v.getHeight() - offset1 && touchY - event.getY() < 0)) {//move to bottom
                                                values[5] = v.getHeight() - offset1 + 5;
                                                if (!(values[2] > v.getWidth() - offset1 && touchX - event.getX() < 0) && !(values[2] + width < offset1 && touchX - event.getX() > 0)) {
                                                    values[2] += event.getX() - touchX;
                                                    touchX = event.getX();
                                                }
                                                matrix.setValues(values);
                                                imageView.setImageMatrix(matrix);
                                                return true;
                         } else if ((values[5] + height < offset2 && touchY - event.getY() > 0)) {//move to top

                             values[5] = offset2 - height - 5;
                             if (!(values[2] > v.getWidth() - offset1 && touchX - event.getX() < 0) && !(values[2] + width < offset1 && touchX - event.getX() > 0)) {                             values[2] += event.getX() - touchX;                             touchX = event.getX();
                             }
                             matrix.setValues(values);
                             imageView.setImageMatrix(matrix);
                             return true;                     }
                             touchX = event.getX();
                         touchY = event.getY();
                         matrix.set(currentMatrix);
                         matrix.postTranslate(dx, dy);
                     } else if (mode == MODE_ZOOM) {
                         float endDis = distance(event);
                         if (endDis > 10f) {
                             float scale = endDis / startDis;
                             if (values[0] / scaleXY < 0.5 && scale < 1 || values[0] / scaleXY > 3 && scale > 1) {
                                 return true;
                             }
                             matrix.set(currentMatrix);
                             matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                         }
                         isClick = false;
                     }
                     break;
                     case MotionEvent.ACTION_UP:
                         if (isClick) {
                             v.performClick();
                         }
                         case MotionEvent.ACTION_POINTER_UP:
                             isClick = false;
                             mode = 0;
                             break;
                             case MotionEvent.ACTION_POINTER_DOWN:
                                 isClick = false;
                                 mode = MODE_ZOOM;
                                 startDis = distance(event);
                                 if (startDis > 10f) {
                                     midPoint = mid(event);
                                     currentMatrix.set(imageView.getImageMatrix());
                                 }
                                 break;


         }
         imageView.setImageMatrix(matrix);
         return true;
     }
     /**      * Calculate the distance between two fingers      */
     private float distance(MotionEvent event) {
         float dx = event.getX(1) - event.getX(0);
         float dy = event.getY(1) - event.getY(0);
         return (float) Math.sqrt(dx * dx + dy * dy);
     }     /**      * calculate the middle point of the two fingers      */
     private PointF mid(MotionEvent event) {
         float midX = (event.getX(1) + event.getX(0)) / 2;
         float midY = (event.getY(1) + event.getY(0)) / 2;
         return new PointF(midX, midY);
     }
    }





}
