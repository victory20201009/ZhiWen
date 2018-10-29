package com.zzu.zk.zhiwen.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.goyourfly.multi_picture.MultiPictureView;
import com.jcraft.jsch.SftpException;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.ProgressParams;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.beans.ProblemDigest;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.BounceScrollView;
import com.zzu.zk.zhiwen.customed_ui.ChooseLabelDia;
import com.zzu.zk.zhiwen.customed_ui.ClearableEditText;
import com.zzu.zk.zhiwen.customed_ui.Label;
import com.zzu.zk.zhiwen.customed_ui.MyFilePickerDialog;
import com.zzu.zk.zhiwen.customed_ui.MyViewPager;
import com.zzu.zk.zhiwen.fragment.Catgority;
import com.zzu.zk.zhiwen.fragment.Index1;
import com.zzu.zk.zhiwen.fragment.Me;
import com.zzu.zk.zhiwen.fragment.Notify;
import com.zzu.zk.zhiwen.fragment.Test;
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
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class Home extends AppCompatActivity {
    final String tag = "Home";
    ImageView qadd;
    FrameLayout q_add;
    boolean open = false;
    public static BottomNavigationView bottomNavigationView;
    public static MyViewPager main_pager;
    ArrayList<Fragment> list = new ArrayList<>();
    Toolbar toolbar;
long firstTime;
    TextView t_cancel;
    TextView t_publish;
    EditText q_con;
    ClearableEditText q_title;
    TextView watcher;
    final int max_length = 2000;
    MultiPictureView multiple_image;
    Uri temp_imgUri;
    TextView choosef, choosef_watcher;
    LinearLayout lf1, lf2, lf3, lf4, lf5;
    TextView tf1, tf2, tf3, tf4, tf5;
    View vf1, vf2, vf3, vf4, vf5;
    Label l1, l2, l3;
    int selected_f_num = 0;
    int selected_f_length = 0;
    int selected_l_num = 0;
    List<String> selectedLabels = new ArrayList<>();
    ArrayList<String> paths = new ArrayList();
    ClearableEditText set_reword;
    TextView set_crouse;
    ImageView add_label;
    Label.OnDrawableRightListener labelOnClickListener = new Label.OnDrawableRightListener() {

        @Override
        public void onDrawableRightClick(View view) {

            add_label.setVisibility(View.VISIBLE);
            selectedLabels.remove((String) (view.getTag()));

            view.setVisibility(View.GONE);
        }
    };
    private final MyHandler mHandler = new MyHandler(this);
    private static DialogFragment dialogFragment;
    private ChooseLabelDia.OnItemClickListener labelListener = new ChooseLabelDia.OnItemClickListener() {
        @Override
        public void OnItemClick(String s) {
            if (l1.getVisibility() == View.GONE) {

                selected_l_num++;
                selectedLabels.add(s);
                l1.setTag(s);
                l1.setText(s);
                l1.setVisibility(View.VISIBLE);
                if (selected_l_num == 3) {
                    add_label.setVisibility(View.GONE);
                }

            } else if (l2.getVisibility() == View.GONE) {
                selected_l_num++;
                l2.setText(s);
                selectedLabels.add(s);
                l2.setTag(s);
                l2.setVisibility(View.VISIBLE);
                if (selected_l_num == 3) {
                    add_label.setVisibility(View.GONE);
                }
            } else {
                selected_l_num++;
                l3.setText(s);
                selectedLabels.add(s);
                l3.setTag(s);
                l3.setVisibility(View.VISIBLE);
                if (selected_l_num == 3) {
                    add_label.setVisibility(View.GONE);
                }
            }


        }
    };


    private ChooseLabelDia.OnItemClickListener courselabelListener = new ChooseLabelDia.OnItemClickListener() {

        @Override
        public void OnItemClick(String s) {
            set_crouse.setText(s);
            set_crouse.setTextColor(Home.this.getResources().getColor(R.color.colorAccent));
        }
    };

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        IndexQuickAdapter indexQuickAdapter;
        recyclerView.setAdapter(indexQuickAdapter = new IndexQuickAdapter());
        indexQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
            add(new ProblemDigest());

        }});


        toolbar = findViewById(R.id.toolbar);

        final Animation q_in = AnimationUtils.loadAnimation(this, R.anim.q_in);
        final Animation add = AnimationUtils.loadAnimation(this, R.anim.add);
        final Animation cancel = AnimationUtils.loadAnimation(this, R.anim.cancel);
        final Animation q_out = AnimationUtils.loadAnimation(this, R.anim.q_out);
        add.setFillAfter(true);
        cancel.setFillAfter(true);
        q_add = findViewById(R.id.q_add);
        q_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }

        });


        ImageView masker = findViewById(R.id.masker);
        masker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }

        });

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        main_pager = findViewById(R.id.main_pager);
        int[] image = {R.drawable.index, R.drawable.ic_class, 0, R.drawable.ic_notify,
                R.drawable.me};
        int[] color = {ContextCompat.getColor(this, R.color.firstColor), ContextCompat.getColor(this, R.color.secondColor),
                0, ContextCompat.getColor(this, R.color.thirdColor), ContextCompat.getColor(this, R.color.forthColor)};
        if (bottomNavigationView != null) {
            bottomNavigationView.isWithText(true);//点击之前有字   没有字
//             bottomNavigationView.activateTabletMode();
            bottomNavigationView.isColoredBackground(true);//是否启用背景色
            bottomNavigationView.setTextActiveSize(getResources().getDimension(R.dimen.text_active));
            bottomNavigationView.setTextInactiveSize(getResources().getDimension(R.dimen.text_inactive));
            bottomNavigationView.setItemActiveColorWithoutColoredBackground(ContextCompat.getColor(this, R.color.colorAccent));

            bottomNavigationView.setFont(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Noh_normal.ttf"));
        }


        BottomNavigationItem bottomNavigationItem = new BottomNavigationItem
                (Cons.NAMES_OF_BOTTOM_TABS[0], color[0], image[0]);
        BottomNavigationItem bottomNavigationItem1 = new BottomNavigationItem
                (Cons.NAMES_OF_BOTTOM_TABS[1], color[0], image[1]);
        BottomNavigationItem bottomNavigationItem2 = new BottomNavigationItem
                (Cons.NAMES_OF_BOTTOM_TABS[2], color[0], image[2]);
        BottomNavigationItem bottomNavigationItem3 = new BottomNavigationItem
                (Cons.NAMES_OF_BOTTOM_TABS[3], color[0], image[3]);
        BottomNavigationItem bottomNavigationItem4 = new BottomNavigationItem
                (Cons.NAMES_OF_BOTTOM_TABS[4], color[0], image[4]);


        assert bottomNavigationView != null;
        bottomNavigationView.addTab(bottomNavigationItem);
        bottomNavigationView.addTab(bottomNavigationItem1);
        bottomNavigationView.addTab(bottomNavigationItem2);
        bottomNavigationView.addTab(bottomNavigationItem3);
        bottomNavigationView.addTab(bottomNavigationItem4);
        bottomNavigationView.selectTab(0);
        init();
        bottomNavigationView.setOnBottomNavigationItemClickListener(new OnBottomNavigationItemClickListener() {
            @Override
            public void onNavigationItemClick(int index) {
                Integer is_login = KlbertjCache.uid;
                if (index < 2) {
                    main_pager.setCurrentItem(index, true);
                } else if (index > 2) {
                    if (index == 3) {

                        if (Cons.NOT_LOGIN.equals(is_login)) {
                            new CircleDialog.Builder()
                                    .setTitle("提醒")
                                    .setText("您还未登录，现在去登录吗？")
                                    .setCanceledOnTouchOutside(false)
                                    .setPositive("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomNavigationView.selectTab(0);
                                            startActivity(new Intent(Home.this, Login.class));
                                        }
                                    })
                                    .setNegative("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomNavigationView.selectTab(0);
                                        }
                                    })
                                    .show(Home.this.getSupportFragmentManager());


                        } else if (-1 == is_login) {
                            ToastUtils.showToast(Home.this, "您的账号还没有激活");
                            bottomNavigationView.selectTab(0);
                        } else {
                            main_pager.setCurrentItem(index - 1, true);
                        }
                    } else {

                        if (Cons.NOT_LOGIN.equals(is_login)) {
                            new CircleDialog.Builder()
                                    .setTitle("提醒")
                                    .setText("您还未登录，现在去登录吗？")
                                    .setCanceledOnTouchOutside(false)
                                    .setPositive("确定", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomNavigationView.selectTab(0);
                                            startActivity(new Intent(Home.this, Login.class));

                                        }
                                    })
                                    .setNegative("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomNavigationView.selectTab(0);
                                        }
                                    })
                                    .show(Home.this.getSupportFragmentManager());


                        } else {
                            main_pager.setCurrentItem(index - 1, true);
                        }
                    }


                }


            }
        });

        qadd = findViewById(R.id.qadd);

        qadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer is_login = KlbertjCache.uid;
                if (Cons.NOT_LOGIN.equals(is_login)) {
                    new CircleDialog.Builder()
                            .setTitle("提醒")
                            .setText("您还未登录，现在去登录吗？")
                            .setCanceledOnTouchOutside(false)
                            .setPositive("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Home.this, Login.class));
                                }
                            })
                            .setNegative("取消", null)
                            .show(Home.this.getSupportFragmentManager());


                } else if (-1 == is_login) {
                    ToastUtils.showToast(Home.this, "您的账号还没有激活");
                }else if(TextUtils.isEmpty(KlbertjCache.major)||TextUtils.isEmpty(KlbertjCache.college)){
                    ToastUtils.showToast(Home.this, "请填写你的院系和专业");
                }else {
                    if (open) {
                        new CircleDialog.Builder()
                                .setTitle("提醒")
                                .setText("你确定要退出本次编辑吗？")
                                .setPositive("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        q_con.setText("");
                                        q_title.setText("");
                                        set_reword.setText("");
                                        set_crouse.setText("选择所属课程");
                                        choosef_watcher.setText("还可以上传5个附件，剩余额度共60M");
                                        watcher.setText("还可输入：2000字");
                                        multiple_image.clearItem();
                                        choosef.setVisibility(View.VISIBLE);
                                        lf1.setVisibility(View.GONE);
                                        lf2.setVisibility(View.GONE);
                                        lf3.setVisibility(View.GONE);
                                        lf4.setVisibility(View.GONE);
                                        lf5.setVisibility(View.GONE);

                                        l1.setVisibility(View.GONE);
                                        l2.setVisibility(View.GONE);
                                        l3.setVisibility(View.GONE);
                                        add_label.setVisibility(View.VISIBLE);
                                        selected_f_num = 0;
                                        selected_f_length = 0;
                                        selected_l_num = 0;
                                        selectedLabels = new ArrayList<>();
                                        paths = new ArrayList();


                                        q_add.startAnimation(q_out);
                                        q_add.setVisibility(View.GONE);
                                        qadd.getBackground().clearColorFilter();
                                        qadd.startAnimation(cancel);
                                        open = !open;
                                    }
                                })
                                .setNegative("取消", null)
                                .show(Home.this.getSupportFragmentManager());

                    } else {
                        q_add.setVisibility(View.VISIBLE);
                        q_add.startAnimation(q_in);
                        qadd.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
                        qadd.startAnimation(add);
                        open = !open;
                    }
                }


            }
        });


        t_cancel = findViewById(R.id.cancel);
        t_publish = findViewById(R.id.publish);
        t_publish.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onClick(View v) {
                if (KlbertjCache.uid == -2) {
                    ToastUtils.showToast(Home.this, "您还没有登陆");
                } else if (KlbertjCache.uid == -1) {
                    ToastUtils.showToast(Home.this, "您还没有激活");
                } else if (KlbertjCache.score == -2) {
                    ToastUtils.showToast(Home.this, "数据异常");
                } else {

                    String title = q_title.getText().toString().trim();
                    if (TextUtils.isEmpty(title)) {
                        ToastUtils.showToast(Home.this, "问题标题不能为空");
                    } else if (TextUtils.isEmpty(q_con.getText().toString().trim())) {
                        ToastUtils.showToast(Home.this, "问题正文不能为空");
                    } else if (TextUtils.isEmpty(set_reword.getText().toString().trim())) {
                        ToastUtils.showToast(Home.this, "奖励积分不能为空");
                    } else if (TextUtils.isEmpty(set_crouse.getText().toString().trim())) {
                        ToastUtils.showToast(Home.this, "问题所属课程不能为空");
                    } else if (selectedLabels.isEmpty()) {
                        ToastUtils.showToast(Home.this, "请设置至少一个问题标签");

                    }else if (Integer.valueOf(set_reword.getText().toString().trim())<=0) {
                        ToastUtils.showToast(Home.this, "问题积分不能小于0");
                       if(Integer.valueOf(set_reword.getText().toString().trim())>KlbertjCache.score){
                           ToastUtils.showToast(Home.this, "您的积分不足");
                       }
                    } else {
                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在发送请求...")
                                .setCancelable(false)
                                .setCanceledOnTouchOutside(false)
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                .show(getSupportFragmentManager());
                        final JSONObject send = new JSONObject();
                        send.put("id", KlbertjCache.uid);
                        JSONObject content = new JSONObject();
                        content.put("q_c_time", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date().getTime()));
                        content.put("q_title", title);
                        content.put("q_content", q_con.getText().toString().trim());
                        int end = Math.min(15, q_con.getText().toString().trim().length() - 1);
                        content.put("q_digest", q_con.getText().toString().trim().substring(0, end));
                        content.put("q_belt_u_id", KlbertjCache.uid);
                        final Map<Uri, String> uri_name = new HashMap<>();
                        final Map<String, String> name_servername = new HashMap<>();
                        String name;
                        Uri uri;
                        JSONArray picsu = new JSONArray();
//                        StringBuilder stringBuilder = new StringBuilder("[");
                        if (multiple_image.getList().isEmpty()) {

                            content.put("q_pic_urls", picsu);
                            content.put("digest_pic_url", "klbertj.jpg");
                        } else {
                            List<Uri> l = multiple_image.getList();
                            int i = 0;
                            for (; i < l.size(); i++) {
                                uri = l.get(i);
                                name = ToastUtils.getCurrentSeconds() + ".jpg";
                                uri_name.put(uri, name);
                                picsu.add(name);
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            content.put("q_pic_urls", picsu);

                            content.put("digest_pic_url", picsu.getString(0));
                        }


                        if (paths.isEmpty()) {
                            content.put("q_file_urls", new JSONObject());
                        } else {
                            JSONObject file_obj = new JSONObject();

                            JSONArray file_server_name = new JSONArray();
                            JSONArray f_server_name_real_name = new JSONArray();
                            JSONObject j_j = null;
                            String temp1, temp2;
                            for (int i = 0; i < paths.size(); i++) {
                                temp1 = paths.get(i);
                                temp1 = temp1.substring(temp1.lastIndexOf("/") + 1);
                                temp2 = ToastUtils.getCurrentSeconds() + "";
                                file_server_name.add(temp2);
                                j_j = new JSONObject();
                                j_j.put(temp2, temp1);
                                f_server_name_real_name.add(j_j);
                                name_servername.put(paths.get(i), temp2);
                                try {
                                    Thread.sleep(2);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            file_obj.put("server_name", file_server_name);
                            file_obj.put("server_real_name", f_server_name_real_name);
                            content.put("q_file_urls", file_obj);

                        }
                        final Integer score =Integer.valueOf(set_reword.getText().toString().trim());
                        content.put("q_score", score);
                        content.put("q_comments_ids", "[]");
                        content.put("q_comm_num", 0);


                        if ("".equals(KlbertjCache.name)) {
                            if (!"".equals(SharePreferenceUtils.getString(Home.this, Cons.NAME))) {
                                KlbertjCache.name = SharePreferenceUtils.getString(Home.this, Cons.NAME);
                                content.put("q_belt_u_name", KlbertjCache.name);
                            } else {
                                ToastUtils.showToast(Home.this, "参数错误");
                                content.put("q_belt_u_name", "");
                            }

                        } else {
                            content.put("q_belt_u_name", KlbertjCache.name);
                        }


                        if ("".equals(KlbertjCache.avator_path)) {
                            if (!"".equals(SharePreferenceUtils.getString(Home.this, Cons.AVATOR_PATH))) {
                                KlbertjCache.avator_path = SharePreferenceUtils.getString(Home.this, Cons.AVATOR_PATH);
                                content.put("q_belt_u_head_url", KlbertjCache.avator_path);
                            } else {
                                ToastUtils.showToast(Home.this, "参数错误");
                                content.put("q_belt_u_head_url", "");
                            }

                        } else {
                            content.put("q_belt_u_head_url", KlbertjCache.avator_path);
                        }

                        content.put("is_of_adopted_reply", -1);
                        content.put("q_belt_subject", set_crouse.getText().toString().trim());
                        JSONArray label = new JSONArray();
                        label.addAll(selectedLabels);
                        content.put("q_label", label);
                        send.put("content", content);




                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                HttpURLConnection urlConnection = null;
                                try {
                                    urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.insertQuestion));
                                    OutputStream out = urlConnection.getOutputStream();

                                    out.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                    out.close();
                                    int responseCode = urlConnection.getResponseCode();
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                        Objects.requireNonNull(Home.this).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }
                                                if ("f".equals(result)) {
                                                    ToastUtils.showToast(Home.this, "token过期");
                                                    uri_name.clear();
                                                    name_servername.clear();
                                                } else {
                                                    dialogFragment = new CircleDialog.Builder()
                                                            .setProgressText("正在保存您的数据...")
                                                            .setCanceledOnTouchOutside(false)
                                                            .setCancelable(false)
                                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                            .show(getSupportFragmentManager());

                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (!uri_name.isEmpty()) {
                                                                SFTPUtils.upLoadPics(Home.this, uri_name, Cons.QUES_PICS_PATH_IN_SERVER);
                                                            }
                                                            if (!name_servername.isEmpty()) {
                                                                SFTPUtils.uploadFiles(name_servername);
                                                            }
                                                            Objects.requireNonNull(Home.this).runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (dialogFragment != null) {
                                                                        dialogFragment.dismiss();
                                                                        dialogFragment = null;
                                                                    }

                                                                    KlbertjCache.score -=score;
                                                                    SharePreferenceUtils.putInteger(Objects.requireNonNull(Home.this), Cons.SCORE, KlbertjCache.score);
                                                                    q_con.setText("");
                                                                    q_title.setText("");
                                                                    set_reword.setText("");
                                                                    set_crouse.setText("选择所属课程");
                                                                    choosef_watcher.setText("还可以上传5个附件，剩余额度共60M");
                                                                    watcher.setText("还可输入：2000字");
                                                                    multiple_image.clearItem();
                                                                    choosef.setVisibility(View.VISIBLE);
                                                                    lf1.setVisibility(View.GONE);
                                                                    lf2.setVisibility(View.GONE);
                                                                    lf3.setVisibility(View.GONE);
                                                                    lf4.setVisibility(View.GONE);
                                                                    lf5.setVisibility(View.GONE);

                                                                    l1.setVisibility(View.GONE);
                                                                    l2.setVisibility(View.GONE);
                                                                    l3.setVisibility(View.GONE);
                                                                    add_label.setVisibility(View.VISIBLE);
                                                                    selected_f_num = 0;
                                                                    selected_f_length = 0;
                                                                    selected_l_num = 0;
                                                                    selectedLabels = new ArrayList<>();
                                                                    paths = new ArrayList();


                                                                    q_add.startAnimation(q_out);
                                                                    q_add.setVisibility(View.GONE);
                                                                    qadd.getBackground().clearColorFilter();
                                                                    qadd.startAnimation(cancel);
                                                                    open = !open;
                                                                    ToastUtils.showToast(Home.this,"操作成功");
                                                                }
                                                            });


                                                        }
                                                    }).start();



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

        t_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CircleDialog.Builder()
                        .setTitle("提醒")
                        .setText("你确定要退出本次编辑吗？")
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                q_con.setText("");
                                q_title.setText("");
                                set_reword.setText("");
                                choosef_watcher.setText("还可以上传5个附件，剩余额度共60M");
                                watcher.setText("还可输入：2000字");
                                multiple_image.clearItem();
                                choosef.setVisibility(View.VISIBLE);
                                lf1.setVisibility(View.GONE);
                                lf2.setVisibility(View.GONE);
                                lf3.setVisibility(View.GONE);
                                lf4.setVisibility(View.GONE);
                                lf5.setVisibility(View.GONE);

                                l1.setVisibility(View.GONE);
                                l2.setVisibility(View.GONE);
                                l3.setVisibility(View.GONE);
                                add_label.setVisibility(View.VISIBLE);
                                selected_f_num = 0;
                                selected_f_length = 0;
                                selected_l_num = 0;
                                selectedLabels = new ArrayList<>();
                                paths = new ArrayList();
                                set_crouse.setText("选择所属课程");


                                q_add.startAnimation(q_out);
                                q_add.setVisibility(View.GONE);
                                qadd.getBackground().clearColorFilter();
                                qadd.startAnimation(cancel);
                                open = !open;
                            }
                        })
                        .setNegative("取消", null)
                        .show(Home.this.getSupportFragmentManager());
            }
        });


