package com.zzu.zk.zhiwen.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.activity.ProblemDetail;
import com.zzu.zk.zhiwen.beans.ProblemDigest;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.ClearableEditText;
import com.zzu.zk.zhiwen.customed_ui.MyCircleView;
import com.zzu.zk.zhiwen.glide.MyGlide;
import com.zzu.zk.zhiwen.utils.EncryptionUtils;
import com.zzu.zk.zhiwen.utils.StatusBarUtil;
import com.zzu.zk.zhiwen.utils.ToastUtils;
import com.zzu.zk.zhiwen.utils.UrlUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import fj.dropdownmenu.lib.concat.DropdownI;
import fj.dropdownmenu.lib.ion.ViewInject;
import fj.dropdownmenu.lib.ion.ViewUtils;
import fj.dropdownmenu.lib.pojo.DropdownItemObject;
import fj.dropdownmenu.lib.utils.DropdownUtils;
import fj.dropdownmenu.lib.view.DropdownButton;
import fj.dropdownmenu.lib.view.DropdownColumnView;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class Catgority extends Fragment implements DropdownI.ThreeRow {


    @ViewInject(R.id.btnRegion)
    @BindView(R.id.btnRegion)
    DropdownButton btnRegion;
    @BindView(R.id.mask)
    View mask;
    @ViewInject(R.id.lvRegion)
    @BindView(R.id.lvRegion)
    DropdownColumnView lvRegion;


    SmartRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    CatgorityQuickAdapter catgorityQuickAdapter = catgorityQuickAdapter = new CatgorityQuickAdapter();;
    CatgorityWithNoDataQuickAdapter catgorityWithNoDataQuickAdapter;
    LinearLayout got;
    ImageButton gotop;
    private static DialogFragment dialogFragment = null;
    String college = "全部院系";
    String major = "全部专业";
    String course = "全部课程";
    @BindView(R.id.find)
    View find;

    @BindView(R.id.search_label)
    ClearableEditText search_label;
    int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.catgority, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        DropdownUtils.initFragment(getActivity(), this, view, mask);
        ViewUtils.injectFragmentViews(this, view, mask);

        lvRegion.setThreeRow(this)
                .setSingleRowList(getCollege(), -1)  //单列数据
                .setDoubleRowList(getMajor(), -1)//双列数据
                .setThreeRowList(getCourse(), -1)//三列数据
                .setButton(btnRegion)//按钮
                .show();


        Log.i("gvfdgd", "college" + college + "major" + major + "course" + course);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        got = view.findViewById(R.id.got);
        gotop = view.findViewById(R.id.gotop);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), VERTICAL));
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

        gotop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        if (KlbertjCache.cate_ques_first_page != null && !KlbertjCache.cate_ques_first_page.isEmpty()) {
            if (catgorityQuickAdapter == null) {
                catgorityQuickAdapter = new CatgorityQuickAdapter();
            }
            catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
            recyclerView.setAdapter(catgorityQuickAdapter);
        } else {
            if (KlbertjCache.index_ques_first_page != null && !KlbertjCache.index_ques_first_page.isEmpty()) {
                if (catgorityQuickAdapter == null) {
                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                }
                catgorityQuickAdapter.replaceData(KlbertjCache.index_ques_first_page);
                recyclerView.setAdapter(catgorityQuickAdapter);
            } else {
                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                    add(new ProblemDigest());
                }});
                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                refreshLayout.setEnableLoadMore(false);
                refreshLayout.setEnableRefresh(true);
            }
        }


        StatusBarUtil.immersive(Objects.requireNonNull(getActivity()));
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(getActivity()), recyclerView);
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(getActivity()), view.findViewById(R.id.container));
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(getActivity()), view.findViewById(R.id.blurView));










        catgorityQuickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                final int qid = catgorityQuickAdapter.getData().get(position).id;
                final ProblemDigest p = catgorityQuickAdapter.getData().get(position);

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








        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout1) {
                final String search_con = search_label.getText().toString().trim();
                page = 1;
                if (KlbertjCache.cate_ques_first_page != null) {
                    KlbertjCache.cate_ques_first_page.clear();
                    KlbertjCache.cate_ques_first_page = null;
                }

                if (KlbertjCache.cate_ques_second_page != null) {
                    KlbertjCache.cate_ques_second_page.clear();
                    KlbertjCache.cate_ques_second_page = null;
                }
                if (KlbertjCache.cate_ques_third_page != null) {
                    KlbertjCache.cate_ques_third_page.clear();
                    KlbertjCache.cate_ques_third_page = null;
                }
                if (KlbertjCache.cate_ques_forth_page != null) {
                    KlbertjCache.cate_ques_forth_page.clear();
                    KlbertjCache.cate_ques_forth_page = null;
                }


                if ("全部院系".equals(college)) {

                    if ("".equals(search_con)) {
                        if (KlbertjCache.index_ques_first_page != null && !KlbertjCache.index_ques_first_page.isEmpty()) {
                            if (catgorityQuickAdapter == null) {
                                catgorityQuickAdapter = new CatgorityQuickAdapter();
                            }
                            catgorityQuickAdapter.replaceData(KlbertjCache.index_ques_first_page);
                            recyclerView.setAdapter(catgorityQuickAdapter);
                        } else {
                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                add(new ProblemDigest());
                            }});
                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                            refreshLayout.setEnableLoadMore(false);
                            refreshLayout.setEnableRefresh(true);
                        }
                        refreshLayout1.finishRefresh(1000);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                    OutputStream out1 = urlConnection1.getOutputStream();

                                    JSONObject send = new JSONObject();
                                    send.put("college", "全部院系");
                                    send.put("major", "全部专业");
                                    send.put("course", "全部课程");
                                    send.put("keyword", search_con);
                                    send.put("page", page);
                                    out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                    out1.close();
                                    int responseCode1 = urlConnection1.getResponseCode();
                                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                        final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                        Thread.sleep(1000);


                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                refreshLayout1.finishRefresh(1000);
                                                if ("server error".equals(result1)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                } else if ("bp".equals(result1)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                } else if ("no data".equals(result1)) {
                                                    catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                    catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
                                                } else {

                                                    JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                            problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                            problemDigest1.labels = j.getString("label");
                                                            problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                            problemDigest1.id = j.getInteger("id");
                                                            problemDigests.add(problemDigest1);
                                                        }

                                                        KlbertjCache.cate_ques_first_page = problemDigests;
                                                        if (catgorityQuickAdapter == null)
                                                            catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                        catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                        recyclerView.setAdapter(catgorityQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(true);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {
                                                        catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                        catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                    if ("全部专业".equals(major)) {

                        if ("".equals(search_con)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                        OutputStream out1 = urlConnection1.getOutputStream();

                                        JSONObject send = new JSONObject();
                                        send.put("college", college);
                                        send.put("major", "全部专业");
                                        send.put("course", "全部课程");
                                        send.put("keyword", "");
                                        send.put("page", page);
                                        out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                        out1.close();
                                        int responseCode1 = urlConnection1.getResponseCode();
                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                            Thread.sleep(1000);


                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    refreshLayout1.finishRefresh(1000);
                                                    if ("server error".equals(result1)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                    } else if ("bp".equals(result1)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                    } else if ("no data".equals(result1)) {
                                                        catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                        catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {

                                                        JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                problemDigest1.labels = j.getString("label");
                                                                problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                problemDigest1.id = j.getInteger("id");
                                                                problemDigests.add(problemDigest1);
                                                            }

                                                            KlbertjCache.cate_ques_first_page = problemDigests;
                                                            if (catgorityQuickAdapter == null)
                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                            catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(true);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                        OutputStream out1 = urlConnection1.getOutputStream();

                                        JSONObject send = new JSONObject();
                                        send.put("college", college);
                                        send.put("major", "全部专业");
                                        send.put("course", "全部课程");
                                        send.put("keyword", search_con);
                                        send.put("page", page);
                                        out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                        out1.close();
                                        int responseCode1 = urlConnection1.getResponseCode();
                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                            Thread.sleep(1000);


                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    refreshLayout1.finishRefresh(1000);
                                                    if ("server error".equals(result1)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                    } else if ("bp".equals(result1)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                    } else if ("no data".equals(result1)) {
                                                        catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                        catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {

                                                        JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                problemDigest1.labels = j.getString("label");
                                                                problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                problemDigest1.id = j.getInteger("id");
                                                                problemDigests.add(problemDigest1);
                                                            }

                                                            KlbertjCache.cate_ques_first_page = problemDigests;
                                                            if (catgorityQuickAdapter == null)
                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                            catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(true);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                        if ("全部课程".equals(course)) {
                            if ("".equals(search_con)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", major);
                                            send.put("course", "全部课程");
                                            send.put("keyword", "");
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        refreshLayout1.finishRefresh(1000);
                                                        if ("server error".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no data".equals(result1)) {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(false);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_first_page = problemDigests;
                                                                if (catgorityQuickAdapter == null)
                                                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                                catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                                recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                            } else {
                                                                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                    add(new ProblemDigest());
                                                                }});
                                                                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", major);
                                            send.put("course", "全部课程");
                                            send.put("keyword", search_con);
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        refreshLayout1.finishRefresh(1000);
                                                        if ("server error".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no data".equals(result1)) {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(false);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_first_page = problemDigests;
                                                                if (catgorityQuickAdapter == null)
                                                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                                catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                                recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                            } else {
                                                                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                    add(new ProblemDigest());
                                                                }});
                                                                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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


                            if ("".equals(search_con)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", major);
                                            send.put("course", course);
                                            send.put("keyword", "");
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        refreshLayout1.finishRefresh(1000);
                                                        if ("server error".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no data".equals(result1)) {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(false);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_first_page = problemDigests;
                                                                if (catgorityQuickAdapter == null)
                                                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                                catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                                recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                            } else {
                                                                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                    add(new ProblemDigest());
                                                                }});
                                                                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", major);
                                            send.put("course", course);
                                            send.put("keyword", search_con);
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        refreshLayout1.finishRefresh(1000);
                                                        if ("server error".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no data".equals(result1)) {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(false);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_first_page = problemDigests;
                                                                if (catgorityQuickAdapter == null)
                                                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                                catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                                recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                            } else {
                                                                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                    add(new ProblemDigest());
                                                                }});
                                                                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                    }


                }


            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String search_con = search_label.getText().toString().trim();
                page = 1;
                if (KlbertjCache.cate_ques_first_page != null) {
                    KlbertjCache.cate_ques_first_page.clear();
                    KlbertjCache.cate_ques_first_page = null;
                }

                if (KlbertjCache.cate_ques_second_page != null) {
                    KlbertjCache.cate_ques_second_page.clear();
                    KlbertjCache.cate_ques_second_page = null;
                }
                if (KlbertjCache.cate_ques_third_page != null) {
                    KlbertjCache.cate_ques_third_page.clear();
                    KlbertjCache.cate_ques_third_page = null;
                }
                if (KlbertjCache.cate_ques_forth_page != null) {
                    KlbertjCache.cate_ques_forth_page.clear();
                    KlbertjCache.cate_ques_forth_page = null;
                }


                if ("全部院系".equals(college)) {

                    if ("".equals(search_con)) {
                       ToastUtils.showToast(Objects.requireNonNull(getActivity()),"搜索条件不能为空");

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

                                try {
                                    HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                    OutputStream out1 = urlConnection1.getOutputStream();

                                    JSONObject send = new JSONObject();
                                    send.put("college", "全部院系");
                                    send.put("major", "全部专业");
                                    send.put("course", "全部课程");
                                    send.put("keyword", search_con);
                                    send.put("page", page);
                                    out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                    out1.close();
                                    int responseCode1 = urlConnection1.getResponseCode();
                                    if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                        final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                        Thread.sleep(1000);


                                        Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }

                                                if ("server error".equals(result1)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                } else if ("bp".equals(result1)) {
                                                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                } else if ("no data".equals(result1)) {
                                                    catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                    catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                        add(new ProblemDigest());
                                                    }});
                                                    recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                    refreshLayout.setEnableLoadMore(false);
                                                    refreshLayout.setEnableRefresh(true);
                                                } else {

                                                    JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                            problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                            problemDigest1.labels = j.getString("label");
                                                            problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                            problemDigest1.id = j.getInteger("id");
                                                            problemDigests.add(problemDigest1);
                                                        }

                                                        KlbertjCache.cate_ques_first_page = problemDigests;
                                                        if (catgorityQuickAdapter == null)
                                                            catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                        catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                        recyclerView.setAdapter(catgorityQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(true);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {
                                                        catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                        catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                    dialogFragment = new CircleDialog.Builder()
                            .setProgressText("正在获取数据...")
                            .setCanceledOnTouchOutside(false)
                            .setCancelable(false)
                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                            .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
                    if ("全部专业".equals(major)) {

                        if ("".equals(search_con)) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                        OutputStream out1 = urlConnection1.getOutputStream();

                                        JSONObject send = new JSONObject();
                                        send.put("college", college);
                                        send.put("major", "全部专业");
                                        send.put("course", "全部课程");
                                        send.put("keyword", "");
                                        send.put("page", page);
                                        out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                        out1.close();
                                        int responseCode1 = urlConnection1.getResponseCode();
                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                            Thread.sleep(1000);


                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialogFragment != null) {
                                                        dialogFragment.dismiss();
                                                        dialogFragment = null;
                                                    }

                                                    if ("server error".equals(result1)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                    } else if ("bp".equals(result1)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                    } else if ("no data".equals(result1)) {
                                                        catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                        catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {

                                                        JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                problemDigest1.labels = j.getString("label");
                                                                problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                problemDigest1.id = j.getInteger("id");
                                                                problemDigests.add(problemDigest1);
                                                            }

                                                            KlbertjCache.cate_ques_first_page = problemDigests;
                                                            if (catgorityQuickAdapter == null)
                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                            catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(true);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                        OutputStream out1 = urlConnection1.getOutputStream();

                                        JSONObject send = new JSONObject();
                                        send.put("college", college);
                                        send.put("major", "全部专业");
                                        send.put("course", "全部课程");
                                        send.put("keyword", search_con);
                                        send.put("page", page);
                                        out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                        out1.close();
                                        int responseCode1 = urlConnection1.getResponseCode();
                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                            Thread.sleep(1000);


                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialogFragment != null) {
                                                        dialogFragment.dismiss();
                                                        dialogFragment = null;
                                                    }

                                                    if ("server error".equals(result1)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                    } else if ("bp".equals(result1)) {
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                    } else if ("no data".equals(result1)) {
                                                        catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                        catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else {

                                                        JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                problemDigest1.labels = j.getString("label");
                                                                problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                problemDigest1.id = j.getInteger("id");
                                                                problemDigests.add(problemDigest1);
                                                            }

                                                            KlbertjCache.cate_ques_first_page = problemDigests;
                                                            if (catgorityQuickAdapter == null)
                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                            catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(true);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                        if ("全部课程".equals(course)) {
                            if ("".equals(search_con)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", major);
                                            send.put("course", "全部课程");
                                            send.put("keyword", "");
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dialogFragment != null) {
                                                            dialogFragment.dismiss();
                                                            dialogFragment = null;
                                                        }

                                                        if ("server error".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no data".equals(result1)) {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(false);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_first_page = problemDigests;
                                                                if (catgorityQuickAdapter == null)
                                                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                                catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                                recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                            } else {
                                                                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                    add(new ProblemDigest());
                                                                }});
                                                                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", major);
                                            send.put("course", "全部课程");
                                            send.put("keyword", search_con);
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dialogFragment != null) {
                                                            dialogFragment.dismiss();
                                                            dialogFragment = null;
                                                        }

                                                        if ("server error".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no data".equals(result1)) {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(false);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_first_page = problemDigests;
                                                                if (catgorityQuickAdapter == null)
                                                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                                catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                                recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                            } else {
                                                                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                    add(new ProblemDigest());
                                                                }});
                                                                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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


                            if ("".equals(search_con)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", major);
                                            send.put("course", course);
                                            send.put("keyword", "");
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dialogFragment != null) {
                                                            dialogFragment.dismiss();
                                                            dialogFragment = null;
                                                        }

                                                        if ("server error".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no data".equals(result1)) {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(false);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_first_page = problemDigests;
                                                                if (catgorityQuickAdapter == null)
                                                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                                catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                                recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                            } else {
                                                                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                    add(new ProblemDigest());
                                                                }});
                                                                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", major);
                                            send.put("course", course);
                                            send.put("keyword", search_con);
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (dialogFragment != null) {
                                                            dialogFragment.dismiss();
                                                            dialogFragment = null;
                                                        }

                                                        if ("server error".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no data".equals(result1)) {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(false);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_first_page = problemDigests;
                                                                if (catgorityQuickAdapter == null)
                                                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                                catgorityQuickAdapter.replaceData(KlbertjCache.cate_ques_first_page);
                                                                recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                            } else {
                                                                catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                                catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                    add(new ProblemDigest());
                                                                }});
                                                                recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                    }


                }


            }


        });



























































































































        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout1) {
                final String search_con = search_label.getText().toString().trim();
                page++;

                    if ("全部院系".equals(college)) {

                        if ("".equals(search_con)) {





                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpURLConnection urlConnection = null;
                                    try {
                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.getAllQuestionByPage));
                                        OutputStream out = urlConnection.getOutputStream();

                                        out.write(EncryptionUtils.decryptByByte(((page) + "").getBytes("UTF-8")));
                                        out.close();
                                        int responseCode = urlConnection.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                            Thread.sleep(1000);

                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if ("no".equals(result)) {
                                                        catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                        catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                        refreshLayout.setEnableLoadMore(false);
                                                        refreshLayout.setEnableRefresh(true);
                                                    } else if ("e".equals(result)) {
                                                        catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                        catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                            add(new ProblemDigest());
                                                        }});
                                                        recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
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
                                                                problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                problemDigest1.id = j.getInteger("id");
                                                                problemDigests.add(problemDigest1);
                                                            }


                                                            if (catgorityQuickAdapter == null)
                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();
                                                            catgorityQuickAdapter.addData(problemDigests);
                                                            refreshLayout.setEnableLoadMore(true);
                                                            refreshLayout.setEnableRefresh(true);
                                                        } else {
                                                            catgorityWithNoDataQuickAdapter = new CatgorityWithNoDataQuickAdapter();
                                                            catgorityWithNoDataQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                                add(new ProblemDigest());
                                                            }});
                                                            recyclerView.setAdapter(catgorityWithNoDataQuickAdapter);
                                                            refreshLayout1.setEnableLoadMore(false);
                                                            refreshLayout1.setEnableRefresh(true);
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














