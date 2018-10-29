package com.zzu.zk.zhiwen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mylhyl.circledialog.CircleDialog;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.activity.MyAsk;
import com.zzu.zk.zhiwen.beans.ProblemDigest;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.BadgeFactory;
import com.zzu.zk.zhiwen.customed_ui.BadgeView;
import com.zzu.zk.zhiwen.utils.PushHandler;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.StatusBarUtil;
import com.zzu.zk.zhiwen.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.internal.ListenerClass;

public class Notify extends Fragment {
    RecyclerView recyclerView;
    View clear;
    BadgeView badgeView1, badgeView2, badgeView3, badgeView4, badgeView5, badgeView6, badgeView7;
    TextView c1, c2, c3, c4, c5, c6, c7;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notify, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        clear = view.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CircleDialog.Builder()
                        .setTitle("提醒")
                        .setText("你确定清空通知吗？")
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                KlbertjCache.event_num[0] = 0;
                                if (badgeView1 != null)
                                    badgeView1.removebindView();
                                KlbertjCache.event_num[1] = 0;
                                if (badgeView3 != null)
                                    badgeView3.removebindView();
                                KlbertjCache.event_num[2] = 0;
                                if (badgeView4 != null)
                                    badgeView4.removebindView();
                                KlbertjCache.event_num[3] = 0;
                                if (badgeView2 != null)
                                    badgeView2.removebindView();
                                KlbertjCache.event_num[4] = 0;
                                if (badgeView6 != null)
                                    badgeView6.removebindView();

                                KlbertjCache.event_num[5] = 0;
                                if (badgeView5 != null)
                                    badgeView5.removebindView();
                                KlbertjCache.event_num[6] = 0;
                                if (badgeView7 != null)
                                    badgeView7.removebindView();
                                c1.setText("暂时没有新的通知");
                                c2.setText("暂时没有新的通知");
                                c3.setText("暂时没有新的通知");
                                c4.setText("暂时没有新的通知");
                                c5.setText("暂时没有新的通知");
                                c6.setText("暂时没有新的通知");
                                c7.setText("暂时没有新的通知");

                                SharePreferenceUtils.putString(getActivity(), Cons.NEW_PRO, "");
                                SharePreferenceUtils.putString(getActivity(), Cons.NEW_REPLY, "");
                                SharePreferenceUtils.putString(getActivity(), Cons.HAS_ATT, "");
                                SharePreferenceUtils.putString(getActivity(), Cons.HAS_APPLY, "");
                                SharePreferenceUtils.putString(getActivity(), Cons.CANCEL_ATT, "");
                                SharePreferenceUtils.putString(getActivity(), Cons.COLL, "");
                                SharePreferenceUtils.putString(getActivity(), Cons.CANCLE_COLL, "");