//        BounceScrollView bounceScrollView  = findViewById(R.id.bounceScrollView);
//        bounceScrollView.setBounceFraction(0.02f);
//        bounceScrollView.setInterpolator(new AccelerateDecelerateInterpolator());


        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        StatusBarUtil.setPaddingSmart(this, recyclerView);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurView));

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(tag, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(tag, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(tag, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(tag, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(tag, "onDestroy");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(tag, "onAttachedToWindow");
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(tag, "onDetachedFromWindow");
    }

    private class IndexQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {


        IndexQuickAdapter() {
            super(R.layout.add_item);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {


            add_label = helper.getView(R.id.add_label);
            l1 = helper.getView(R.id.l1);
            l2 = helper.getView(R.id.l2);
            l3 = helper.getView(R.id.l3);
            set_crouse = helper.getView(R.id.set_crouse);
            set_reword = helper.getView(R.id.set_reword);

            lf1 = helper.getView(R.id.lf1);
            lf2 = helper.getView(R.id.lf2);
            lf3 = helper.getView(R.id.lf3);
            lf4 = helper.getView(R.id.lf4);
            lf5 = helper.getView(R.id.lf5);

            tf1 = helper.getView(R.id.tf1);
            tf2 = helper.getView(R.id.tf2);
            tf3 = helper.getView(R.id.tf3);
            tf4 = helper.getView(R.id.tf4);
            tf5 = helper.getView(R.id.tf5);

            vf1 = helper.getView(R.id.vf1);
            vf2 = helper.getView(R.id.vf2);
            vf3 = helper.getView(R.id.vf3);
            vf4 = helper.getView(R.id.vf4);
            vf5 = helper.getView(R.id.vf5);
            choosef_watcher = helper.getView(R.id.choosef_watcher);
            choosef = helper.getView(R.id.choosef);
            multiple_image = helper.getView(R.id.multiple_image);
            watcher = helper.getView(R.id.watcher);
            q_con = helper.getView(R.id.q_con);
            q_title = helper.getView(R.id.q_title);


//            l3 = findViewById(R.id.l3);
            l1.setOnDrawableRightClickListener(labelOnClickListener);
            l2.setOnDrawableRightClickListener(labelOnClickListener);
            l3.setOnDrawableRightClickListener(labelOnClickListener);


//        add_label = findViewById(R.id.add_label);
            add_label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File label = new File(Home.this.getFilesDir().getAbsolutePath() + Cons.label);
                    if (!label.exists()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SFTPUtils.download(Cons.ROOT_PATH_IN_SERVER + Cons.labelPathInServer + "/" + Cons.college + "/",
                                            Cons.major + ".json", Home.this.getFilesDir().getAbsolutePath() + Cons.label);
                                    Message msg = Message.obtain();
                                    msg.what = 1;
                                    mHandler.sendMessage(msg);
                                } catch (SftpException e) {
                                    Home.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.showToast(Home.this, "网络缓慢，请稍候重试");
                                        }
                                    });
                                    Log.i(Cons.debugLabel, "下载出错");
                                    e.printStackTrace();
                                } catch (FileNotFoundException e) {
                                    Log.i(Cons.debugLabel, "文件不存在");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在获取数据...")
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
//                        .setProgressDrawable(R.drawable.bg_progress_s)
                                .show(getSupportFragmentManager());


                    } else {
                        ChooseLabelDia.getInstance(labelListener, selectedLabels, 0).show(getSupportFragmentManager(), "DialogLogout");
                    }


                }
            });
