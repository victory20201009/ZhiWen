package com.zzu.zk.zhiwen.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.youngkaaa.ycircleview.CircleView;
import com.google.gson.JsonObject;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.ProgressParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnInputCounterChangeListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.adapter.CheckedAdapter;
import com.zzu.zk.zhiwen.beans.Gift;
import com.zzu.zk.zhiwen.beans.PeopleDigest;
import com.zzu.zk.zhiwen.beans.ProblemDigest;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.ClearableEditText;
import com.zzu.zk.zhiwen.customed_ui.MyCircleView;
import com.zzu.zk.zhiwen.customed_ui.MySwitchButton;
import com.zzu.zk.zhiwen.glide.MyGlide;
import com.zzu.zk.zhiwen.utils.EncryptionUtils;
import com.zzu.zk.zhiwen.utils.PushHandler;
import com.zzu.zk.zhiwen.utils.SFTPUtils;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.StatusBarUtil;
import com.zzu.zk.zhiwen.utils.StorageUtils;
import com.zzu.zk.zhiwen.utils.ToastUtils;
import com.zzu.zk.zhiwen.utils.UrlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


public class MyAsk extends AppCompatActivity {
    TextView titleView;
    View prodback;
    Uri temp_imgUri;
    RecyclerView recyclerView;
    SmartRefreshLayout refreshLayout;
    MyCircleView avatar;
    ImageButton gotop;
    int itemType = 0;
    int getPicType = 0;
    Intent resultData;
    private DialogFragment dialogFragment;
    private int myQuesPage = 1;
    private int mycollPage = 1;
    private int myattPage = 1;
    MyAskWithNoDataQuickAdapter myAskWithNoDataQuickAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_ask);
        resultData = new Intent();
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        itemType = intent.getIntExtra("itemType", 0);
        titleView = findViewById(R.id.title);
        titleView.setText(title);
        prodback = findViewById(R.id.prodback);
        prodback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  setResult(RESULT_OK, resultData);
                finish();
            }
        });
        gotop = findViewById(R.id.gotop);

        recyclerView = findViewById(R.id.recyclerView);
        refreshLayout = findViewById(R.id.refreshLayout);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(MyAsk.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(MyAsk.this), VERTICAL));
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
                        gotop.setVisibility(View.GONE);
                    } else {
                        gotop.setVisibility(View.VISIBLE);
                    }
                    //获取RecyclerView滑动时候的状态
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });


        StatusBarUtil.immersive(Objects.requireNonNull(MyAsk.this));
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(MyAsk.this), recyclerView);
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(MyAsk.this), findViewById(R.id.container));
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(MyAsk.this), findViewById(R.id.blurView));


        gotop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        if (itemType == 0 || itemType == 1) {
            View masker = findViewById(R.id.masker);
            masker.setVisibility(View.GONE);
            TextView masker1 = findViewById(R.id.masker1);
            masker1.setVisibility(View.VISIBLE);
            final MyAskQuickAdapter1 myAskQuickAdapter1 = new MyAskQuickAdapter1();
            if (itemType == 0) {
                String bargen = intent.getStringExtra(Cons.COUNT_OF_MY_QUESTIONS);
                masker1.setText(bargen);
                final String data = intent.getStringExtra("contentStr");
                if ("no".equals(data)) {
                    myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                    myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                        add(new ProblemDigest());
                    }});
                    recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                    refreshLayout.setEnableRefresh(true);
                    refreshLayout.setEnableLoadMore(false);
                    //  ToastUtils.showToast(MyAsk.this, "您还没有提问问题");
                } else {
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    List<JSONObject> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        list.add(jsonArray.getJSONObject(i));
                    }


                    myAskQuickAdapter1.replaceData(list);
                    recyclerView.setAdapter(myAskQuickAdapter1);
                }
                myAskQuickAdapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        List<JSONObject> l = myAskQuickAdapter1.getData();
                        final JSONObject j = l.get(position);
                        Log.i("ssss", j.toJSONString());
                        final Intent i = new Intent(MyAsk.this, ProblemDetail.class);
                        i.putExtra("come_intent", "my_ques");
                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + j.getString("belong_to_user_head_url"));

                        i.putExtra("belong_to_user_uname", j.getString("belong_to_user_uname"));
                        i.putExtra("title", j.getString("title"));
                        i.putExtra("label", j.getString("label"));
                        i.putExtra("create_time", j.getString("create_time"));
                        i.putExtra("qqq_id", j.getInteger("id"));
                        final int qid = j.getInteger("id");

                        if (KlbertjCache.prodetail.containsKey(qid)) {
                            String detail = KlbertjCache.prodetail.get(qid);
                            i.putExtra("contentStr", detail);
                            startActivity(i);
                        } else {
                            dialogFragment = new CircleDialog.Builder()
                                    .setProgressText("正在获取数据...")
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)
                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                    .show(getSupportFragmentManager());
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

                                            if (KlbertjCache.prodetail.size() > 512) {
                                                int ii = 1;
                                                for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                    ii = i;
                                                    break;
                                                }
                                                KlbertjCache.prodetail.remove(ii);
                                            }
                                            KlbertjCache.prodetail.put(qid, result);

                                            Thread.sleep(1000);
                                            Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialogFragment != null) {
                                                        dialogFragment.dismiss();
                                                        dialogFragment = null;
                                                    }
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
                });
                refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(@NonNull final RefreshLayout refreshLayout1) {
                        myQuesPage = 1;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionsDigestBelongsToUserByPage));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject j = new JSONObject();
                                    j.put("id", KlbertjCache.uid);
                                    j.put("qid", KlbertjCache.uid);
                                    j.put("pn", 1);

                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                        Thread.sleep(1000);
                                        Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshLayout1.finishRefresh(1000);
                                                if ("f".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "登录已经过期");
                                                } else if ("server has occured a problem".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "服务器错误");
                                                } else if ("bp".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "参数错误");
                                                } else if ("no".equals(result)) {
                                                    if (myAskWithNoDataQuickAdapter == null)
                                                        myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                                                    myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableRefresh(true);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    KlbertjCache.my_pro_list = result;
                                                } else {
                                                    KlbertjCache.my_pro_list = result;
                                                    JSONArray jsonArray = JSONArray.parseArray(result);
                                                    List<JSONObject> list = myAskQuickAdapter1.getData();
                                                    list.clear();
                                                    if (jsonArray == null) {
                                                        if (myAskWithNoDataQuickAdapter == null)
                                                            myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                                                        myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableRefresh(true);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        KlbertjCache.my_pro_list = "";
                                                        ToastUtils.showToast(MyAsk.this, "数据异常");
                                                    } else {
                                                        refreshLayout.setEnableRefresh(true);
                                                        refreshLayout.setEnableLoadMore(true);
                                                        for (int i = 0; i < jsonArray.size(); i++) {
                                                            list.add(jsonArray.getJSONObject(i));
                                                        }
                                                        myAskQuickAdapter1.replaceData(list);
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
                    public void onLoadMore(@NonNull RefreshLayout refreshLayout1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionsDigestBelongsToUserByPage));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject j = new JSONObject();
                                    j.put("id", KlbertjCache.uid);
                                    j.put("qid", KlbertjCache.uid);
                                    j.put("pn", ++myQuesPage);

                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {

                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                        Thread.sleep(1000);
                                        Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                if ("f".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "登录已经过期");
                                                    refreshLayout.finishLoadMore();
                                                } else if ("e".equals(result) || "server has occured a problem".equals(result)) {

                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "服务器错误");
                                                    refreshLayout.finishLoadMore();
                                                } else if ("no".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "没有数据");
                                                    refreshLayout.finishLoadMore();
                                                } else if ("nm".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "没有更多数据");
                                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                                } else {
                                                    JSONArray jsonArray = JSONArray.parseArray(result);
                                                    List<JSONObject> list = new ArrayList<>();
                                                    for (int i = 0; i < jsonArray.size(); i++) {
                                                        list.add(jsonArray.getJSONObject(i));
                                                    }
                                                    myAskQuickAdapter1.addData(list);
                                                    refreshLayout.finishLoadMore();
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


            } else {
                String bargen = intent.getStringExtra(Cons.NUMS_OF_COLLECTION_QUESTIONS);
                masker1.setText(bargen);


                final String data = intent.getStringExtra("contentStr");
                if ("no".equals(data)) {
                    myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                    myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                        add(new ProblemDigest());
                    }});
                    recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                    refreshLayout.setEnableRefresh(true);
                    refreshLayout.setEnableLoadMore(false);
                    //  ToastUtils.showToast(MyAsk.this, "您还没有提问问题");
                } else {
                    JSONArray jsonArray = JSONArray.parseArray(data);
                    List<JSONObject> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        list.add(jsonArray.getJSONObject(i));
                    }


                    myAskQuickAdapter1.replaceData(list);
                    recyclerView.setAdapter(myAskQuickAdapter1);
                }
                myAskQuickAdapter1.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        List<JSONObject> l = myAskQuickAdapter1.getData();
                        final JSONObject j = l.get(position);
                        Log.i("ssss", j.toJSONString());
                        final Intent i = new Intent(MyAsk.this, ProblemDetail.class);
                        i.putExtra("come_intent", "nmy_ques");
                        i.putExtra("come_coll", 1);
                        i.putExtra("belongToUserHead_url", Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + j.getString("belong_to_user_head_url"));
                        i.putExtra("belong_to_user_uname", j.getString("belong_to_user_uname"));
                        i.putExtra("title", j.getString("title"));
                        i.putExtra("label", j.getString("label"));
                        i.putExtra("create_time", j.getString("create_time"));
                        i.putExtra("qqq_id", j.getInteger("id"));
                        final int qid = j.getInteger("id");

                        if (KlbertjCache.prodetail.containsKey(qid)) {
                            String detail = KlbertjCache.prodetail.get(qid);
                            JSONObject jj = JSONObject.parseObject(detail);
                            if (jj != null && !jj.isEmpty()) {
                                int is_att = jj.getInteger("is_att");
                                int is_coll = jj.getInteger("is_coll");
                                i.putExtra("is_coll", is_coll);
                                i.putExtra("is_att", is_att);
                                i.putExtra("contentStr", detail);
                                startActivity(i);
                            } else {
                                ToastUtils.showToast(MyAsk.this, "数据异常");
                            }


                        } else {
                            dialogFragment = new CircleDialog.Builder()
                                    .setProgressText("正在获取数据...")
                                    .setCanceledOnTouchOutside(false)
                                    .setCancelable(false)
                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                    .show(getSupportFragmentManager());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpURLConnection urlConnection = null;
                                    try {
                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getcollQuestionDetail));
                                        OutputStream out = urlConnection.getOutputStream();
                                        JSONObject j = new JSONObject();
                                        j.put("id", KlbertjCache.uid);
                                        j.put("q_id", qid);
                                        out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                        out.close();
                                        int responseCode = urlConnection.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {


                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。


                                            Thread.sleep(1000);
                                            Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialogFragment != null) {
                                                        dialogFragment.dismiss();
                                                        dialogFragment = null;
                                                    }
                                                    if ("bp".equals(result)) {
                                                        ToastUtils.showToast(MyAsk.this, "权限过期");
                                                    } else {
                                                        if (KlbertjCache.prodetail.size() > 512) {
                                                            int ii = 1;
                                                            for (Integer i : KlbertjCache.prodetail.keySet()) {
                                                                ii = i;
                                                                break;
                                                            }
                                                            KlbertjCache.prodetail.remove(ii);
                                                        }
                                                        KlbertjCache.prodetail.put(qid, result);

                                                        JSONObject jj = JSONObject.parseObject(result);
                                                        if (jj != null && !jj.isEmpty()) {
                                                            int is_att = jj.getInteger("is_att");
                                                            int is_coll = jj.getInteger("is_coll");
                                                            i.putExtra("is_coll", is_coll);
                                                            i.putExtra("is_att", is_att);
                                                            i.putExtra("contentStr", result);
                                                            startActivity(i);
                                                        } else {
                                                            ToastUtils.showToast(MyAsk.this, "数据异常");
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
                refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(@NonNull final RefreshLayout refreshLayout1) {
                        mycollPage = 1;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllCollectQuestionsDigestByPage));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject j = new JSONObject();
                                    j.put("id", KlbertjCache.uid);
                                    j.put("pn", 1);

                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                        Thread.sleep(1000);
                                        Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshLayout1.finishRefresh(500);
                                                if ("f".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "登录已经过期");
                                                } else if ("server has occured a problem".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "服务器错误");
                                                } else if ("bp".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "参数错误");
                                                } else if ("no".equals(result)) {
                                                    if (myAskWithNoDataQuickAdapter == null)
                                                        myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                                                    myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableRefresh(true);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    KlbertjCache.my_coll_list = result;
                                                } else {
                                                    KlbertjCache.my_coll_list = result;
                                                    JSONArray jsonArray = JSONArray.parseArray(result);
                                                    List<JSONObject> list = myAskQuickAdapter1.getData();
                                                    list.clear();
                                                    if (jsonArray == null) {
                                                        if (myAskWithNoDataQuickAdapter == null)
                                                            myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                                                        myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableRefresh(true);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        KlbertjCache.my_coll_list = "";
                                                        ToastUtils.showToast(MyAsk.this, "数据异常");
                                                    } else {
                                                        refreshLayout.setEnableRefresh(true);
                                                        refreshLayout.setEnableLoadMore(true);
                                                        for (int i = 0; i < jsonArray.size(); i++) {
                                                            list.add(jsonArray.getJSONObject(i));
                                                        }
                                                        myAskQuickAdapter1.replaceData(list);
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
                    public void onLoadMore(@NonNull RefreshLayout refreshLayout1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllCollectQuestionsDigestByPage));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject j = new JSONObject();
                                    j.put("id", KlbertjCache.uid);
                                    j.put("pn", ++mycollPage);

                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {

                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                        Thread.sleep(1000);
                                        Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                if ("f".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "登录已经过期");
                                                    refreshLayout.finishLoadMore();
                                                } else if ("e".equals(result) || "server has occured a problem".equals(result)) {

                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "服务器错误");
                                                    refreshLayout.finishLoadMore();
                                                } else if ("no".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "没有数据");
                                                    refreshLayout.finishLoadMore();
                                                } else if ("nm".equals(result)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "没有更多数据");
                                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                                } else {
                                                    JSONArray jsonArray = JSONArray.parseArray(result);
                                                    List<JSONObject> list = new ArrayList<>();
                                                    for (int i = 0; i < jsonArray.size(); i++) {
                                                        list.add(jsonArray.getJSONObject(i));
                                                    }
                                                    myAskQuickAdapter1.addData(list);
                                                    refreshLayout.finishLoadMore(50);
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


            }


        } else if (itemType == 2) {

            View masker = findViewById(R.id.masker);
            masker.setVisibility(View.GONE);
            TextView masker1 = findViewById(R.id.masker1);
            masker1.setVisibility(View.VISIBLE);
            String bargen = intent.getStringExtra(Cons.ATTENTION_PEOPLE_NUM);
            masker1.setText(bargen);
            final MyAskQuickAdapter2 myAskQuickAdapter2 = new MyAskQuickAdapter2();

            final String data = intent.getStringExtra("contentStr");
            if ("no".equals(data)) {
                myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                    add(new ProblemDigest());
                }});
                recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                refreshLayout.setEnableRefresh(true);
                refreshLayout.setEnableLoadMore(false);
                //  ToastUtils.showToast(MyAsk.this, "您还没有提问问题");
            } else {

                JSONArray jsonArray = JSONArray.parseArray(data);
                List<PeopleDigest> problemDigestslist = new ArrayList<>();
                PeopleDigest peopleDigest = null;
                JSONObject jsonObject = null;
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        peopleDigest = new PeopleDigest();
                        peopleDigest.id = jsonObject.getInteger("id");
                        peopleDigest.name = jsonObject.getString("name");
                        peopleDigest.head_url = jsonObject.getString("ava");
                        peopleDigest.num_of_ask = jsonObject.getInteger("count_of_my_questions");
                        peopleDigest.num_of_bargen = jsonObject.getInteger("score");
                        peopleDigest.num_of_discuss = jsonObject.getInteger("discuss_num");
                        peopleDigest.num_of_fans = jsonObject.getInteger("fans_num");
                        problemDigestslist.add(peopleDigest);
                    }

                    myAskQuickAdapter2.replaceData(problemDigestslist);
                    recyclerView.setAdapter(myAskQuickAdapter2);
                }


            }

            refreshLayout.setOnRefreshListener(new OnRefreshListener() {
                @Override
                public void onRefresh(@NonNull final RefreshLayout refreshLayout1) {
                    myattPage = 1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection urlConnection = null;
                            try {
                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAttUserByPage));
                                OutputStream out = urlConnection.getOutputStream();
                                JSONObject j = new JSONObject();
                                j.put("id", KlbertjCache.uid);
                                j.put("pn", 1);

                                out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                out.close();
                                int responseCode = urlConnection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                    Thread.sleep(1000);
                                    Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            refreshLayout1.finishRefresh(500);
                                            if ("f".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "登录已经过期");
                                            } else if ("server has occured a problem".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "服务器错误");
                                            } else if ("bp".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "参数错误");
                                            } else if ("no".equals(result)) {
                                                if (myAskWithNoDataQuickAdapter == null)
                                                    myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                                                myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                    add(new ProblemDigest());
                                                }});
                                                recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                                                refreshLayout.setEnableRefresh(true);
                                                refreshLayout.setEnableLoadMore(false);
                                                KlbertjCache.my_att_list = result;
                                            } else {
                                                KlbertjCache.my_att_list = result;
                                                JSONArray jsonArray = JSONArray.parseArray(result);
                                                List<PeopleDigest> list = myAskQuickAdapter2.getData();
                                                list.clear();
                                                if (jsonArray == null) {
                                                    if (myAskWithNoDataQuickAdapter == null)
                                                        myAskWithNoDataQuickAdapter = new MyAskWithNoDataQuickAdapter();
                                                    myAskWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(myAskWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableRefresh(true);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    KlbertjCache.my_coll_list = "";
                                                    ToastUtils.showToast(MyAsk.this, "数据异常");
                                                } else {
                                                    refreshLayout.setEnableRefresh(true);
                                                    refreshLayout.setEnableLoadMore(true);
                                                    PeopleDigest peopleDigest = null;
                                                    JSONObject jsonObject = null;
                                                    for (int i = 0; i < jsonArray.size(); i++) {
                                                        jsonObject = jsonArray.getJSONObject(i);
                                                        peopleDigest = new PeopleDigest();
                                                        peopleDigest.id = jsonObject.getInteger("id");
                                                        peopleDigest.name = jsonObject.getString("name");
                                                        peopleDigest.head_url = jsonObject.getString("ava");
                                                        peopleDigest.num_of_ask = jsonObject.getInteger("count_of_my_questions");
                                                        peopleDigest.num_of_bargen = jsonObject.getInteger("score");
                                                        peopleDigest.num_of_discuss = jsonObject.getInteger("discuss_num");
                                                        peopleDigest.num_of_fans = jsonObject.getInteger("fans_num");
                                                        list.add(peopleDigest);
                                                    }

                                                    myAskQuickAdapter2.replaceData(list);
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
                public void onLoadMore(@NonNull RefreshLayout refreshLayout1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection urlConnection = null;
                            try {
                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAttUserByPage));
                                OutputStream out = urlConnection.getOutputStream();
                                JSONObject j = new JSONObject();
                                j.put("id", KlbertjCache.uid);
                                j.put("pn", ++myattPage);

                                out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                out.close();
                                int responseCode = urlConnection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {

                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                    Thread.sleep(1000);
                                    Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if ("f".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "登录已经过期");
                                                refreshLayout.finishLoadMore();
                                            } else if ("e".equals(result) || "server has occured a problem".equals(result)) {

                                                ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "服务器错误");
                                                refreshLayout.finishLoadMore();
                                            } else if ("no".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "没有数据");
                                                refreshLayout.finishLoadMore();
                                            } else if ("nm".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(MyAsk.this), "没有更多数据");
                                                refreshLayout.finishLoadMoreWithNoMoreData();
                                            } else {

                                                JSONArray jsonArray = JSONArray.parseArray(result);
                                                List<PeopleDigest> list = new ArrayList<>();
                                                PeopleDigest peopleDigest = null;
                                                JSONObject jsonObject = null;
                                                if (jsonArray != null && !jsonArray.isEmpty()) {
                                                    for (int i = 0; i < jsonArray.size(); i++) {
                                                        jsonObject = jsonArray.getJSONObject(i);
                                                        peopleDigest = new PeopleDigest();
                                                        peopleDigest.id = jsonObject.getInteger("id");
                                                        peopleDigest.name = jsonObject.getString("name");
                                                        peopleDigest.head_url = jsonObject.getString("ava");
                                                        peopleDigest.num_of_ask = jsonObject.getInteger("count_of_my_questions");
                                                        peopleDigest.num_of_bargen = jsonObject.getInteger("score");
                                                        peopleDigest.num_of_discuss = jsonObject.getInteger("discuss_num");
                                                        peopleDigest.num_of_fans = jsonObject.getInteger("fans_num");
                                                        list.add(peopleDigest);
                                                    }
                                                    myAskQuickAdapter2.addData(list);
                                                    refreshLayout.finishLoadMore(50);
                                                } else {
                                                    ToastUtils.showToast(MyAsk.this, "数据异常");
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

        } else if (itemType == 3) {
            MyAskQuickAdapter3 myAskQuickAdapter3 = new MyAskQuickAdapter3();
            myAskQuickAdapter3.replaceData(new ArrayList<ProblemDigest>() {{
                add(new ProblemDigest());
            }});
            recyclerView.setAdapter(myAskQuickAdapter3);
            View masker = findViewById(R.id.masker);
            masker.setVisibility(View.GONE);
            TextView masker1 = findViewById(R.id.masker1);
            masker1.setVisibility(View.VISIBLE);
            String bargen = intent.getStringExtra("bargen");
            masker1.setText(bargen);
            recyclerView.removeItemDecoration(recyclerView.getItemDecorationAt(0));
            refreshLayout.setEnableOverScrollDrag(true);

            refreshLayout.setEnablePureScrollMode(true);
            refreshLayout.setDragRate(0.5555f);
            refreshLayout.setHeaderHeight(200f);
            refreshLayout.setFooterHeight(300f);
        } else if (itemType == 4) {
            MyAskQuickAdapter4 myAskQuickAdapter4 = new MyAskQuickAdapter4();
            myAskQuickAdapter4.replaceData(new ArrayList<ProblemDigest>() {{
                add(new ProblemDigest());
            }});
            recyclerView.setAdapter(myAskQuickAdapter4);
            recyclerView.removeItemDecoration(recyclerView.getItemDecorationAt(0));
            refreshLayout.setEnableOverScrollDrag(true);

            refreshLayout.setEnablePureScrollMode(true);
            refreshLayout.setDragRate(0.5555f);
            refreshLayout.setHeaderHeight(200f);
            refreshLayout.setFooterHeight(300f);
        } else if (itemType == 5) {
            View masker = findViewById(R.id.masker);
            masker.setVisibility(View.GONE);
            TextView masker1 = findViewById(R.id.masker1);
            masker1.setVisibility(View.VISIBLE);
            String bargen = intent.getStringExtra("bargen");
            masker1.setText(bargen);
            MyAskQuickAdapter5 myAskQuickAdapter5 = new MyAskQuickAdapter5();

            List<Gift> problemDigestslist = new ArrayList<>();
            Gift gift = null;
            for (int i = 0; i < 20; i++) {
                gift = new Gift();
                gift.gift1_id = i;
                gift.gift2_id = i + 1;
                gift.gift1_need_bar = 200 + i;
                gift.gift2_need_bar = 300 + i;
                gift.gitf1_name = "name" + i;
                gift.gitf2_name = "name" + i;
                problemDigestslist.add(gift);
            }
            myAskQuickAdapter5.replaceData(problemDigestslist);
            recyclerView.setAdapter(myAskQuickAdapter5);

            int num = recyclerView.getItemDecorationCount();
            for (int i = 0; i < num; i++) {
                recyclerView.removeItemDecorationAt(i);
            }
        } else if (itemType == 6) {

            MyAskQuickAdapter6 myAskQuickAdapter6 = new MyAskQuickAdapter6();

            myAskQuickAdapter6.replaceData(new ArrayList<Gift>() {{
                add(new Gift());
            }});
            recyclerView.setAdapter(myAskQuickAdapter6);

            recyclerView.removeItemDecorationAt(0);
            refreshLayout.setEnableOverScrollDrag(true);

            refreshLayout.setEnablePureScrollMode(true);
            refreshLayout.setDragRate(0.5555f);
            refreshLayout.setHeaderHeight(200f);
            refreshLayout.setFooterHeight(300f);


        } else if (itemType == 7) {

            MyAskQuickAdapter7 myAskQuickAdapter7 = new MyAskQuickAdapter7();

            myAskQuickAdapter7.replaceData(new ArrayList<Gift>() {{
                add(new Gift());
            }});
            recyclerView.setAdapter(myAskQuickAdapter7);

            recyclerView.removeItemDecorationAt(0);
            refreshLayout.setEnableOverScrollDrag(true);

            refreshLayout.setEnablePureScrollMode(true);
            refreshLayout.setDragRate(0.5555f);
            refreshLayout.setHeaderHeight(200f);
            refreshLayout.setFooterHeight(300f);


        } else {

            MyAskQuickAdapter8 myAskQuickAdapter8 = new MyAskQuickAdapter8();

            if (itemType == 8) {
                myAskQuickAdapter8.replaceData(KlbertjCache.notification2.get("新的问题"));
            } else if (itemType == 9) {
                myAskQuickAdapter8.replaceData(KlbertjCache.notification2.get("新的回复"));
            } else if (itemType == 10) {
                myAskQuickAdapter8.replaceData(KlbertjCache.notification2.get("有人关注"));
            } else if (itemType == 11) {
                myAskQuickAdapter8.replaceData(KlbertjCache.notification2.get("你被采纳"));
            } else if (itemType == 12) {
                myAskQuickAdapter8.replaceData(KlbertjCache.notification2.get("收藏问题"));
            } else if (itemType == 13) {
                myAskQuickAdapter8.replaceData(KlbertjCache.notification2.get("取消关注"));
            } else if (itemType == 14) {
                myAskQuickAdapter8.replaceData(KlbertjCache.notification2.get("取消收藏问题"));
            }

            recyclerView.setAdapter(myAskQuickAdapter8);

            recyclerView.removeItemDecorationAt(0);
            refreshLayout.setEnableOverScrollDrag(true);

            refreshLayout.setEnablePureScrollMode(true);
            refreshLayout.setDragRate(0.5555f);
            refreshLayout.setHeaderHeight(200f);
            refreshLayout.setFooterHeight(300f);


        }


    }

    private class MyAskQuickAdapter1 extends BaseQuickAdapter<JSONObject, BaseViewHolder> {


        MyAskQuickAdapter1() {
            super(R.layout.problem_item);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final JSONObject item) {
            helper.setText(R.id.title, item.getString("title"))
                    .setText(R.id.time, item.getString("create_time"))
                    .setText(R.id.num_of_dis, item.getIntValue("comments_count") + "")
                    .setText(R.id.belt_course, item.getString("q_belt_subject"))
                    .setText(R.id.digest, item.getString("digest"));


            ImageView diges_pic = helper.getView(R.id.diges_pic);
            Glide.with(MyAsk.this).load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + item.getString("digest_pic_url"))
                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(diges_pic);
            TextView label = helper.getView(R.id.label);
            Log.i("aaasss", item.toJSONString());
            JSONArray labels = item.getJSONArray("label");

            StringBuilder la = new StringBuilder();
            int i = 0;
            for (; i < labels.size() - 1; i++) {
                la.append(labels.getString(i)).append("/");
            }
            la.append(labels.getString(i));
            label.setText(la.toString());
            avatar = helper.getView(R.id.avatar);
            TextView uname = helper.getView(R.id.uname);
            View cancel_collect_indecator = helper.getView(R.id.cancel_collect_indecator);
            TextView cancel_collect = helper.getView(R.id.cancel_collect);
            TextView delete = helper.getView(R.id.delete);
            View delete_indecator = helper.getView(R.id.delete_indecator);
            final List<JSONObject> listtremp = getData();
            if (itemType == 0) {

                cancel_collect_indecator.setVisibility(View.GONE);
                cancel_collect.setVisibility(View.GONE);


                delete.setOnClickListener(new View.OnClickListener() {
                    final int position = helper.getAdapterPosition();

                    @Override
                    public void onClick(View v) {


                        new CircleDialog.Builder()
                                .setTitle("提示")
                                .setText("删除此问题，将删除此此问题的所有内容，包括文字，图片，文件，回复等，确定要删除吗")
                                .setPositive("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogFragment = new CircleDialog.Builder()
                                                .setProgressText("正在发送请求...")
                                                .setCancelable(false)
                                                .setCanceledOnTouchOutside(false)
                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                .show(Objects.requireNonNull(MyAsk.this).getSupportFragmentManager());

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                HttpURLConnection urlConnection = null;
                                                try {
                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.deleteQuestion));
                                                    OutputStream out = urlConnection.getOutputStream();
                                                    JSONObject send = new JSONObject();
                                                    if (KlbertjCache.uid != -2) {
                                                        send.put("id", KlbertjCache.uid);
                                                    } else {
                                                        KlbertjCache.uid = SharePreferenceUtils.getInteger(MyAsk.this, Cons.IS_LOGIN);
                                                        send.put("id", KlbertjCache.uid);
                                                    }

                                                    send.put("qid", item.getInteger("id"));

                                                    out.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                                    out.close();
                                                    int responseCode = urlConnection.getResponseCode();
                                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。

                                                        Thread.sleep(1000);
                                                        Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (dialogFragment != null) {
                                                                    dialogFragment.dismiss();
                                                                    dialogFragment = null;
                                                                }

                                                                if ("f".equals(result)) {
                                                                    ToastUtils.showToast(MyAsk.this, "token过期");
                                                                } else if ("bp".equals(result)) {
                                                                    ToastUtils.showToast(MyAsk.this, "参数错误");
                                                                } else {
                                                                    JSONObject j = JSONObject.parseObject(result);


                                                                    final JSONArray pics = j.getJSONArray("qu");
                                                                    final JSONObject file = j.getJSONObject("fu");
//                                                                    final JSONArray reply_pics =j.getJSONArray("ru");
                                                                    final JSONArray reply_pics = JSONArray.parseArray(j.getString("ru"));
                                                                    if (pics.isEmpty() && file.isEmpty() && Objects.requireNonNull(reply_pics).isEmpty()) {
                                                                        ToastUtils.showToast(MyAsk.this, "操作成功");
                                                                        MyAskQuickAdapter1.this.getData().remove(position);
                                                                        MyAskQuickAdapter1.this.notifyItemRemoved(position);
                                                                        KlbertjCache.my_pro_list = "";
                                                                        MyAsk.this.finish();
                                                                    } else {
                                                                        dialogFragment = new CircleDialog.Builder()
                                                                                .setProgressText("正在删除文件...")
                                                                                .setCanceledOnTouchOutside(false)
                                                                                .setCancelable(false)
                                                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                .show(Objects.requireNonNull(MyAsk.this).getSupportFragmentManager());


                                                                        new Thread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                List<String> paths = null;
                                                                                if (!pics.isEmpty()) {
                                                                                    paths = new ArrayList<>();
                                                                                    for (int i = 0; i < pics.size(); i++) {
                                                                                        paths.add(pics.getString(i));
                                                                                    }
                                                                                    SFTPUtils.deletePics(MyAsk.this, paths, Cons.QUES_PICS_PATH_IN_SERVER);
                                                                                }
                                                                                if (!file.isEmpty()) {
                                                                                    JSONArray fileurrls = file.getJSONArray("server_name");
                                                                                    if (!fileurrls.isEmpty()) {
                                                                                        if (paths == null) {
                                                                                            paths = new ArrayList<>();
                                                                                        }
                                                                                        paths.clear();
                                                                                        for (int i = 0; i < fileurrls.size(); i++) {
                                                                                            paths.add(fileurrls.getString(i));
                                                                                        }
                                                                                        SFTPUtils.deleteFiles(MyAsk.this, paths);

                                                                                    }
                                                                                }
                                                                                if (!reply_pics.isEmpty()) {
                                                                                    if (paths == null) {
                                                                                        paths = new ArrayList<>();
                                                                                    }
                                                                                    paths.clear();
                                                                                    for (int i = 0; i < reply_pics.size(); i++) {
                                                                                        paths.add(reply_pics.getString(i));
                                                                                    }

                                                                                    SFTPUtils.deletePics(MyAsk.this, paths, Cons.REPLY_PICS_PATH_IN_SERVER);

                                                                                }

                                                                                try {
                                                                                    Thread.sleep(500);
                                                                                } catch (InterruptedException e) {
                                                                                    e.printStackTrace();
                                                                                }


                                                                                Objects.requireNonNull(MyAsk.this).runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        if (dialogFragment != null) {
                                                                                            dialogFragment.dismiss();
                                                                                            dialogFragment = null;
                                                                                        }


                                                                                        MyAskQuickAdapter1.this.getData().remove(position);
                                                                                        MyAskQuickAdapter1.this.notifyItemRemoved(position);
                                                                                        KlbertjCache.my_pro_list = "";

                                                                                        ToastUtils.showToast(MyAsk.this, "操作成功");
                                                                                        MyAsk.this.finish();
                                                                                    }
                                                                                });

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

                                    }
                                })
                                .setNegative("取消", null)
                                .show(MyAsk.this.getSupportFragmentManager());
                    }
                });

                avatar.setVisibility(View.GONE);

                uname.setVisibility(View.GONE);

            } else {
                cancel_collect_indecator.setVisibility(View.VISIBLE);
                cancel_collect.setVisibility(View.VISIBLE);
                avatar.setVisibility(View.VISIBLE);
                uname.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
                delete_indecator.setVisibility(View.GONE);
                Glide.with(MyAsk.this).asBitmap().load(Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + item.getString("belong_to_user_head_url")).apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                                avatar.setBitmap(resource);
                            }
                        });
                helper.setText(R.id.uname, item.getString("belong_to_user_uname"));
                cancel_collect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CircleDialog.Builder()
                                .setTitle("提醒")
                                .setText("你确定取消收藏吗？")
                                .setPositive("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialogFragment = new CircleDialog.Builder()
                                                .setProgressText("正在发送请求...")
                                                .setCancelable(false)
                                                .setCanceledOnTouchOutside(false)
                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                .show(getSupportFragmentManager());

                                        final int position = helper.getAdapterPosition();
//
                                        final JSONObject j = listtremp.get(position);
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                HttpURLConnection urlConnection = null;
                                                try {
                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.cancelCollectQuestion));
                                                    OutputStream out = urlConnection.getOutputStream();
                                                    JSONObject request = new JSONObject();
                                                    request.put("qid", j.getInteger("id"));
                                                    request.put("id", KlbertjCache.uid);
                                                    out.write(EncryptionUtils.decryptByByte(request.toJSONString().getBytes("UTF-8")));
                                                    out.close();
                                                    int responseCode = urlConnection.getResponseCode();
                                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                                        InputStream inputStream = urlConnection.getInputStream();
                                                        final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。
                                                        Thread.sleep(1000);
                                                        if ("ok".equals(result)) {
                                                            MyAsk.this.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (dialogFragment != null) {
                                                                        dialogFragment.dismiss();
                                                                        dialogFragment = null;
                                                                    }
                                                                    ToastUtils.showloading("正在保存您的点击", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                                        @Override
                                                                        public void OnLoadingCancel() {
                                                                            ToastUtils.showToast(MyAsk.this, "取消收藏成功");
                                                                            listtremp.remove(position);
                                                                            MyAskQuickAdapter1.this.notifyItemRemoved(position);

                                                                            KlbertjCache.my_coll_list = "";

                                                                        }
                                                                    }, MyAsk.this);
                                                                }
                                                            });
                                                        } else if ("f".equals(result)) {

                                                            MyAsk.this.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (dialogFragment != null) {
                                                                        dialogFragment.dismiss();
                                                                        dialogFragment = null;
                                                                    }
                                                                    ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                                        @Override
                                                                        public void OnLoadingCancel() {
                                                                            ToastUtils.showToast(MyAsk.this, "token过期");

                                                                        }
                                                                    }, MyAsk.this);


                                                                }
                                                            });
                                                        } else {
                                                            MyAsk.this.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (dialogFragment != null) {
                                                                        dialogFragment.dismiss();
                                                                        dialogFragment = null;
                                                                    }
                                                                    ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                                        @Override
                                                                        public void OnLoadingCancel() {
                                                                            ToastUtils.showToast(MyAsk.this, "请求参数错误");

                                                                        }
                                                                    }, MyAsk.this);


                                                                }
                                                            });
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }).start();


                                    }
                                })
                                .setNegative("取消", null)
                                .show(MyAsk.this.getSupportFragmentManager());
                    }
                });


            }


