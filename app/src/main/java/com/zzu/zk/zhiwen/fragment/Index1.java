package com.zzu.zk.zhiwen.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;
import com.roy.library.FlodableButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.activity.Home;
import com.zzu.zk.zhiwen.activity.Login;
import com.zzu.zk.zhiwen.activity.MyAsk;
import com.zzu.zk.zhiwen.activity.ProblemDetail;
import com.zzu.zk.zhiwen.beans.ProblemDigest;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.MyCircleView;
import com.zzu.zk.zhiwen.glide.MyGlide;
import com.zzu.zk.zhiwen.utils.EncryptionUtils;
import com.zzu.zk.zhiwen.utils.SFTPUtils;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.StatusBarUtil;
import com.zzu.zk.zhiwen.utils.ToastUtils;
import com.zzu.zk.zhiwen.utils.UrlUtils;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class Index1 extends Fragment implements AdapterView.OnItemClickListener {
    final String tag = "Index1";
    private float llOffDistance;
    private FrameLayout.LayoutParams params;
    private boolean isUp = false;   //判断是否为上滑状态
    private boolean isDown = false; //判断是否为下拉状态
    private int i = 0;
    private IndexQuickAdapter mAdapter;
    LinearLayout got;
    private static final int DOWN_BANNER = 0;
    ImageButton gotop;
    CollapsingToolbarLayout toolbarLayout;
    private static DialogFragment dialogFragment = null;
    private final Index1Handler mHandler = new Index1Handler(this);
    AppBarLayout appBar;
    static Banner banner;
    FlodableButton text;
    TextView hot_indicator;
    IndexWithNoDataQuickAdapter indexWithNoDataQuickAdapter;
    View view, h_re;
    int page = 1;
    FrameLayout fl;
    TextView h_t1, h_t2, h_t3, h_n1, h_n2, h_n3, h_c1, h_c2, h_c3, h_ti1, h_ti2, h_ti3;
    ImageView h_p1, h_p2, h_p3;
    LinearLayout h_l1, h_l2, h_l3, hhh;
    SmartRefreshLayout refreshLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.i(tag, "onAttach");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(tag, "onCreate");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(tag, "onCreateView");
        return inflater.inflate(R.layout.index1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        refreshLayout = root.findViewById(R.id.refreshLayout);
        hot_indicator = root.findViewById(R.id.hot_indicator);
        hhh = root.findViewById(R.id.hhh);
        h_t1 = root.findViewById(R.id.h_t1);
        h_t2 = root.findViewById(R.id.h_t2);
        h_t3 = root.findViewById(R.id.h_t3);
        h_n1 = root.findViewById(R.id.h_n1);
        h_n2 = root.findViewById(R.id.h_n2);
        h_n3 = root.findViewById(R.id.h_n3);

        h_c1 = root.findViewById(R.id.h_c1);
        h_c2 = root.findViewById(R.id.h_c2);
        h_c3 = root.findViewById(R.id.h_c3);

        h_ti1 = root.findViewById(R.id.h_ti1);
        h_ti2 = root.findViewById(R.id.h_ti2);
        h_ti3 = root.findViewById(R.id.h_ti3);

        h_p1 = root.findViewById(R.id.h_p1);
        h_p2 = root.findViewById(R.id.h_p2);
        h_p3 = root.findViewById(R.id.h_p3);

        h_l1 = root.findViewById(R.id.h_l1);
        h_l2 = root.findViewById(R.id.h_l2);
        h_l3 = root.findViewById(R.id.h_l3);
        h_re = root.findViewById(R.id.h_re);


        if (KlbertjCache.hot_ques != null && !KlbertjCache.hot_ques.isEmpty()) {
            hhh.setVisibility(View.VISIBLE);
            hot_indicator.setVisibility(View.GONE);
            h_l1.setVisibility(View.VISIBLE);
//            h_l1.setFocusable(true);
//            h_l1.setClickable(true);
//            h_l1.setFocusableInTouchMode(true);
          final  JSONObject jj = KlbertjCache.hot_ques.getJSONObject(0);
            h_t1.setText(jj.getString("create_time"));
            h_n1.setText(jj.getString("belt_user_name"));
            h_c1.setText(String.valueOf(jj.getInteger("dis_num")));
            h_ti1.setText(jj.getString("title"));
            final Integer qqqq = jj.getInteger("qid");
            h_l1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在获取数据...")
                                .setCanceledOnTouchOutside(false)
                                .setCancelable(false)
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject j = new JSONObject();
                                    j.put("qid", qqqq);
                                    j.put("id", KlbertjCache.uid);
                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                        Thread.sleep(1000);
                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }

                                                if ("f".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                } else if ("bp".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                } else {
                                                    if (KlbertjCache.prodetail.containsKey(qqqq)) {
                                                        String detail = KlbertjCache.prodetail.get(qqqq);

                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                        JSONObject j = JSONObject.parseObject(detail);
                                                        if(j!=null){
                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                        }


                                                        i.putExtra("belong_to_user_uname", jj.getString("belt_user_name"));
                                                        i.putExtra("title", jj.getString("title"));

                                                        i.putExtra("create_time", jj.getString("create_time"));
                                                        i.putExtra("qqq_id", jj.getInteger("qid"));

                                                        i.putExtra("contentStr", detail);
                                                        JSONObject jjjjj = JSONObject.parseObject(result);
                                                        int is_my = jjjjj.getInteger("is_my");
                                                        if (1 != is_my) {
                                                            i.putExtra("come_intent", "nmy_ques");
                                                            int is_coll = jjjjj.getInteger("is_coll");

                                                            i.putExtra("is_coll", is_coll);
                                                            int is_att = jjjjj.getInteger("is_att");
                                                            i.putExtra("is_att", is_att);
                                                        } else {
                                                            i.putExtra("come_intent", "my_ques");
                                                        }
                                                        startActivity(i);
                                                    } else {
                                                        dialogFragment = new CircleDialog.Builder()
                                                                .setProgressText("正在获取问题详情...")
                                                                .setCanceledOnTouchOutside(false)
                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                try {
                                                                    HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                    OutputStream out1 = urlConnection1.getOutputStream();

                                                                    out1.write(EncryptionUtils.decryptByByte((qqqq + "").getBytes("UTF-8")));
                                                                    out1.close();
                                                                    int responseCode1 = urlConnection1.getResponseCode();
                                                                    if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                        final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                        Thread.sleep(600);

                                                                        if (KlbertjCache.prodetail.size() > 512) {
                                                                            int ii = 1;
                                                                            for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                ii = i;
                                                                                break;
                                                                            }
                                                                            KlbertjCache.prodetail.remove(ii);
                                                                        }
                                                                        KlbertjCache.prodetail.put(qqqq, result1);

                                                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                if (dialogFragment != null) {
                                                                                    dialogFragment.dismiss();
                                                                                    dialogFragment = null;
                                                                                }

                                                                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                int is_my = jjjjj.getInteger("is_my");

                                                                                if (1 != is_my) {
                                                                                    i.putExtra("come_intent", "nmy_ques");
                                                                                    int is_coll = jjjjj.getInteger("is_coll");

                                                                                    i.putExtra("is_coll", is_coll);
                                                                                    int is_att = jjjjj.getInteger("is_att");
                                                                                    i.putExtra("is_att", is_att);
                                                                                } else {
                                                                                    i.putExtra("come_intent", "my_ques");
                                                                                }

                                                                                JSONObject j = JSONObject.parseObject(result1);
                                                                                if(j!=null){
                                                                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                }



                                                                                i.putExtra("contentStr", result1);


                                                                                i.putExtra("belong_to_user_uname", jj.getString("belt_user_name"));
                                                                                i.putExtra("title", jj.getString("title"));

                                                                                i.putExtra("create_time", jj.getString("create_time"));
                                                                                i.putExtra("qqq_id", jj.getInteger("qid"));

                                                                                startActivity(i);
                                                                            }
                                                                        });

                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        }).start();
                                                    }


                                                }

                                            }
                                        });


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } else {

                        if (KlbertjCache.prodetail.containsKey(qqqq)) {
                            String detail = KlbertjCache.prodetail.get(qqqq);

                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                            JSONObject j = JSONObject.parseObject(detail);
                            if(j!=null){
                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                            }
                            i.putExtra("come_intent", "un_login");


                            i.putExtra("belong_to_user_uname", jj.getString("belt_user_name"));
                            i.putExtra("title", jj.getString("title"));
                            i.putExtra("create_time", jj.getString("create_time"));
                            i.putExtra("qqq_id", jj.getInteger("qid"));
                            i.putExtra("contentStr", detail);

                            startActivity(i);

                        } else {
                            dialogFragment = new CircleDialog.Builder()
                                    .setProgressText("正在获取数据...")
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)
                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpURLConnection urlConnection = null;
                                    try {
                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                        OutputStream out = urlConnection.getOutputStream();
                                        out.write(EncryptionUtils.decryptByByte((qqqq + "").getBytes("UTF-8")));
                                        out.close();
                                        int responseCode = urlConnection.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                            Thread.sleep(1000);
                                            if (KlbertjCache.prodetail.size() > 512) {
                                                int ii = 1;
                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                    ii = i;
                                                    break;
                                                }
                                                KlbertjCache.prodetail.remove(ii);
                                            }
                                            KlbertjCache.prodetail.put(qqqq, result);
                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialogFragment != null) {
                                                        dialogFragment.dismiss();
                                                        dialogFragment = null;
                                                    }

                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                    JSONObject j = JSONObject.parseObject(result);
                                                    if(j!=null){
                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                    }
                                                    i.putExtra("come_intent", "un_login");


                                                    i.putExtra("belong_to_user_uname", jj.getString("belt_user_name"));
                                                    i.putExtra("title", jj.getString("title"));
                                                    i.putExtra("create_time", jj.getString("create_time"));
                                                    i.putExtra("qqq_id", jj.getInteger("qid"));
                                                    i.putExtra("contentStr", result);

                                                    startActivity(i);

                                                }
                                            });

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        }


                    }


                }
            });
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj.getString("dis_pic_url"))
                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p1);


            if (KlbertjCache.hot_ques.size() > 1) {
                h_l2.setVisibility(View.VISIBLE);
//                h_l2.setFocusable(true);
//                h_l2.setClickable(true);
//                h_l2.setFocusableInTouchMode(true);
                final  JSONObject jj2 = KlbertjCache.hot_ques.getJSONObject(1);
                final Integer qqqq2 = jj.getInteger("qid");
                h_t2.setText(jj2.getString("create_time"));
                h_n2.setText(jj2.getString("belt_user_name"));
                h_c2.setText(String.valueOf(jj2.getInteger("dis_num")));
                h_ti2.setText(jj2.getString("title"));
                h_l2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                            dialogFragment = new CircleDialog.Builder()
                                    .setProgressText("正在获取数据...")
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)
                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpURLConnection urlConnection = null;
                                    try {
                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                        OutputStream out = urlConnection.getOutputStream();
                                        JSONObject j = new JSONObject();
                                        j.put("qid", qqqq2);
                                        j.put("id", KlbertjCache.uid);
                                        out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                        out.close();
                                        int responseCode = urlConnection.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                            Thread.sleep(1000);
                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialogFragment != null) {
                                                        dialogFragment.dismiss();
                                                        dialogFragment = null;
                                                    }

                                                    if ("f".equals(result)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                    } else if ("bp".equals(result)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                    } else {
                                                        if (KlbertjCache.prodetail.containsKey(qqqq2)) {
                                                            String detail = KlbertjCache.prodetail.get(qqqq2);

                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                            JSONObject j = JSONObject.parseObject(detail);
                                                            if(j!=null){
                                                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                            }


                                                            i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                            i.putExtra("title", jj2.getString("title"));

                                                            i.putExtra("create_time", jj2.getString("create_time"));
                                                            i.putExtra("qqq_id", jj2.getInteger("qid"));

                                                            i.putExtra("contentStr", detail);
                                                            JSONObject jjjjj = JSONObject.parseObject(result);
                                                            int is_my = jjjjj.getInteger("is_my");
                                                            if (1 != is_my) {
                                                                i.putExtra("come_intent", "nmy_ques");
                                                                int is_coll = jjjjj.getInteger("is_coll");

                                                                i.putExtra("is_coll", is_coll);
                                                                int is_att = jjjjj.getInteger("is_att");
                                                                i.putExtra("is_att", is_att);
                                                            } else {
                                                                i.putExtra("come_intent", "my_ques");
                                                            }
                                                            startActivity(i);
                                                        } else {
                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取问题详情...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                        OutputStream out1 = urlConnection1.getOutputStream();

                                                                        out1.write(EncryptionUtils.decryptByByte((qqqq2 + "").getBytes("UTF-8")));
                                                                        out1.close();
                                                                        int responseCode1 = urlConnection1.getResponseCode();
                                                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                            Thread.sleep(600);

                                                                            if (KlbertjCache.prodetail.size() > 512) {
                                                                                int ii = 1;
                                                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                    ii = i;
                                                                                    break;
                                                                                }
                                                                                KlbertjCache.prodetail.remove(ii);
                                                                            }
                                                                            KlbertjCache.prodetail.put(qqqq2, result1);

                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (dialogFragment != null) {
                                                                                        dialogFragment.dismiss();
                                                                                        dialogFragment = null;
                                                                                    }

                                                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                    JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                    int is_my = jjjjj.getInteger("is_my");

                                                                                    if (1 != is_my) {
                                                                                        i.putExtra("come_intent", "nmy_ques");
                                                                                        int is_coll = jjjjj.getInteger("is_coll");

                                                                                        i.putExtra("is_coll", is_coll);
                                                                                        int is_att = jjjjj.getInteger("is_att");
                                                                                        i.putExtra("is_att", is_att);
                                                                                    } else {
                                                                                        i.putExtra("come_intent", "my_ques");
                                                                                    }

                                                                                    JSONObject j = JSONObject.parseObject(result1);
                                                                                    if(j!=null){
                                                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                    }



                                                                                    i.putExtra("contentStr", result1);


                                                                                    i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                                                    i.putExtra("title", jj2.getString("title"));

                                                                                    i.putExtra("create_time", jj2.getString("create_time"));
                                                                                    i.putExtra("qqq_id", jj2.getInteger("qid"));

                                                                                    startActivity(i);
                                                                                }
                                                                            });

                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }
                                                            }).start();
                                                        }


                                                    }

                                                }
                                            });


                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        } else {

                            if (KlbertjCache.prodetail.containsKey(qqqq2)) {
                                String detail = KlbertjCache.prodetail.get(qqqq2);

                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                JSONObject j = JSONObject.parseObject(detail);
                                if(j!=null){
                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                }
                                i.putExtra("come_intent", "un_login");


                                i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                i.putExtra("title", jj2.getString("title"));
                                i.putExtra("create_time", jj2.getString("create_time"));
                                i.putExtra("qqq_id", jj2.getInteger("qid"));
                                i.putExtra("contentStr", detail);

                                startActivity(i);

                            } else {
                                dialogFragment = new CircleDialog.Builder()
                                        .setProgressText("正在获取数据...")
                                        .setCanceledOnTouchOutside(false)
                                        .setCancelable(false)
                                        .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                        .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HttpURLConnection urlConnection = null;
                                        try {
                                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                            OutputStream out = urlConnection.getOutputStream();
                                            out.write(EncryptionUtils.decryptByByte((qqqq2 + "").getBytes("UTF-8")));
                                            out.close();
                                            int responseCode = urlConnection.getResponseCode();
                                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                Thread.sleep(1000);
                                                if (KlbertjCache.prodetail.size() > 512) {
                                                    int ii = 1;
                                                    for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                        ii = i;
                                                        break;
                                                    }
                                                    KlbertjCache.prodetail.remove(ii);
                                                }
                                                KlbertjCache.prodetail.put(qqqq2, result);
                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dialogFragment != null) {
                                                            dialogFragment.dismiss();
                                                            dialogFragment = null;
                                                        }

                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                        JSONObject j = JSONObject.parseObject(result);
                                                        if(j!=null){
                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                        }
                                                        i.putExtra("come_intent", "un_login");


                                                        i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                        i.putExtra("title", jj2.getString("title"));
                                                        i.putExtra("create_time", jj2.getString("create_time"));
                                                        i.putExtra("qqq_id", jj2.getInteger("qid"));
                                                        i.putExtra("contentStr", result);

                                                        startActivity(i);

                                                    }
                                                });

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            }


                        }




                    }
                });
                Glide.with(Objects.requireNonNull(getActivity()))
                        .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj2.getString("dis_pic_url"))
                        .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p2);
            }

            if (KlbertjCache.hot_ques.size() > 2) {
                h_l3.setVisibility(View.VISIBLE);
//                h_l3.setFocusable(true);
//                h_l3.setClickable(true);
//                h_l3.setFocusableInTouchMode(true);
                final  JSONObject jj3 = KlbertjCache.hot_ques.getJSONObject(2);
                final Integer qqqq3 = jj3.getInteger("qid");
                h_t3.setText(jj3.getString("create_time"));
                h_n3.setText(jj3.getString("belt_user_name"));
                h_c3.setText(String.valueOf(jj3.getInteger("dis_num")));
                h_ti3.setText(jj3.getString("title"));
                h_l3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {




                        if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                            dialogFragment = new CircleDialog.Builder()
                                    .setProgressText("正在获取数据...")
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)
                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpURLConnection urlConnection = null;
                                    try {
                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                        OutputStream out = urlConnection.getOutputStream();
                                        JSONObject j = new JSONObject();
                                        j.put("qid", qqqq3);
                                        j.put("id", KlbertjCache.uid);
                                        out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                        out.close();
                                        int responseCode = urlConnection.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                            Thread.sleep(1000);
                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialogFragment != null) {
                                                        dialogFragment.dismiss();
                                                        dialogFragment = null;
                                                    }

                                                    if ("f".equals(result)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                    } else if ("bp".equals(result)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                    } else {
                                                        if (KlbertjCache.prodetail.containsKey(qqqq3)) {
                                                            String detail = KlbertjCache.prodetail.get(qqqq3);

                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                            JSONObject j = JSONObject.parseObject(detail);
                                                            if(j!=null){
                                                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                            }


                                                            i.putExtra("belong_to_user_uname", jj3.getString("belt_user_name"));
                                                            i.putExtra("title", jj3.getString("title"));

                                                            i.putExtra("create_time", jj3.getString("create_time"));
                                                            i.putExtra("qqq_id", jj3.getInteger("qid"));

                                                            i.putExtra("contentStr", detail);
                                                            JSONObject jjjjj = JSONObject.parseObject(result);
                                                            int is_my = jjjjj.getInteger("is_my");
                                                            if (1 != is_my) {
                                                                i.putExtra("come_intent", "nmy_ques");
                                                                int is_coll = jjjjj.getInteger("is_coll");

                                                                i.putExtra("is_coll", is_coll);
                                                                int is_att = jjjjj.getInteger("is_att");
                                                                i.putExtra("is_att", is_att);
                                                            } else {
                                                                i.putExtra("come_intent", "my_ques");
                                                            }
                                                            startActivity(i);
                                                        } else {
                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取问题详情...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    try {
                                                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                        OutputStream out1 = urlConnection1.getOutputStream();

                                                                        out1.write(EncryptionUtils.decryptByByte((qqqq3 + "").getBytes("UTF-8")));
                                                                        out1.close();
                                                                        int responseCode1 = urlConnection1.getResponseCode();
                                                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                            Thread.sleep(600);

                                                                            if (KlbertjCache.prodetail.size() > 512) {
                                                                                int ii = 1;
                                                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                    ii = i;
                                                                                    break;
                                                                                }
                                                                                KlbertjCache.prodetail.remove(ii);
                                                                            }
                                                                            KlbertjCache.prodetail.put(qqqq3, result1);

                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (dialogFragment != null) {
                                                                                        dialogFragment.dismiss();
                                                                                        dialogFragment = null;
                                                                                    }

                                                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                    JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                    int is_my = jjjjj.getInteger("is_my");

                                                                                    if (1 != is_my) {
                                                                                        i.putExtra("come_intent", "nmy_ques");
                                                                                        int is_coll = jjjjj.getInteger("is_coll");

                                                                                        i.putExtra("is_coll", is_coll);
                                                                                        int is_att = jjjjj.getInteger("is_att");
                                                                                        i.putExtra("is_att", is_att);
                                                                                    } else {
                                                                                        i.putExtra("come_intent", "my_ques");
                                                                                    }

                                                                                    JSONObject j = JSONObject.parseObject(result1);
                                                                                    if(j!=null){
                                                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                    }



                                                                                    i.putExtra("contentStr", result1);


                                                                                    i.putExtra("belong_to_user_uname", jj3.getString("belt_user_name"));
                                                                                    i.putExtra("title", jj3.getString("title"));

                                                                                    i.putExtra("create_time", jj3.getString("create_time"));
                                                                                    i.putExtra("qqq_id", jj3.getInteger("qid"));

                                                                                    startActivity(i);
                                                                                }
                                                                            });

                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                }
                                                            }).start();
                                                        }


                                                    }

                                                }
                                            });


                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        } else {

                            if (KlbertjCache.prodetail.containsKey(qqqq3)) {
                                String detail = KlbertjCache.prodetail.get(qqqq3);

                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                JSONObject j = JSONObject.parseObject(detail);
                                if(j!=null){
                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                }
                                i.putExtra("come_intent", "un_login");


                                i.putExtra("belong_to_user_uname", jj3.getString("belt_user_name"));
                                i.putExtra("title", jj3.getString("title"));
                                i.putExtra("create_time", jj3.getString("create_time"));
                                i.putExtra("qqq_id", jj3.getInteger("qid"));
                                i.putExtra("contentStr", detail);

                                startActivity(i);

                            } else {
                                dialogFragment = new CircleDialog.Builder()
                                        .setProgressText("正在获取数据...")
                                        .setCanceledOnTouchOutside(false)
                                        .setCancelable(false)
                                        .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                        .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HttpURLConnection urlConnection = null;
                                        try {
                                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                            OutputStream out = urlConnection.getOutputStream();
                                            out.write(EncryptionUtils.decryptByByte((qqqq3 + "").getBytes("UTF-8")));
                                            out.close();
                                            int responseCode = urlConnection.getResponseCode();
                                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                Thread.sleep(1000);
                                                if (KlbertjCache.prodetail.size() > 512) {
                                                    int ii = 1;
                                                    for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                        ii = i;
                                                        break;
                                                    }
                                                    KlbertjCache.prodetail.remove(ii);
                                                }
                                                KlbertjCache.prodetail.put(qqqq3, result);
                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dialogFragment != null) {
                                                            dialogFragment.dismiss();
                                                            dialogFragment = null;
                                                        }

                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                        JSONObject j = JSONObject.parseObject(result);
                                                        if(j!=null){
                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                        }
                                                        i.putExtra("come_intent", "un_login");


                                                        i.putExtra("belong_to_user_uname", jj3.getString("belt_user_name"));
                                                        i.putExtra("title", jj3.getString("title"));
                                                        i.putExtra("create_time", jj3.getString("create_time"));
                                                        i.putExtra("qqq_id", jj3.getInteger("qid"));
                                                        i.putExtra("contentStr", result);

                                                        startActivity(i);

                                                    }
                                                });

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                            }


                        }



                    }
                });
                Glide.with(Objects.requireNonNull(getActivity()))
                        .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj3.getString("dis_pic_url"))
                        .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p3);
            }


        } else {

            dialogFragment = new CircleDialog.Builder()
                    .setProgressText("正在获取数据...")
                    .setCanceledOnTouchOutside(false)
                    .setCancelable(false)
                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection;
                    try {
                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.hotDiscuss));
                        int responseCode = urlConnection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                            Thread.sleep(1000);
                            Log.i("hotDiscuss", result);

                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialogFragment != null) {
                                        dialogFragment.dismiss();
                                        dialogFragment = null;
                                    }

                                    if ("server error".equals(result)) {
                                        hhh.setVisibility(View.GONE);
                                        hot_indicator.setVisibility(View.VISIBLE);
                                        hot_indicator.setText("服务器错误");
                                        KlbertjCache.hot_ques = new JSONArray();
                                    } else if ("no data".equals(result)) {
                                        hhh.setVisibility(View.GONE);
                                        hot_indicator.setVisibility(View.VISIBLE);
                                        hot_indicator.setText("没有数据");
                                        KlbertjCache.hot_ques = new JSONArray();
                                    } else {
                                        hhh.setVisibility(View.VISIBLE);
                                        h_l1.setVisibility(View.VISIBLE);
                                        hot_indicator.setVisibility(View.GONE);
                                        KlbertjCache.hot_ques = JSONArray.parseArray(result);
                                        final JSONObject jj0 = KlbertjCache.hot_ques.getJSONObject(0);
                                        final Integer qqqq0 = jj0.getInteger("qid");



                                        h_t1.setText(jj0.getString("create_time"));
                                        h_n1.setText(jj0.getString("belt_user_name"));
                                        h_c1.setText(String.valueOf(jj0.getInteger("dis_num")));
                                        h_ti1.setText(jj0.getString("title"));
                                        h_l1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                                                    dialogFragment = new CircleDialog.Builder()
                                                            .setProgressText("正在获取数据...")
                                                            .setCanceledOnTouchOutside(false)
                                                            .setCancelable(false)
                                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                            .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            HttpURLConnection urlConnection = null;
                                                            try {
                                                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                                                OutputStream out = urlConnection.getOutputStream();
                                                                JSONObject j = new JSONObject();
                                                                j.put("qid", qqqq0);
                                                                j.put("id", KlbertjCache.uid);
                                                                out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                                                out.close();
                                                                int responseCode = urlConnection.getResponseCode();
                                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                    Thread.sleep(1000);
                                                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (dialogFragment != null) {
                                                                                dialogFragment.dismiss();
                                                                                dialogFragment = null;
                                                                            }

                                                                            if ("f".equals(result)) {
                                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                                            } else if ("bp".equals(result)) {
                                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                                            } else {
                                                                                if (KlbertjCache.prodetail.containsKey(qqqq0)) {
                                                                                    String detail = KlbertjCache.prodetail.get(qqqq0);

                                                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                                                    JSONObject j = JSONObject.parseObject(detail);
                                                                                    if(j!=null){
                                                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                    }


                                                                                    i.putExtra("belong_to_user_uname", jj0.getString("belt_user_name"));
                                                                                    i.putExtra("title", jj0.getString("title"));

                                                                                    i.putExtra("create_time", jj0.getString("create_time"));
                                                                                    i.putExtra("qqq_id", jj0.getInteger("qid"));

                                                                                    i.putExtra("contentStr", detail);
                                                                                    JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                    int is_my = jjjjj.getInteger("is_my");
                                                                                    if (1 != is_my) {
                                                                                        i.putExtra("come_intent", "nmy_ques");
                                                                                        int is_coll = jjjjj.getInteger("is_coll");

                                                                                        i.putExtra("is_coll", is_coll);
                                                                                        int is_att = jjjjj.getInteger("is_att");
                                                                                        i.putExtra("is_att", is_att);
                                                                                    } else {
                                                                                        i.putExtra("come_intent", "my_ques");
                                                                                    }
                                                                                    startActivity(i);
                                                                                } else {
                                                                                    dialogFragment = new CircleDialog.Builder()
                                                                                            .setProgressText("正在获取问题详情...")
                                                                                            .setCanceledOnTouchOutside(false)
                                                                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                            .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                                                    new Thread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            try {
                                                                                                HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                                                OutputStream out1 = urlConnection1.getOutputStream();

                                                                                                out1.write(EncryptionUtils.decryptByByte((qqqq0 + "").getBytes("UTF-8")));
                                                                                                out1.close();
                                                                                                int responseCode1 = urlConnection1.getResponseCode();
                                                                                                if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                                                    final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                                                    Thread.sleep(600);

                                                                                                    if (KlbertjCache.prodetail.size() > 512) {
                                                                                                        int ii = 1;
                                                                                                        for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                                            ii = i;
                                                                                                            break;
                                                                                                        }
                                                                                                        KlbertjCache.prodetail.remove(ii);
                                                                                                    }
                                                                                                    KlbertjCache.prodetail.put(qqqq0, result1);

                                                                                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                                        @Override
                                                                                                        public void run() {
                                                                                                            if (dialogFragment != null) {
                                                                                                                dialogFragment.dismiss();
                                                                                                                dialogFragment = null;
                                                                                                            }

                                                                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                                            JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                                            int is_my = jjjjj.getInteger("is_my");

                                                                                                            if (1 != is_my) {
                                                                                                                i.putExtra("come_intent", "nmy_ques");
                                                                                                                int is_coll = jjjjj.getInteger("is_coll");

                                                                                                                i.putExtra("is_coll", is_coll);
                                                                                                                int is_att = jjjjj.getInteger("is_att");
                                                                                                                i.putExtra("is_att", is_att);
                                                                                                            } else {
                                                                                                                i.putExtra("come_intent", "my_ques");
                                                                                                            }

                                                                                                            JSONObject j = JSONObject.parseObject(result1);
                                                                                                            if(j!=null){
                                                                                                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                                            }



                                                                                                            i.putExtra("contentStr", result1);


                                                                                                            i.putExtra("belong_to_user_uname", jj0.getString("belt_user_name"));
                                                                                                            i.putExtra("title", jj0.getString("title"));

                                                                                                            i.putExtra("create_time", jj0.getString("create_time"));
                                                                                                            i.putExtra("qqq_id", jj0.getInteger("qid"));

                                                                                                            startActivity(i);
                                                                                                        }
                                                                                                    });

                                                                                                }
                                                                                            } catch (Exception e) {
                                                                                                e.printStackTrace();
                                                                                            }

                                                                                        }
                                                                                    }).start();
                                                                                }


                                                                            }

                                                                        }
                                                                    });


                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }).start();

                                                } else {

                                                    if (KlbertjCache.prodetail.containsKey(qqqq0)) {
                                                        String detail = KlbertjCache.prodetail.get(qqqq0);

                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                        JSONObject j = JSONObject.parseObject(detail);
                                                        if(j!=null){
                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                        }
                                                        i.putExtra("come_intent", "un_login");


                                                        i.putExtra("belong_to_user_uname", jj0.getString("belt_user_name"));
                                                        i.putExtra("title", jj0.getString("title"));
                                                        i.putExtra("create_time", jj0.getString("create_time"));
                                                        i.putExtra("qqq_id", jj0.getInteger("qid"));
                                                        i.putExtra("contentStr", detail);

                                                        startActivity(i);

                                                    } else {
                                                        dialogFragment = new CircleDialog.Builder()
                                                                .setProgressText("正在获取数据...")
                                                                .setCanceledOnTouchOutside(false)
                                                                .setCancelable(false)
                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                HttpURLConnection urlConnection = null;
                                                                try {
                                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                                                    OutputStream out = urlConnection.getOutputStream();
                                                                    out.write(EncryptionUtils.decryptByByte((qqqq0 + "").getBytes("UTF-8")));
                                                                    out.close();
                                                                    int responseCode = urlConnection.getResponseCode();
                                                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                        Thread.sleep(1000);
                                                                        if (KlbertjCache.prodetail.size() > 512) {
                                                                            int ii = 1;
                                                                            for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                ii = i;
                                                                                break;
                                                                            }
                                                                            KlbertjCache.prodetail.remove(ii);
                                                                        }
                                                                        KlbertjCache.prodetail.put(qqqq0, result);
                                                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                if (dialogFragment != null) {
                                                                                    dialogFragment.dismiss();
                                                                                    dialogFragment = null;
                                                                                }

                                                                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                                                JSONObject j = JSONObject.parseObject(result);
                                                                                if(j!=null){
                                                                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                }
                                                                                i.putExtra("come_intent", "un_login");


                                                                                i.putExtra("belong_to_user_uname", jj0.getString("belt_user_name"));
                                                                                i.putExtra("title", jj0.getString("title"));
                                                                                i.putExtra("create_time", jj0.getString("create_time"));
                                                                                i.putExtra("qqq_id", jj0.getInteger("qid"));
                                                                                i.putExtra("contentStr", result);

                                                                                startActivity(i);

                                                                            }
                                                                        });

                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }).start();

                                                    }


                                                }
                                            }
                                        });

                                        Glide.with(Objects.requireNonNull(getActivity()))
                                                .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj0.getString("dis_pic_url"))
                                                .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p1);
                                        if (KlbertjCache.hot_ques.size() > 1) {
                                            h_l2.setVisibility(View.VISIBLE);
                                            final JSONObject jj1 = KlbertjCache.hot_ques.getJSONObject(1);
                                            final Integer qqqq1 = jj1.getInteger("qid");

                                            h_t2.setText(jj1.getString("create_time"));
                                            h_n2.setText(jj1.getString("belt_user_name"));
                                            h_c2.setText(String.valueOf(jj1.getInteger("dis_num")));
                                            h_ti2.setText(jj1.getString("title"));
                                            h_l2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {


                                                    if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                                                        dialogFragment = new CircleDialog.Builder()
                                                                .setProgressText("正在获取数据...")
                                                                .setCanceledOnTouchOutside(false)
                                                                .setCancelable(false)
                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                HttpURLConnection urlConnection = null;
                                                                try {
                                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                                                    OutputStream out = urlConnection.getOutputStream();
                                                                    JSONObject j = new JSONObject();
                                                                    j.put("qid", qqqq1);
                                                                    j.put("id", KlbertjCache.uid);
                                                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                                                    out.close();
                                                                    int responseCode = urlConnection.getResponseCode();
                                                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                        Thread.sleep(1000);
                                                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                if (dialogFragment != null) {
                                                                                    dialogFragment.dismiss();
                                                                                    dialogFragment = null;
                                                                                }

                                                                                if ("f".equals(result)) {
                                                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                                                } else if ("bp".equals(result)) {
                                                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                                                } else {
                                                                                    if (KlbertjCache.prodetail.containsKey(qqqq1)) {
                                                                                        String detail = KlbertjCache.prodetail.get(qqqq1);

                                                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                                                        JSONObject j = JSONObject.parseObject(detail);
                                                                                        if(j!=null){
                                                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                        }


                                                                                        i.putExtra("belong_to_user_uname", jj1.getString("belt_user_name"));
                                                                                        i.putExtra("title", jj1.getString("title"));

                                                                                        i.putExtra("create_time", jj1.getString("create_time"));
                                                                                        i.putExtra("qqq_id", jj1.getInteger("qid"));

                                                                                        i.putExtra("contentStr", detail);
                                                                                        JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                        int is_my = jjjjj.getInteger("is_my");
                                                                                        if (1 != is_my) {
                                                                                            i.putExtra("come_intent", "nmy_ques");
                                                                                            int is_coll = jjjjj.getInteger("is_coll");

                                                                                            i.putExtra("is_coll", is_coll);
                                                                                            int is_att = jjjjj.getInteger("is_att");
                                                                                            i.putExtra("is_att", is_att);
                                                                                        } else {
                                                                                            i.putExtra("come_intent", "my_ques");
                                                                                        }
                                                                                        startActivity(i);
                                                                                    } else {
                                                                                        dialogFragment = new CircleDialog.Builder()
                                                                                                .setProgressText("正在获取问题详情...")
                                                                                                .setCanceledOnTouchOutside(false)
                                                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                                                        new Thread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                try {
                                                                                                    HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                                                    OutputStream out1 = urlConnection1.getOutputStream();

                                                                                                    out1.write(EncryptionUtils.decryptByByte((qqqq1 + "").getBytes("UTF-8")));
                                                                                                    out1.close();
                                                                                                    int responseCode1 = urlConnection1.getResponseCode();
                                                                                                    if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                                                        final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                                                        Thread.sleep(600);

                                                                                                        if (KlbertjCache.prodetail.size() > 512) {
                                                                                                            int ii = 1;
                                                                                                            for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                                                ii = i;
                                                                                                                break;
                                                                                                            }
                                                                                                            KlbertjCache.prodetail.remove(ii);
                                                                                                        }
                                                                                                        KlbertjCache.prodetail.put(qqqq1, result1);

                                                                                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                if (dialogFragment != null) {
                                                                                                                    dialogFragment.dismiss();
                                                                                                                    dialogFragment = null;
                                                                                                                }

                                                                                                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                                                JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                                                int is_my = jjjjj.getInteger("is_my");

                                                                                                                if (1 != is_my) {
                                                                                                                    i.putExtra("come_intent", "nmy_ques");
                                                                                                                    int is_coll = jjjjj.getInteger("is_coll");

                                                                                                                    i.putExtra("is_coll", is_coll);
                                                                                                                    int is_att = jjjjj.getInteger("is_att");
                                                                                                                    i.putExtra("is_att", is_att);
                                                                                                                } else {
                                                                                                                    i.putExtra("come_intent", "my_ques");
                                                                                                                }

                                                                                                                JSONObject j = JSONObject.parseObject(result1);
                                                                                                                if(j!=null){
                                                                                                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                                                }



                                                                                                                i.putExtra("contentStr", result1);


                                                                                                                i.putExtra("belong_to_user_uname", jj1.getString("belt_user_name"));
                                                                                                                i.putExtra("title", jj1.getString("title"));

                                                                                                                i.putExtra("create_time", jj1.getString("create_time"));
                                                                                                                i.putExtra("qqq_id", jj1.getInteger("qid"));

                                                                                                                startActivity(i);
                                                                                                            }
                                                                                                        });

                                                                                                    }
                                                                                                } catch (Exception e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        }).start();
                                                                                    }


                                                                                }

                                                                            }
                                                                        });


                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }).start();

                                                    } else {

                                                        if (KlbertjCache.prodetail.containsKey(qqqq1)) {
                                                            String detail = KlbertjCache.prodetail.get(qqqq1);

                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                            JSONObject j = JSONObject.parseObject(detail);
                                                            if(j!=null){
                                                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                            }
                                                            i.putExtra("come_intent", "un_login");


                                                            i.putExtra("belong_to_user_uname", jj1.getString("belt_user_name"));
                                                            i.putExtra("title", jj1.getString("title"));
                                                            i.putExtra("create_time", jj1.getString("create_time"));
                                                            i.putExtra("qqq_id", jj1.getInteger("qid"));
                                                            i.putExtra("contentStr", detail);

                                                            startActivity(i);

                                                        } else {
                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取数据...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setCancelable(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    HttpURLConnection urlConnection = null;
                                                                    try {
                                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                                                        OutputStream out = urlConnection.getOutputStream();
                                                                        out.write(EncryptionUtils.decryptByByte((qqqq1 + "").getBytes("UTF-8")));
                                                                        out.close();
                                                                        int responseCode = urlConnection.getResponseCode();
                                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                            Thread.sleep(1000);
                                                                            if (KlbertjCache.prodetail.size() > 512) {
                                                                                int ii = 1;
                                                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                    ii = i;
                                                                                    break;
                                                                                }
                                                                                KlbertjCache.prodetail.remove(ii);
                                                                            }
                                                                            KlbertjCache.prodetail.put(qqqq1, result);
                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (dialogFragment != null) {
                                                                                        dialogFragment.dismiss();
                                                                                        dialogFragment = null;
                                                                                    }

                                                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                                                    JSONObject j = JSONObject.parseObject(result);
                                                                                    if(j!=null){
                                                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                    }
                                                                                    i.putExtra("come_intent", "un_login");


                                                                                    i.putExtra("belong_to_user_uname", jj1.getString("belt_user_name"));
                                                                                    i.putExtra("title", jj1.getString("title"));
                                                                                    i.putExtra("create_time", jj1.getString("create_time"));
                                                                                    i.putExtra("qqq_id", jj1.getInteger("qid"));
                                                                                    i.putExtra("contentStr", result);

                                                                                    startActivity(i);

                                                                                }
                                                                            });

                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }).start();

                                                        }


                                                    }



                                                }
                                            });
                                            Glide.with(Objects.requireNonNull(getActivity()))
                                                    .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj1.getString("dis_pic_url"))
                                                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p2);
                                        }

                                        if (KlbertjCache.hot_ques.size() > 2) {
                                            h_l3.setVisibility(View.VISIBLE);

                                            final JSONObject jj2 = KlbertjCache.hot_ques.getJSONObject(2);
                                            final Integer qqqq2 = jj2.getInteger("qid");
                                            h_t3.setText(jj2.getString("create_time"));
                                            h_n3.setText(jj2.getString("belt_user_name"));
                                            h_c3.setText(String.valueOf(jj2.getInteger("dis_num")));
                                            h_ti3.setText(jj2.getString("title"));
                                            h_l3.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {



                                                    if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                                                        dialogFragment = new CircleDialog.Builder()
                                                                .setProgressText("正在获取数据...")
                                                                .setCanceledOnTouchOutside(false)
                                                                .setCancelable(false)
                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                HttpURLConnection urlConnection = null;
                                                                try {
                                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                                                    OutputStream out = urlConnection.getOutputStream();
                                                                    JSONObject j = new JSONObject();
                                                                    j.put("qid", qqqq2);
                                                                    j.put("id", KlbertjCache.uid);
                                                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                                                    out.close();
                                                                    int responseCode = urlConnection.getResponseCode();
                                                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                        Thread.sleep(1000);
                                                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                if (dialogFragment != null) {
                                                                                    dialogFragment.dismiss();
                                                                                    dialogFragment = null;
                                                                                }

                                                                                if ("f".equals(result)) {
                                                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                                                } else if ("bp".equals(result)) {
                                                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                                                } else {
                                                                                    if (KlbertjCache.prodetail.containsKey(qqqq2)) {
                                                                                        String detail = KlbertjCache.prodetail.get(qqqq2);

                                                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                                                        JSONObject j = JSONObject.parseObject(detail);
                                                                                        if(j!=null){
                                                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                        }


                                                                                        i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                                                        i.putExtra("title", jj2.getString("title"));

                                                                                        i.putExtra("create_time", jj2.getString("create_time"));
                                                                                        i.putExtra("qqq_id", jj2.getInteger("qid"));

                                                                                        i.putExtra("contentStr", detail);
                                                                                        JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                        int is_my = jjjjj.getInteger("is_my");
                                                                                        if (1 != is_my) {
                                                                                            i.putExtra("come_intent", "nmy_ques");
                                                                                            int is_coll = jjjjj.getInteger("is_coll");

                                                                                            i.putExtra("is_coll", is_coll);
                                                                                            int is_att = jjjjj.getInteger("is_att");
                                                                                            i.putExtra("is_att", is_att);
                                                                                        } else {
                                                                                            i.putExtra("come_intent", "my_ques");
                                                                                        }
                                                                                        startActivity(i);
                                                                                    } else {
                                                                                        dialogFragment = new CircleDialog.Builder()
                                                                                                .setProgressText("正在获取问题详情...")
                                                                                                .setCanceledOnTouchOutside(false)
                                                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                                                        new Thread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                try {
                                                                                                    HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                                                    OutputStream out1 = urlConnection1.getOutputStream();

                                                                                                    out1.write(EncryptionUtils.decryptByByte((qqqq2 + "").getBytes("UTF-8")));
                                                                                                    out1.close();
                                                                                                    int responseCode1 = urlConnection1.getResponseCode();
                                                                                                    if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                                                        final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                                                        Thread.sleep(600);

                                                                                                        if (KlbertjCache.prodetail.size() > 512) {
                                                                                                            int ii = 1;
                                                                                                            for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                                                ii = i;
                                                                                                                break;
                                                                                                            }
                                                                                                            KlbertjCache.prodetail.remove(ii);
                                                                                                        }
                                                                                                        KlbertjCache.prodetail.put(qqqq2, result1);

                                                                                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                if (dialogFragment != null) {
                                                                                                                    dialogFragment.dismiss();
                                                                                                                    dialogFragment = null;
                                                                                                                }

                                                                                                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                                                JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                                                int is_my = jjjjj.getInteger("is_my");

                                                                                                                if (1 != is_my) {
                                                                                                                    i.putExtra("come_intent", "nmy_ques");
                                                                                                                    int is_coll = jjjjj.getInteger("is_coll");

                                                                                                                    i.putExtra("is_coll", is_coll);
                                                                                                                    int is_att = jjjjj.getInteger("is_att");
                                                                                                                    i.putExtra("is_att", is_att);
                                                                                                                } else {
                                                                                                                    i.putExtra("come_intent", "my_ques");
                                                                                                                }

                                                                                                                JSONObject j = JSONObject.parseObject(result1);
                                                                                                                if(j!=null){
                                                                                                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                                                }



                                                                                                                i.putExtra("contentStr", result1);


                                                                                                                i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                                                                                i.putExtra("title", jj2.getString("title"));

                                                                                                                i.putExtra("create_time", jj2.getString("create_time"));
                                                                                                                i.putExtra("qqq_id", jj2.getInteger("qid"));

                                                                                                                startActivity(i);
                                                                                                            }
                                                                                                        });

                                                                                                    }
                                                                                                } catch (Exception e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        }).start();
                                                                                    }


                                                                                }

                                                                            }
                                                                        });


                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }).start();

                                                    } else {

                                                        if (KlbertjCache.prodetail.containsKey(qqqq2)) {
                                                            String detail = KlbertjCache.prodetail.get(qqqq2);

                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                            JSONObject j = JSONObject.parseObject(detail);
                                                            if(j!=null){
                                                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                            }
                                                            i.putExtra("come_intent", "un_login");


                                                            i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                            i.putExtra("title", jj2.getString("title"));
                                                            i.putExtra("create_time", jj2.getString("create_time"));
                                                            i.putExtra("qqq_id", jj2.getInteger("qid"));
                                                            i.putExtra("contentStr", detail);

                                                            startActivity(i);

                                                        } else {
                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取数据...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setCancelable(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    HttpURLConnection urlConnection = null;
                                                                    try {
                                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                                                        OutputStream out = urlConnection.getOutputStream();
                                                                        out.write(EncryptionUtils.decryptByByte((qqqq2 + "").getBytes("UTF-8")));
                                                                        out.close();
                                                                        int responseCode = urlConnection.getResponseCode();
                                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                            Thread.sleep(1000);
                                                                            if (KlbertjCache.prodetail.size() > 512) {
                                                                                int ii = 1;
                                                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                    ii = i;
                                                                                    break;
                                                                                }
                                                                                KlbertjCache.prodetail.remove(ii);
                                                                            }
                                                                            KlbertjCache.prodetail.put(qqqq2, result);
                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (dialogFragment != null) {
                                                                                        dialogFragment.dismiss();
                                                                                        dialogFragment = null;
                                                                                    }

                                                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                                                    JSONObject j = JSONObject.parseObject(result);
                                                                                    if(j!=null){
                                                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                    }
                                                                                    i.putExtra("come_intent", "un_login");


                                                                                    i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                                                    i.putExtra("title", jj2.getString("title"));
                                                                                    i.putExtra("create_time", jj2.getString("create_time"));
                                                                                    i.putExtra("qqq_id", jj2.getInteger("qid"));
                                                                                    i.putExtra("contentStr", result);

                                                                                    startActivity(i);

                                                                                }
                                                                            });

                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }).start();

                                                        }


                                                    }



                                                }
                                            });
                                            Glide.with(Objects.requireNonNull(getActivity()))
                                                    .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj2.getString("dis_pic_url"))
                                                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p3);
                                        }


                                    }
                                }
                            });


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        h_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h_l3.setVisibility(View.GONE);
                h_l2.setVisibility(View.GONE);
                h_l1.setVisibility(View.GONE);

                KlbertjCache.hot_ques.clear();
                KlbertjCache.hot_ques = null;
                dialogFragment = new CircleDialog.Builder()
                        .setProgressText("正在获取数据...")
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)
                        .setProgressStyle(ProgressParams.STYLE_SPINNER)
                        .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection urlConnection = null;
                        try {
                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.hotDiscuss));
                            int responseCode = urlConnection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                Thread.sleep(1000);

                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if ("server error".equals(result)) {
                                            hhh.setVisibility(View.GONE);
                                            hot_indicator.setVisibility(View.VISIBLE);
                                            hot_indicator.setText("服务器错误");
                                            KlbertjCache.hot_ques = new JSONArray();
                                        } else if ("no data".equals(result)) {
                                            hhh.setVisibility(View.GONE);
                                            hot_indicator.setVisibility(View.VISIBLE);
                                            hot_indicator.setText("没有数据");
                                            KlbertjCache.hot_ques = new JSONArray();
                                        } else {
                                            hhh.setVisibility(View.VISIBLE);
                                            h_l1.setVisibility(View.VISIBLE);
                                            hot_indicator.setVisibility(View.GONE);
                                            KlbertjCache.hot_ques = JSONArray.parseArray(result);
                                            final JSONObject jj0 = KlbertjCache.hot_ques.getJSONObject(0);
                                            final Integer qqqq0 = jj0.getInteger("qid");



                                            h_t1.setText(jj0.getString("create_time"));
                                            h_n1.setText(jj0.getString("belt_user_name"));
                                            h_c1.setText(String.valueOf(jj0.getInteger("dis_num")));
                                            h_ti1.setText(jj0.getString("title"));
                                            h_l1.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                                                        dialogFragment = new CircleDialog.Builder()
                                                                .setProgressText("正在获取数据...")
                                                                .setCanceledOnTouchOutside(false)
                                                                .setCancelable(false)
                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                HttpURLConnection urlConnection = null;
                                                                try {
                                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                                                    OutputStream out = urlConnection.getOutputStream();
                                                                    JSONObject j = new JSONObject();
                                                                    j.put("qid", qqqq0);
                                                                    j.put("id", KlbertjCache.uid);
                                                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                                                    out.close();
                                                                    int responseCode = urlConnection.getResponseCode();
                                                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                        Thread.sleep(1000);
                                                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                if (dialogFragment != null) {
                                                                                    dialogFragment.dismiss();
                                                                                    dialogFragment = null;
                                                                                }

                                                                                if ("f".equals(result)) {
                                                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                                                } else if ("bp".equals(result)) {
                                                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                                                } else {
                                                                                    if (KlbertjCache.prodetail.containsKey(qqqq0)) {
                                                                                        String detail = KlbertjCache.prodetail.get(qqqq0);

                                                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                                                        JSONObject j = JSONObject.parseObject(detail);
                                                                                        if(j!=null){
                                                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                        }


                                                                                        i.putExtra("belong_to_user_uname", jj0.getString("belt_user_name"));
                                                                                        i.putExtra("title", jj0.getString("title"));

                                                                                        i.putExtra("create_time", jj0.getString("create_time"));
                                                                                        i.putExtra("qqq_id", jj0.getInteger("qid"));

                                                                                        i.putExtra("contentStr", detail);
                                                                                        JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                        int is_my = jjjjj.getInteger("is_my");
                                                                                        if (1 != is_my) {
                                                                                            i.putExtra("come_intent", "nmy_ques");
                                                                                            int is_coll = jjjjj.getInteger("is_coll");

                                                                                            i.putExtra("is_coll", is_coll);
                                                                                            int is_att = jjjjj.getInteger("is_att");
                                                                                            i.putExtra("is_att", is_att);
                                                                                        } else {
                                                                                            i.putExtra("come_intent", "my_ques");
                                                                                        }
                                                                                        startActivity(i);
                                                                                    } else {
                                                                                        dialogFragment = new CircleDialog.Builder()
                                                                                                .setProgressText("正在获取问题详情...")
                                                                                                .setCanceledOnTouchOutside(false)
                                                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                                                        new Thread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                try {
                                                                                                    HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                                                    OutputStream out1 = urlConnection1.getOutputStream();

                                                                                                    out1.write(EncryptionUtils.decryptByByte((qqqq0 + "").getBytes("UTF-8")));
                                                                                                    out1.close();
                                                                                                    int responseCode1 = urlConnection1.getResponseCode();
                                                                                                    if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                                                        final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                                                        Thread.sleep(600);

                                                                                                        if (KlbertjCache.prodetail.size() > 512) {
                                                                                                            int ii = 1;
                                                                                                            for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                                                ii = i;
                                                                                                                break;
                                                                                                            }
                                                                                                            KlbertjCache.prodetail.remove(ii);
                                                                                                        }
                                                                                                        KlbertjCache.prodetail.put(qqqq0, result1);

                                                                                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                                            @Override
                                                                                                            public void run() {
                                                                                                                if (dialogFragment != null) {
                                                                                                                    dialogFragment.dismiss();
                                                                                                                    dialogFragment = null;
                                                                                                                }

                                                                                                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                                                JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                                                int is_my = jjjjj.getInteger("is_my");

                                                                                                                if (1 != is_my) {
                                                                                                                    i.putExtra("come_intent", "nmy_ques");
                                                                                                                    int is_coll = jjjjj.getInteger("is_coll");

                                                                                                                    i.putExtra("is_coll", is_coll);
                                                                                                                    int is_att = jjjjj.getInteger("is_att");
                                                                                                                    i.putExtra("is_att", is_att);
                                                                                                                } else {
                                                                                                                    i.putExtra("come_intent", "my_ques");
                                                                                                                }

                                                                                                                JSONObject j = JSONObject.parseObject(result1);
                                                                                                                if(j!=null){
                                                                                                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                                                }



                                                                                                                i.putExtra("contentStr", result1);


                                                                                                                i.putExtra("belong_to_user_uname", jj0.getString("belt_user_name"));
                                                                                                                i.putExtra("title", jj0.getString("title"));

                                                                                                                i.putExtra("create_time", jj0.getString("create_time"));
                                                                                                                i.putExtra("qqq_id", jj0.getInteger("qid"));

                                                                                                                startActivity(i);
                                                                                                            }
                                                                                                        });

                                                                                                    }
                                                                                                } catch (Exception e) {
                                                                                                    e.printStackTrace();
                                                                                                }

                                                                                            }
                                                                                        }).start();
                                                                                    }


                                                                                }

                                                                            }
                                                                        });


                                                                    }
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }).start();

                                                    } else {

                                                        if (KlbertjCache.prodetail.containsKey(qqqq0)) {
                                                            String detail = KlbertjCache.prodetail.get(qqqq0);

                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                            JSONObject j = JSONObject.parseObject(detail);
                                                            if(j!=null){
                                                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                            }
                                                            i.putExtra("come_intent", "un_login");


                                                            i.putExtra("belong_to_user_uname", jj0.getString("belt_user_name"));
                                                            i.putExtra("title", jj0.getString("title"));
                                                            i.putExtra("create_time", jj0.getString("create_time"));
                                                            i.putExtra("qqq_id", jj0.getInteger("qid"));
                                                            i.putExtra("contentStr", detail);

                                                            startActivity(i);

                                                        } else {
                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取数据...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setCancelable(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    HttpURLConnection urlConnection = null;
                                                                    try {
                                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                                                        OutputStream out = urlConnection.getOutputStream();
                                                                        out.write(EncryptionUtils.decryptByByte((qqqq0 + "").getBytes("UTF-8")));
                                                                        out.close();
                                                                        int responseCode = urlConnection.getResponseCode();
                                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                            Thread.sleep(1000);
                                                                            if (KlbertjCache.prodetail.size() > 512) {
                                                                                int ii = 1;
                                                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                    ii = i;
                                                                                    break;
                                                                                }
                                                                                KlbertjCache.prodetail.remove(ii);
                                                                            }
                                                                            KlbertjCache.prodetail.put(qqqq0, result);
                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (dialogFragment != null) {
                                                                                        dialogFragment.dismiss();
                                                                                        dialogFragment = null;
                                                                                    }

                                                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                                                    JSONObject j = JSONObject.parseObject(result);
                                                                                    if(j!=null){
                                                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                    }
                                                                                    i.putExtra("come_intent", "un_login");


                                                                                    i.putExtra("belong_to_user_uname", jj0.getString("belt_user_name"));
                                                                                    i.putExtra("title", jj0.getString("title"));
                                                                                    i.putExtra("create_time", jj0.getString("create_time"));
                                                                                    i.putExtra("qqq_id", jj0.getInteger("qid"));
                                                                                    i.putExtra("contentStr", result);

                                                                                    startActivity(i);

                                                                                }
                                                                            });

                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }).start();

                                                        }


                                                    }
                                                }
                                            });

                                            Glide.with(Objects.requireNonNull(getActivity()))
                                                    .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj0.getString("dis_pic_url"))
                                                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p1);
                                            if (KlbertjCache.hot_ques.size() > 1) {
                                                h_l2.setVisibility(View.VISIBLE);
                                                final JSONObject jj1 = KlbertjCache.hot_ques.getJSONObject(1);
                                                final Integer qqqq1 = jj1.getInteger("qid");

                                                h_t2.setText(jj1.getString("create_time"));
                                                h_n2.setText(jj1.getString("belt_user_name"));
                                                h_c2.setText(String.valueOf(jj1.getInteger("dis_num")));
                                                h_ti2.setText(jj1.getString("title"));
                                                h_l2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {


                                                        if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取数据...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setCancelable(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    HttpURLConnection urlConnection = null;
                                                                    try {
                                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                                                        OutputStream out = urlConnection.getOutputStream();
                                                                        JSONObject j = new JSONObject();
                                                                        j.put("qid", qqqq1);
                                                                        j.put("id", KlbertjCache.uid);
                                                                        out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                                                        out.close();
                                                                        int responseCode = urlConnection.getResponseCode();
                                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                            Thread.sleep(1000);
                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (dialogFragment != null) {
                                                                                        dialogFragment.dismiss();
                                                                                        dialogFragment = null;
                                                                                    }

                                                                                    if ("f".equals(result)) {
                                                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                                                    } else if ("bp".equals(result)) {
                                                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                                                    } else {
                                                                                        if (KlbertjCache.prodetail.containsKey(qqqq1)) {
                                                                                            String detail = KlbertjCache.prodetail.get(qqqq1);

                                                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                                                            JSONObject j = JSONObject.parseObject(detail);
                                                                                            if(j!=null){
                                                                                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                            }


                                                                                            i.putExtra("belong_to_user_uname", jj1.getString("belt_user_name"));
                                                                                            i.putExtra("title", jj1.getString("title"));

                                                                                            i.putExtra("create_time", jj1.getString("create_time"));
                                                                                            i.putExtra("qqq_id", jj1.getInteger("qid"));

                                                                                            i.putExtra("contentStr", detail);
                                                                                            JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                            int is_my = jjjjj.getInteger("is_my");
                                                                                            if (1 != is_my) {
                                                                                                i.putExtra("come_intent", "nmy_ques");
                                                                                                int is_coll = jjjjj.getInteger("is_coll");

                                                                                                i.putExtra("is_coll", is_coll);
                                                                                                int is_att = jjjjj.getInteger("is_att");
                                                                                                i.putExtra("is_att", is_att);
                                                                                            } else {
                                                                                                i.putExtra("come_intent", "my_ques");
                                                                                            }
                                                                                            startActivity(i);
                                                                                        } else {
                                                                                            dialogFragment = new CircleDialog.Builder()
                                                                                                    .setProgressText("正在获取问题详情...")
                                                                                                    .setCanceledOnTouchOutside(false)
                                                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                                                            new Thread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    try {
                                                                                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                                                        OutputStream out1 = urlConnection1.getOutputStream();

                                                                                                        out1.write(EncryptionUtils.decryptByByte((qqqq1 + "").getBytes("UTF-8")));
                                                                                                        out1.close();
                                                                                                        int responseCode1 = urlConnection1.getResponseCode();
                                                                                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                                                            Thread.sleep(600);

                                                                                                            if (KlbertjCache.prodetail.size() > 512) {
                                                                                                                int ii = 1;
                                                                                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                                                    ii = i;
                                                                                                                    break;
                                                                                                                }
                                                                                                                KlbertjCache.prodetail.remove(ii);
                                                                                                            }
                                                                                                            KlbertjCache.prodetail.put(qqqq1, result1);

                                                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    if (dialogFragment != null) {
                                                                                                                        dialogFragment.dismiss();
                                                                                                                        dialogFragment = null;
                                                                                                                    }

                                                                                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                                                    JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                                                    int is_my = jjjjj.getInteger("is_my");

                                                                                                                    if (1 != is_my) {
                                                                                                                        i.putExtra("come_intent", "nmy_ques");
                                                                                                                        int is_coll = jjjjj.getInteger("is_coll");

                                                                                                                        i.putExtra("is_coll", is_coll);
                                                                                                                        int is_att = jjjjj.getInteger("is_att");
                                                                                                                        i.putExtra("is_att", is_att);
                                                                                                                    } else {
                                                                                                                        i.putExtra("come_intent", "my_ques");
                                                                                                                    }

                                                                                                                    JSONObject j = JSONObject.parseObject(result1);
                                                                                                                    if(j!=null){
                                                                                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                                                    }



                                                                                                                    i.putExtra("contentStr", result1);


                                                                                                                    i.putExtra("belong_to_user_uname", jj1.getString("belt_user_name"));
                                                                                                                    i.putExtra("title", jj1.getString("title"));

                                                                                                                    i.putExtra("create_time", jj1.getString("create_time"));
                                                                                                                    i.putExtra("qqq_id", jj1.getInteger("qid"));

                                                                                                                    startActivity(i);
                                                                                                                }
                                                                                                            });

                                                                                                        }
                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                    }

                                                                                                }
                                                                                            }).start();
                                                                                        }


                                                                                    }

                                                                                }
                                                                            });


                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }).start();

                                                        } else {

                                                            if (KlbertjCache.prodetail.containsKey(qqqq1)) {
                                                                String detail = KlbertjCache.prodetail.get(qqqq1);

                                                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                                JSONObject j = JSONObject.parseObject(detail);
                                                                if(j!=null){
                                                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                }
                                                                i.putExtra("come_intent", "un_login");


                                                                i.putExtra("belong_to_user_uname", jj1.getString("belt_user_name"));
                                                                i.putExtra("title", jj1.getString("title"));
                                                                i.putExtra("create_time", jj1.getString("create_time"));
                                                                i.putExtra("qqq_id", jj1.getInteger("qid"));
                                                                i.putExtra("contentStr", detail);

                                                                startActivity(i);

                                                            } else {
                                                                dialogFragment = new CircleDialog.Builder()
                                                                        .setProgressText("正在获取数据...")
                                                                        .setCanceledOnTouchOutside(false)
                                                                        .setCancelable(false)
                                                                        .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                        .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                                new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        HttpURLConnection urlConnection = null;
                                                                        try {
                                                                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                                                            OutputStream out = urlConnection.getOutputStream();
                                                                            out.write(EncryptionUtils.decryptByByte((qqqq1 + "").getBytes("UTF-8")));
                                                                            out.close();
                                                                            int responseCode = urlConnection.getResponseCode();
                                                                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                                Thread.sleep(1000);
                                                                                if (KlbertjCache.prodetail.size() > 512) {
                                                                                    int ii = 1;
                                                                                    for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                        ii = i;
                                                                                        break;
                                                                                    }
                                                                                    KlbertjCache.prodetail.remove(ii);
                                                                                }
                                                                                KlbertjCache.prodetail.put(qqqq1, result);
                                                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        if (dialogFragment != null) {
                                                                                            dialogFragment.dismiss();
                                                                                            dialogFragment = null;
                                                                                        }

                                                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                                                        JSONObject j = JSONObject.parseObject(result);
                                                                                        if(j!=null){
                                                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                        }
                                                                                        i.putExtra("come_intent", "un_login");


                                                                                        i.putExtra("belong_to_user_uname", jj1.getString("belt_user_name"));
                                                                                        i.putExtra("title", jj1.getString("title"));
                                                                                        i.putExtra("create_time", jj1.getString("create_time"));
                                                                                        i.putExtra("qqq_id", jj1.getInteger("qid"));
                                                                                        i.putExtra("contentStr", result);

                                                                                        startActivity(i);

                                                                                    }
                                                                                });

                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }).start();

                                                            }


                                                        }



                                                    }
                                                });
                                                Glide.with(Objects.requireNonNull(getActivity()))
                                                        .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj1.getString("dis_pic_url"))
                                                        .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p2);
                                            }

                                            if (KlbertjCache.hot_ques.size() > 2) {
                                                h_l3.setVisibility(View.VISIBLE);

                                                final JSONObject jj2 = KlbertjCache.hot_ques.getJSONObject(2);
                                                final Integer qqqq2 = jj2.getInteger("qid");
                                                h_t3.setText(jj2.getString("create_time"));
                                                h_n3.setText(jj2.getString("belt_user_name"));
                                                h_c3.setText(String.valueOf(jj2.getInteger("dis_num")));
                                                h_ti3.setText(jj2.getString("title"));
                                                h_l3.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {



                                                        if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取数据...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setCancelable(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    HttpURLConnection urlConnection = null;
                                                                    try {
                                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                                                        OutputStream out = urlConnection.getOutputStream();
                                                                        JSONObject j = new JSONObject();
                                                                        j.put("qid", qqqq2);
                                                                        j.put("id", KlbertjCache.uid);
                                                                        out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                                                        out.close();
                                                                        int responseCode = urlConnection.getResponseCode();
                                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                            Thread.sleep(1000);
                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (dialogFragment != null) {
                                                                                        dialogFragment.dismiss();
                                                                                        dialogFragment = null;
                                                                                    }

                                                                                    if ("f".equals(result)) {
                                                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                                                                    } else if ("bp".equals(result)) {
                                                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                                                                    } else {
                                                                                        if (KlbertjCache.prodetail.containsKey(qqqq2)) {
                                                                                            String detail = KlbertjCache.prodetail.get(qqqq2);

                                                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                                                            JSONObject j = JSONObject.parseObject(detail);
                                                                                            if(j!=null){
                                                                                                Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                                i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                            }


                                                                                            i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                                                            i.putExtra("title", jj2.getString("title"));

                                                                                            i.putExtra("create_time", jj2.getString("create_time"));
                                                                                            i.putExtra("qqq_id", jj2.getInteger("qid"));

                                                                                            i.putExtra("contentStr", detail);
                                                                                            JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                            int is_my = jjjjj.getInteger("is_my");
                                                                                            if (1 != is_my) {
                                                                                                i.putExtra("come_intent", "nmy_ques");
                                                                                                int is_coll = jjjjj.getInteger("is_coll");

                                                                                                i.putExtra("is_coll", is_coll);
                                                                                                int is_att = jjjjj.getInteger("is_att");
                                                                                                i.putExtra("is_att", is_att);
                                                                                            } else {
                                                                                                i.putExtra("come_intent", "my_ques");
                                                                                            }
                                                                                            startActivity(i);
                                                                                        } else {
                                                                                            dialogFragment = new CircleDialog.Builder()
                                                                                                    .setProgressText("正在获取问题详情...")
                                                                                                    .setCanceledOnTouchOutside(false)
                                                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                                    .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                                                            new Thread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    try {
                                                                                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getHotQuestionDetail));
                                                                                                        OutputStream out1 = urlConnection1.getOutputStream();

                                                                                                        out1.write(EncryptionUtils.decryptByByte((qqqq2 + "").getBytes("UTF-8")));
                                                                                                        out1.close();
                                                                                                        int responseCode1 = urlConnection1.getResponseCode();
                                                                                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                                                            Thread.sleep(600);

                                                                                                            if (KlbertjCache.prodetail.size() > 512) {
                                                                                                                int ii = 1;
                                                                                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                                                    ii = i;
                                                                                                                    break;
                                                                                                                }
                                                                                                                KlbertjCache.prodetail.remove(ii);
                                                                                                            }
                                                                                                            KlbertjCache.prodetail.put(qqqq2, result1);

                                                                                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    if (dialogFragment != null) {
                                                                                                                        dialogFragment.dismiss();
                                                                                                                        dialogFragment = null;
                                                                                                                    }

                                                                                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                                                                    JSONObject jjjjj = JSONObject.parseObject(result);
                                                                                                                    int is_my = jjjjj.getInteger("is_my");

                                                                                                                    if (1 != is_my) {
                                                                                                                        i.putExtra("come_intent", "nmy_ques");
                                                                                                                        int is_coll = jjjjj.getInteger("is_coll");

                                                                                                                        i.putExtra("is_coll", is_coll);
                                                                                                                        int is_att = jjjjj.getInteger("is_att");
                                                                                                                        i.putExtra("is_att", is_att);
                                                                                                                    } else {
                                                                                                                        i.putExtra("come_intent", "my_ques");
                                                                                                                    }

                                                                                                                    JSONObject j = JSONObject.parseObject(result1);
                                                                                                                    if(j!=null){
                                                                                                                        Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                                                        i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                                                        i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                                                    }



                                                                                                                    i.putExtra("contentStr", result1);


                                                                                                                    i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                                                                                    i.putExtra("title", jj2.getString("title"));

                                                                                                                    i.putExtra("create_time", jj2.getString("create_time"));
                                                                                                                    i.putExtra("qqq_id", jj2.getInteger("qid"));

                                                                                                                    startActivity(i);
                                                                                                                }
                                                                                                            });

                                                                                                        }
                                                                                                    } catch (Exception e) {
                                                                                                        e.printStackTrace();
                                                                                                    }

                                                                                                }
                                                                                            }).start();
                                                                                        }


                                                                                    }

                                                                                }
                                                                            });


                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }).start();

                                                        } else {

                                                            if (KlbertjCache.prodetail.containsKey(qqqq2)) {
                                                                String detail = KlbertjCache.prodetail.get(qqqq2);

                                                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);

                                                                JSONObject j = JSONObject.parseObject(detail);
                                                                if(j!=null){
                                                                    Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                    i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                    i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                    i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                }
                                                                i.putExtra("come_intent", "un_login");


                                                                i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                                i.putExtra("title", jj2.getString("title"));
                                                                i.putExtra("create_time", jj2.getString("create_time"));
                                                                i.putExtra("qqq_id", jj2.getInteger("qid"));
                                                                i.putExtra("contentStr", detail);

                                                                startActivity(i);

                                                            } else {
                                                                dialogFragment = new CircleDialog.Builder()
                                                                        .setProgressText("正在获取数据...")
                                                                        .setCanceledOnTouchOutside(false)
                                                                        .setCancelable(false)
                                                                        .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                        .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                                new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        HttpURLConnection urlConnection = null;
                                                                        try {
                                                                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                                                            OutputStream out = urlConnection.getOutputStream();
                                                                            out.write(EncryptionUtils.decryptByByte((qqqq2 + "").getBytes("UTF-8")));
                                                                            out.close();
                                                                            int responseCode = urlConnection.getResponseCode();
                                                                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                                Thread.sleep(1000);
                                                                                if (KlbertjCache.prodetail.size() > 512) {
                                                                                    int ii = 1;
                                                                                    for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                                        ii = i;
                                                                                        break;
                                                                                    }
                                                                                    KlbertjCache.prodetail.remove(ii);
                                                                                }
                                                                                KlbertjCache.prodetail.put(qqqq2, result);
                                                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        if (dialogFragment != null) {
                                                                                            dialogFragment.dismiss();
                                                                                            dialogFragment = null;
                                                                                        }

                                                                                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);


                                                                                        JSONObject j = JSONObject.parseObject(result);
                                                                                        if(j!=null){
                                                                                            Log.i("fdfc",Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL+Cons.AVATOR_PICS_PATH_IN_SERVER+"/"+j.getString("ava"));
                                                                                            i.putExtra("label", Objects.requireNonNull(j).getString("label"));
                                                                                            i.putExtra("belt_uid", Objects.requireNonNull(j).getInteger("belt_uid"));
                                                                                        }
                                                                                        i.putExtra("come_intent", "un_login");


                                                                                        i.putExtra("belong_to_user_uname", jj2.getString("belt_user_name"));
                                                                                        i.putExtra("title", jj2.getString("title"));
                                                                                        i.putExtra("create_time", jj2.getString("create_time"));
                                                                                        i.putExtra("qqq_id", jj2.getInteger("qid"));
                                                                                        i.putExtra("contentStr", result);

                                                                                        startActivity(i);

                                                                                    }
                                                                                });

                                                                            }
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }).start();

                                                            }


                                                        }



                                                    }
                                                });
                                                Glide.with(Objects.requireNonNull(getActivity()))
                                                        .load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + jj2.getString("dis_pic_url"))
                                                        .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(h_p3);
                                            }

















                                        }
                                        if (dialogFragment != null) {
                                            dialogFragment.dismiss();
                                            dialogFragment = null;
                                        }

                                    }
                                });


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });

        fl = root.findViewById(R.id.fl);
        text = root.findViewById(R.id.text);
        appBar = root.findViewById(R.id.appbar);

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //防止初始化进来两次
                i++;
                if (i <= 2) {
                    llOffDistance = ((FrameLayout.LayoutParams) text.getLayoutParams()).topMargin;


                    return;

                }

                if (params == null) {
                    params = (FrameLayout.LayoutParams) text.getLayoutParams();

//                        llOffDistance = params.topMargin;


                    isUp = true;
                    isDown = true;
                }

                float distance = llOffDistance + verticalOffset;
                //滑倒顶端状态 保持20的间距