//        set_crouse = findViewById(R.id.set_crouse);
            set_crouse.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    File course = new File(Home.this.getFilesDir().getAbsolutePath() + Cons.course);
                    if (!course.exists()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    SFTPUtils.download(Cons.ROOT_PATH_IN_SERVER + Cons.coursePathInServer + "/" + Cons.college + "/",
                                            Cons.major + ".json", Home.this.getFilesDir().getAbsolutePath() + Cons.course);
                                    Message msg = Message.obtain();
                                    msg.what = 2;
                                    mHandler.sendMessage(msg);
                                } catch (SftpException e) {
                                    Home.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtils.showToast(Home.this, "网络缓慢，请稍候重试");
                                        }
                                    });
                                    Log.i(Cons.debugLabel, "下载出错");
                                    e.printStackTrace();
                                } catch (FileNotFoundException e) {
                                    Log.i(Cons.debugLabel, "文件不存在");
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在获取数据...")
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
//                        .setProgressDrawable(R.drawable.bg_progress_s)
                                .show(getSupportFragmentManager());

                    } else {
                        ChooseLabelDia.getInstance(courselabelListener, null, 1).show(getSupportFragmentManager(), "DialogLogout");
                    }
                }
            });


            vf1.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    lf1.setVisibility(View.GONE);

                    selected_f_num--;
                    String[] s = (String[]) (tf1.getTag());
                    paths.remove(s[1]);
                    selected_f_length -= Long.valueOf(s[0]);

                    choosef_watcher.setText("还可以上传" + (5 - selected_f_num) +
                            "个附件，剩余额度共" + new DecimalFormat("#.00")
                            .format((Cons.max_upload_file_length - selected_f_length) / 1024 / 1024) + "M");
                    if (choosef.getVisibility() == View.GONE) {
                        choosef.setVisibility(View.VISIBLE);
                    }
                }
            });

            vf2.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    lf2.setVisibility(View.GONE);
                    selected_f_num--;
                    String[] s = (String[]) (tf2.getTag());
                    paths.remove(s[1]);
                    selected_f_length -= Long.valueOf(s[0]);
                    choosef_watcher.setText("还可以上传" + (5 - selected_f_num) +
                            "个附件，剩余额度共" + new DecimalFormat("#.00")
                            .format((Cons.max_upload_file_length - selected_f_length) / 1024 / 1024) + "M");
                    if (choosef.getVisibility() == View.GONE) {
                        choosef.setVisibility(View.VISIBLE);
                    }
                }
            });
            vf3.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    lf3.setVisibility(View.GONE);
                    selected_f_num--;
                    String[] s = (String[]) (tf3.getTag());
                    paths.remove(s[1]);
                    selected_f_length -= Long.valueOf(s[0]);
                    choosef_watcher.setText("还可以上传" + (5 - selected_f_num) +
                            "个附件，剩余额度共" + new DecimalFormat("#.00")
                            .format((Cons.max_upload_file_length - selected_f_length) / 1024 / 1024) + "M");
                    if (choosef.getVisibility() == View.GONE) {
                        choosef.setVisibility(View.VISIBLE);
                    }
                }
            });
            vf4.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    lf4.setVisibility(View.GONE);
                    selected_f_num--;
                    String[] s = (String[]) (tf4.getTag());
                    paths.remove(s[1]);
                    selected_f_length -= Long.valueOf(s[0]);
                    choosef_watcher.setText("还可以上传" + (5 - selected_f_num) +
                            "个附件，剩余额度共" + new DecimalFormat("#.00")
                            .format((Cons.max_upload_file_length - selected_f_length) / 1024 / 1024) + "M");
                    if (choosef.getVisibility() == View.GONE) {
                        choosef.setVisibility(View.VISIBLE);
                    }
                }
            });
            vf5.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    lf5.setVisibility(View.GONE);
                    selected_f_num--;
                    String[] s = (String[]) (tf5.getTag());
                    paths.remove(s[1]);
                    selected_f_length -= Long.valueOf(s[0]);
                    choosef_watcher.setText("还可以上传" + (5 - selected_f_num) +
                            "个附件，剩余额度共" + new DecimalFormat("#.00")
                            .format((Cons.max_upload_file_length - selected_f_length) / 1024 / 1024) + "M");
                    if (choosef.getVisibility() == View.GONE) {
                        choosef.setVisibility(View.VISIBLE);
                    }
                }
            });


