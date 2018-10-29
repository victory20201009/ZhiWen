package com.zzu.zk.zhiwen.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jcraft.jsch.SftpException;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.beans.ProblemDigest;
import com.zzu.zk.zhiwen.beans.Reply;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.MyCircleView;
import com.zzu.zk.zhiwen.customed_ui.NineGridLayout;
import com.zzu.zk.zhiwen.glide.MyGlide;
import com.zzu.zk.zhiwen.utils.EncryptionUtils;
import com.zzu.zk.zhiwen.utils.SFTPUtils;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.StatusBarUtil;
import com.zzu.zk.zhiwen.utils.StorageUtils;
import com.zzu.zk.zhiwen.utils.ToastUtils;
import com.zzu.zk.zhiwen.utils.UrlUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class ProblemDetail extends AppCompatActivity {
    public static List<Uri> ulist;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    RecyclerView recyclerView;
    View promenu, zan;
    View prodback;
    NineGridLayout layout_nine_grid;
    private ProblemDetail.IndexQuickAdapter mAdapter;
    ProblemDetailWithNoDataQuickAdapter problemDetailWithNoDataQuickAdapter;
    View mParent;
    View mBg;
    PhotoView mPhotoView;
    Info mInfo;
    MyCircleView avator;
    LinearLayout file_ll;
    private DialogFragment dialogFragment;
    AlphaAnimation in = new AlphaAnimation(0, 1);
    AlphaAnimation out = new AlphaAnimation(1, 0);
    TextView uname, title, score, creat_time, content, num_of_comment;
    String come_intent;
    boolean is_adopted = false;
    int qqq_id = -1;
    int reply_page = 1;
    int come_coll = 0;
    SmartRefreshLayout srefreshLayout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_detail);
        score = findViewById(R.id.score);
        srefreshLayout = findViewById(R.id.srefreshLayout);
        prodback = findViewById(R.id.prodback);
        prodback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        layout_nine_grid = findViewById(R.id.layout_nine_grid);
        layout_nine_grid.setIsShowAll(false); //当传入的图片数超过9张时，是否全部显示
        layout_nine_grid.setSpacing(5); //动态设置图片之间的间隔
        layout_nine_grid.setonClickImageListener(new NineGridLayout.OnClickImageListener() {
            @Override
            public void onClickImage(View v, int position, String url, ArrayList<String> urlList) {
                PhotoView p = (PhotoView) v;
                mInfo = p.getInfo();

                Glide.with(ProblemDetail.this).load(url).apply(MyGlide.getRequestOptions()).into(mPhotoView);
//                mPhotoView.setImageResource(imgs[position]);


                mBg.startAnimation(in);
                mBg.setVisibility(View.VISIBLE);
                mParent.setVisibility(View.VISIBLE);
                mPhotoView.animaFrom(mInfo);


            }
        });
        mPhotoView = findViewById(R.id.img);
        mPhotoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mPhotoView.enable();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBg.startAnimation(out);
                mPhotoView.animaTo(mInfo, new Runnable() {
                    @Override
                    public void run() {
                        mParent.setVisibility(View.GONE);
                    }
                });
            }
        });
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                ToastUtils.showToast(ProblemDetail.this, "chang an le");
                return false;
            }
        });
        in.setDuration(300);
        out.setDuration(300);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        num_of_comment = findViewById(R.id.num_of_comment);
        file_ll = findViewById(R.id.file_ll);
        content = findViewById(R.id.content);
        creat_time = findViewById(R.id.creat_time);
        title = findViewById(R.id.title);
        uname = findViewById(R.id.uname);
        avator = findViewById(R.id.avator);
        mParent = findViewById(R.id.parent);
        mBg = findViewById(R.id.bg);

        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
        promenu = findViewById(R.id.promenu);
        final PopupMenu pop = new PopupMenu(ProblemDetail.this, promenu);
        final Intent intent = getIntent();
        List<String> pro_pic_urls = new ArrayList<>();
        if (intent != null) {
            come_intent = intent.getStringExtra("come_intent");
            final int qid = intent.getIntExtra("qqq_id", -1);
            qqq_id = qid;
            if ("my_ques".equals(come_intent)) {
                pop.getMenuInflater().inflate(R.menu.my_ques_pro_menu, pop.getMenu());
                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addresp:
                                Intent i = new Intent(ProblemDetail.this, AddReply.class);
                                i.putExtra("qid", intent.getIntExtra("qqq_id", -1));
                                startActivity(i);
                                break;
                            case R.id.delete:
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
                                                        .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());

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
                                                                KlbertjCache.uid = SharePreferenceUtils.getInteger(ProblemDetail.this, Cons.IS_LOGIN);
                                                                send.put("id", KlbertjCache.uid);
                                                            }

                                                            send.put("qid", qqq_id);

                                                            out.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                                            out.close();
                                                            int responseCode = urlConnection.getResponseCode();
                                                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。

                                                                Thread.sleep(1000);
                                                                Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (dialogFragment != null) {
                                                                            dialogFragment.dismiss();
                                                                            dialogFragment = null;
                                                                        }

                                                                        if ("f".equals(result)) {
                                                                            ToastUtils.showToast(ProblemDetail.this, "token过期");
                                                                        } else if ("bp".equals(result)) {
                                                                            ToastUtils.showToast(ProblemDetail.this, "参数错误");
                                                                        } else {
                                                                            String contentStr = intent.getStringExtra("contentStr");
                                                                            JSONObject ques_detail1 = JSONObject.parseObject(contentStr);
                                                                            final JSONArray pics = ques_detail1.getJSONArray("pic_urls");
                                                                            final JSONObject file = ques_detail1.getJSONObject("file_urls");
                                                                            JSONObject j = JSONObject.parseObject(result);
                                                                            final JSONArray reply_pics = JSONArray.parseArray(j.getString("ru"));

                                                                            if (pics.isEmpty() && file.isEmpty() && Objects.requireNonNull(reply_pics).isEmpty()) {
                                                                                ToastUtils.showToast(ProblemDetail.this, "操作成功");
                                                                                ProblemDetail.this.finish();
                                                                            } else {
                                                                                dialogFragment = new CircleDialog.Builder()
                                                                                        .setProgressText("正在删除文件...")
                                                                                        .setCanceledOnTouchOutside(false)
                                                                                        .setCancelable(false)
                                                                                        .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                        .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());


                                                                                new Thread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        List<String> paths = null;
                                                                                        if (!pics.isEmpty()) {
                                                                                            paths = new ArrayList<>();
                                                                                            for (int i = 0; i < pics.size(); i++) {
                                                                                                paths.add(pics.getString(i));
                                                                                            }
                                                                                            SFTPUtils.deletePics(ProblemDetail.this, paths, Cons.QUES_PICS_PATH_IN_SERVER);
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
                                                                                                SFTPUtils.deleteFiles(ProblemDetail.this, paths);

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

                                                                                            SFTPUtils.deletePics(ProblemDetail.this, paths, Cons.REPLY_PICS_PATH_IN_SERVER);

                                                                                        }

                                                                                        try {
                                                                                            Thread.sleep(500);
                                                                                        } catch (InterruptedException e) {
                                                                                            e.printStackTrace();
                                                                                        }


                                                                                        Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                if (dialogFragment != null) {
                                                                                                    dialogFragment.dismiss();
                                                                                                    dialogFragment = null;
                                                                                                }

                                                                                                ToastUtils.showToast(ProblemDetail.this, "操作成功");
                                                                                                ProblemDetail.this.finish();
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
                                        .show(ProblemDetail.this.getSupportFragmentManager());
                                break;
                        }
                        pop.dismiss();
                        return false;
                    }
                });

                String contentStr = intent.getStringExtra("contentStr");

                JSONObject ques_detail = JSONObject.parseObject(contentStr);
                Log.i("aaasss", ques_detail.toJSONString());
                String avator_path = intent.getStringExtra("belongToUserHead_url");
                //   Log.i("avator_path",avator_path);
                Glide.with(ProblemDetail.this).asBitmap().apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).load(avator_path).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        avator.setBitmap(bitmap);
                    }
                });

                uname.setText(intent.getStringExtra("belong_to_user_uname"));
                title.setText(intent.getStringExtra("title"));
                score.setText(ques_detail.getInteger("score") + "积分");
                creat_time.setText(intent.getStringExtra("create_time"));
                content.setText(ques_detail.getString("content"));
                JSONArray pic_urls = ques_detail.getJSONArray("pic_urls");
                if (pic_urls.isEmpty()) {
                    layout_nine_grid.setVisibility(View.GONE);
                } else {
                    layout_nine_grid.setVisibility(View.VISIBLE);
                    List<String> ques_pics = new ArrayList<>();
                    for (int i = 0; i < pic_urls.size(); i++) {
                        ques_pics.add(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + pic_urls.getString(i));
                    }
                    layout_nine_grid.setUrlList(ques_pics);
                }


                JSONObject file_url = ques_detail.getJSONObject("file_urls");

                if (file_url.isEmpty()) {
                    file_ll.setVisibility(View.GONE);
                } else {
                    JSONArray servername = file_url.getJSONArray("server_name");
                    JSONArray s_r = file_url.getJSONArray("server_real_name");
                    JSONObject jj = null;
                    TextView v = null;
                    for (int i_i = 0; i_i < servername.size(); i_i++) {
                        v = (TextView) file_ll.getChildAt(i_i);
                        String sname = servername.getString(i_i);
                        Log.i("sname", sname);
                        for (int j_j = 0; j_j < s_r.size(); j_j++) {
                            for (int k_k = 0; k_k < s_r.size(); k_k++) {
                                jj = s_r.getJSONObject(k_k);
                                if (jj.containsKey(sname)) {
                                    break;
                                }
                            }

                            v.setVisibility(View.VISIBLE);

                            v.setText(jj.getString(sname));
                            final String realname = jj.getString(sname);
                            final String servername1 = sname;
                            Log.i("s_r.getJSONObject(j_j)", jj.getString(sname));
//                            v.setTag(sname);
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (StorageUtils.is_disk_enough()) {
                                        final String path = Cons.ROOTDir + Cons.ATTACHMENTS + realname;
                                        if (new File(path).exists()) {
                                            ToastUtils.showToast(ProblemDetail.this, "该文件已经存在");
                                        } else {
                                            new CircleDialog.Builder()
                                                    .setTitle("提醒")
                                                    .setText("您确定要下载此附件吗？")
                                                    .setCanceledOnTouchOutside(false)
                                                    .setPositive("确定", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(final View v) {

                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取数据...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setCancelable(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());


                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    try {
                                                                        SFTPUtils.download(Cons.FILES_ROOT_PATH_IN_SERVER, servername1, path);
                                                                        Thread.sleep(1000);
                                                                    } catch (SftpException | FileNotFoundException | InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }


                                                                    Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            if (dialogFragment != null) {
                                                                                dialogFragment.dismiss();
                                                                                dialogFragment = null;
                                                                            }

                                                                            ToastUtils.showToast(ProblemDetail.this, "文件被保存在" + path + "目录下");

                                                                        }
                                                                    });
                                                                }
                                                            }).start();


                                                        }
                                                    })
                                                    .setNegative("取消", null)
                                                    .show(ProblemDetail.this.getSupportFragmentManager());
                                        }


                                    } else {
                                        ToastUtils.showToast(ProblemDetail.this, "您的存储空间已经不足，请先清理磁盘垃圾");
                                    }


                                }
                            });
                        }
                    }
                }


                num_of_comment.setText(ques_detail.getInteger("comment_num") + "条回复");

                pic_urls = ques_detail.getJSONArray("reply");
                if (pic_urls.isEmpty()) {
                    if ("".equals(ques_detail.getString("adopted_reply"))) {
                        problemDetailWithNoDataQuickAdapter = new ProblemDetailWithNoDataQuickAdapter();
                        problemDetailWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                            add(new ProblemDigest());
                        }});
                        recyclerView.setAdapter(problemDetailWithNoDataQuickAdapter);
                        is_adopted = false;
                        srefreshLayout.setEnableRefresh(true);
                        srefreshLayout.setEnableLoadMore(false);
                    } else {
                        final String adopted_reply = ques_detail.getString("adopted_reply");
                        mAdapter = new ProblemDetail.IndexQuickAdapter(qid);
                        mAdapter.replaceData(new ArrayList<JSONObject>() {{
                            add(JSONObject.parseObject(adopted_reply));
                        }});
                        recyclerView.setAdapter(mAdapter);
                        is_adopted = true;
                        srefreshLayout.setEnableLoadMore(false);
                        srefreshLayout.setEnableRefresh(true);
                    }

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    List<JSONObject> reply_list = new ArrayList<>();

                    if (!"".equals(ques_detail.getString("adopted_reply"))) {
                        final String adopted_reply = ques_detail.getString("adopted_reply");
                        reply_list.add(JSONObject.parseObject(adopted_reply));
                        is_adopted = true;
                    } else {
                        is_adopted = false;
                    }


                    for (int i = 0; i < pic_urls.size(); i++) {
                        reply_list.add(pic_urls.getJSONObject(i));
                    }
                    mAdapter = new ProblemDetail.IndexQuickAdapter(qid);
                    mAdapter.replaceData(reply_list);
                    recyclerView.setAdapter(mAdapter);
                    srefreshLayout.setEnableRefresh(true);
                    srefreshLayout.setEnableLoadMore(true);
                }
                promenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        pop.show();

                    }
                });

            } else if ("nmy_ques".equals(come_intent)) {

                come_coll = intent.getIntExtra("come_coll", 0);
                final int belt_uid = intent.getIntExtra("belt_uid", -1);
                pop.getMenuInflater().inflate(R.menu.pro_menu, pop.getMenu());
                MenuItem att = pop.getMenu().findItem(R.id.follow);
                int groupid = att.getGroupId();
                int is_coll = intent.getIntExtra("is_coll", -1);
                int is_att = intent.getIntExtra("is_att", -1);
                pop.getMenu().removeItem(R.id.collect);
                pop.getMenu().removeItem(R.id.follow);
//                ToastUtils.showToast(ProblemDetail.this, is_coll + "----" + is_att);
                if (is_coll == -1) {
                    ToastUtils.showToast(ProblemDetail.this, "参数错误");
                } else if (is_coll == 0) {
                    pop.getMenu().add(groupid, R.id.collect, 1, "收藏该问题");

                } else {
                    pop.getMenu().add(groupid, R.id.collect, 1, "取消收藏该问题");

                }

                if (is_att == -1) {
                    ToastUtils.showToast(ProblemDetail.this, "参数错误");
                } else if (is_att == 0) {
                    pop.getMenu().add(groupid, R.id.follow, 2, "关注此人");

                } else {
//                    ToastUtils.showToast(ProblemDetail.this, "dfdsfdsfds");
                    pop.getMenu().add(groupid, R.id.follow, 2, "取消关注此人");

                }

//                pop.getMenu().add(groupid,R.id.collect,1,"取消收藏该问题");
//                pop.getMenu().add(groupid,R.id.follow,2,"取消关注此人");


                pop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem item) {
                        pop.dismiss();
                        switch (item.getItemId()) {

                            case R.id.addresp:
                                Intent i = new Intent(ProblemDetail.this, AddReply.class);
                                i.putExtra("qid", intent.getIntExtra("qqq_id", -1));
                                startActivity(i);
                                break;

                            case R.id.collect:
                                String coll_t = item.getTitle().toString();
                                if ("收藏该问题".equals(coll_t)) {

                                    dialogFragment = new CircleDialog.Builder()
                                            .setProgressText("正在发送请求...")
                                            .setCancelable(false)
                                            .setCanceledOnTouchOutside(false)
                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                            .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());


                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpURLConnection urlConnection = null;
                                            try {
                                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.collectQuestion));
                                                OutputStream out = urlConnection.getOutputStream();
                                                JSONObject j_ = new JSONObject();
                                                j_.put("id", KlbertjCache.uid);
                                                j_.put("qid", qid);
                                                out.write(EncryptionUtils.decryptByByte(j_.toJSONString().getBytes("UTF-8")));
                                                out.close();
                                                int responseCode = urlConnection.getResponseCode();
                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                    Thread.sleep(1000);
                                                    Objects.requireNonNull(Objects.requireNonNull(ProblemDetail.this)).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (dialogFragment != null) {
                                                                dialogFragment.dismiss();
                                                                dialogFragment = null;
                                                            }

                                                            if ("f".equals(result)) {
                                                                ToastUtils.showToast(ProblemDetail.this, "token过期");
                                                            } else if ("bp".equals(result)) {
                                                                ToastUtils.showToast(ProblemDetail.this, "请求参数错误");
                                                            } else {
                                                                item.setTitle("取消收藏该问题");
                                                                ToastUtils.showToast(ProblemDetail.this, "操作成功");
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
                                    dialogFragment = new CircleDialog.Builder()
                                            .setProgressText("正在发送请求...")
                                            .setCancelable(false)
                                            .setCanceledOnTouchOutside(false)
                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                            .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HttpURLConnection urlConnection = null;
                                            try {
                                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.cancelCollectQuestion));
                                                OutputStream out = urlConnection.getOutputStream();
                                                JSONObject j_ = new JSONObject();
                                                j_.put("id", KlbertjCache.uid);
                                                j_.put("qid", qid);
                                                out.write(EncryptionUtils.decryptByByte(j_.toJSONString().getBytes("UTF-8")));
                                                out.close();
                                                int responseCode = urlConnection.getResponseCode();
                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                    Thread.sleep(1000);
                                                    Objects.requireNonNull(Objects.requireNonNull(ProblemDetail.this)).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (dialogFragment != null) {
                                                                dialogFragment.dismiss();
                                                                dialogFragment = null;
                                                            }

                                                            if ("f".equals(result)) {
                                                                ToastUtils.showToast(ProblemDetail.this, "token过期");
                                                            } else if ("bp".equals(result)) {
                                                                ToastUtils.showToast(ProblemDetail.this, "请求参数错误");
                                                            } else {
                                                                item.setTitle("收藏该问题");
                                                                ToastUtils.showToast(ProblemDetail.this, "操作成功");
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


                                break;
                            case R.id.follow:
                                String att_t = item.getTitle().toString();
                                if (belt_uid != -1) {
                                    dialogFragment = new CircleDialog.Builder()
                                            .setProgressText("正在发送请求...")
                                            .setCancelable(false)
                                            .setCanceledOnTouchOutside(false)
                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                            .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());

                                    if ("关注此人".equals(att_t)) {


                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                HttpURLConnection urlConnection = null;
                                                try {
                                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.attentionUser));
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
                                                        Objects.requireNonNull(Objects.requireNonNull(ProblemDetail.this)).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (dialogFragment != null) {
                                                                    dialogFragment.dismiss();
                                                                    dialogFragment = null;
                                                                }

                                                                if ("f".equals(result)) {
                                                                    ToastUtils.showToast(ProblemDetail.this, "token过期");
                                                                } else if ("server error".equals(result)) {
                                                                    ToastUtils.showToast(ProblemDetail.this, "服务器错误");

                                                                } else if ("bp".equals(result)) {
                                                                    ToastUtils.showToast(ProblemDetail.this, "参数错误");
                                                                } else {
                                                                    item.setTitle("取消关注此人");
                                                                    ToastUtils.showToast(ProblemDetail.this, "操作成功");
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
                                                        Objects.requireNonNull(Objects.requireNonNull(ProblemDetail.this)).runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (dialogFragment != null) {
                                                                    dialogFragment.dismiss();
                                                                    dialogFragment = null;
                                                                }


                                                                if ("f".equals(result)) {
                                                                    ToastUtils.showToast(ProblemDetail.this, "token过期");
                                                                } else if ("server error".equals(result)) {
                                                                    ToastUtils.showToast(ProblemDetail.this, "服务器错误");

                                                                } else if ("bp".equals(result)) {
                                                                    ToastUtils.showToast(ProblemDetail.this, "参数错误");
                                                                } else {
                                                                    item.setTitle("关注此人");
                                                                    ToastUtils.showToast(ProblemDetail.this, "操作成功");
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
                                    ToastUtils.showToast(ProblemDetail.this, "参数错误");
                                }


                                break;


                        }

                        return false;
                    }
                });


                String contentStr = intent.getStringExtra("contentStr");
                JSONObject ques_detail = JSONObject.parseObject(contentStr);
                String avator_path = intent.getStringExtra("belongToUserHead_url");
                //  ToastUtils.showToast(ProblemDetail.this,avator_path);
                Glide.with(ProblemDetail.this).asBitmap().apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).load(avator_path).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        avator.setBitmap(bitmap);
                    }
                });

                uname.setText(intent.getStringExtra("belong_to_user_uname"));
                title.setText(intent.getStringExtra("title"));
                score.setText(ques_detail.getInteger("score") + "积分");
                creat_time.setText(intent.getStringExtra("create_time"));
                content.setText(ques_detail.getString("content"));
                JSONArray pic_urls = ques_detail.getJSONArray("pic_urls");
                if (pic_urls.isEmpty()) {
                    layout_nine_grid.setVisibility(View.GONE);
                } else {
                    layout_nine_grid.setVisibility(View.VISIBLE);
                    List<String> ques_pics = new ArrayList<>();
                    for (int i = 0; i < pic_urls.size(); i++) {
                        ques_pics.add(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + pic_urls.getString(i));
                    }
                    layout_nine_grid.setUrlList(ques_pics);
                }


                JSONObject file_url = ques_detail.getJSONObject("file_urls");

                if (file_url.isEmpty()) {
                    file_ll.setVisibility(View.GONE);
                } else {
                    JSONArray servername = file_url.getJSONArray("server_name");
                    JSONArray s_r = file_url.getJSONArray("server_real_name");
                    JSONObject jj = null;
                    TextView v = null;
                    for (int i_i = 0; i_i < servername.size(); i_i++) {
                        v = (TextView) file_ll.getChildAt(i_i);
                        String sname = servername.getString(i_i);
                        Log.i("sname", sname);
                        for (int j_j = 0; j_j < s_r.size(); j_j++) {
                            for (int k_k = 0; k_k < s_r.size(); k_k++) {
                                jj = s_r.getJSONObject(k_k);
                                if (jj.containsKey(sname)) {
                                    break;
                                }
                            }

                            v.setVisibility(View.VISIBLE);
                            v.setText(jj.getString(sname));
                            Log.i("s_r.getJSONObject(j_j)", jj.getString(sname));
                            final String realname = jj.getString(sname);
                            final String servername1 = sname;
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (StorageUtils.is_disk_enough()) {
                                        final String path = Cons.ROOTDir + Cons.ATTACHMENTS + realname;
                                        if (new File(path).exists()) {
                                            ToastUtils.showToast(ProblemDetail.this, "该文件已经存在");
                                        } else {
                                            new CircleDialog.Builder()
                                                    .setTitle("提醒")
                                                    .setText("您确定要下载此附件吗？")
                                                    .setCanceledOnTouchOutside(false)
                                                    .setPositive("确定", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(final View v) {

                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取数据...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setCancelable(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());


                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    try {
                                                                        SFTPUtils.download(Cons.FILES_ROOT_PATH_IN_SERVER, servername1, path);
                                                                        Thread.sleep(1000);
                                                                    } catch (SftpException | FileNotFoundException | InterruptedException e) {
                                                                        e.printStackTrace();
                                                                    }


                                                                    Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            if (dialogFragment != null) {
                                                                                dialogFragment.dismiss();
                                                                                dialogFragment = null;
                                                                            }

                                                                            ToastUtils.showToast(ProblemDetail.this, "文件被保存在" + path + "目录下");

                                                                        }
                                                                    });
                                                                }
                                                            }).start();


                                                        }
                                                    })
                                                    .setNegative("取消", null)
                                                    .show(ProblemDetail.this.getSupportFragmentManager());
                                        }


                                    } else {
                                        ToastUtils.showToast(ProblemDetail.this, "您的存储空间已经不足，请先清理磁盘垃圾");
                                    }

                                }
                            });
                        }
                    }
                }


                num_of_comment.setText(ques_detail.getInteger("comment_num") + "条回复");

                pic_urls = ques_detail.getJSONArray("reply");
                if (pic_urls.isEmpty()) {
                    if ("".equals(ques_detail.getString("adopted_reply"))) {
                        problemDetailWithNoDataQuickAdapter = new ProblemDetailWithNoDataQuickAdapter();
                        problemDetailWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                            add(new ProblemDigest());
                        }});
                        recyclerView.setAdapter(problemDetailWithNoDataQuickAdapter);
                        is_adopted = false;
                        srefreshLayout.setEnableRefresh(true);
                        srefreshLayout.setEnableLoadMore(false);
                    } else {
                        final String adopted_reply = ques_detail.getString("adopted_reply");
                        mAdapter = new ProblemDetail.IndexQuickAdapter(qid);
                        mAdapter.replaceData(new ArrayList<JSONObject>() {{
                            add(JSONObject.parseObject(adopted_reply));
                        }});
                        recyclerView.setAdapter(mAdapter);
                        is_adopted = true;
                        srefreshLayout.setEnableLoadMore(false);
                        srefreshLayout.setEnableRefresh(true);
                    }

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    List<JSONObject> reply_list = new ArrayList<>();

                    if (!"".equals(ques_detail.getString("adopted_reply"))) {
                        final String adopted_reply = ques_detail.getString("adopted_reply");
                        reply_list.add(JSONObject.parseObject(adopted_reply));
                        is_adopted = true;
                    } else {
                        is_adopted = false;
                    }


                    for (int i = 0; i < pic_urls.size(); i++) {
                        reply_list.add(pic_urls.getJSONObject(i));
                    }
                    mAdapter = new ProblemDetail.IndexQuickAdapter(qid);
                    mAdapter.replaceData(reply_list);
                    recyclerView.setAdapter(mAdapter);
                    srefreshLayout.setEnableRefresh(true);
                    srefreshLayout.setEnableLoadMore(true);
                }
                promenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        pop.show();

                    }
                });
            } else {


                String contentStr = intent.getStringExtra("contentStr");
                JSONObject ques_detail = JSONObject.parseObject(contentStr);
                String avator_path = intent.getStringExtra("belongToUserHead_url");
                Glide.with(ProblemDetail.this).asBitmap().apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).load(avator_path).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        avator.setBitmap(bitmap);
                    }
                });

                uname.setText(intent.getStringExtra("belong_to_user_uname"));
                title.setText(intent.getStringExtra("title"));
                score.setText(ques_detail.getInteger("score") + "积分");
                creat_time.setText(intent.getStringExtra("create_time"));
                content.setText(ques_detail.getString("content"));
                JSONArray pic_urls = ques_detail.getJSONArray("pic_urls");
                if (pic_urls.isEmpty()) {
                    layout_nine_grid.setVisibility(View.GONE);
                } else {
                    layout_nine_grid.setVisibility(View.VISIBLE);
                    List<String> ques_pics = new ArrayList<>();
                    for (int i = 0; i < pic_urls.size(); i++) {
                        ques_pics.add(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + pic_urls.getString(i));
                    }
                    layout_nine_grid.setUrlList(ques_pics);
                }


                JSONObject file_url = ques_detail.getJSONObject("file_urls");

                if (file_url.isEmpty()) {
                    file_ll.setVisibility(View.GONE);
                } else {
                    JSONArray servername = file_url.getJSONArray("server_name");
                    JSONArray s_r = file_url.getJSONArray("server_real_name");
                    JSONObject jj = null;
                    TextView v = null;
                    for (int i_i = 0; i_i < servername.size(); i_i++) {
                        v = (TextView) file_ll.getChildAt(i_i);
                        String sname = servername.getString(i_i);
                        Log.i("sname", sname);
                        for (int j_j = 0; j_j < s_r.size(); j_j++) {
                            for (int k_k = 0; k_k < s_r.size(); k_k++) {
                                jj = s_r.getJSONObject(k_k);
                                if (jj.containsKey(sname)) {
                                    break;
                                }
                            }

                            v.setVisibility(View.VISIBLE);
                            v.setText(jj.getString(sname));
                            Log.i("s_r.getJSONObject(j_j)", jj.getString(sname));
                            v.setTag(sname);
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtils.showToast(ProblemDetail.this, "请先登录");
                                }
                            });
                        }
                    }
                }


                num_of_comment.setText(ques_detail.getInteger("comment_num") + "条回复");

                pic_urls = ques_detail.getJSONArray("reply");
                if (pic_urls.isEmpty()) {
                    if ("".equals(ques_detail.getString("adopted_reply"))) {
                        recyclerView.setVisibility(View.GONE);
                        is_adopted = false;
                        srefreshLayout.setEnableLoadMore(false);
                    } else {
                        final String adopted_reply = ques_detail.getString("adopted_reply");
                        mAdapter = new ProblemDetail.IndexQuickAdapter(qid);
                        mAdapter.replaceData(new ArrayList<JSONObject>() {{
                            add(JSONObject.parseObject(adopted_reply));
                        }});
                        recyclerView.setAdapter(mAdapter);
                        is_adopted = true;
                        srefreshLayout.setEnableLoadMore(false);
                    }

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    List<JSONObject> reply_list = new ArrayList<>();

                    if (!"".equals(ques_detail.getString("adopted_reply"))) {
                        final String adopted_reply = ques_detail.getString("adopted_reply");
                        reply_list.add(JSONObject.parseObject(adopted_reply));
                        is_adopted = true;
                    } else {
                        is_adopted = false;
                    }


                    for (int i = 0; i < pic_urls.size(); i++) {
                        reply_list.add(pic_urls.getJSONObject(i));
                    }
                    mAdapter = new ProblemDetail.IndexQuickAdapter(qid);
                    mAdapter.replaceData(reply_list);
                    recyclerView.setAdapter(mAdapter);
                    srefreshLayout.setEnableLoadMore(true);
                }


                promenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new CircleDialog.Builder()
                                .setTitle("提醒")
                                .setText("您还没有登录，现在去登录吗？")
                                .setPositive("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ProblemDetail.this, Login.class));
                                        finish();
                                    }
                                })
                                .setNegative("取消", null)
                                .show(ProblemDetail.this.getSupportFragmentManager());
                    }
                });
            }
        }


        srefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                if (qqq_id == -1) {
                    ToastUtils.showToast(ProblemDetail.this, "参数错误");
                } else {
                    reply_page = 1;

                    if (come_coll == 1) {


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getcollQuestionDetail));
                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject j = new JSONObject();
                                    j.put("id", KlbertjCache.uid);
                                    j.put("q_id", qqq_id);
                                    out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                        Thread.sleep(1000);
                                        Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshLayout.finishRefresh(2000);
                                                if ("bp".equals(result)) {
                                                    ToastUtils.showToast(ProblemDetail.this, "权限过期");
                                                } else {
                                                    KlbertjCache.prodetail.put(qqq_id, result);

                                                    JSONObject ques_detail = JSONObject.parseObject(result);
                                                    score.setText(ques_detail.getInteger("score") + "积分");
                                                    content.setText(ques_detail.getString("content"));
                                                    JSONArray pic_urls = ques_detail.getJSONArray("pic_urls");
                                                    if (pic_urls.isEmpty()) {
                                                        layout_nine_grid.setVisibility(View.GONE);
                                                    } else {
                                                        layout_nine_grid.setVisibility(View.VISIBLE);
                                                        List<String> ques_pics = new ArrayList<>();
                                                        for (int i = 0; i < pic_urls.size(); i++) {
                                                            ques_pics.add(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + pic_urls.getString(i));
                                                        }
                                                        layout_nine_grid.setUrlList(ques_pics);
                                                    }


                                                    JSONObject file_url = ques_detail.getJSONObject("file_urls");

                                                    if (file_url.isEmpty()) {
                                                        file_ll.setVisibility(View.GONE);
                                                    } else {
                                                        JSONArray servername = file_url.getJSONArray("server_name");
                                                        JSONArray s_r = file_url.getJSONArray("server_real_name");
                                                        JSONObject jj = null;
                                                        TextView v = null;
                                                        for (int i_i = 0; i_i < servername.size(); i_i++) {
                                                            v = (TextView) file_ll.getChildAt(i_i);
                                                            String sname = servername.getString(i_i);
                                                            Log.i("sname", sname);
                                                            for (int j_j = 0; j_j < s_r.size(); j_j++) {
                                                                for (int k_k = 0; k_k < s_r.size(); k_k++) {
                                                                    jj = s_r.getJSONObject(k_k);
                                                                    if (jj.containsKey(sname)) {
                                                                        break;
                                                                    }
                                                                }

                                                                v.setVisibility(View.VISIBLE);
                                                                v.setText(jj.getString(sname));
                                                                Log.i("s_r.getJSONObject(j_j)", jj.getString(sname));
                                                                v.setTag(sname);
                                                                v.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }


                                                    num_of_comment.setText(ques_detail.getInteger("comment_num") + "条回复");

                                                    pic_urls = ques_detail.getJSONArray("reply");
                                                    if (pic_urls.isEmpty()) {
                                                        if ("".equals(ques_detail.getString("adopted_reply"))) {
                                                            problemDetailWithNoDataQuickAdapter = new ProblemDetailWithNoDataQuickAdapter();
                                                            problemDetailWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(problemDetailWithNoDataQuickAdapter);
                                                            is_adopted = false;
                                                            srefreshLayout.setEnableRefresh(true);
                                                            srefreshLayout.setEnableLoadMore(false);
                                                        } else {
                                                            final String adopted_reply = ques_detail.getString("adopted_reply");
                                                            mAdapter = new ProblemDetail.IndexQuickAdapter(qqq_id);
                                                            mAdapter.replaceData(new ArrayList<JSONObject>() {{
                                                                add(JSONObject.parseObject(adopted_reply));
                                                            }});
                                                            recyclerView.setAdapter(mAdapter);
                                                            is_adopted = true;
                                                            srefreshLayout.setEnableLoadMore(false);
                                                            srefreshLayout.setEnableRefresh(true);
                                                        }

                                                    } else {
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        List<JSONObject> reply_list = new ArrayList<>();

                                                        if (!"".equals(ques_detail.getString("adopted_reply"))) {
                                                            final String adopted_reply = ques_detail.getString("adopted_reply");
                                                            reply_list.add(JSONObject.parseObject(adopted_reply));
                                                            is_adopted = true;
                                                        } else {
                                                            is_adopted = false;
                                                        }


                                                        for (int i = 0; i < pic_urls.size(); i++) {
                                                            reply_list.add(pic_urls.getJSONObject(i));
                                                        }
                                                        mAdapter = new ProblemDetail.IndexQuickAdapter(qqq_id);
                                                        mAdapter.replaceData(reply_list);
                                                        // mAdapter.notifyDataSetChanged();
                                                        recyclerView.setAdapter(mAdapter);
                                                        srefreshLayout.setEnableRefresh(true);
                                                        srefreshLayout.setEnableLoadMore(true);
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
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionDetail));
                                    OutputStream out = urlConnection.getOutputStream();
                                    out.write(EncryptionUtils.decryptByByte((qqq_id + "").getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。

                                        KlbertjCache.prodetail.put(qqq_id, result);

                                        Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshLayout.finishRefresh(2000);
                                                JSONObject ques_detail = JSONObject.parseObject(result);
                                                score.setText(ques_detail.getInteger("score") + "积分");
                                                content.setText(ques_detail.getString("content"));
                                                JSONArray pic_urls = ques_detail.getJSONArray("pic_urls");
                                                if (pic_urls.isEmpty()) {
                                                    layout_nine_grid.setVisibility(View.GONE);
                                                } else {
                                                    layout_nine_grid.setVisibility(View.VISIBLE);
                                                    List<String> ques_pics = new ArrayList<>();
                                                    for (int i = 0; i < pic_urls.size(); i++) {
                                                        ques_pics.add(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + pic_urls.getString(i));
                                                    }
                                                    layout_nine_grid.setUrlList(ques_pics);
                                                }


                                                JSONObject file_url = ques_detail.getJSONObject("file_urls");

                                                if (file_url.isEmpty()) {
                                                    file_ll.setVisibility(View.GONE);
                                                } else {
                                                    JSONArray servername = file_url.getJSONArray("server_name");
                                                    JSONArray s_r = file_url.getJSONArray("server_real_name");
                                                    JSONObject jj = null;
                                                    TextView v = null;
                                                    for (int i_i = 0; i_i < servername.size(); i_i++) {
                                                        v = (TextView) file_ll.getChildAt(i_i);
                                                        String sname = servername.getString(i_i);
                                                        Log.i("sname", sname);
                                                        for (int j_j = 0; j_j < s_r.size(); j_j++) {
                                                            for (int k_k = 0; k_k < s_r.size(); k_k++) {
                                                                jj = s_r.getJSONObject(k_k);
                                                                if (jj.containsKey(sname)) {
                                                                    break;
                                                                }
                                                            }

                                                            v.setVisibility(View.VISIBLE);
                                                            v.setText(jj.getString(sname));
                                                            Log.i("s_r.getJSONObject(j_j)", jj.getString(sname));
                                                            v.setTag(sname);
                                                            v.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                }


                                                num_of_comment.setText(ques_detail.getInteger("comment_num") + "条回复");

                                                pic_urls = ques_detail.getJSONArray("reply");
                                                if (pic_urls.isEmpty()) {
                                                    if ("".equals(ques_detail.getString("adopted_reply"))) {
                                                        problemDetailWithNoDataQuickAdapter = new ProblemDetailWithNoDataQuickAdapter();
                                                        problemDetailWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(problemDetailWithNoDataQuickAdapter);
                                                        is_adopted = false;
                                                        srefreshLayout.setEnableRefresh(true);
                                                        srefreshLayout.setEnableLoadMore(false);
                                                    } else {
                                                        final String adopted_reply = ques_detail.getString("adopted_reply");
                                                        mAdapter = new ProblemDetail.IndexQuickAdapter(qqq_id);
                                                        mAdapter.replaceData(new ArrayList<JSONObject>() {{
                                                            add(JSONObject.parseObject(adopted_reply));
                                                        }});
                                                        recyclerView.setAdapter(mAdapter);
                                                        is_adopted = true;
                                                        srefreshLayout.setEnableLoadMore(false);
                                                        srefreshLayout.setEnableRefresh(true);
                                                    }

                                                } else {
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    List<JSONObject> reply_list = new ArrayList<>();

                                                    if (!"".equals(ques_detail.getString("adopted_reply"))) {
                                                        final String adopted_reply = ques_detail.getString("adopted_reply");
                                                        reply_list.add(JSONObject.parseObject(adopted_reply));
                                                        is_adopted = true;
                                                    } else {
                                                        is_adopted = false;
                                                    }


                                                    for (int i = 0; i < pic_urls.size(); i++) {
                                                        reply_list.add(pic_urls.getJSONObject(i));
                                                    }
                                                    mAdapter = new ProblemDetail.IndexQuickAdapter(qqq_id);
                                                    mAdapter.replaceData(reply_list);
                                                    // mAdapter.notifyDataSetChanged();
                                                    recyclerView.setAdapter(mAdapter);
                                                    srefreshLayout.setEnableRefresh(true);
                                                    srefreshLayout.setEnableLoadMore(true);
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


            }
        });


        srefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        HttpURLConnection urlConnection = null;
                        try {
                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getQuestionReplyByPage));
                            OutputStream out = urlConnection.getOutputStream();
                            JSONObject send = new JSONObject();
                            send.put("qid", qqq_id);
                            send.put("page", ++reply_page);
                            out.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                            out.close();
                            int responseCode = urlConnection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                Thread.sleep(1000);

                                Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if ("nm".equals(result)) {
                                            refreshLayout.finishLoadMoreWithNoMoreData();
                                            ToastUtils.showToast(ProblemDetail.this, "没有更多数据");
                                        } else {
                                            JSONObject ques_detail = JSONObject.parseObject(result);
                                            JSONArray add_reply = ques_detail.getJSONArray("reply");
                                            if (add_reply.isEmpty()) {
                                                refreshLayout.finishLoadMoreWithNoMoreData();
                                                ToastUtils.showToast(ProblemDetail.this, "没有数据");
                                            } else {
                                                refreshLayout.finishLoadMore(500);
                                                List<JSONObject> adds = new ArrayList<>();
                                                for (int i = 0; i < add_reply.size(); i++) {
                                                    adds.add(add_reply.getJSONObject(i));
                                                }
                                                mAdapter.addData(adds);

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

        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        StatusBarUtil.setPaddingSmart(this, appBarLayout);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurView));

    }

    @Override
    public void onBackPressed() {
        if (mParent.getVisibility() == View.VISIBLE) {
            mBg.startAnimation(out);
            mPhotoView.animaTo(mInfo, new Runnable() {
                @Override
                public void run() {
                    mParent.setVisibility(View.GONE);
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class IndexQuickAdapter extends BaseQuickAdapter<JSONObject, BaseViewHolder> {
        int aqid;
        MyCircleView reply_avator;
        TextView adopt, delete_reply;
        View delete_reply_indecator;

        IndexQuickAdapter(int qid) {
            super(R.layout.reply_item);
            this.aqid = qid;
        }

        @Override
        protected void convert(final BaseViewHolder helper, final JSONObject item) {
            if (item != null) {
                delete_reply = helper.getView(R.id.delete_reply);
                delete_reply_indecator = helper.getView(R.id.delete_reply_indecator);
                if (KlbertjCache.uid.equals(item.getInteger("belt_user_id")) && item.getInteger("is_adopted") == 0) {
                    delete_reply.setVisibility(View.VISIBLE);
                    delete_reply_indecator.setVisibility(View.VISIBLE);
                    delete_reply.setClickable(true);
                    delete_reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final int rrid = item.getInteger("id");
                            final int possition = helper.getAdapterPosition();
                            Log.i("helper.getAdapte", helper.getAdapterPosition() + "===============================");
                            new CircleDialog.Builder()
                                    .setTitle("提示")
                                    .setText("确定要删除吗")
                                    .setPositive("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogFragment = new CircleDialog.Builder()
                                                    .setProgressText("正在发送请求...")
                                                    .setCanceledOnTouchOutside(false)
                                                    .setCancelable(false)
                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                    .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    HttpURLConnection urlConnection = null;
                                                    try {
                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.deleteReply));
                                                        OutputStream out = urlConnection.getOutputStream();
                                                        JSONObject j = new JSONObject();
                                                        j.put("id", KlbertjCache.uid);
                                                        j.put("qid", aqid);
                                                        j.put("rid", rrid);
                                                        out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                                        out.close();
                                                        int responseCode = urlConnection.getResponseCode();
                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                            Thread.sleep(1000);
                                                            Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (dialogFragment != null) {
                                                                        dialogFragment.dismiss();
                                                                        dialogFragment = null;
                                                                    }
                                                                    ToastUtils.showloading("正在获取你的点击", "正在检查权限", new ToastUtils.OnLoadingCancelListener() {
                                                                        @Override
                                                                        public void OnLoadingCancel() {
                                                                            if ("f".equals(result)) {
                                                                                ToastUtils.showToast(Objects.requireNonNull(ProblemDetail.this), "登录已经过期");
                                                                            } else if ("bp".equals(result)) {
                                                                                ToastUtils.showToast(Objects.requireNonNull(ProblemDetail.this), "参数错误");

                                                                            } else {
                                                                                final JSONArray pics = item.getJSONArray("pic_urls");
                                                                                if (pics.isEmpty()) {
                                                                                    IndexQuickAdapter.this.getData().remove(possition);
                                                                                    IndexQuickAdapter.this.notifyItemRemoved(possition);

                                                                                    ToastUtils.showToast(ProblemDetail.this, "操作成功");
                                                                                } else {
                                                                                    dialogFragment = new CircleDialog.Builder()
                                                                                            .setProgressText("正在删除相关数据...")
                                                                                            .setCancelable(false)
                                                                                            .setCanceledOnTouchOutside(false)
                                                                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                            .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());

                                                                                    new Thread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {

                                                                                            List<String> pic_n = new ArrayList<>();
                                                                                            for (int i = 0; i < pics.size(); i++) {
                                                                                                pic_n.add(pics.getString(i));
                                                                                            }
                                                                                            SFTPUtils.deletePics(ProblemDetail.this, pic_n, Cons.REPLY_PICS_PATH_IN_SERVER);
                                                                                            try {
                                                                                                Thread.sleep(500);
                                                                                            } catch (InterruptedException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                            Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    if (dialogFragment != null) {
                                                                                                        dialogFragment.dismiss();
                                                                                                        dialogFragment = null;
                                                                                                    }
                                                                                                    IndexQuickAdapter.this.getData().remove(possition);
                                                                                                    IndexQuickAdapter.this.notifyItemRemoved(possition);
                                                                                                    ToastUtils.showToast(ProblemDetail.this, "操作成功");
                                                                                                }
                                                                                            });


                                                                                        }
                                                                                    }).start();
                                                                                }


                                                                            }
                                                                        }
                                                                    }, ProblemDetail.this);
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
                                    .show(ProblemDetail.this.getSupportFragmentManager());


                        }
                    });

                } else {
                    delete_reply.setVisibility(View.GONE);
                    delete_reply.setClickable(false);
                    delete_reply_indecator.setVisibility(View.GONE);
                }
                reply_avator = helper.getView(R.id.reply_avator);
                Log.i("dsasada", Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + item.getString("belt_user_head_url"));
                Glide.with(ProblemDetail.this).asBitmap().apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565).override(100, 100).priority(Priority.LOW))
                        .load(Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + item.getString("belt_user_head_url")).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        reply_avator.setBitmap(bitmap);
                    }
                });
                helper.setText(R.id.time, item.getString("create_time"))
                        .setText(R.id.uname, item.getString("belt_user_uname"))
                        .setText(R.id.reply_con, item.getString("content"));

                adopt = helper.getView(R.id.adopt);
                if ("my_ques".equals(come_intent)) {
                    if (is_adopted) {
                        adopt.setClickable(false);
                        adopt.setText(item.getInteger("is_adopted") == 0 ? "未被采纳" : "已被采纳");
                        adopt.setTextColor(item.getInteger("is_adopted") == 0 ? getResources().getColor(R.color.colorAccent) : getResources().getColor(R.color.forthColor));
                    } else {
                        adopt.setClickable(true);
                        adopt.setText("采纳他");
                        adopt.setTextColor(getResources().getColor(R.color.colorAccent));
                        adopt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final int position = helper.getAdapterPosition();
                                dialogFragment = new CircleDialog.Builder()
                                        .setProgressText("正在发送请求...")
                                        .setCanceledOnTouchOutside(false)
                                        .setCancelable(false)
                                        .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                        .show(Objects.requireNonNull(ProblemDetail.this).getSupportFragmentManager());
                                final int rid = getData().get(position).getInteger("id");
                                final List<JSONObject> lll = getData();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HttpURLConnection urlConnection = null;
                                        try {
                                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.adopteReply));
                                            OutputStream out = urlConnection.getOutputStream();
                                            JSONObject j = new JSONObject();
                                            j.put("id", SharePreferenceUtils.getInteger(Objects.requireNonNull(ProblemDetail.this), Cons.IS_LOGIN));
                                            j.put("qid", aqid);
                                            j.put("rid", rid);
                                            out.write(EncryptionUtils.decryptByByte(j.toJSONString().getBytes("UTF-8")));
                                            out.close();
                                            int responseCode = urlConnection.getResponseCode();
                                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                Thread.sleep(1000);
                                                Objects.requireNonNull(ProblemDetail.this).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dialogFragment != null) {
                                                            dialogFragment.dismiss();
                                                            dialogFragment = null;
                                                        }
                                                        ToastUtils.showloading("正在获取你的点击", "正在检查权限", new ToastUtils.OnLoadingCancelListener() {
                                                            @Override
                                                            public void OnLoadingCancel() {
                                                                if ("f".equals(result)) {
                                                                    ToastUtils.showToast(Objects.requireNonNull(ProblemDetail.this), "登录已经过期");
                                                                } else if ("bp".equals(result)) {
                                                                    ToastUtils.showToast(Objects.requireNonNull(ProblemDetail.this), "参数错误");
                                                                } else if ("server error".equals(result)) {
                                                                    ToastUtils.showToast(Objects.requireNonNull(ProblemDetail.this), "服务器错误");
                                                                } else {
                                                                    lll.get(position).put("is_adopted", 1);
                                                                    is_adopted = true;
                                                                    List<JSONObject> jso = new ArrayList<>();
                                                                    jso.add(lll.get(position));
                                                                    lll.remove(position);
                                                                    jso.addAll(lll);
                                                                    IndexQuickAdapter.this.replaceData(jso);
                                                                    IndexQuickAdapter.this.notifyDataSetChanged();
                                                                    ToastUtils.showToast(Objects.requireNonNull(ProblemDetail.this), "操作成功");
                                                                }
                                                            }
                                                        }, ProblemDetail.this);


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
                } else {
                    adopt.setClickable(false);
                    adopt.setText(item.getInteger("is_adopted") == 0 ? "未被采纳" : "已被采纳");
                    adopt.setTextColor(item.getInteger("is_adopted") == 0 ? getResources().getColor(R.color.colorAccent) : getResources().getColor(R.color.forthColor));
                }

                NineGridLayout nineGridLayout = helper.getView(R.id.layout_nine_grid);
                JSONArray reply_pics = item.getJSONArray("pic_urls");
                if (reply_pics.isEmpty()) {
                    nineGridLayout.setVisibility(View.GONE);
                } else {
                    nineGridLayout.setVisibility(View.VISIBLE);
                    List<String> pics = new ArrayList<>();
                    for (int i = 0; i < reply_pics.size(); i++) {
                        pics.add(Cons.PICS_BASE_URL + Cons.REPLY_PICS_PATH_IN_SERVER + "/" + reply_pics.getString(i));
                    }
                    nineGridLayout.setIsShowAll(false); //当传入的图片数超过9张时，是否全部显示
                    nineGridLayout.setSpacing(5); //动态设置图片之间的间隔
                    nineGridLayout.setUrlList(pics);
                    nineGridLayout.setonClickImageListener(new NineGridLayout.OnClickImageListener() {
                        @Override
                        public void onClickImage(View v, int position, String url, ArrayList<String> urlList) {
                            PhotoView p = (PhotoView) v;
                            mInfo = p.getInfo();

                            Glide.with(ProblemDetail.this).load(url).apply(MyGlide.getRequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)).into(mPhotoView);
//                mPhotoView.setImageResource(imgs[position]);
                            mBg.startAnimation(in);
                            mBg.setVisibility(View.VISIBLE);
                            mParent.setVisibility(View.VISIBLE);
                            mPhotoView.animaFrom(mInfo);

                        }
                    });

                }

            }


        }
    }


    private class ProblemDetailWithNoDataQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {


        ProblemDetailWithNoDataQuickAdapter() {
            super(R.layout.index_with_nodata);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {

        }
    }
}