//                Log.i(tag, "distance=======" + distance + "llOffDistance==" + llOffDistance + "verticalOffset==" + verticalOffset);
                if (distance <= 150) {
                    distance = 150;
//                    Log.i(tag, "distance<<<<<<150");
                    startScroll();
                }
                //滑倒底端状态
                if (verticalOffset == 0) {

                    if (isDown && !text.isIncrease()) {
                        text.startScroll();
                    }
                }
                params.topMargin = (int) distance;
                text.requestLayout();
            }
        });

        toolbarLayout = root.findViewById(R.id.toolbarLayout);

        text.setFoldListener(new FlodableButton.FoldListener() {
            @Override
            public void onFold(boolean isIncrease, FlodableButton sfb) {
                if (isIncrease) {

                    isUp = true;
                } else
                    isDown = true;
            }
        });

        text.setOnClickListener(new FlodableButton.OnClickListener() {
            @Override
            public void onClick(FlodableButton sfb) {
                Home.main_pager.setCurrentItem(1, true);
                Home.bottomNavigationView.selectTab(1);
            }
        });

        StatusBarUtil.immersive(getActivity());
        gotop = root.findViewById(R.id.gotop);
        got = root.findViewById(R.id.got);
        banner = root.findViewById(R.id.banner);
        banner.setImageLoader(new BannerImageLoader());
        banner.setImages(Arrays.asList("http://112.74.53.233:8888/p/banner/p1.jpg", "http://112.74.53.233:8888/p/banner/p2.jpg", "http://112.74.53.233:8888/p/banner/p3.jpg"));
        banner.start();


        final RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        gotop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getActivity())));
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), VERTICAL));
        mAdapter = new IndexQuickAdapter();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final int qid = mAdapter.getData().get(position).id;
                final ProblemDigest p = mAdapter.getData().get(position);

                if (!KlbertjCache.uid.equals(Cons.NOT_LOGIN)) {


                    dialogFragment = new CircleDialog.Builder()
                            .setProgressText("正在获取数据...")
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(false)
                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                            .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection urlConnection = null;
                            try {
                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.isMyQues));
                                OutputStream out = urlConnection.getOutputStream();
                                JSONObject j = new JSONObject();
                                j.put("qid", qid);
                                j.put("id", KlbertjCache.uid);
                                out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                out.close();
                                int responseCode = urlConnection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                    Thread.sleep(1000);
                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialogFragment != null) {
                                                dialogFragment.dismiss();
                                                dialogFragment = null;
                                            }

                                            if ("f".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限错误");

                                            } else if ("bp".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "问题已经被删除");

                                            } else {
                                                if (KlbertjCache.prodetail.containsKey(p.id)) {
                                                    String detail = KlbertjCache.prodetail.get(p.id);

                                                    Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                    JSONObject jjjjj = JSONObject.parseObject(result);
                                                    int is_my = jjjjj.getInteger("is_my");

                                                    if (1 != is_my) {
                                                        i.putExtra("come_intent", "nmy_ques");
                                                        int is_coll = jjjjj.getInteger("is_coll");

                                                        i.putExtra("is_coll", is_coll);
                                                        int is_att = jjjjj.getInteger("is_att");
                                                        i.putExtra("is_att", is_att);
                                                    } else {
                                                        i.putExtra("come_intent", "my_ques");
                                                    }

                                                    i.putExtra("belongToUserHead_url", p.belongToUserHead_url);
                                                    i.putExtra("belong_to_user_uname", p.belongToUser);
                                                    i.putExtra("title", p.pro_title);
                                                    i.putExtra("label", p.labels);
                                                    i.putExtra("create_time", p.time);
                                                    i.putExtra("qqq_id", p.id);
                                                    i.putExtra("belt_uid", p.belt_uid);
                                                    i.putExtra("contentStr", detail);
                                                    startActivity(i);
                                                } else {
                                                    dialogFragment = new CircleDialog.Builder()
                                                            .setProgressText("正在获取问题详情...")
                                                            .setCanceledOnTouchOutside(false)
                                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                            .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                                                OutputStream out1 = urlConnection1.getOutputStream();

                                                                out1.write(EncryptionUtils.decryptByByte((qid + "").getBytes("UTF-8")));
                                                                out1.close();
                                                                int responseCode1 = urlConnection1.getResponseCode();
                                                                if (responseCode1 == HttpURLConnection.HTTP_OK) {


                                                                    final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。
                                                                    Thread.sleep(600);

                                                                    if (KlbertjCache.prodetail.size() > 512) {
                                                                        int ii = 1;
                                                                        for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                            ii = i;
                                                                            break;
                                                                        }
                                                                        KlbertjCache.prodetail.remove(ii);
                                                                    }
                                                                    KlbertjCache.prodetail.put(p.id, result1);

                                                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (dialogFragment != null) {
                                                                                dialogFragment.dismiss();
                                                                                dialogFragment = null;
                                                                            }

                                                                            Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                                            JSONObject jjjjj = JSONObject.parseObject(result);
                                                                            int is_my = jjjjj.getInteger("is_my");

                                                                            if (1 != is_my) {
                                                                                i.putExtra("come_intent", "nmy_ques");
                                                                                int is_coll = jjjjj.getInteger("is_coll");

                                                                                i.putExtra("is_coll", is_coll);
                                                                                int is_att = jjjjj.getInteger("is_att");
                                                                                i.putExtra("is_att", is_att);
                                                                            } else {
                                                                                i.putExtra("come_intent", "my_ques");
                                                                            }

                                                                            i.putExtra("belongToUserHead_url", p.belongToUserHead_url);
                                                                            i.putExtra("belong_to_user_uname", p.belongToUser);
                                                                            i.putExtra("title", p.pro_title);
                                                                            i.putExtra("label", p.labels);
                                                                            i.putExtra("create_time", p.time);
                                                                            i.putExtra("qqq_id", p.id);
                                                                            i.putExtra("belt_uid", p.belt_uid);

                                                                            i.putExtra("contentStr", result1);

                                                                            startActivity(i);
                                                                        }
                                                                    });

                                                                }
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    }).start();
                                                }


                                            }

                                        }
                                    });


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                } else {

                    if (KlbertjCache.prodetail.containsKey(p.id)) {
                        String detail = KlbertjCache.prodetail.get(p.id);

                        Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                        i.putExtra("come_intent", "un_login");
                        i.putExtra("belongToUserHead_url", p.belongToUserHead_url);
                        i.putExtra("belong_to_user_uname", p.belongToUser);
                        i.putExtra("title", p.pro_title);
                        i.putExtra("label", p.labels);
                        i.putExtra("create_time", p.time);
                        i.putExtra("qqq_id", p.id);
                        i.putExtra("belt_uid", p.belt_uid);
                        i.putExtra("contentStr", detail);

                        startActivity(i);

                    } else {
                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在获取数据...")
                                .setCanceledOnTouchOutside(false)
                                .setCancelable(false)
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                    OutputStream out = urlConnection.getOutputStream();
                                    out.write(EncryptionUtils.decryptByByte((qid + "").getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                        Thread.sleep(1000);
                                        if (KlbertjCache.prodetail.size() > 512) {
                                            int ii = 1;
                                            for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                ii = i;
                                                break;
                                            }
                                            KlbertjCache.prodetail.remove(ii);
                                        }
                                        KlbertjCache.prodetail.put(p.id, result);
                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }

                                                Intent i = new Intent(Objects.requireNonNull(getActivity()), ProblemDetail.class);
                                                i.putExtra("come_intent", "un_login");
                                                i.putExtra("belongToUserHead_url", p.belongToUserHead_url);
                                                i.putExtra("belong_to_user_uname", p.belongToUser);
                                                i.putExtra("title", p.pro_title);
                                                i.putExtra("label", p.labels);
                                                i.putExtra("create_time", p.time);
                                                i.putExtra("qqq_id", p.id);
                                                i.putExtra("belt_uid", p.belt_uid);
                                                i.putExtra("contentStr", result);

                                                startActivity(i);

                                            }
                                        });

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }


                }
            }
        });
        if (KlbertjCache.index_ques_first_page != null && !KlbertjCache.index_ques_first_page.isEmpty()) {
            if (mAdapter == null) {
                mAdapter = new IndexQuickAdapter();
            }
            mAdapter.replaceData(KlbertjCache.index_ques_first_page);
            recyclerView.setAdapter(mAdapter);
        } else {
            if (dialogFragment == null) {
                dialogFragment = new CircleDialog.Builder()
                        .setProgressText("正在获取数据...")
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(false)
                        .setProgressStyle(ProgressParams.STYLE_SPINNER)
                        .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());

            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection urlConnection = null;
                    try {
                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionByPage));
                        OutputStream out = urlConnection.getOutputStream();

                        out.write(EncryptionUtils.decryptByByte("1".getBytes("UTF-8")));
                        out.close();
                        int responseCode = urlConnection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。


                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialogFragment != null) {
                                        dialogFragment.dismiss();
                                        dialogFragment = null;
                                    }
                                    // ToastUtils.showToast(getActivity(),result);
                                    if ("no".equals(result)) {
                                        indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                        indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                            add(new ProblemDigest());
                                        }});
                                        recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                        refreshLayout.setEnableLoadMore(false);
                                        refreshLayout.setEnableRefresh(true);
                                        KlbertjCache.index_ques_first_page = new ArrayList<ProblemDigest>();
                                    } else if ("e".equals(result)) {
                                        indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                        indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                            add(new ProblemDigest());
                                        }});
                                        recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                        refreshLayout.setEnableLoadMore(false);
                                        refreshLayout.setEnableRefresh(true);