//        choosef = findViewById(R.id.choosef);
            choosef.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogProperties properties = new DialogProperties();
                    properties.selection_mode = DialogConfigs.MULTI_MODE;
                    properties.extensions = Cons.mime_types;
                    MyFilePickerDialog dialog = new MyFilePickerDialog(Home.this, properties);
                    dialog.setTitle("请选择附件");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.setNegativeBtnName("取消");
                    dialog.setInitialParams(new int[]{selected_f_num, selected_f_length}, paths);
                    dialog.setPositiveBtnName("确定");
                    dialog.setDialogSelectionListener(new DialogSelectionListener() {
                        @Override
                        public void onSelectedFilePaths(String[] files) {
                            long temp = 0;
                            for (String path : files) {
                                temp += new File(path).length();
                                paths.add(path);
                            }
                            if (files.length + selected_f_num == 5) {
                                choosef.setVisibility(View.GONE);
                                choosef_watcher.setText("附件个数已达上限");
                            } else {
                                choosef_watcher.setText("还可以上传" + (5 - selected_f_num - files.length) + "个附件，剩余额度共" + new DecimalFormat("#.00").format((Cons.max_upload_file_length - temp - selected_f_length) / 1024 / 1024) + "M");
                            }

                            selected_f_num += files.length;
                            selected_f_length += temp;

                            for (String path : files) {

                                if (lf1.getVisibility() == View.GONE) {
                                    lf1.setVisibility(View.VISIBLE);
                                    tf1.setText("附件1：" + path.substring(path.lastIndexOf("/") + 1));
                                    tf1.setTag(new String[]{new File(path).length() + "", path});
                                } else if (lf2.getVisibility() == View.GONE) {
                                    lf2.setVisibility(View.VISIBLE);
                                    tf2.setText("附件2：" + path.substring(path.lastIndexOf("/") + 1));
                                    tf2.setTag(new String[]{new File(path).length() + "", path});
                                } else if (lf3.getVisibility() == View.GONE) {
                                    lf3.setVisibility(View.VISIBLE);
                                    tf3.setText("附件3：" + path.substring(path.lastIndexOf("/") + 1));
                                    tf3.setTag(new String[]{new File(path).length() + "", path});
                                } else if (lf4.getVisibility() == View.GONE) {
                                    lf4.setVisibility(View.VISIBLE);
                                    tf4.setText("附件4：" + path.substring(path.lastIndexOf("/") + 1));
                                    tf4.setTag(new String[]{new File(path).length() + "", path});
                                } else {
                                    lf5.setVisibility(View.VISIBLE);
                                    tf5.setText("附件5：" + path.substring(path.lastIndexOf("/") + 1));
                                    tf5.setTag(new String[]{new File(path).length() + "", path});
                                }
                            }


                        }
                    });
                    dialog.show();
                }
            });