//            Glide.with(mContext).load(item.picaddr).into((ImageView) helper.getView(R.id.lmi_avatar));
        }
    }


    private class MyAskQuickAdapter2 extends BaseQuickAdapter<PeopleDigest, BaseViewHolder> {


        public MyAskQuickAdapter2() {
            super(R.layout.people_digest);
        }

        @Override
        protected void convert(final BaseViewHolder helper, PeopleDigest item) {
            /**
             *   String text = label.getBody()
             .replaceFirst(floatingSearchView.getQuery(),
             "<font color=\"" + textLight + "\">" + floatingSearchView.getQuery() + "</font>");
             textView.setText(Html.fromHtml(text));
             */
            helper.setText(R.id.uname, item.name)
                    .setText(R.id.ask_num, Html.fromHtml("<font color=\"#029ff4\">提问：</font>" + item.num_of_ask + "次"))
                    .setText(R.id.num_of_discuss, Html.fromHtml("<font color=\"#029ff4\">参与讨论：</font>" + item.num_of_discuss + "次"))
                    .setText(R.id.num_of_bargen, Html.fromHtml("<font color=\"#029ff4\">积分：</font>" + item.num_of_bargen))
                    .setText(R.id.num_of_fans, Html.fromHtml("<font color=\"#029ff4\">粉丝数量：</font>" + item.num_of_fans));

            TextView canale_attention = helper.getView(R.id.canale_attention);
            final List<PeopleDigest> listtremp = getData();
            canale_attention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CircleDialog.Builder()
                            .setTitle("提醒")
                            .setText("你确定取消关注吗？")
                            .setPositive("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final int belt_uid = listtremp.get(helper.getAdapterPosition()).id;


                                    dialogFragment = new CircleDialog.Builder()
                                            .setProgressText("正在发送请求...")
                                            .setCancelable(false)
                                            .setCanceledOnTouchOutside(false)
                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                            .show(Objects.requireNonNull(MyAsk.this).getSupportFragmentManager());


                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpURLConnection urlConnection = null;
                                            try {
                                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.cancleAttentionUser));
                                                OutputStream out = urlConnection.getOutputStream();
                                                JSONObject j_ = new JSONObject();
                                                j_.put("id", KlbertjCache.uid);
                                                j_.put("atten_id", belt_uid);
                                                out.write(EncryptionUtils.decryptByByte(j_.toJSONString().getBytes("UTF-8")));
                                                out.close();
                                                int responseCode = urlConnection.getResponseCode();
                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                    Thread.sleep(1000);
                                                    Objects.requireNonNull(Objects.requireNonNull(MyAsk.this)).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (dialogFragment != null) {
                                                                dialogFragment.dismiss();
                                                                dialogFragment = null;
                                                            }


                                                            if ("f".equals(result)) {
                                                                ToastUtils.showToast(MyAsk.this, "token过期");
                                                            } else if ("server error".equals(result)) {
                                                                ToastUtils.showToast(MyAsk.this, "服务器错误");

                                                            } else if ("bp".equals(result)) {
                                                                ToastUtils.showToast(MyAsk.this, "参数错误");
                                                            } else {

                                                                ToastUtils.showToast(MyAsk.this, "操作成功");
                                                                listtremp.remove(helper.getAdapterPosition());
                                                                MyAskQuickAdapter2.this.notifyItemRemoved(helper.getAdapterPosition());

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
                            })
                            .setNegative("取消", null)
                            .show(MyAsk.this.getSupportFragmentManager());
                }
            });


        }
    }


    private class MyAskQuickAdapter3 extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {


        public MyAskQuickAdapter3() {
            super(R.layout.bargen_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {

            View recharge_detail = helper.getView(R.id.recharge_detail);
            View exchange_detail = helper.getView(R.id.exchange_detail);

            recharge_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Objects.requireNonNull(MyAsk.this), MyAsk.class);
                    i.putExtra("title", "积分充值");
                    i.putExtra("itemType", 4);
                    startActivity(i);
                }
            });
            exchange_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Objects.requireNonNull(MyAsk.this), MyAsk.class);
                    i.putExtra("title", "积分兑换");
                    i.putExtra("itemType", 5);
                    i.putExtra("bargen", "60分");
                    startActivity(i);
                }
            });

        }
    }

    private class MyAskQuickAdapter4 extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {


        public MyAskQuickAdapter4() {
            super(R.layout.recharge);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {
            ClearableEditText bargen_num;
            Button charge;
            bargen_num = helper.getView(R.id.bargen_num);

            charge = helper.getView(R.id.charge);
            charge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //充值逻辑
                }
            });

        }
    }


    private class MyAskQuickAdapter5 extends BaseQuickAdapter<Gift, BaseViewHolder> {


        public MyAskQuickAdapter5() {
            super(R.layout.gift);
        }

        @Override
        protected void convert(BaseViewHolder helper, Gift item) {
            helper.setText(R.id.gift1_desc, Html.fromHtml("<font color=\"#029ff4\">名称：</font>" + item.gitf1_name));
            helper.setText(R.id.gift1_need_bargen, Html.fromHtml("<font color=\"#029ff4\">所需积分：</font>" + item.gift1_need_bar));
            helper.setText(R.id.gift2_desc, Html.fromHtml("<font color=\"#029ff4\">名称：</font>" + item.gitf2_name));
            helper.setText(R.id.gift2_need_bargen, Html.fromHtml("<font color=\"#029ff4\">所需积分：</font>" + item.gift2_need_bar));
            ImageView gift1_pic = helper.getView(R.id.gift1_pic);
            ImageView gift2_pic = helper.getView(R.id.gift2_pic);
            final int position = helper.getAdapterPosition();
            final List<Gift> gift_list = getData();
            gift1_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("MyAskQuickAdapter5", gift_list.get(position).gift1_id + "");
                }
            });
            gift2_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("MyAskQuickAdapter5", gift_list.get(position).gift2_id + "");
                }
            });

        }
    }

    private class MyAskQuickAdapter6 extends BaseQuickAdapter<Gift, BaseViewHolder> {


        public MyAskQuickAdapter6() {
            super(R.layout.settings_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, Gift item) {
            MySwitchButton night_taggle = helper.getView(R.id.night_taggle);
            MySwitchButton open_taggle = helper.getView(R.id.open_taggle);
            LinearLayout clear_cache = helper.getView(R.id.clear_cache);
            Button logout = helper.getView(R.id.logout);

            night_taggle.setOnSwitchListener(new MySwitchButton.onSwitchListener() {
                @Override
                public void onSwitchChanged(boolean isCheck) {
                    Log.i("onSwitchChanged", isCheck + "");

                }
            });
            open_taggle.setOnSwitchListener(new MySwitchButton.onSwitchListener() {
                @Override
                public void onSwitchChanged(boolean isCheck) {
                    Log.i("onSwitchChanged", isCheck + "");

                }
            });
            clear_cache.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CircleDialog.Builder()
                            .setTitle("提醒")
                            .setText("你确定清空缓存吗？")
                            .setPositive("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtils.showToast(MyAsk.this, "已清除");
                                }
                            })
                            .setNegative("取消", null)
                            .show(MyAsk.this.getSupportFragmentManager());
                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CircleDialog.Builder()
                            .setTitle("提醒")
                            .setText("你确定退出登录吗？")
                            .setPositive("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialogFragment = new CircleDialog.Builder()
                                            .setProgressText("正在发送请求...")
                                            .setCanceledOnTouchOutside(false)
                                            .setCancelable(false)
                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                            .show(getSupportFragmentManager());




                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpURLConnection urlConnection = null;
                                            try {
                                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.logout));
                                                OutputStream out = urlConnection.getOutputStream();


                                                out.write(EncryptionUtils.decryptByByte((KlbertjCache.uid + "").getBytes("UTF-8")));
                                                out.close();
                                                int responseCode = urlConnection.getResponseCode();
                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                    if(PushHandler.out!=null){
                                                        try {
                                                            PushHandler.out.write("exit".getBytes());
                                                            PushHandler. out.flush();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    InputStream inputStream = urlConnection.getInputStream();
                                                    final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。
                                                    if ("ok".equals(result)) {
                                                        MyAsk.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (dialogFragment != null) {
                                                                    dialogFragment.dismiss();
                                                                    dialogFragment = null;
                                                                }

                                                                ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                                    @Override
                                                                    public void OnLoadingCancel() {
                                                                        ToastUtils.showToast(MyAsk.this, "已退出");

                                                                        SharePreferenceUtils.putInteger(MyAsk.this, Cons.IS_LOGIN, Cons.NOT_LOGIN);
                                                                        KlbertjCache.uid = Cons.NOT_LOGIN;



                                                                        startActivity(new Intent(MyAsk.this, Login.class));
                                                                        MyAsk.this.finish();
                                                                    }
                                                                }, MyAsk.this);
                                                            }
                                                        });
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();

                                }
                            })
                            .setNegative("取消", null)
                            .show(MyAsk.this.getSupportFragmentManager());
                }
            });
        }
    }

    private class MyAskQuickAdapter7 extends BaseQuickAdapter<Gift, BaseViewHolder> {


        public MyAskQuickAdapter7() {
            super(R.layout.personal_item);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void convert(BaseViewHolder helper, Gift item) {
            final TextView t_active = helper.getView(R.id.t_active);
            View change_nick_name = helper.getView(R.id.change_nick_name);

            View change_pass = helper.getView(R.id.change_pass);
          final  TextView nickname = helper.getView(R.id.nickname);
            View change_avator = helper.getView(R.id.change_avator);
            nickname.setText(KlbertjCache.name);
            View change_bg = helper.getView(R.id.change_bg);
            final View active = helper.getView(R.id.active);
            change_nick_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    new CircleDialog.Builder()
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(true)
                            .setInputManualClose(false)
                            .setTitle("更改昵称")

                            .setInputHint("请输入您的新昵称")

                            .autoInputShowKeyboard()

                            .setInputCounter(80, new OnInputCounterChangeListener() {
                                @Override
                                public String onCounterChange(int maxLen, int currentLen) {
                                    return maxLen - currentLen + "/" + maxLen;
                                }
                            })
                            .setInputCounterColor(getResources().getColor(R.color.colorAccent))
                            .setNegative("取消", null)

                            .setPositiveInput("确定", new OnInputClickListener() {
                                @Override
                                public void onClick(final String text, View v) {
                                    if (TextUtils.isEmpty(text)) {
                                        ToastUtils.showToast(MyAsk.this, "内容不能为空");
                                    }  else {
                                        dialogFragment = new CircleDialog.Builder()
                                                .setProgressText("正在发送请求...")
                                                .setCanceledOnTouchOutside(false)
                                                .setCancelable(false)
                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                .show(getSupportFragmentManager());
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                HttpURLConnection urlConnection = null;
                                                try {
                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.modifyUn));
                                                    OutputStream out = urlConnection.getOutputStream();
                                                    JSONObject sebd = new JSONObject();
                                                    sebd.put("n_uname", text);
                                                    sebd.put("id", KlbertjCache.uid + "");
                                                    out.write(EncryptionUtils.decryptByByte(sebd.toJSONString().getBytes("UTF-8")));
                                                    out.close();
                                                    int responseCode = urlConnection.getResponseCode();
                                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                                        InputStream inputStream = urlConnection.getInputStream();
                                                        final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。

                                                        MyAsk.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (dialogFragment != null) {
                                                                    dialogFragment.dismiss();
                                                                    dialogFragment = null;
                                                                }

                                                                if ("3".equals(result)) {
                                                                    ToastUtils.showToast(MyAsk.this, "token过期");
                                                                } else if ("1".equals(result)) {
                                                                    ToastUtils.showToast(MyAsk.this, "名称被占用");
                                                                } else {
                                                                    ToastUtils.showToast(MyAsk.this, "修改成功");
                                                                    nickname.setText(text);
                                                                    KlbertjCache.name = text;
                                                                    SharePreferenceUtils.putString(MyAsk.this,Cons.NAME,text);

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
                            })

                            .show(MyAsk.this.getSupportFragmentManager());
                }
            });
            change_pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    new CircleDialog.Builder()
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(true)
                            .setInputManualClose(false)
                            .setTitle("更改密码")

                            .setInputHint("请输入您的旧密码和新密码 格式：旧密码##新密码")

                            .autoInputShowKeyboard()

                            .setInputCounter(80, new OnInputCounterChangeListener() {
                                @Override
                                public String onCounterChange(int maxLen, int currentLen) {
                                    return maxLen - currentLen + "/" + maxLen;
                                }
                            })
                            .setInputCounterColor(getResources().getColor(R.color.colorAccent))
                            .setNegative("取消", null)

                            .setPositiveInput("确定", new OnInputClickListener() {
                                @Override
                                public void onClick(String text, View v) {
                                    if (TextUtils.isEmpty(text)) {
                                        ToastUtils.showToast(MyAsk.this, "内容不能为空");
                                    } else if (!text.contains("##")) {
                                        ToastUtils.showToast(MyAsk.this, "格式错误");
                                    } else {
                                        final String[] opw = text.split("##");

                                        dialogFragment = new CircleDialog.Builder()
                                                .setProgressText("正在发送请求...")
                                                .setCanceledOnTouchOutside(false)
                                                .setCancelable(false)
                                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                .show(getSupportFragmentManager());
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                HttpURLConnection urlConnection = null;
                                                try {
                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.modifyPw));
                                                    OutputStream out = urlConnection.getOutputStream();
                                                    JSONObject sebd = new JSONObject();
                                                    sebd.put("opw", opw[0]);
                                                    sebd.put("npw", opw[1]);
                                                    sebd.put("id", KlbertjCache.uid + "");
                                                    out.write(EncryptionUtils.decryptByByte(sebd.toJSONString().getBytes("UTF-8")));
                                                    out.close();
                                                    int responseCode = urlConnection.getResponseCode();
                                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                                        InputStream inputStream = urlConnection.getInputStream();
                                                        final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。

                                                        MyAsk.this.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (dialogFragment != null) {
                                                                    dialogFragment.dismiss();
                                                                    dialogFragment = null;
                                                                }

                                                                if ("2".equals(result)) {
                                                                    ToastUtils.showToast(MyAsk.this, "token过期");
                                                                } else if ("1".equals(result)) {
                                                                    ToastUtils.showToast(MyAsk.this, "原密码错误");
                                                                } else {
                                                                    ToastUtils.showToast(MyAsk.this, "修改成功");
                                                                    startActivity(new Intent(MyAsk.this, Login.class));
                                                                    MyAsk.this.finish();
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
                            })

                            .show(MyAsk.this.getSupportFragmentManager());


                }
            });


            if (SharePreferenceUtils.getInteger(MyAsk.this, Cons.IS_LOGIN) == -1) {
                active.setVisibility(View.VISIBLE);
                t_active.setText("请激活您的账户");
                t_active.setTextColor(getResources().getColor(android.R.color.black));
            } else {
                t_active.setText("您的账户已激活");
                active.setVisibility(View.GONE);
                t_active.setTextColor(getResources().getColor(R.color.colorTextAssistant));
            }
            final TextView major, college;
            major = helper.getView(R.id.major);
            college = helper.getView(R.id.college);
            final View change_major = helper.getView(R.id.change_major);
            final View change_college = helper.getView(R.id.change_college);

            if (TextUtils.isEmpty(SharePreferenceUtils.getString(MyAsk.this, Cons.COLLEGE))) {
                change_college.setVisibility(View.VISIBLE);
            } else {

                change_college.setVisibility(View.GONE);
                college.setText("院系：" + SharePreferenceUtils.getString(MyAsk.this, Cons.COLLEGE));
                college.setTextColor(getResources().getColor(R.color.colorTextAssistant));

            }
            if (TextUtils.isEmpty(SharePreferenceUtils.getString(MyAsk.this, Cons.MAJOR))) {
                change_major.setVisibility(View.VISIBLE);
            } else {
                change_major.setVisibility(View.GONE);
                major.setText("专业：" + SharePreferenceUtils.getString(MyAsk.this, Cons.MAJOR));
                major.setTextColor(getResources().getColor(R.color.colorTextAssistant));
            }
            avatar = helper.getView(R.id.avatar);
            Glide.with(MyAsk.this).asBitmap().load(Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + KlbertjCache.avator_path).apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            avatar.setBitmap(resource);
                        }
                    });
            change_college.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SharePreferenceUtils.getInteger(MyAsk.this, Cons.IS_LOGIN) == -1) {
                        ToastUtils.showToast(MyAsk.this, "您的账户还没有激活，请先激活");
                    } else {
                        final CheckedAdapter checkedAdapterR = new CheckedAdapter(MyAsk.this, Cons.college_name, true);

                        new CircleDialog.Builder()
                                .configDialog(new ConfigDialog() {
                                    @Override
                                    public void onConfig(DialogParams params) {
                                        params.backgroundColorPress = Color.CYAN;
                                        params.animStyle = R.style.dialogWindowAnim;
                                    }
                                })
                                .setTitle("请选择院系")

                                .setItems(checkedAdapterR, new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        checkedAdapterR.toggle(position, Cons.college_name[position]);

                                    }
                                })
                                .setItemsManualClose(true)
                                .setPositive("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String college2 = checkedAdapterR.getSaveChecked().toString();

                                        final String college1 = college2.substring(college2.indexOf("=") + 1, college2.indexOf("}"));
                                        if ("{".equals(college1)) {
                                            ToastUtils.showToast(MyAsk.this, "你还没有选择院系");
                                        } else {
                                            new CircleDialog.Builder()
                                                    .setTitle("提醒")
                                                    .setText("院系只能修改一次，确定要修改吗？")
                                                    .setCanceledOnTouchOutside(false)
                                                    .setPositive("确定", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在发送请求...")
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(getSupportFragmentManager());

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    HttpURLConnection urlConnection = null;
                                                                    try {
                                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.setCollege));
                                                                        OutputStream out = urlConnection.getOutputStream();
                                                                        JSONObject job = new JSONObject();
                                                                        job.put("id", SharePreferenceUtils.getInteger(MyAsk.this, Cons.IS_LOGIN));
                                                                        job.put("college", college1);
                                                                        out.write(EncryptionUtils.decryptByByte(job.toJSONString().getBytes("UTF-8")));
                                                                        out.close();
                                                                        int responseCode = urlConnection.getResponseCode();
                                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                            InputStream inputStream = urlConnection.getInputStream();
                                                                            final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。
                                                                            if ("ok".equals(result)) {
                                                                                MyAsk.this.runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        if (dialogFragment != null) {
                                                                                            dialogFragment.dismiss();
                                                                                            dialogFragment = null;
                                                                                        }

                                                                                        ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                                                            @Override
                                                                                            public void OnLoadingCancel() {
                                                                                                college.setText("院系：" + college1);
                                                                                                college.setTextColor(getResources().getColor(R.color.colorTextAssistant));
                                                                                                SharePreferenceUtils.putString(MyAsk.this, Cons.COLLEGE, college1);
                                                                                                KlbertjCache.college = college1;
                                                                                                change_college.setVisibility(View.GONE);
//                                                                            ToastUtils.showToast(MyAsk.this, result);

                                                                                                ToastUtils.showToast(MyAsk.this, "设置成功");
                                                                                            }
                                                                                        }, MyAsk.this);
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }).start();


                                                        }
                                                    })
                                                    .setNegative("取消", null)
                                                    .show(MyAsk.this.getSupportFragmentManager());
                                        }

                                    }
                                })
                                .show(getSupportFragmentManager());
                    }


                }
            });
            change_major.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String college = SharePreferenceUtils.getString(MyAsk.this, Cons.COLLEGE);
                    if (TextUtils.isEmpty(college)) {
                        ToastUtils.showToast(MyAsk.this, "请先选择院系");
                    } else {
                        JSONObject jsonObject = JSONObject.parseObject(Cons.college_major);
                        JSONArray majors = jsonObject.getJSONArray(college);
                        final String[] s = new String[majors.size()];
                        majors.toArray(s);
                        final CheckedAdapter checkedAdapter = new CheckedAdapter(MyAsk.this, s, true);
                        new CircleDialog.Builder()
                                .configDialog(new ConfigDialog() {
                                    @Override
                                    public void onConfig(DialogParams params) {
                                        params.backgroundColorPress = Color.CYAN;
                                        params.animStyle = R.style.dialogWindowAnim;
                                    }
                                })
                                .setTitle("请选择专业")

                                .setItems(checkedAdapter, new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        checkedAdapter.toggle(position, s[position]);

                                    }
                                })
                                .setItemsManualClose(true)
                                .setPositive("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String major1 = checkedAdapter.getSaveChecked().toString();
                                        final String major2 = major1.substring(major1.indexOf("=") + 1, major1.indexOf("}"));
                                        if ("{".equals(major2)) {
                                            ToastUtils.showToast(MyAsk.this, "你还没有选择专业");
                                        } else {
                                            new CircleDialog.Builder()
                                                    .setTitle("提醒")
                                                    .setText("专业只能修改一次，确定要修改吗？")
                                                    .setCanceledOnTouchOutside(false)
                                                    .setPositive("确定", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {


                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在发送请求...")
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(getSupportFragmentManager());

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    HttpURLConnection urlConnection = null;
                                                                    try {
                                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.setMajor));
                                                                        OutputStream out = urlConnection.getOutputStream();
                                                                        JSONObject job = new JSONObject();
                                                                        job.put("id", SharePreferenceUtils.getInteger(MyAsk.this, Cons.IS_LOGIN));
                                                                        job.put("major", major2);
                                                                        out.write(EncryptionUtils.decryptByByte(job.toJSONString().getBytes("UTF-8")));
                                                                        out.close();
                                                                        int responseCode = urlConnection.getResponseCode();
                                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                            InputStream inputStream = urlConnection.getInputStream();
                                                                            final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。
                                                                            if ("ok".equals(result)) {
                                                                                MyAsk.this.runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        if (dialogFragment != null) {
                                                                                            dialogFragment.dismiss();
                                                                                            dialogFragment = null;
                                                                                        }

                                                                                        ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                                                            @Override
                                                                                            public void OnLoadingCancel() {
                                                                                                major.setText("专业：" + major2);
                                                                                                major.setTextColor(getResources().getColor(R.color.colorTextAssistant));
                                                                                                SharePreferenceUtils.putString(MyAsk.this, Cons.MAJOR, major2);
                                                                                                KlbertjCache.major = major2;
                                                                                                change_major.setVisibility(View.GONE);
                                                                                                ToastUtils.showToast(MyAsk.this, "设置成功");
                                                                                            }
                                                                                        }, MyAsk.this);
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }).start();


                                                        }
                                                    })
                                                    .setNegative("取消", null)
                                                    .show(MyAsk.this.getSupportFragmentManager());
                                        }

                                    }
                                })
                                .show(getSupportFragmentManager());

                    }
                }
            });
            change_avator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPicType = 0;
                    temp_imgUri = StorageUtils.get_avator_photo_uri();
                    alertDialog();
                }
            });
            change_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPicType = 1;
                    temp_imgUri = StorageUtils.get_bg_photo_uri();
                    alertDialog();
                }
            });
            active.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SharePreferenceUtils.getInteger(MyAsk.this, Cons.IS_LOGIN) == -1) {

                        new CircleDialog.Builder()
                                .setCanceledOnTouchOutside(false)
                                .setCancelable(false)
                                .setInputManualClose(false)
                                .setTitle("账户激活")

                                .setInputHint("请输入发送到您邮箱的激活码")

                                .autoInputShowKeyboard()

                                .setInputCounter(14, new OnInputCounterChangeListener() {
                                    @Override
                                    public String onCounterChange(int maxLen, int currentLen) {
                                        return maxLen - currentLen + "/" + maxLen;
                                    }
                                })
                                .setInputCounterColor(getResources().getColor(R.color.colorAccent))
                                .setNegative("取消", null)
                                .setPositiveInput("发送", new OnInputClickListener() {
                                    @Override
                                    public void onClick(final String text, View v) {
                                        if (TextUtils.isEmpty(text)) {
                                            ToastUtils.showToast(MyAsk.this, "内容不能为空");
                                        } else {

                                            dialogFragment = new CircleDialog.Builder()
                                                    .setProgressText("正在发送请求...")
                                                    .setCancelable(false)
                                                    .setCanceledOnTouchOutside(false)
                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                    .show(getSupportFragmentManager());


                                            //网络请求
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    HttpURLConnection urlConnection = null;
                                                    try {
                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.active));
                                                        OutputStream out = urlConnection.getOutputStream();
                                                        out.write(EncryptionUtils.decryptByByte(text.getBytes("UTF-8")));
                                                        out.close();
                                                        int responseCode = urlConnection.getResponseCode();
                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                            InputStream inputStream = urlConnection.getInputStream();
                                                            final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。
                                                            if ("timeout".equals(result)) {
                                                                MyAsk.this.runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (dialogFragment != null) {
                                                                            dialogFragment.dismiss();
                                                                            dialogFragment = null;
                                                                        }
                                                                        ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                                            @Override
                                                                            public void OnLoadingCancel() {
                                                                                active.setVisibility(View.VISIBLE);
                                                                                t_active.setText("请激活您的账户");
                                                                                t_active.setTextColor(getResources().getColor(android.R.color.black));
                                                                                ToastUtils.showToast(MyAsk.this, "验证码输入有误或验证码已经过期");
                                                                            }
                                                                        }, MyAsk.this);
                                                                    }
                                                                });
                                                            } else {
                                                                SharePreferenceUtils.putInteger(MyAsk.this, Cons.IS_LOGIN, Integer.valueOf(result));
                                                                KlbertjCache.uid = Integer.valueOf(result);
                                                                MyAsk.this.runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        dialogFragment.dismiss();
                                                                        ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                                            @Override
                                                                            public void OnLoadingCancel() {
                                                                                ToastUtils.showToast(MyAsk.this, "激活成功");
                                                                                t_active.setText("您的账户已激活");
                                                                                active.setVisibility(View.GONE);
                                                                                t_active.setTextColor(getResources().getColor(R.color.colorTextAssistant));
                                                                            }
                                                                        }, MyAsk.this);


                                                                    }
                                                                });
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }).start();


                                        }


                                    }
                                }).show(MyAsk.this.getSupportFragmentManager());

                    } else {
                        ToastUtils.showToast(MyAsk.this, "您的账户已经激活");
                    }


                }
            });

        }
    }


    private class MyAskWithNoDataQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {

        MyAskWithNoDataQuickAdapter() {
            super(R.layout.index_with_nodata);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {

        }
    }

    private Intent get_take_photo_intent(Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;

    }

    private void addImage() {

        if (ContextCompat.checkSelfPermission(MyAsk.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Cons.REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Matisse.from(MyAsk.this)
                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                    .maxSelectable(1)
                    .thumbnailScale(0.85f)
                    .theme(R.style.my_zhihu_theme)
                    .imageEngine(new ImageEngine() {
                        @Override
                        public void loadThumbnail(Context context, int i, Drawable drawable, ImageView imageView, Uri uri) {
                            Glide.with(imageView.getContext())
                                    .load(uri)
                                    .apply(new RequestOptions().centerCrop()
                                            .placeholder(R.drawable.ic_loading)
                                            .error(R.drawable.ic_loadingerr))
                                    .into(imageView);
                        }

                        @Override
                        public void loadAnimatedGifThumbnail(Context context, int i, Drawable drawable, ImageView imageView, Uri uri) {

                            Glide.with(imageView.getContext())
                                    .load(uri)
                                    .apply(new RequestOptions().centerCrop()
                                            .placeholder(R.drawable.ic_loading)
                                            .error(R.drawable.ic_loadingerr))
                                    .into(imageView);
                        }

                        @Override
                        public void loadImage(Context context, int i, int i1, ImageView imageView, Uri uri) {

                            Glide.with(imageView.getContext())
                                    .load(uri)
                                    .apply(new RequestOptions().centerCrop()
                                            .placeholder(R.drawable.ic_loading)
                                            .error(R.drawable.ic_loadingerr))
                                    .into(imageView);
                        }

                        @Override
                        public void loadAnimatedGifImage(Context context, int i, int i1, ImageView imageView, Uri uri) {

                            Glide.with(imageView.getContext())
                                    .load(uri)
                                    .apply(new RequestOptions().centerCrop()
                                            .placeholder(R.drawable.ic_loading)
                                            .error(R.drawable.ic_loadingerr))
                                    .into(imageView);
                        }

                        @Override
                        public boolean supportAnimatedGif() {
                            return false;
                        }
                    })
                    .forResult(Cons.REQUEST_ADD_IMAGE);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        temp_imgUri = StorageUtils.get_temp_photo_uri();
        if (requestCode == Cons.REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addImage();
            }
        } else if (requestCode == Cons.REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA_MOUNT_UNMOUNT_FILESYSTEMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                startActivityForResult(get_take_photo_intent(temp_imgUri), Cons.REQUEST_TAKE_PHOTO);
            } else {
                ToastUtils.showToast(this, "meiyouquanxian" + grantResults[0] + grantResults[1]);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Cons.REQUEST_ADD_IMAGE && resultCode == RESULT_OK) {
            //   ProblemDetail.ulist = Matisse.obtainResult(data);
            // multiple_image.addItem(Matisse.obtainResult(data));
            List<Uri> uri_list = Matisse.obtainResult(data);
            startActivityForResult(
                    cropPhoto(uri_list.get(0), temp_imgUri, 300, 400),
                    Cons.REQUEST_CROP_PHOTO);
        } else if (requestCode == Cons.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            startActivityForResult(
                    cropPhoto(temp_imgUri, temp_imgUri, 300, 400),
                    Cons.REQUEST_CROP_PHOTO);

        } else if (requestCode == Cons.REQUEST_CROP_PHOTO && resultCode == RESULT_OK) {
            final Uri urii = temp_imgUri;
            if (getPicType == 0) {


                if (TextUtils.isEmpty(KlbertjCache.avator_path) || KlbertjCache.uid == -2) {
                    ToastUtils.showToast(MyAsk.this, "权限错误");
                } else {
                    dialogFragment = new CircleDialog.Builder()
                            .setProgressText("正在发送请求...")
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                            .show(getSupportFragmentManager());
                    final String name = ToastUtils.getCurrentSeconds() + ".jpg";
                    if ("klbertj.jpg".equals(KlbertjCache.avator_path)) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.modifyAvator));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("id", KlbertjCache.uid);
                                    jsonObject.put("avator_path", name);
                                    out.write(EncryptionUtils.decryptByByte(jsonObject.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = urlConnection.getInputStream();
                                        Thread.sleep(1000);
                                        final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。


                                        MyAsk.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }


                                                ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                    @Override
                                                    public void OnLoadingCancel() {

                                                        if ("f".equals(result)) {
                                                            ToastUtils.showToast(MyAsk.this, "权限过期");
                                                        } else {


                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在更改图片...")
                                                                    .setCancelable(false)
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(getSupportFragmentManager());

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {


                                                                    SFTPUtils.upLoadAvator(MyAsk.this, urii, Cons.AVATOR_PICS_PATH_IN_SERVER, name);
                                                                    try {
                                                                        Thread.sleep(1200);
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    MyAsk.this.runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (dialogFragment != null) {
                                                                                dialogFragment.dismiss();
                                                                                dialogFragment = null;
                                                                            }


                                                                            SharePreferenceUtils.putString(Objects.requireNonNull(MyAsk.this), Cons.AVATOR_PATH, name);
                                                                            KlbertjCache.avator_path = name;
                                                                            Glide.with(MyAsk.this).asBitmap().load(temp_imgUri).into(new SimpleTarget<Bitmap>() {
                                                                                @Override
                                                                                public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {

                                                                                    avatar.setBitmap(resource);

                                                                                }
                                                                            });
                                                                            ToastUtils.showToast(MyAsk.this, "操作成功");

                                                                        }
                                                                    });

                                                                }
                                                            }).start();

                                                        }

                                                    }
                                                }, MyAsk.this);
                                            }
                                        });

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();

                    } else {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.modifyAvator));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("id", KlbertjCache.uid);
                                    jsonObject.put("avator_path", name);
                                    out.write(EncryptionUtils.decryptByByte(jsonObject.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = urlConnection.getInputStream();
                                        Thread.sleep(1000);
                                        final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。


                                        MyAsk.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }


                                                ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                    @Override
                                                    public void OnLoadingCancel() {

                                                        if ("f".equals(result)) {
                                                            ToastUtils.showToast(MyAsk.this, "权限过期");
                                                        } else {


                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在更改图片...")
                                                                    .setCancelable(false)
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(getSupportFragmentManager());

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    SFTPUtils.deleteAvator(MyAsk.this, Cons.AVATOR_PICS_PATH_IN_SERVER, KlbertjCache.avator_path);
                                                                    SFTPUtils.upLoadAvator(MyAsk.this, urii, Cons.AVATOR_PICS_PATH_IN_SERVER, name);
                                                                    try {
                                                                        Thread.sleep(1200);
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    MyAsk.this.runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (dialogFragment != null) {
                                                                                dialogFragment.dismiss();
                                                                                dialogFragment = null;
                                                                            }


                                                                            SharePreferenceUtils.putString(Objects.requireNonNull(MyAsk.this), Cons.AVATOR_PATH, name);
                                                                            KlbertjCache.avator_path = name;
                                                                            Glide.with(MyAsk.this).asBitmap().load(temp_imgUri).into(new SimpleTarget<Bitmap>() {
                                                                                @Override
                                                                                public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {

                                                                                    avatar.setBitmap(resource);

                                                                                }
                                                                            });
                                                                            ToastUtils.showToast(MyAsk.this, "操作成功");

                                                                        }
                                                                    });

                                                                }
                                                            }).start();

                                                        }

                                                    }
                                                }, MyAsk.this);
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


            } else {
                if (KlbertjCache.uid == -2) {
                    ToastUtils.showToast(MyAsk.this, "权限错误");
                } else {
                    dialogFragment = new CircleDialog.Builder()
                            .setProgressText("正在发送请求...")
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                            .show(getSupportFragmentManager());
                    final String name = ToastUtils.getCurrentSeconds() + ".jpg";
                    if ("".equals(KlbertjCache.bg)) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.modifyBg));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("id", KlbertjCache.uid);
                                    jsonObject.put("bg_path", name);
                                    out.write(EncryptionUtils.decryptByByte(jsonObject.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = urlConnection.getInputStream();
                                        Thread.sleep(1000);
                                        final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。


                                        MyAsk.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }


                                                ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                    @Override
                                                    public void OnLoadingCancel() {

                                                        if ("f".equals(result)) {
                                                            ToastUtils.showToast(MyAsk.this, "权限过期");
                                                        } else {


                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在更改图片...")
                                                                    .setCancelable(false)
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(getSupportFragmentManager());

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {


                                                                    SFTPUtils.upLoadAvator(MyAsk.this, urii, Cons.BG_PICS_PATH_IN_SERVER, name);
                                                                    try {
                                                                        Thread.sleep(1200);
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    MyAsk.this.runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (dialogFragment != null) {
                                                                                dialogFragment.dismiss();
                                                                                dialogFragment = null;
                                                                            }


                                                                            SharePreferenceUtils.putString(Objects.requireNonNull(MyAsk.this), Cons.AVATOR_PATH, name);
                                                                            KlbertjCache.bg = name;

                                                                            ToastUtils.showToast(MyAsk.this, "操作成功");

                                                                        }
                                                                    });

                                                                }
                                                            }).start();

                                                        }

                                                    }
                                                }, MyAsk.this);
                                            }
                                        });

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();

                    } else {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.modifyBg));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("id", KlbertjCache.uid);
                                    jsonObject.put("bg_path", name);
                                    out.write(EncryptionUtils.decryptByByte(jsonObject.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = urlConnection.getInputStream();
                                        Thread.sleep(1000);
                                        final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。


                                        MyAsk.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }


                                                ToastUtils.showloading("正在保存您的输入", "正在验证..", new ToastUtils.OnLoadingCancelListener() {
                                                    @Override
                                                    public void OnLoadingCancel() {

                                                        if ("f".equals(result)) {
                                                            ToastUtils.showToast(MyAsk.this, "权限过期");
                                                        } else {


                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在更改图片...")
                                                                    .setCancelable(false)
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(getSupportFragmentManager());

                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    SFTPUtils.deleteAvator(MyAsk.this, Cons.BG_PICS_PATH_IN_SERVER, KlbertjCache.bg);
                                                                    SFTPUtils.upLoadAvator(MyAsk.this, urii, Cons.BG_PICS_PATH_IN_SERVER, name);
                                                                    try {
                                                                        Thread.sleep(1200);
                                                                    } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    MyAsk.this.runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if (dialogFragment != null) {
                                                                                dialogFragment.dismiss();
                                                                                dialogFragment = null;
                                                                            }


                                                                            SharePreferenceUtils.putString(Objects.requireNonNull(MyAsk.this), Cons.AVATOR_PATH, name);
                                                                            KlbertjCache.bg = name;

                                                                            ToastUtils.showToast(MyAsk.this, "操作成功");

                                                                        }
                                                                    });

                                                                }
                                                            }).start();

                                                        }

                                                    }
                                                }, MyAsk.this);
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


        }

    }

    private Intent cropPhoto(Uri uri, Uri cropUri, int outputX, int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//可裁剪
        intent.putExtra("aspectX", 1); //裁剪的宽比例
        intent.putExtra("aspectY", 1);  //裁剪的高比例
        intent.putExtra("outputX", outputX); //裁剪的宽度
        intent.putExtra("outputY", outputY);  //裁剪的高度
        intent.putExtra("scale", true); //支持缩放
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);  //将裁剪的结果输出到指定的Uri
        intent.putExtra("return-data", true); //若为true则表示返回数据
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//裁剪成的图片的格式
        intent.putExtra("noFaceDetection", true);  //启用人脸识别
        return intent;
    }

    private void alertDialog() {

        new CircleDialog.Builder()
                .configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                        params.backgroundColorPress = Color.CYAN;
                        params.animStyle = R.style.dialogWindowAnim;
                    }
                })
                .setTitle("选择操作方式")
                .setItems(new String[]{
                        "拍照", "从相册选择"}, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {

                            if ((ContextCompat.checkSelfPermission(MyAsk.this,
                                    Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(MyAsk.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED)) {

                                ActivityCompat.requestPermissions(MyAsk.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA},
                                        Cons.REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA_MOUNT_UNMOUNT_FILESYSTEMS);
                            } else {
                                /*shouldShowRequestPermissionRationale();*/

                                startActivityForResult(get_take_photo_intent(temp_imgUri), Cons.REQUEST_TAKE_PHOTO);
                            }


                        } else {
                            addImage();
                        }

                    }
                })
                .setNegative("取消", null)
                .show(MyAsk.this.getSupportFragmentManager());
    }


    private class MyAskQuickAdapter8 extends BaseQuickAdapter<String, BaseViewHolder> {


        public MyAskQuickAdapter8() {
            super(R.layout.notify_itemm);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            JSONObject noti = null;
            noti = JSONObject.parseObject(item);

            helper.setText(R.id.con, noti.getString("con"))
                    .setText(R.id.time, noti.getString("time"));

        }
    }
}