//                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                        KlbertjCache.index_ques_first_page = new ArrayList<ProblemDigest>();
                                    } else {
                                        Log.i("aassaass", result + "fdfdsfdsfdkpods===================");
                                        JSONArray jsonArray = JSONArray.parseArray(result);

                                        if (jsonArray != null && !jsonArray.isEmpty()) {
                                            List<ProblemDigest> problemDigests = new ArrayList<>();
                                            JSONObject j = null;
                                            ProblemDigest problemDigest1 = null;
                                            // ToastUtils.showToast(getActivity(),"j.getString(\"belong_to_user_head_url\")"+j.getString("belong_to_user_head_url"));
                                            for (int i = 0; i < jsonArray.size(); i++) {
                                                j = jsonArray.getJSONObject(i);
//                                                ToastUtils.showToast(getActivity(), "j.getString(\"belong_to_user_head_url\")" + j.getString("belong_to_user_head_url"));
                                                problemDigest1 = new ProblemDigest();
                                                problemDigest1.pro_title = j.getString("title");
                                                problemDigest1.belongToUserHead_url = Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + j.getString("belong_to_user_head_url");
                                                problemDigest1.belongToUser = j.getString("belong_to_user_uname");
                                                problemDigest1.num_of_discussion = j.getInteger("comments_count") + "";
                                                problemDigest1.time = j.getString("create_time");
                                                problemDigest1.q_belt_subject = j.getString("q_belt_subject");
                                                problemDigest1.pro_digest = j.getString("digest");
                                                problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                problemDigest1.labels = j.getString("label");
                                                problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                problemDigest1.id = j.getInteger("id");
                                                problemDigests.add(problemDigest1);
                                            }

                                            KlbertjCache.index_ques_first_page = problemDigests;
                                            if (mAdapter == null)
                                                mAdapter = new IndexQuickAdapter();
                                            mAdapter.replaceData(KlbertjCache.index_ques_first_page);
                                            recyclerView.setAdapter(mAdapter);
                                            refreshLayout.setEnableLoadMore(true);
                                            refreshLayout.setEnableRefresh(true);
                                        } else {
                                            indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                            indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                add(new ProblemDigest());
                                            }});
                                            recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                            refreshLayout.setEnableLoadMore(false);
                                            refreshLayout.setEnableRefresh(true);