//        multiple_image = findViewById(R.id.multiple_image);
            multiple_image.setDeleteResource(R.drawable.ic_clear_selec);
            multiple_image.setDeleteClickCallback(new MultiPictureView.DeleteClickCallback() {
                @Override
                public void onDeleted(View view, final int i) {
                    new CircleDialog.Builder()
                            .setTitle("提醒")
                            .setText("你确定取消选择吗？")
                            .setPositive("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    multiple_image.removeItem(i, true);
                                }
                            })
                            .setNegative("取消", null)
                            .show(Home.this.getSupportFragmentManager());
                }
            });


            multiple_image.setItemClickCallback(new MultiPictureView.ItemClickCallback() {
                @Override
                public void onItemClicked(View view, int i, ArrayList<Uri> arrayList) {

                }
            });
            multiple_image.setAddClickCallback(new MultiPictureView.AddClickCallback() {
                @Override
                public void onAddClick(View view) {
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

                                        if ((ContextCompat.checkSelfPermission(Home.this,
                                                Manifest.permission.CAMERA)
                                                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(Home.this,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED)) {

                                            ActivityCompat.requestPermissions(Home.this,
                                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                            Manifest.permission.CAMERA},
                                                    Cons.REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA_MOUNT_UNMOUNT_FILESYSTEMS);
                                        } else {
                                            /*shouldShowRequestPermissionRationale();*/
                                            temp_imgUri = StorageUtils.get_temp_photo_uri();
                                            startActivityForResult(get_take_photo_intent(temp_imgUri), Cons.REQUEST_TAKE_PHOTO);
                                        }


                                    } else {
                                        addImage();
                                    }
//                                    ToastUtils.showToast(Home.this, position + "");
                                }
                            })
                            .setNegative("取消", null)
                            .show(Home.this.getSupportFragmentManager());

                }
            });