//                            if (KlbertjCache.index_ques_second_page != null && !KlbertjCache.index_ques_second_page.isEmpty()) {
////                                if (catgorityQuickAdapter == null) {
////                                    catgorityQuickAdapter = new CatgorityQuickAdapter();
////                                }
//                                catgorityQuickAdapter.addData(KlbertjCache.index_ques_second_page);
////                                recyclerView.setAdapter(catgorityQuickAdapter);
//                                refreshLayout1.finishLoadMore();
//                            } else {
//                                refreshLayout1.finishLoadMoreWithNoMoreData();
//                            }

                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                        OutputStream out1 = urlConnection1.getOutputStream();

                                        JSONObject send = new JSONObject();
                                        send.put("college", "全部院系");
                                        send.put("major", "全部专业");
                                        send.put("course", "全部课程");
                                        send.put("keyword", search_con);
                                        send.put("page", page);
                                        out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                        out1.close();
                                        int responseCode1 = urlConnection1.getResponseCode();
                                        if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                            final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                            Thread.sleep(1000);


                                            Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if ("server error".equals(result1)) {
                                                        refreshLayout1.finishLoadMore();
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                    } else if ("bp".equals(result1)) {
                                                        refreshLayout1.finishLoadMore();
                                                        ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                    } else if ("no more data".equals(result1)) {
                                                        refreshLayout1.finishLoadMoreWithNoMoreData();
                                                    } else {

                                                        JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                problemDigest1.labels = j.getString("label");
                                                                problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                problemDigest1.id = j.getInteger("id");
                                                                problemDigests.add(problemDigest1);
                                                            }

                                                            KlbertjCache.cate_ques_second_page = problemDigests;