//                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "数据异常");
                                            KlbertjCache.index_ques_first_page = new ArrayList<ProblemDigest>();
                                        }
                                    }

                                }
                            });


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //获得recyclerView的线性布局管理器
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //获取到第一个item的显示的下标  不等于0表示第一个item处于不可见状态 说明列表没有滑动到顶部 显示回到顶部按钮
                int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 判断是否滚动超过一屏
                    if (firstVisibleItemPosition <= 5) {
                        got.setVisibility(View.GONE);
                    } else {
                        got.setVisibility(View.VISIBLE);
                    }
                    //获取RecyclerView滑动时候的状态
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        //  RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayoutq) {
                if (KlbertjCache.index_ques_first_page != null) {
                    KlbertjCache.index_ques_first_page.clear();
                    KlbertjCache.index_ques_first_page = null;
                }

                if (KlbertjCache.index_ques_second_page != null) {
                    KlbertjCache.index_ques_second_page.clear();
                    KlbertjCache.index_ques_second_page = null;
                }
                if (KlbertjCache.index_ques_third_page != null) {
                    KlbertjCache.index_ques_third_page.clear();
                    KlbertjCache.index_ques_third_page = null;
                }
                if (KlbertjCache.index_ques_forth_page != null) {
                    KlbertjCache.index_ques_forth_page.clear();
                    KlbertjCache.index_ques_forth_page = null;
                }


                page = 1;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection urlConnection = null;
                        try {
                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionByPage));
                            OutputStream out = urlConnection.getOutputStream();

                            out.write(EncryptionUtils.decryptByByte("1".getBytes("UTF-8")));
                            out.close();
                            int responseCode = urlConnection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。


                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshLayoutq.finishRefresh(1000);
                                        if ("no".equals(result)) {
                                            indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                            indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                add(new ProblemDigest());
                                            }});
                                            recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                            refreshLayout.setEnableLoadMore(false);
                                            refreshLayout.setEnableRefresh(true);
                                        } else if ("e".equals(result)) {
                                            indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                            indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                add(new ProblemDigest());
                                            }});
                                            recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                            refreshLayout.setEnableLoadMore(false);
                                            refreshLayout.setEnableRefresh(true);