                                KlbertjCache.notification2.get("新的问题").clear();
                                KlbertjCache.notification2.get("新的回复").clear();
                                KlbertjCache.notification2.get("有人关注").clear();
                                KlbertjCache.notification2.get("你被采纳").clear();
                                KlbertjCache.notification2.get("取消关注").clear();
                                KlbertjCache.notification2.get("收藏问题").clear();
                                KlbertjCache.notification2.get("取消收藏问题").clear();
                            }
                        })
                        .setNegative("取消", null)
                        .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getActivity())));
        NotifyQuickAdapter notifyQuickAdapter = new NotifyQuickAdapter();
        PushHandler.setOnDataReceiveListener(notifyQuickAdapter, getActivity());
        notifyQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
            add(new ProblemDigest());
        }});
        recyclerView.setAdapter(notifyQuickAdapter);
        StatusBarUtil.immersive(Objects.requireNonNull(getActivity()));
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(getActivity()), recyclerView);
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(getActivity()), view.findViewById(R.id.container));
        StatusBarUtil.setPaddingSmart(Objects.requireNonNull(getActivity()), view.findViewById(R.id.blurView));
    }


    @Override
    public void onPause() {
        super.onPause();
        PushHandler.setOnDataReceiveListener(null, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        PushHandler.setOnDataReceiveListener(null, null);
    }

    public class NotifyQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> implements PushHandler.OnDataReceiveListener {
        LinearLayout no1, no2, no3, no4, no5, no6, no7;


        public NotifyQuickAdapter() {
            super(R.layout.notify_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {
            no1 = helper.getView(R.id.no1);
            no2 = helper.getView(R.id.no2);
            no3 = helper.getView(R.id.no3);
            no4 = helper.getView(R.id.no4);

            no5 = helper.getView(R.id.no5);
            no6 = helper.getView(R.id.no6);
            no7 = helper.getView(R.id.no7);

            c1 = helper.getView(R.id.c1);
            c2 = helper.getView(R.id.c2);
            c3 = helper.getView(R.id.c3);
            c4 = helper.getView(R.id.c4);

            c5 = helper.getView(R.id.c5);
            c6 = helper.getView(R.id.c6);
            c7 = helper.getView(R.id.c7);
            no1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KlbertjCache.event_num[0] = 0;
                    if (badgeView1 != null)
                        badgeView1.removebindView();
                    if (KlbertjCache.notification2.get("新的问题").size() > 0) {
                        Intent i = new Intent(Objects.requireNonNull(Notify.this.getActivity()), MyAsk.class);
                        i.putExtra("title", "新的问题");
                        i.putExtra("itemType", 8);
                        i.putExtra("bargen", KlbertjCache.notification2.get("新的问题").size() + "条");

                        startActivity(i);
                    } else {
                        ToastUtils.showToast(Notify.this.getActivity(), "没有通知");
                    }

                }
            });
            no2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KlbertjCache.event_num[1] = 0;
                    if (badgeView3 != null)
                        badgeView3.removebindView();
                    if (KlbertjCache.notification2.get("新的回复").size() > 0) {
                        Intent i = new Intent(Objects.requireNonNull(Notify.this.getActivity()), MyAsk.class);
                        i.putExtra("title", "新的回复");
                        i.putExtra("itemType", 9);
                        i.putExtra("bargen", KlbertjCache.notification2.get("新的回复").size() + "条");

                        startActivity(i);
                    } else {
                        ToastUtils.showToast(Notify.this.getActivity(), "没有通知");
                    }
                }
            });
            no3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KlbertjCache.event_num[2] = 0;
                    if (badgeView4 != null)
                        badgeView4.removebindView();

                    if (KlbertjCache.notification2.get("有人关注").size() > 0) {
                        Intent i = new Intent(Objects.requireNonNull(Notify.this.getActivity()), MyAsk.class);
                        i.putExtra("title", "有人关注");
                        i.putExtra("itemType", 10);
                        i.putExtra("bargen", KlbertjCache.notification2.get("有人关注").size() + "条");

                        startActivity(i);
                    } else {
                        ToastUtils.showToast(Notify.this.getActivity(), "没有通知");
                    }
                }
            });
            no4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KlbertjCache.event_num[3] = 0;
                    if (badgeView2 != null)
                        badgeView2.removebindView();

                    if (KlbertjCache.notification2.get("你被采纳").size() > 0) {
                        Intent i = new Intent(Objects.requireNonNull(Notify.this.getActivity()), MyAsk.class);
                        i.putExtra("title", "你被采纳");
                        i.putExtra("itemType", 11);
                        i.putExtra("bargen", KlbertjCache.notification2.get("你被采纳").size() + "条");

                        startActivity(i);
                    } else {
                        ToastUtils.showToast(Notify.this.getActivity(), "没有通知");
                    }
                }
            });
            no5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KlbertjCache.event_num[4] = 0;
                    if (badgeView6 != null)
                        badgeView6.removebindView();
                    if (KlbertjCache.notification2.get("收藏问题").size() > 0) {
                        Intent i = new Intent(Objects.requireNonNull(Notify.this.getActivity()), MyAsk.class);
                        i.putExtra("title", "收藏问题");
                        i.putExtra("itemType", 12);
                        i.putExtra("bargen", KlbertjCache.notification2.get("收藏问题").size() + "条");

                        startActivity(i);
                    } else {
                        ToastUtils.showToast(Notify.this.getActivity(), "没有通知");
                    }
                }
            });
            no6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KlbertjCache.event_num[5] = 0;
                    if (badgeView5 != null)
                        badgeView5.removebindView();


                    if (KlbertjCache.notification2.get("取消关注").size() > 0) {
                        Intent i = new Intent(Objects.requireNonNull(Notify.this.getActivity()), MyAsk.class);
                        i.putExtra("title", "取消关注");
                        i.putExtra("itemType", 13);
                        i.putExtra("bargen", KlbertjCache.notification2.get("取消关注").size() + "条");

                        startActivity(i);
                    } else {
                        ToastUtils.showToast(Notify.this.getActivity(), "没有通知");
                    }
                }
            });
            no7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KlbertjCache.event_num[6] = 0;
                    if (badgeView7 != null)
                        badgeView7.removebindView();

                    if (KlbertjCache.notification2.get("取消收藏问题").size() > 0) {
                        Intent i = new Intent(Objects.requireNonNull(Notify.this.getActivity()), MyAsk.class);
                        i.putExtra("title", "取消收藏问题");
                        i.putExtra("itemType", 14);
                        i.putExtra("bargen", KlbertjCache.notification2.get("取消收藏问题").size() + "条");

                        startActivity(i);
                    } else {
                        ToastUtils.showToast(Notify.this.getActivity(), "没有通知");
                    }
                }
            });
            if (!KlbertjCache.notification.isEmpty()) {
//                if (KlbertjCache.notification.get("新的问题").size() > 0) {
//                     badgeView1 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                    badgeView1.setBadgeText(String.valueOf(KlbertjCache.notification.get("新的问题").size()));
//                    badgeView1.setBindView(no1);
//
//                    c1.setText(KlbertjCache.notification.get("新的问题").get(KlbertjCache.notification.get("新的问题").size() - 1));
//                    KlbertjCache.notification2.get("新的问题").addAll(KlbertjCache.notification.get("新的问题"));
//
//                    KlbertjCache.notification.get("新的问题").clear();
//                } else {
//                    if (!KlbertjCache.notification2.get("新的问题").isEmpty())
//                        c1.setText(KlbertjCache.notification2.get("新的问题").get(KlbertjCache.notification2.get("新的问题").size() - 1));
//
//                    else
//                        c1.setText("暂时没有新的通知");
//                    if(KlbertjCache.event_num[0]!=0){
//                        if(badgeView1==null)
//                            badgeView1 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                        badgeView1.setBadgeText(String.valueOf(KlbertjCache.event_num[0]));
//                        badgeView1.setBindView(no1);
//
//                    }
//
//                }
//                if (KlbertjCache.notification.get("你被采纳").size() > 0) {
//                    badgeView2 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                    badgeView2.setBadgeText(String.valueOf(KlbertjCache.notification.get("你被采纳").size()));
//                    badgeView2.setBindView(no4);
//                    KlbertjCache.notification2.get("你被采纳").addAll(KlbertjCache.notification.get("你被采纳"));
//                    c4.setText(KlbertjCache.notification.get("你被采纳").get(KlbertjCache.notification.get("你被采纳").size() - 1));
//                    KlbertjCache.notification.get("你被采纳").clear();
//                } else {
//                    if (!KlbertjCache.notification2.get("你被采纳").isEmpty())
//                    c4.setText(KlbertjCache.notification2.get("你被采纳").get(KlbertjCache.notification2.get("你被采纳").size() - 1));
//                    else
//                        c4.setText("暂时没有新的通知");
//
//                    if(KlbertjCache.event_num[3]!=0){
//                        if(badgeView2==null)
//                            badgeView2 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                        badgeView2.setBadgeText(String.valueOf(KlbertjCache.event_num[3]));
//                        badgeView2.setBindView(no4);
//
//                    }
//                }
//                if (KlbertjCache.notification.get("新的回复").size() > 0) {
//                    badgeView3 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                    badgeView3.setBadgeText(String.valueOf(KlbertjCache.notification.get("新的回复").size()));
//                    badgeView3.setBindView(no2);
//                    Log.i("cdsf", KlbertjCache.notification.get("新的回复").toString());
//                    c2.setText(KlbertjCache.notification.get("新的回复").get(KlbertjCache.notification.get("新的回复").size() - 1));
//                    KlbertjCache.notification2.get("新的回复").addAll(KlbertjCache.notification.get("新的回复"));
//
//                    KlbertjCache.notification.get("新的回复").clear();
//
//                } else {
//                    if (!KlbertjCache.notification2.get("新的回复").isEmpty())
//                    c2.setText(KlbertjCache.notification2.get("新的回复").get(KlbertjCache.notification2.get("新的回复").size() - 1));
//                    else
//                        c2.setText("暂时没有新的通知");
//                    if(KlbertjCache.event_num[1]!=0){
//                        if(badgeView3==null)
//                            badgeView3 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                        badgeView3.setBadgeText(String.valueOf(KlbertjCache.event_num[1]));
//                        badgeView3.setBindView(no2);
//
//                    }
//                }
//                if (KlbertjCache.notification.get("有人关注").size() > 0) {
//                    badgeView4 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                    badgeView4.setBadgeText(String.valueOf(KlbertjCache.notification.get("有人关注").size()));
//                    badgeView4.setBindView(no3);
//                    KlbertjCache.notification2.get("有人关注").addAll(KlbertjCache.notification.get("有人关注"));
//                    c3.setText(KlbertjCache.notification.get("有人关注").get(KlbertjCache.notification.get("有人关注").size() - 1));
//                    KlbertjCache.notification.get("有人关注").clear();
//                } else {
//                    if (!KlbertjCache.notification2.get("有人关注").isEmpty())
//                    c3.setText(KlbertjCache.notification2.get("有人关注").get(KlbertjCache.notification2.get("有人关注").size() - 1));
//                    else
//                        c3.setText("暂时没有新的通知");
//
//                    if(KlbertjCache.event_num[2]!=0){
//                        if(badgeView4==null)
//                            badgeView4 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                        badgeView4.setBadgeText(String.valueOf(KlbertjCache.event_num[2]));
//                        badgeView4.setBindView(no3);
//
//                    }
//                }
//
//
//                if (KlbertjCache.notification.get("取消关注").size() > 0) {
//                    badgeView5 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                    badgeView5.setBadgeText(String.valueOf(KlbertjCache.notification.get("取消关注").size()));
//                    badgeView5.setBindView(no6);
//                    KlbertjCache.notification2.get("取消关注").addAll(KlbertjCache.notification.get("取消关注"));
//                    c6.setText(KlbertjCache.notification.get("取消关注").get(KlbertjCache.notification.get("取消关注").size() - 1));
//                    KlbertjCache.notification.get("取消关注").clear();
//                } else {
//                    if (!KlbertjCache.notification2.get("取消关注").isEmpty())
//                    c6.setText(KlbertjCache.notification2.get("取消关注").get(KlbertjCache.notification2.get("取消关注").size() - 1));
//                    else
//                        c6.setText("暂时没有新的通知");
//
//                    if(KlbertjCache.event_num[5]!=0){
//                        if(badgeView5==null)
//                            badgeView5 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                        badgeView5.setBadgeText(String.valueOf(KlbertjCache.event_num[5]));
//                        badgeView5.setBindView(no6);
//
//                    }
//                }
//                if (KlbertjCache.notification.get("收藏问题").size() > 0) {
//                    badgeView6 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                    badgeView6.setBadgeText(String.valueOf(KlbertjCache.notification.get("收藏问题").size()));
//                    badgeView6.setBindView(no5);
//                    KlbertjCache.notification2.get("收藏问题").addAll(KlbertjCache.notification.get("收藏问题"));
//                    c5.setText(KlbertjCache.notification.get("收藏问题").get(KlbertjCache.notification.get("收藏问题").size() - 1));
//                    KlbertjCache.notification.get("收藏问题").clear();
//
//                } else {
//                    if (!KlbertjCache.notification2.get("收藏问题").isEmpty())
//                    c5.setText(KlbertjCache.notification2.get("收藏问题").get(KlbertjCache.notification2.get("收藏问题").size() - 1));
//                    else
//                        c5.setText("暂时没有新的通知");
//                    if(KlbertjCache.event_num[4]!=0){
//                        if(badgeView6==null)
//                            badgeView6 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                        badgeView6.setBadgeText(String.valueOf(KlbertjCache.event_num[4]));
//                        badgeView6.setBindView(no5);
//
//                    }
//                }
//                if (KlbertjCache.notification.get("取消收藏问题").size() > 0) {
//                    badgeView7= BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                    badgeView7.setBadgeText(String.valueOf(KlbertjCache.notification.get("取消收藏问题").size()));
//                    badgeView7.setBindView(no7);
//                    c7.setText(KlbertjCache.notification.get("取消收藏问题").get(KlbertjCache.notification.get("取消收藏问题").size() - 1));
//                    KlbertjCache.notification2.get("取消收藏问题").addAll(KlbertjCache.notification.get("取消收藏问题"));
//                    KlbertjCache.notification.get("取消收藏问题").clear();
//                } else {
//                    if (!KlbertjCache.notification2.get("取消收藏问题").isEmpty())
//                    c7.setText(KlbertjCache.notification2.get("取消收藏问题").get(KlbertjCache.notification2.get("取消收藏问题").size() - 1));
//                    else
//                        c7.setText("暂时没有新的通知");
//
//                    if(KlbertjCache.event_num[6]!=0){
//                        if(badgeView7==null)
//                            badgeView7 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
//                        badgeView7.setBadgeText(String.valueOf(KlbertjCache.event_num[6]));
//                        badgeView7.setBindView(no7);
//
//                    }
//                }


                JSONObject noti = null;
                if (KlbertjCache.notification.get("新的问题").size() > 0) {
                    badgeView1 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[0] += KlbertjCache.notification.get("新的问题").size();
                    badgeView1.setBadgeText(String.valueOf(KlbertjCache.event_num[0]));

                    badgeView1.setBindView(no1);
                    noti = JSONObject.parseObject(KlbertjCache.notification.get("新的问题").get(KlbertjCache.notification.get("新的问题").size() - 1));
                    c1.setText(noti.getString("con"));
                    KlbertjCache.notification2.get("新的问题").addAll(KlbertjCache.notification.get("新的问题"));

                    SharePreferenceUtils.putString(getActivity(), Cons.NEW_PRO, JSONArray.toJSONString(KlbertjCache.notification2.get("新的问题")));
                    KlbertjCache.notification.get("新的问题").clear();
                } else {
                    if (!KlbertjCache.notification2.get("新的问题").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("新的问题").get(KlbertjCache.notification2.get("新的问题").size() - 1));
                        c1.setText(noti.getString("con"));

                    } else
                        c1.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("你被采纳").size() > 0) {
                    badgeView2 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[3] += KlbertjCache.notification.get("你被采纳").size();
                    badgeView2.setBadgeText(String.valueOf(KlbertjCache.event_num[3]));
                    badgeView2.setBindView(no4);


                    noti = JSONObject.parseObject(KlbertjCache.notification.get("你被采纳").get(KlbertjCache.notification.get("你被采纳").size() - 1));
                    c4.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("你被采纳").addAll(KlbertjCache.notification.get("你被采纳"));
                    SharePreferenceUtils.putString(getActivity(), Cons.HAS_APPLY, JSONArray.toJSONString(KlbertjCache.notification2.get("你被采纳")));
                    KlbertjCache.notification.get("你被采纳").clear();
                } else {
                    if (!KlbertjCache.notification2.get("你被采纳").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("你被采纳").get(KlbertjCache.notification2.get("你被采纳").size() - 1));
                        c4.setText(noti.getString("con"));

                    } else
                        c4.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("新的回复").size() > 0) {
                    badgeView3 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[1] += KlbertjCache.notification.get("新的回复").size();
                    badgeView3.setBadgeText(String.valueOf(KlbertjCache.event_num[1]));
                    badgeView3.setBindView(no2);


                    noti = JSONObject.parseObject(KlbertjCache.notification.get("新的回复").get(KlbertjCache.notification.get("新的回复").size() - 1));
                    c2.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("新的回复").addAll(KlbertjCache.notification.get("新的回复"));
                    SharePreferenceUtils.putString(getActivity(), Cons.NEW_REPLY, JSONArray.toJSONString(KlbertjCache.notification2.get("新的回复")));
                    KlbertjCache.notification.get("新的回复").clear();

                } else {
                    if (!KlbertjCache.notification2.get("新的回复").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("新的回复").get(KlbertjCache.notification2.get("新的回复").size() - 1));
                        c2.setText(noti.getString("con"));

                    } else
                        c2.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("有人关注").size() > 0) {
                    badgeView4 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[2] += KlbertjCache.notification.get("有人关注").size();
                    badgeView4.setBadgeText(String.valueOf(KlbertjCache.event_num[2]));
                    badgeView4.setBindView(no3);


                    noti = JSONObject.parseObject(KlbertjCache.notification.get("有人关注").get(KlbertjCache.notification.get("有人关注").size() - 1));
                    c3.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("有人关注").addAll(KlbertjCache.notification.get("有人关注"));
                    SharePreferenceUtils.putString(getActivity(), Cons.HAS_ATT, JSONArray.toJSONString(KlbertjCache.notification2.get("有人关注")));
                    KlbertjCache.notification.get("有人关注").clear();
                } else {
                    if (!KlbertjCache.notification2.get("有人关注").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("有人关注").get(KlbertjCache.notification2.get("有人关注").size() - 1));
                        c3.setText(noti.getString("con"));
                    } else
                        c3.setText("暂时没有新的通知");
                }


                if (KlbertjCache.notification.get("取消关注").size() > 0) {
                    badgeView5 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[5] += KlbertjCache.notification.get("取消关注").size();
                    badgeView5.setBadgeText(String.valueOf(KlbertjCache.event_num[5]));
                    badgeView5.setBindView(no6);

                    noti = JSONObject.parseObject(KlbertjCache.notification.get("取消关注").get(KlbertjCache.notification.get("取消关注").size() - 1));
                    c6.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("取消关注").addAll(KlbertjCache.notification.get("取消关注"));
                    SharePreferenceUtils.putString(getActivity(), Cons.CANCEL_ATT, JSONArray.toJSONString(KlbertjCache.notification2.get("取消关注")));
                    KlbertjCache.notification.get("取消关注").clear();
                } else {
                    if (!KlbertjCache.notification2.get("取消关注").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("取消关注").get(KlbertjCache.notification2.get("取消关注").size() - 1));
                        c6.setText(noti.getString("con"));
                    } else
                        c6.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("收藏问题").size() > 0) {
                    badgeView6 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[4] += KlbertjCache.notification.get("收藏问题").size();
                    badgeView6.setBadgeText(String.valueOf(KlbertjCache.event_num[4]));
                    badgeView6.setBindView(no5);


                    noti = JSONObject.parseObject(KlbertjCache.notification.get("收藏问题").get(KlbertjCache.notification.get("收藏问题").size() - 1));
                    c5.setText(noti.getString("con"));


                    KlbertjCache.notification2.get("收藏问题").addAll(KlbertjCache.notification.get("收藏问题"));
                    SharePreferenceUtils.putString(getActivity(), Cons.COLL, JSONArray.toJSONString(KlbertjCache.notification2.get("收藏问题")));
                    KlbertjCache.notification.get("收藏问题").clear();

                } else {
                    if (!KlbertjCache.notification2.get("收藏问题").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("收藏问题").get(KlbertjCache.notification2.get("收藏问题").size() - 1));
                        c5.setText(noti.getString("con"));
                    } else
                        c5.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("取消收藏问题").size() > 0) {
                    badgeView7 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[6] += KlbertjCache.notification.get("取消收藏问题").size();
                    badgeView7.setBadgeText(String.valueOf(KlbertjCache.event_num[6]));
                    badgeView7.setBindView(no7);

                    noti = JSONObject.parseObject(KlbertjCache.notification.get("取消收藏问题").get(KlbertjCache.notification.get("取消收藏问题").size() - 1));
                    c7.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("取消收藏问题").addAll(KlbertjCache.notification.get("取消收藏问题"));
                    SharePreferenceUtils.putString(getActivity(), Cons.CANCLE_COLL, JSONArray.toJSONString(KlbertjCache.notification2.get("取消收藏问题")));
                    KlbertjCache.notification.get("取消收藏问题").clear();
                } else {
                    if (!KlbertjCache.notification2.get("取消收藏问题").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("取消收藏问题").get(KlbertjCache.notification2.get("取消收藏问题").size() - 1));
                        c7.setText(noti.getString("con"));
                    } else
                        c7.setText("暂时没有新的通知");
                }

            }


        }

        @Override
        public void OnDataReceive(String type, String con) {
            List<String> list = null;
            String title = "";
            switch (type) {

                case "new_question":
                    title = "新的问题";
                    list = KlbertjCache.notification.get("新的问题");
                    break;
                case "reply":
                    title = "新的回复";
                    list = KlbertjCache.notification.get("新的回复");
                    break;
                case "atten":
                    title = "有人关注";
                    list = KlbertjCache.notification.get("有人关注");
                    break;
                case "apply":
                    title = "你被采纳";
                    list = KlbertjCache.notification.get("你被采纳");
                    break;
                case "unatten":
                    title = "取消关注";
                    list = KlbertjCache.notification.get("取消关注");
                    break;
                case "coll":
                    title = "收藏问题";
                    list = KlbertjCache.notification.get("收藏问题");
                    break;
                case "uncoll":
                    title = "取消收藏问题";
                    list = KlbertjCache.notification.get("取消收藏问题");
                    break;
            }
            Objects.requireNonNull(list).add(con);
            KlbertjCache.notification.put(title, list);
            if (!KlbertjCache.notification.isEmpty()) {
                JSONObject noti = null;
                if (KlbertjCache.notification.get("新的问题").size() > 0) {
                    badgeView1 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[0] += KlbertjCache.notification.get("新的问题").size();
                    badgeView1.setBadgeText(String.valueOf(KlbertjCache.event_num[0]));

                    badgeView1.setBindView(no1);

                    noti = JSONObject.parseObject(KlbertjCache.notification.get("新的问题").get(KlbertjCache.notification.get("新的问题").size() - 1));
                    c1.setText(noti.getString("con"));
                    KlbertjCache.notification2.get("新的问题").addAll(KlbertjCache.notification.get("新的问题"));

                    SharePreferenceUtils.putString(getActivity(), Cons.NEW_PRO, JSONArray.toJSONString(KlbertjCache.notification2.get("新的问题")));
                    KlbertjCache.notification.get("新的问题").clear();
                } else {
                    if (!KlbertjCache.notification2.get("新的问题").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("新的问题").get(KlbertjCache.notification2.get("新的问题").size() - 1));
                        c1.setText(noti.getString("con"));

                    } else
                        c1.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("你被采纳").size() > 0) {
                    badgeView2 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[3] += KlbertjCache.notification.get("你被采纳").size();
                    badgeView2.setBadgeText(String.valueOf(KlbertjCache.event_num[3]));
                    badgeView2.setBindView(no4);


                    noti = JSONObject.parseObject(KlbertjCache.notification.get("你被采纳").get(KlbertjCache.notification.get("你被采纳").size() - 1));
                    c4.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("你被采纳").addAll(KlbertjCache.notification.get("你被采纳"));
                    SharePreferenceUtils.putString(getActivity(), Cons.HAS_APPLY, JSONArray.toJSONString(KlbertjCache.notification2.get("你被采纳")));
                    KlbertjCache.notification.get("你被采纳").clear();
                } else {
                    if (!KlbertjCache.notification2.get("你被采纳").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("你被采纳").get(KlbertjCache.notification2.get("你被采纳").size() - 1));
                        c4.setText(noti.getString("con"));

                    } else
                        c4.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("新的回复").size() > 0) {
                    badgeView3 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[1] += KlbertjCache.notification.get("新的回复").size();
                    badgeView3.setBadgeText(String.valueOf(KlbertjCache.event_num[1]));
                    badgeView3.setBindView(no2);

Log.i("fdsfdsf",KlbertjCache.notification.get("新的回复").get(KlbertjCache.notification.get("新的回复").size() - 1));
                    noti = JSONObject.parseObject(KlbertjCache.notification.get("新的回复").get(KlbertjCache.notification.get("新的回复").size() - 1));
                    c2.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("新的回复").addAll(KlbertjCache.notification.get("新的回复"));
                    SharePreferenceUtils.putString(getActivity(), Cons.NEW_REPLY, JSONArray.toJSONString(KlbertjCache.notification2.get("新的回复")));
                    KlbertjCache.notification.get("新的回复").clear();

                } else {
                    if (!KlbertjCache.notification2.get("新的回复").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("新的回复").get(KlbertjCache.notification2.get("新的回复").size() - 1));
                        c2.setText(noti.getString("con"));

                    } else
                        c2.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("有人关注").size() > 0) {
                    badgeView4 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[2] += KlbertjCache.notification.get("有人关注").size();
                    badgeView4.setBadgeText(String.valueOf(KlbertjCache.event_num[2]));
                    badgeView4.setBindView(no3);


                    noti = JSONObject.parseObject(KlbertjCache.notification.get("有人关注").get(KlbertjCache.notification.get("有人关注").size() - 1));
                    c3.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("有人关注").addAll(KlbertjCache.notification.get("有人关注"));
                    SharePreferenceUtils.putString(getActivity(), Cons.HAS_ATT, JSONArray.toJSONString(KlbertjCache.notification2.get("有人关注")));
                    KlbertjCache.notification.get("有人关注").clear();
                } else {
                    if (!KlbertjCache.notification2.get("有人关注").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("有人关注").get(KlbertjCache.notification2.get("有人关注").size() - 1));
                        c3.setText(noti.getString("con"));
                    } else
                        c3.setText("暂时没有新的通知");
                }


                if (KlbertjCache.notification.get("取消关注").size() > 0) {
                    badgeView5 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[5] += KlbertjCache.notification.get("取消关注").size();
                    badgeView5.setBadgeText(String.valueOf(KlbertjCache.event_num[5]));
                    badgeView5.setBindView(no6);

                    noti = JSONObject.parseObject(KlbertjCache.notification.get("取消关注").get(KlbertjCache.notification.get("取消关注").size() - 1));
                    c6.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("取消关注").addAll(KlbertjCache.notification.get("取消关注"));
                    SharePreferenceUtils.putString(getActivity(), Cons.CANCEL_ATT, JSONArray.toJSONString(KlbertjCache.notification2.get("取消关注")));
                    KlbertjCache.notification.get("取消关注").clear();
                } else {
                    if (!KlbertjCache.notification2.get("取消关注").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("取消关注").get(KlbertjCache.notification2.get("取消关注").size() - 1));
                        c6.setText(noti.getString("con"));
                    } else
                        c6.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("收藏问题").size() > 0) {
                    badgeView6 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[4] += KlbertjCache.notification.get("收藏问题").size();
                    badgeView6.setBadgeText(String.valueOf(KlbertjCache.event_num[4]));
                    badgeView6.setBindView(no5);


                    noti = JSONObject.parseObject(KlbertjCache.notification.get("收藏问题").get(KlbertjCache.notification.get("收藏问题").size() - 1));
                    c5.setText(noti.getString("con"));


                    KlbertjCache.notification2.get("收藏问题").addAll(KlbertjCache.notification.get("收藏问题"));
                    SharePreferenceUtils.putString(getActivity(), Cons.COLL, JSONArray.toJSONString(KlbertjCache.notification2.get("收藏问题")));
                    KlbertjCache.notification.get("收藏问题").clear();

                } else {
                    if (!KlbertjCache.notification2.get("收藏问题").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("收藏问题").get(KlbertjCache.notification2.get("收藏问题").size() - 1));
                        c5.setText(noti.getString("con"));
                    } else
                        c5.setText("暂时没有新的通知");
                }
                if (KlbertjCache.notification.get("取消收藏问题").size() > 0) {
                    badgeView7 = BadgeFactory.createRoundRect(Objects.requireNonNull(getActivity()));
                    KlbertjCache.event_num[6] += KlbertjCache.notification.get("取消收藏问题").size();
                    badgeView7.setBadgeText(String.valueOf(KlbertjCache.event_num[6]));
                    badgeView7.setBindView(no7);

                    noti = JSONObject.parseObject(KlbertjCache.notification.get("取消收藏问题").get(KlbertjCache.notification.get("取消收藏问题").size() - 1));
                    c7.setText(noti.getString("con"));

                    KlbertjCache.notification2.get("取消收藏问题").addAll(KlbertjCache.notification.get("取消收藏问题"));
                    SharePreferenceUtils.putString(getActivity(), Cons.CANCLE_COLL, JSONArray.toJSONString(KlbertjCache.notification2.get("取消收藏问题")));
                    KlbertjCache.notification.get("取消收藏问题").clear();
                } else {
                    if (!KlbertjCache.notification2.get("取消收藏问题").isEmpty()) {
                        noti = JSONObject.parseObject(KlbertjCache.notification2.get("取消收藏问题").get(KlbertjCache.notification2.get("取消收藏问题").size() - 1));
                        c7.setText(noti.getString("con"));
                    } else
                        c7.setText("暂时没有新的通知");
                }

            }


        }
    }

}