//                                                            if (catgorityQuickAdapter == null)
//                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();

                                                            catgorityQuickAdapter.addData(KlbertjCache.cate_ques_second_page);
//                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                            refreshLayout.setEnableLoadMore(true);
                                                            refreshLayout.setEnableRefresh(true);
                                                            refreshLayout1.finishLoadMore();
                                                        } else {
                                                            refreshLayout1.finishLoadMoreWithNoMoreData();
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

                        if ("全部专业".equals(major)) {

                            if ("".equals(search_con)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", "全部专业");
                                            send.put("course", "全部课程");
                                            send.put("keyword", "");
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if ("server error".equals(result1)) {
                                                            refreshLayout1.finishLoadMore();
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            refreshLayout1.finishLoadMore();
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no more data".equals(result1)) {
                                                            refreshLayout1.finishLoadMoreWithNoMoreData();
                                                        }else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_second_page = problemDigests;
//                                                            if (catgorityQuickAdapter == null)
//                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();

                                                                catgorityQuickAdapter.addData(KlbertjCache.cate_ques_second_page);
//                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                                refreshLayout1.finishLoadMore();
                                                            } else {
                                                                refreshLayout1.finishLoadMoreWithNoMoreData();
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
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try {
                                            HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                            OutputStream out1 = urlConnection1.getOutputStream();

                                            JSONObject send = new JSONObject();
                                            send.put("college", college);
                                            send.put("major", "全部专业");
                                            send.put("course", "全部课程");
                                            send.put("keyword", search_con);
                                            send.put("page", page);
                                            out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                            out1.close();
                                            int responseCode1 = urlConnection1.getResponseCode();
                                            if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                Thread.sleep(1000);


                                                Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if ("server error".equals(result1)) {
                                                            refreshLayout1.finishLoadMore();
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result1)) {
                                                            refreshLayout1.finishLoadMore();
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no more data".equals(result1)) {
                                                            refreshLayout1.finishLoadMoreWithNoMoreData();
                                                        }else {

                                                            JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                    problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                    problemDigest1.labels = j.getString("label");
                                                                    problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                    problemDigest1.id = j.getInteger("id");
                                                                    problemDigests.add(problemDigest1);
                                                                }

                                                                KlbertjCache.cate_ques_second_page = problemDigests;
//                                                            if (catgorityQuickAdapter == null)
//                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();

                                                                catgorityQuickAdapter.addData(KlbertjCache.cate_ques_second_page);
//                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                                refreshLayout.setEnableLoadMore(true);
                                                                refreshLayout.setEnableRefresh(true);
                                                                refreshLayout1.finishLoadMore();
                                                            } else {
                                                                refreshLayout1.finishLoadMoreWithNoMoreData();
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
                            if ("全部课程".equals(course)) {
                                if ("".equals(search_con)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                                OutputStream out1 = urlConnection1.getOutputStream();

                                                JSONObject send = new JSONObject();
                                                send.put("college", college);
                                                send.put("major", major);
                                                send.put("course", "全部课程");
                                                send.put("keyword", "");
                                                send.put("page", page);
                                                out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                                out1.close();
                                                int responseCode1 = urlConnection1.getResponseCode();
                                                if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                    final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                    Thread.sleep(1000);


                                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if ("server error".equals(result1)) {
                                                                refreshLayout1.finishLoadMore();
                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                            } else if ("bp".equals(result1)) {
                                                                refreshLayout1.finishLoadMore();
                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                            } else if ("no more data".equals(result1)) {
                                                                refreshLayout1.finishLoadMoreWithNoMoreData();
                                                            }else {

                                                                JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                        problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                        problemDigest1.labels = j.getString("label");
                                                                        problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                        problemDigest1.id = j.getInteger("id");
                                                                        problemDigests.add(problemDigest1);
                                                                    }

                                                                    KlbertjCache.cate_ques_second_page = problemDigests;
//                                                            if (catgorityQuickAdapter == null)
//                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();

                                                                    catgorityQuickAdapter.addData(KlbertjCache.cate_ques_second_page);
//                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                                    refreshLayout.setEnableLoadMore(true);
                                                                    refreshLayout.setEnableRefresh(true);
                                                                    refreshLayout1.finishLoadMore();
                                                                } else {
                                                                    refreshLayout1.finishLoadMoreWithNoMoreData();
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
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                                OutputStream out1 = urlConnection1.getOutputStream();

                                                JSONObject send = new JSONObject();
                                                send.put("college", college);
                                                send.put("major", major);
                                                send.put("course", "全部课程");
                                                send.put("keyword", search_con);
                                                send.put("page", page);
                                                out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                                out1.close();
                                                int responseCode1 = urlConnection1.getResponseCode();
                                                if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                    final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                    Thread.sleep(1000);


                                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if ("server error".equals(result1)) {
                                                                refreshLayout1.finishLoadMore();
                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                            } else if ("bp".equals(result1)) {
                                                                refreshLayout1.finishLoadMore();
                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                            } else if ("no more data".equals(result1)) {
                                                                refreshLayout1.finishLoadMoreWithNoMoreData();
                                                            }else {

                                                                JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                        problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                        problemDigest1.labels = j.getString("label");
                                                                        problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                        problemDigest1.id = j.getInteger("id");
                                                                        problemDigests.add(problemDigest1);
                                                                    }

                                                                    KlbertjCache.cate_ques_second_page = problemDigests;
//                                                            if (catgorityQuickAdapter == null)
//                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();

                                                                    catgorityQuickAdapter.addData(KlbertjCache.cate_ques_second_page);
//                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                                    refreshLayout.setEnableLoadMore(true);
                                                                    refreshLayout.setEnableRefresh(true);
                                                                    refreshLayout1.finishLoadMore();
                                                                } else {
                                                                    refreshLayout1.finishLoadMoreWithNoMoreData();
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


                                if ("".equals(search_con)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                                OutputStream out1 = urlConnection1.getOutputStream();

                                                JSONObject send = new JSONObject();
                                                send.put("college", college);
                                                send.put("major", major);
                                                send.put("course", course);
                                                send.put("keyword", "");
                                                send.put("page", page);
                                                out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                                out1.close();
                                                int responseCode1 = urlConnection1.getResponseCode();
                                                if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                    final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                    Thread.sleep(1000);


                                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if ("server error".equals(result1)) {
                                                                refreshLayout1.finishLoadMore();
                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                            } else if ("bp".equals(result1)) {
                                                                refreshLayout1.finishLoadMore();
                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                            } else if ("no more data".equals(result1)) {
                                                                refreshLayout1.finishLoadMoreWithNoMoreData();
                                                            }else {

                                                                JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                        problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                        problemDigest1.labels = j.getString("label");
                                                                        problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                        problemDigest1.id = j.getInteger("id");
                                                                        problemDigests.add(problemDigest1);
                                                                    }

                                                                    KlbertjCache.cate_ques_second_page = problemDigests;
//                                                            if (catgorityQuickAdapter == null)
//                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();

                                                                    catgorityQuickAdapter.addData(KlbertjCache.cate_ques_second_page);
//                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                                    refreshLayout.setEnableLoadMore(true);
                                                                    refreshLayout.setEnableRefresh(true);
                                                                    refreshLayout1.finishLoadMore();
                                                                } else {
                                                                    refreshLayout1.finishLoadMoreWithNoMoreData();
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
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                HttpURLConnection urlConnection1 = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.multiSearch));
                                                OutputStream out1 = urlConnection1.getOutputStream();

                                                JSONObject send = new JSONObject();
                                                send.put("college", college);
                                                send.put("major", major);
                                                send.put("course", course);
                                                send.put("keyword", search_con);
                                                send.put("page", page);
                                                out1.write(EncryptionUtils.decryptByByte(send.toJSONString().getBytes("UTF-8")));
                                                out1.close();
                                                int responseCode1 = urlConnection1.getResponseCode();
                                                if (responseCode1 == HttpURLConnection.HTTP_OK) {
                                                    final String result1 = EncryptionUtils.inputstream2String(urlConnection1.getInputStream());//将流转换为字符串。

                                                    Thread.sleep(1000);


                                                    Objects.requireNonNull(Objects.requireNonNull(getActivity())).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if ("server error".equals(result1)) {
                                                                refreshLayout1.finishLoadMore();
                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                            } else if ("bp".equals(result1)) {
                                                                refreshLayout1.finishLoadMore();
                                                                ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                            } else if ("no more data".equals(result1)) {
                                                                refreshLayout1.finishLoadMoreWithNoMoreData();
                                                            }else {

                                                                JSONArray jsonArray = JSONArray.parseArray(result1);
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
                                                                        problemDigest1.digest_pic_url = j.getString("digest_pic_url");
                                                                        problemDigest1.labels = j.getString("label");
                                                                        problemDigest1.belt_uid = j.getInteger("belt_uid");
                                                                        problemDigest1.id = j.getInteger("id");
                                                                        problemDigests.add(problemDigest1);
                                                                    }

                                                                    KlbertjCache.cate_ques_second_page = problemDigests;
//                                                            if (catgorityQuickAdapter == null)
//                                                                catgorityQuickAdapter = new CatgorityQuickAdapter();

                                                                    catgorityQuickAdapter.addData(KlbertjCache.cate_ques_second_page);
//                                                            recyclerView.setAdapter(catgorityQuickAdapter);
                                                                    refreshLayout.setEnableLoadMore(true);
                                                                    refreshLayout.setEnableRefresh(true);
                                                                    refreshLayout1.finishLoadMore();
                                                                } else {
                                                                    refreshLayout1.finishLoadMoreWithNoMoreData();
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
                        }


                    }




            }
        });
    }


    @Override
    public void onThreeSingleChanged(DropdownItemObject dropdownItemObject) {
        //1
        Log.i("onThreeSingleChanged", dropdownItemObject.value);
        college = dropdownItemObject.value;
    }

    @Override
    public void onThreeDoubleChanged(DropdownItemObject dropdownItemObject) {
        //2
        Log.i("onThreeDoubleChanged", dropdownItemObject.value);
        major = dropdownItemObject.value;
    }

    @Override
    public void onThreeChanged(DropdownItemObject dropdownItemObject) {
        //3
        Log.i("onThreeChanged", dropdownItemObject.value);
        course = dropdownItemObject.value;
    }

    public List<DropdownItemObject> getCollege() {
        List<DropdownItemObject> regionProvinceList = new ArrayList<>();//院系
        DropdownItemObject regionProvinceObj = new DropdownItemObject(-1, "全部院系", "全部院系");
        regionProvinceList.add(regionProvinceObj);
        int count = Cons.college_name.length;
        for (int i = 0; i < count; i++) {
            regionProvinceObj = new DropdownItemObject(i + 1, Cons.college_name[i], Cons.college_name[i]);
            regionProvinceList.add(regionProvinceObj);
        }
        return regionProvinceList;
    }

    public List<DropdownItemObject> getMajor() {
        List<DropdownItemObject> regionCityList = new ArrayList<>();//所有专业
        DropdownItemObject regionCityObj = new DropdownItemObject(-1, -1, "全部专业", "全部专业");
        regionCityList.add(regionCityObj);


        int count = Cons.college_name.length;
        JSONObject jsonObject = JSONObject.parseObject(Cons.college_major);
        JSONArray jsonArray = null;
        for (int i = 0; i < count; i++) {
            regionCityObj = new DropdownItemObject(i + 1, -1, "全部专业", "全部专业");
            regionCityList.add(regionCityObj);
            jsonArray = jsonObject.getJSONArray(Cons.college_name[i]);
            for (int j = 0; j < jsonArray.size(); j++) {
                regionCityObj = new DropdownItemObject(i + 1, j + 1, jsonArray.getString(j), jsonArray.getString(j));
                regionCityList.add(regionCityObj);
            }
        }
        return regionCityList;
    }

    /**
     * 获取地区区数据
     *
     * @return itemType
     */
    public List<DropdownItemObject> getCourse() {
        List<DropdownItemObject> regionAreaList = new ArrayList<>();//所有课程
        DropdownItemObject regionAreaObj = new DropdownItemObject(-1, -1, -1, "全部课程", "全部课程");
        regionAreaList.add(regionAreaObj);

        int count = Cons.college_name.length;
        JSONObject jsonObject = JSONObject.parseObject(Cons.college_major);
        JSONArray jsonArray = null;
        for (int i = 0; i < count; i++) {
            regionAreaObj = new DropdownItemObject(i + 1, -1, -1, "全部课程", "全部课程");
            regionAreaList.add(regionAreaObj);
            jsonArray = jsonObject.getJSONArray(Cons.college_name[i]);
            for (int j = 0; j < jsonArray.size(); j++) {
                regionAreaObj = new DropdownItemObject(i + 1, j + 1, -1, "全部课程", "全部课程");
                regionAreaList.add(regionAreaObj);
                String major = jsonArray.getString(j);
                if ("计算机科学与技术".equals(major)) {
                    JSONArray jsonArray2 = JSONArray.parseArray(Cons.engineercourse);
                    for (int k = 0; k < jsonArray2.size(); k++) {
                        regionAreaObj = new DropdownItemObject(i + 1, j + 1, k + 1, jsonArray2.getString(k), jsonArray2.getString(k));
                        regionAreaList.add(regionAreaObj);
                    }
                }
            }
        }

        return regionAreaList;
    }


    private class CatgorityQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {

        TextView label;
        MyCircleView myCircleView;
        ImageView diges_pic;

        CatgorityQuickAdapter() {
            super(R.layout.problem_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {
            helper.setText(R.id.title, item.pro_title)
                    .setText(R.id.uname, item.belongToUser)
                    .setText(R.id.time, item.time)
                    .setText(R.id.digest, item.pro_digest)
                    .setText(R.id.belt_course, item.q_belt_subject)
                    .setText(R.id.num_of_dis, item.num_of_discussion);

            label = helper.getView(R.id.label);
            myCircleView = helper.getView(R.id.avatar);
            diges_pic = helper.getView(R.id.diges_pic);
            JSONArray labels = JSONArray.parseArray(item.labels);

            StringBuilder la = new StringBuilder();
            int i = 0;
            for (; i < labels.size() - 1; i++) {
                la.append(labels.getString(i)).append("/");
            }
            la.append(labels.getString(i));
            label.setText(la.toString());
            Glide.with(Objects.requireNonNull(getActivity())).load(Cons.PICS_BASE_URL + Cons.QUES_PICS_PATH_IN_SERVER + "/" + item.digest_pic_url)
                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.DEFAULT)).into(diges_pic);

            Glide.with(Objects.requireNonNull(getActivity())).asBitmap().load(item.belongToUserHead_url)
                    .apply(MyGlide.getRequestOptions().format(DecodeFormat.PREFER_RGB_565)).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                    myCircleView.setBitmap(bitmap);
                }
            });


            View delete_indecator = helper.getView(R.id.delete_indecator);
            TextView delete = helper.getView(R.id.delete);
            View cancel_collect_indecator = helper.getView(R.id.cancel_collect_indecator);
            TextView cancel_collect = helper.getView(R.id.cancel_collect);
            delete_indecator.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            cancel_collect_indecator.setVisibility(View.GONE);
            cancel_collect.setVisibility(View.GONE);
//            Glide.with(mContext).load(item.picaddr).into((ImageView) helper.getView(R.id.lmi_avatar));
        }
    }

    private class CatgorityWithNoDataQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {


        CatgorityWithNoDataQuickAdapter() {
            super(R.layout.index_with_nodata);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {

        }
    }


}