//                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                        } else {

                                            JSONArray jsonArray = JSONArray.parseArray(result);

                                            if (jsonArray != null && !jsonArray.isEmpty()) {
                                                Log.i("zhangfdfsfssdfdsf", jsonArray.toJSONString());

                                                List<ProblemDigest> problemDigests = new ArrayList<>();
                                                JSONObject j = null;
                                                ProblemDigest problemDigest1 = null;
                                                for (int i = 0; i < jsonArray.size(); i++) {
                                                    j = jsonArray.getJSONObject(i);
                                                    problemDigest1 = new ProblemDigest();
                                                    problemDigest1.pro_title = j.getString("title");
                                                    problemDigest1.belongToUserHead_url = Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + j.getString("belong_to_user_head_url");

                                                    problemDigest1.belongToUser = j.getString("belong_to_user_uname");
                                                    problemDigest1.num_of_discussion = j.getInteger("comments_count") + "";
                                                    problemDigest1.time = j.getString("create_time");
                                                    problemDigest1.pro_digest = j.getString("digest");
                                                    problemDigest1.q_belt_subject = j.getString("q_belt_subject");
                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                    problemDigest1.labels = j.getString("label");
                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                    problemDigest1.id = j.getInteger("id");
                                                    problemDigests.add(problemDigest1);
                                                }

                                                KlbertjCache.index_ques_first_page = problemDigests;
                                                if (mAdapter == null)
                                                    mAdapter = new IndexQuickAdapter();
                                                mAdapter.replaceData(KlbertjCache.index_ques_first_page);
                                                recyclerView.setAdapter(mAdapter);
                                                refreshLayout.setEnableLoadMore(true);
                                                refreshLayout.setEnableRefresh(true);
                                            } else {
                                                indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                    add(new ProblemDigest());
                                                }});
                                                recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                refreshLayout.setEnableLoadMore(false);
                                                refreshLayout.setEnableRefresh(true);