//        watcher = findViewById(R.id.watcher);
            watcher.setText("还可输入：2000字");


            q_con.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (v.getId()) {
                        case R.id.q_con:
                            // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                            if (canVerticalScroll(q_con))
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_UP:
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }
                    }
                    return false;
                }
            });
            q_con.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                wordNum= s;//实时记录输入的字数
                }

                @Override
                public void afterTextChanged(Editable s) {


                    watcher.setText("还可输入：" + (max_length - s.length()) + "字");
//                selectionStart=q_con.getSelectionStart();
//                selectionEnd = q_con.getSelectionEnd();
//                if (wordNum.length() > max_length) {
//                    //删除多余输入的字（不会显示出来）
//                    s.delete(selectionStart - 1, selectionEnd);
//                    int tempSelection = selectionEnd;
//                    q_con.setText(s);
//                    q_con.setSelection(tempSelection);//设置光标在最后
//                }
                }
            });


        }
    }

    private void addImage() {

        if (ContextCompat.checkSelfPermission(Home.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Cons.REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Matisse.from(Home.this)
                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                    .maxSelectable(9 - multiple_image.getCount())
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

    private void init() {


        Fragment fg1 = new Index1();


        Fragment fg2 = new Catgority();

        Bundle bundle3 = new Bundle();


        Fragment fg3 = new Notify();


        Fragment fg4 = new Me();


        list.add(fg1);
        list.add(fg2);
        list.add(fg3);
        list.add(fg4);
        main_pager.setNoScroll(true);
        main_pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
//        main_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if (position >= 2) {
//
//                    bottomNavigationView.selectTab(position + 1);
//                } else {
//                    bottomNavigationView.selectTab(position);
//                }
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });


    }

    private class MainPagerAdapter extends FragmentPagerAdapter {


        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Cons.REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addImage();
            }
        } else if (requestCode == Cons.REQUEST_WRITE_EXTERNAL_STORAGE_CAMERA_MOUNT_UNMOUNT_FILESYSTEMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                temp_imgUri = StorageUtils.get_temp_photo_uri();
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
            ProblemDetail.ulist = Matisse.obtainResult(data);
            multiple_image.addItem(Matisse.obtainResult(data));
        } else if (requestCode == Cons.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            startActivityForResult(
                    cropPhoto(temp_imgUri, temp_imgUri, 300, 400),
                    Cons.REQUEST_CROP_PHOTO);

        } else if (requestCode == Cons.REQUEST_CROP_PHOTO && resultCode == RESULT_OK) {
            ArrayList<Uri> list = new ArrayList<Uri>();
            list.add(temp_imgUri);
            multiple_image.addItem(list);

        }
    }
    @Override
    public void  onBackPressed() {

            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                ToastUtils.showToast(Home.this,"再按一次退出程序");
//                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;

            } else {
               super.onBackPressed();
            }


    }

    private Intent get_take_photo_intent(Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;

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

    private static class MyHandler extends Handler {

        //对Activity的弱引用
        private final WeakReference<Home> mActivity;

        public MyHandler(Home activity) {
            mActivity = new WeakReference<Home>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Home activity = mActivity.get();
            if (activity == null) {
                super.handleMessage(msg);
                return;
            }
            switch (msg.what) {
                case 1:
                    if (dialogFragment != null) {
                        dialogFragment.dismiss();
                    }
                    ChooseLabelDia.getInstance(activity.labelListener, activity.selectedLabels, 0).show(activity.getSupportFragmentManager(), "DialogLogout");
                    break;
                case 2:
                    if (dialogFragment != null) {
                        dialogFragment.dismiss();
                    }
                    ChooseLabelDia.getInstance(activity.courselabelListener, null, 1).show(activity.getSupportFragmentManager(), "DialogLogout");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}