//                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "数据异常");
                                            }
                                        }

                                    }
                                });


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout1) {
                page++;
                if (page == 2) {
                    if (KlbertjCache.index_ques_second_page != null) {
                        if (mAdapter == null)
                            mAdapter = new IndexQuickAdapter();
                        mAdapter.addData(KlbertjCache.index_ques_second_page);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionByPage));
                                    OutputStream out = urlConnection.getOutputStream();

                                    out.write(EncryptionUtils.decryptByByte("2".getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。

                                        Thread.sleep(1000);
                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                if ("no".equals(result)) {
                                                    indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                    indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
                                                } else if ("e".equals(result)) {
                                                    indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                    indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
//                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                } else if ("nm".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "没有更多数据");
                                                    refreshLayout1.finishLoadMoreWithNoMoreData();
                                                } else {
                                                    Log.i("aassaass", result + "fdfdsfdsfdkpods===================");
                                                    JSONArray jsonArray = JSONArray.parseArray(result);
                                                    refreshLayout.finishLoadMore(500);
                                                    if (jsonArray != null && !jsonArray.isEmpty()) {
                                                        List<ProblemDigest> problemDigests = new ArrayList<>();
                                                        JSONObject j = null;
                                                        ProblemDigest problemDigest1 = null;
                                                        for (int i = 0; i < jsonArray.size(); i++) {
                                                            j = jsonArray.getJSONObject(i);
                                                            problemDigest1 = new ProblemDigest();
                                                            problemDigest1.pro_title = j.getString("title");
                                                            problemDigest1.belongToUserHead_url = Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + j.getString("belong_to_user_head_url");
                                                            problemDigest1.belongToUser = j.getString("belong_to_user_uname");
                                                            problemDigest1.num_of_discussion = j.getInteger("comments_count") + "";
                                                            problemDigest1.time = j.getString("create_time");
                                                            problemDigest1.pro_digest = j.getString("digest");
                                                            problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                            problemDigest1.labels = j.getString("label");
                                                            problemDigest1.q_belt_subject = j.getString("q_belt_subject");
                                                            problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                            problemDigest1.id = j.getInteger("id");
                                                            problemDigests.add(problemDigest1);
                                                        }

                                                        KlbertjCache.index_ques_second_page = problemDigests;
                                                        if (mAdapter == null)
                                                            mAdapter = new IndexQuickAdapter();
                                                        mAdapter.addData(KlbertjCache.index_ques_second_page);
                                                        //  recyclerView.setAdapter(mAdapter);
                                                        refreshLayout.setEnableLoadMore(true);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {
                                                        indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                        indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        refreshLayout.setEnableRefresh(true);
//                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "数据异常");
                                                    }
                                                }

                                            }
                                        });


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } else if (page == 3) {
                    if (KlbertjCache.index_ques_third_page != null) {
                        mAdapter.addData(KlbertjCache.index_ques_third_page);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionByPage));
                                    OutputStream out = urlConnection.getOutputStream();

                                    out.write(EncryptionUtils.decryptByByte("3".getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。

                                        Thread.sleep(1000);
                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                if ("no".equals(result)) {
                                                    indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                    indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
                                                } else if ("e".equals(result)) {
                                                    indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                    indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                } else if ("nm".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "没有更多数据");
                                                    refreshLayout1.finishLoadMoreWithNoMoreData();
                                                } else {
                                                    Log.i("aassaass", result + "fdfdsfdsfdkpods===================");
                                                    JSONArray jsonArray = JSONArray.parseArray(result);
                                                    refreshLayout.finishLoadMore(500);
                                                    if (jsonArray != null && !jsonArray.isEmpty()) {
                                                        List<ProblemDigest> problemDigests = new ArrayList<>();
                                                        JSONObject j = null;
                                                        ProblemDigest problemDigest1 = null;
                                                        for (int i = 0; i < jsonArray.size(); i++) {
                                                            j = jsonArray.getJSONObject(i);
                                                            problemDigest1 = new ProblemDigest();
                                                            problemDigest1.pro_title = j.getString("title");
                                                            problemDigest1.belongToUserHead_url = Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + j.getString("belong_to_user_head_url");
                                                            problemDigest1.belongToUser = j.getString("belong_to_user_uname");
                                                            problemDigest1.num_of_discussion = j.getInteger("comments_count") + "";
                                                            problemDigest1.time = j.getString("create_time");
                                                            problemDigest1.pro_digest = j.getString("digest");
                                                            problemDigest1.q_belt_subject = j.getString("q_belt_subject");
                                                            problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                            problemDigest1.labels = j.getString("label");
                                                            problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                            problemDigest1.id = j.getInteger("id");
                                                            problemDigests.add(problemDigest1);
                                                        }

                                                        KlbertjCache.index_ques_third_page = problemDigests;
                                                        if (mAdapter == null)
                                                            mAdapter = new IndexQuickAdapter();
                                                        mAdapter.addData(KlbertjCache.index_ques_third_page);
                                                        refreshLayout.setEnableLoadMore(true);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {
                                                        indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                        indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        refreshLayout.setEnableRefresh(true);
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "数据异常");
                                                    }
                                                }


                                            }
                                        });


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } else if (page == 4) {
                    if (KlbertjCache.index_ques_forth_page != null) {
                        mAdapter.addData(KlbertjCache.index_ques_forth_page);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionByPage));
                                    OutputStream out = urlConnection.getOutputStream();

                                    out.write(EncryptionUtils.decryptByByte("4".getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。

                                        Thread.sleep(1000);
                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {


                                                if ("no".equals(result)) {
                                                    indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                    indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
                                                } else if ("e".equals(result)) {
                                                    indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                    indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                } else if ("nm".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "没有更多数据");
                                                    refreshLayout1.finishLoadMoreWithNoMoreData();
                                                } else {
                                                    Log.i("aassaass", result + "fdfdsfdsfdkpods===================");
                                                    JSONArray jsonArray = JSONArray.parseArray(result);
                                                    refreshLayout.finishLoadMore(500);
                                                    if (jsonArray != null && !jsonArray.isEmpty()) {
                                                        List<ProblemDigest> problemDigests = new ArrayList<>();
                                                        JSONObject j = null;
                                                        ProblemDigest problemDigest1 = null;
                                                        for (int i = 0; i < jsonArray.size(); i++) {
                                                            j = jsonArray.getJSONObject(i);
                                                            problemDigest1 = new ProblemDigest();
                                                            problemDigest1.pro_title = j.getString("title");
                                                            problemDigest1.belongToUserHead_url = Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + j.getString("belong_to_user_head_url");
                                                            problemDigest1.belongToUser = j.getString("belong_to_user_uname");
                                                            problemDigest1.num_of_discussion = j.getInteger("comments_count") + "";
                                                            problemDigest1.time = j.getString("create_time");
                                                            problemDigest1.pro_digest = j.getString("digest");
                                                            problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                            problemDigest1.labels = j.getString("label");
                                                            problemDigest1.q_belt_subject = j.getString("q_belt_subject");
                                                            problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                            problemDigest1.id = j.getInteger("id");
                                                            problemDigests.add(problemDigest1);
                                                        }

                                                        KlbertjCache.index_ques_forth_page = problemDigests;
                                                        if (mAdapter == null)
                                                            mAdapter = new IndexQuickAdapter();
                                                        mAdapter.addData(KlbertjCache.index_ques_forth_page);
                                                        refreshLayout.setEnableLoadMore(true);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {
                                                        indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                        indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        refreshLayout.setEnableRefresh(true);
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "数据异常");
                                                    }
                                                }

                                            }
                                        });


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection urlConnection = null;
                            try {
                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionByPage));
                                OutputStream out = urlConnection.getOutputStream();

                                out.write(EncryptionUtils.decryptByByte(((++page) + "").getBytes("UTF-8")));
                                out.close();
                                int responseCode = urlConnection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                    Thread.sleep(1000);

                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if ("no".equals(result)) {
                                                indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                    add(new ProblemDigest());
                                                }});
                                                recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                refreshLayout.setEnableLoadMore(false);
                                                refreshLayout.setEnableRefresh(true);
                                            } else if ("e".equals(result)) {
                                                indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                    add(new ProblemDigest());
                                                }});
                                                recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                refreshLayout.setEnableLoadMore(false);
                                                refreshLayout.setEnableRefresh(true);
                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                            } else if ("nm".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "没有更多数据");
                                                refreshLayout1.finishLoadMoreWithNoMoreData();
                                            } else {
                                                Log.i("aassaass", result + "fdfdsfdsfdkpods===================");
                                                JSONArray jsonArray = JSONArray.parseArray(result);
                                                refreshLayout.finishLoadMore(500);
                                                if (jsonArray != null && !jsonArray.isEmpty()) {
                                                    List<ProblemDigest> problemDigests = new ArrayList<>();
                                                    JSONObject j = null;
                                                    ProblemDigest problemDigest1 = null;
                                                    for (int i = 0; i < jsonArray.size(); i++) {
                                                        j = jsonArray.getJSONObject(i);
                                                        problemDigest1 = new ProblemDigest();
                                                        problemDigest1.pro_title = j.getString("title");
                                                        problemDigest1.belongToUserHead_url = Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + j.getString("belong_to_user_head_url");
                                                        problemDigest1.belongToUser = j.getString("belong_to_user_uname");
                                                        problemDigest1.num_of_discussion = j.getInteger("comments_count") + "";
                                                        problemDigest1.time = j.getString("create_time");
                                                        problemDigest1.pro_digest = j.getString("digest");
                                                        problemDigest1.q_belt_subject = j.getString("q_belt_subject");
                                                        problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                        problemDigest1.labels = j.getString("label");
                                                        problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                        problemDigest1.id = j.getInteger("id");
                                                        problemDigests.add(problemDigest1);
                                                    }


                                                    if (mAdapter == null)
                                                        mAdapter = new IndexQuickAdapter();
                                                    mAdapter.addData(problemDigests);
                                                    refreshLayout.setEnableLoadMore(true);
                                                    refreshLayout.setEnableRefresh(true);
                                                } else {
                                                    indexWithNoDataQuickAdapter = new IndexWithNoDataQuickAdapter();
                                                    indexWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(indexWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "数据异常");
                                                }
                                            }

                                        }
                                    });


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }
        });


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        view= getActivity().getWindow().getDecorView();
        Log.i(tag, "onActivityCreated");
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(tag, "onStart");
    }


    @Override
    public void onResume() {
        super.onResume();
        if (params == null)
            Log.i(tag, "params==null");

        Log.i(tag, "onResume");
    }


    @Override
    public void onPause() {
        params = null;
        super.onPause();


        Log.i(tag, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(tag, "onStop");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(tag, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(tag, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(tag, "onDetach");
    }

    public void startScroll() {
        if (isUp) {
            isUp = false;
            text.startScroll();
        }
    }

//    private Collection<Item> buildItems() {
//        List<Item> items = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            items.addAll(Arrays.asList(Item.values()));
//        }
//        return items;
//    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private class BannerImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {


            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource((Integer) path);
//            imageView.setImageBitmap((Bitmap) path);
            Glide.with(Objects.requireNonNull(getActivity())).load((String) path)
                    .apply(MyGlide.getRequestOptions()
                            .override(Target.SIZE_ORIGINAL)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .priority(Priority.HIGH)
                    )
                    .into(imageView);


        }
    }


    private class IndexQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {

        MyCircleView myCircleView;

        IndexQuickAdapter() {
            super(R.layout.problem_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {
            helper.setText(R.id.title, item.pro_title)
                    .setText(R.id.uname, item.belongToUser)
                    .setText(R.id.time, item.time)
                    .setText(R.id.belt_course,item.q_belt_subject)
                    .setText(R.id.digest, item.pro_digest)
                    .setText(R.id.num_of_dis, item.num_of_discussion);
            Log.i("fdds","fdsfdsdsfds"+item.q_belt_subject);
            myCircleView = helper.getView(R.id.avatar);
            TextView label = helper.getView(R.id.label);
            JSONArray labels = JSONArray.parseArray(item.labels);

            StringBuilder la = new StringBuilder();
            int i = 0;
            for (; i < labels.size() - 1; i++) {
                la.append(labels.getString(i)).append("/");

            }
            la.append(labels.getString(i));
            label.setText(la.toString());

            View delete_indecator = helper.getView(R.id.delete_indecator);
            TextView delete = helper.getView(R.id.delete);
            View cancel_collect_indecator = helper.getView(R.id.cancel_collect_indecator);
            TextView cancel_collect = helper.getView(R.id.cancel_collect);
            delete_indecator.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            cancel_collect_indecator.setVisibility(View.GONE);
            cancel_collect.setVisibility(View.GONE);
            ImageView diges_pic = helper.getView(R.id.diges_pic);
            Glide.with(Objects.requireNonNull(getActivity())).load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + item.digest_pic_url)
                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.DEFAULT)).into(diges_pic);
//            ToastUtils.showToast(getActivity(), item.belongToUserHead_url);
            Glide.with(Objects.requireNonNull(getActivity())).asBitmap().load(item.belongToUserHead_url)
                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                    myCircleView.setBitmap(bitmap);
                }
            });
        }
    }

    private class IndexWithNoDataQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {


        IndexWithNoDataQuickAdapter() {
            super(R.layout.index_with_nodata);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {

        }
    }

    private static class Index1Handler extends Handler {

        //对Activity的弱引用
        private final WeakReference<Index1> mIndex1;

        public Index1Handler(Index1 f) {
            mIndex1 = new WeakReference<Index1>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            Index1 activity = mIndex1.get();
            if (activity == null) {
                super.handleMessage(msg);
                return;
            }
            switch (msg.what) {
                case Index1.DOWN_BANNER:

                    break;
                case 2:

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }


}
