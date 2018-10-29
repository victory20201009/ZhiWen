package com.zzu.zk.zhiwen.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnMultiPurposeListener;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.activity.Login;
import com.zzu.zk.zhiwen.activity.MyAsk;
import com.zzu.zk.zhiwen.activity.ProblemDetail;
import com.zzu.zk.zhiwen.beans.ProblemDigest;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.MyCircleView;
import com.zzu.zk.zhiwen.glide.MyGlide;
import com.zzu.zk.zhiwen.utils.EncryptionUtils;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.ToastUtils;
import com.zzu.zk.zhiwen.utils.UrlUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Objects;

public class Me extends Fragment {
    RecyclerView recyclerView;
    MeQuickAdapter meQuickAdapter;
    MyCircleView avatar;
    TextView name, num_of_pro, num_of_fans, dis_num;
    View head_detail, me_pro_detail, me_collect_detail, me_attention_detail, me_bargen_detail, me_setting_detail;
    ImageView parallax;
    SmartRefreshLayout refreshLayout;
    private DialogFragment dialogFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.me, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        parallax = view.findViewById(R.id.parallax);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getActivity())));
        meQuickAdapter = new MeQuickAdapter();
        recyclerView.setAdapter(meQuickAdapter);
        if (!TextUtils.isEmpty(KlbertjCache.bg)) {
            Log.i("fgddsgd","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            Glide.with(Objects.requireNonNull(getActivity())).load(Cons.PICS_BASE_URL + Cons.BG_PICS_PATH_IN_SERVER + "/" + KlbertjCache.bg).apply(MyGlide.getRequestOptions()).into(parallax);
        }

        meQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
            add(new ProblemDigest());
        }});

        refreshLayout.setEnableLoadMore(false);

        refreshLayout.setOnMultiPurposeListener(new OnMultiPurposeListener() {


            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

            }

            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection urlConnection = null;
                        try {
                            urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.refreshPerson));
                            Log.i("url", Cons.refreshPerson);
                            OutputStream out = urlConnection.getOutputStream();
                            out.write(EncryptionUtils.decryptByByte((KlbertjCache.uid + "").getBytes("UTF-8")));
                            out.close();
                            int responseCode = urlConnection.getResponseCode();
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                Thread.sleep(1000);
                                final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。


                                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if ("f".equals(result)) {
                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "权限过期");
                                        } else {
                                            final JSONObject j = JSONObject.parseObject(result);
                                            SharePreferenceUtils.putString(Objects.requireNonNull(getActivity()), Cons.AVATOR_PATH, j.getString("avator_path"));
                                            SharePreferenceUtils.putString(Objects.requireNonNull(getActivity()), Cons.BG_PATH, j.getString("bg_path"));
                                            SharePreferenceUtils.putInteger(Objects.requireNonNull(getActivity()), Cons.COUNT_OF_MY_QUESTIONS, j.getInteger("count_of_my_questions"));
                                            SharePreferenceUtils.putInteger(Objects.requireNonNull(getActivity()), Cons.DISCUSS_NUM, j.getInteger("discuss_num"));
                                            SharePreferenceUtils.putInteger(Objects.requireNonNull(getActivity()), Cons.FANS_NUM, j.getInteger("fans_num"));
                                            SharePreferenceUtils.putString(Objects.requireNonNull(getActivity()), Cons.NAME, j.getString("name"));
                                            SharePreferenceUtils.putInteger(Objects.requireNonNull(getActivity()), Cons.NUMS_OF_COLLECTION_QUESTIONS, j.getInteger("nums_of_collection_questions"));
                                            SharePreferenceUtils.putInteger(Objects.requireNonNull(getActivity()), Cons.ATTENTION_PEOPLE_NUM, j.getInteger("attention_people_num"));
                                            SharePreferenceUtils.putInteger(Objects.requireNonNull(getActivity()), Cons.SCORE, j.getInteger("score"));
                                            KlbertjCache.name = j.getString("name");
                                            KlbertjCache.avator_path = j.getString("avator_path");
                                            KlbertjCache.bg = j.getString("bg_path");
                                            KlbertjCache.my_q_num = j.getInteger("count_of_my_questions");
                                            KlbertjCache.discuss_num = j.getInteger("discuss_num");
                                            KlbertjCache.fans_num = j.getInteger("fans_num");
                                            KlbertjCache.coll_q_num = j.getInteger("nums_of_collection_questions");
                                            KlbertjCache.att_p_num = j.getInteger("attention_people_num");
                                            KlbertjCache.score = j.getInteger("score");
                                            if (!TextUtils.isEmpty(KlbertjCache.bg)) {
                                                Glide.with(Objects.requireNonNull(getActivity())).load(Cons.PICS_BASE_URL + Cons.BG_PICS_PATH_IN_SERVER + "/" + KlbertjCache.bg).apply(MyGlide.getRequestOptions()).into(parallax);
                                            }


                                            meQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
                                                add(new ProblemDigest());
                                            }});
                                        }

                                        refreshLayout.finishRefresh(500);


                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();


            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }

            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {

//                Log.i("Reserve", "isDragging   " + isDragging + "percent   " + percent + "offset   " + offset + "headerHeight   " + headerHeight
//                        + "maxDragHeight      " + maxDragHeight);
                parallax.setTranslationY(offset * 0.444f);
            }

            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {

            }

            @Override
            public void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int maxDragHeight) {

            }

            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) {

            }

            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {

            }

        });


    }

    private class MeQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {


        private MeQuickAdapter() {
            super(R.layout.me_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {
            dis_num = helper.getView(R.id.dis_num);
            avatar = helper.getView(R.id.avatar);
            name = helper.getView(R.id.name);
            num_of_pro = helper.getView(R.id.num_of_pro);
            num_of_fans = helper.getView(R.id.num_of_fans);
            head_detail = helper.getView(R.id.head_detail);
            me_pro_detail = helper.getView(R.id.me_pro_detail);
            me_collect_detail = helper.getView(R.id.me_collect_detail);
            me_attention_detail = helper.getView(R.id.me_attention_detail);
            me_bargen_detail = helper.getView(R.id.me_bargen_detail);
            me_setting_detail = helper.getView(R.id.me_setting_detail);
            head_detail.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);


//            ToastUtils.showToast(getActivity(),"debug"+KlbertjCache.avator_path);

            if (!TextUtils.isEmpty(KlbertjCache.avator_path)) {
                Glide.with(Objects.requireNonNull(getActivity())).asBitmap().load(Cons.PICS_BASE_URL + Cons.AVATOR_PICS_PATH_IN_SERVER + "/" + KlbertjCache.avator_path).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        avatar.setBitmap(bitmap);
                    }
                });
            }

            if (!TextUtils.isEmpty(KlbertjCache.name)) {
                /**
                 *       String text = label.getBody()
                 .replaceFirst(floatingSearchView.getQuery(),
                 "<font color=\"" + textLight + "\">" + floatingSearchView.getQuery() + "</font>");
                 textView.setText(Html.fromHtml(text));
                 */

                name.setText(Html.fromHtml("<font color=\"#00BCD4\">用户名：</font>" + KlbertjCache.name));

            }

            if (KlbertjCache.discuss_num != -2) {
                /**
                 *       String text = label.getBody()
                 .replaceFirst(floatingSearchView.getQuery(),
                 "<font color=\"" + textLight + "\">" + floatingSearchView.getQuery() + "</font>");
                 textView.setText(Html.fromHtml(text));
                 */

                dis_num.setText(Html.fromHtml("<font color=\"#000000\">参与讨论：</font>" + KlbertjCache.discuss_num + "次"));
            }
            if (KlbertjCache.my_q_num != -2) {
                /**
                 *       String text = label.getBody()
                 .replaceFirst(floatingSearchView.getQuery(),
                 "<font color=\"" + textLight + "\">" + floatingSearchView.getQuery() + "</font>");
                 textView.setText(Html.fromHtml(text));
                 */

                num_of_pro.setText(Html.fromHtml("<font color=\"#00BCD4\">问题数：</font>" + KlbertjCache.my_q_num));
            }

            if (KlbertjCache.fans_num != -2) {
                /**
                 *       String text = label.getBody()
                 .replaceFirst(floatingSearchView.getQuery(),
                 "<font color=\"" + textLight + "\">" + floatingSearchView.getQuery() + "</font>");
                 textView.setText(Html.fromHtml(text));
                 */

                num_of_fans.setText(Html.fromHtml("<font color=\"#000000\">粉丝数：</font>" + KlbertjCache.fans_num));
            }


            me_pro_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent i = new Intent(Objects.requireNonNull(Me.this.getActivity()), MyAsk.class);
                    i.putExtra("title", "我的提问");
                    i.putExtra(Cons.COUNT_OF_MY_QUESTIONS, num_of_pro.getText().toString().replace("问题数：", "") + "个问题");
                    if (!TextUtils.isEmpty(KlbertjCache.my_pro_list)) {
                        i.putExtra("contentStr", KlbertjCache.my_pro_list);
                        startActivity(i);
                    } else {
                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在获取数据...")
                                .setCancelable(false)
                                .setCanceledOnTouchOutside(false)
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
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

                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
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
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "登录已经过期");
                                                        } else if ("server has occured a problem".equals(result)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no".equals(result)) {
                                                            i.putExtra("contentStr", "no");
                                                            startActivity(i);
                                                            KlbertjCache.my_pro_list = result;
                                                        } else {
                                                            KlbertjCache.my_pro_list = result;
                                                            i.putExtra("contentStr", result);
                                                            startActivity(i);
                                                        }

                                                    }
                                                }, Objects.requireNonNull(getActivity()));
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
            head_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Objects.requireNonNull(Me.this.getActivity()), MyAsk.class);
                    i.putExtra("title", "个人信息");
                    i.putExtra("itemType", 7);
                    startActivityForResult(i, Cons.REQUEST_CHANGE_BG);
//                    startActivity(i);
                }
            });
            me_collect_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent i = new Intent(Objects.requireNonNull(Me.this.getActivity()), MyAsk.class);
                    i.putExtra("title", "收藏问题");
                    i.putExtra(Cons.NUMS_OF_COLLECTION_QUESTIONS, SharePreferenceUtils.getInteger(Objects.requireNonNull(getActivity()), Cons.NUMS_OF_COLLECTION_QUESTIONS) + "个问题");
                    i.putExtra("itemType", 1);
                    if (!TextUtils.isEmpty(KlbertjCache.my_coll_list)) {
                        i.putExtra("contentStr", KlbertjCache.my_coll_list);
                        startActivity(i);
                    } else {
                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在获取数据...")
                                .setCancelable(false)
                                .setCanceledOnTouchOutside(false)
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
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

                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
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
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "登录已经过期");
                                                        } else if ("server has occured a problem".equals(result)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no".equals(result)) {
                                                            i.putExtra("contentStr", "no");
                                                            startActivity(i);
                                                            KlbertjCache.my_coll_list = result;
                                                        } else {
                                                            KlbertjCache.my_coll_list = result;
                                                            i.putExtra("contentStr", result);
                                                            startActivity(i);
                                                        }

                                                    }
                                                }, Objects.requireNonNull(getActivity()));
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
            me_attention_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent i = new Intent(Objects.requireNonNull(Me.this.getActivity()), MyAsk.class);
                    i.putExtra("title", "关注的人");
                    i.putExtra("itemType", 2);
                    i.putExtra(Cons.ATTENTION_PEOPLE_NUM, KlbertjCache.att_p_num+ "人");
                   // Log.i("fdsdf",SharePreferenceUtils.getInteger(Objects.requireNonNull(getActivity()), Cons.ATTENTION_PEOPLE_NUM) + "人");
                    if (!TextUtils.isEmpty(KlbertjCache.my_att_list)) {
                        i.putExtra("contentStr", KlbertjCache.my_att_list);
                        startActivity(i);
                    } else {
                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在获取数据...")
                                .setCancelable(false)
                                .setCanceledOnTouchOutside(false)
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                .show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
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

                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
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
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "登录已经过期");
                                                        } else if ("server has occured a problem".equals(result)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "服务器错误");
                                                        } else if ("bp".equals(result)) {
                                                            ToastUtils.showToast(Objects.requireNonNull(getActivity()), "参数错误");
                                                        } else if ("no".equals(result)) {
                                                            i.putExtra("contentStr", "no");
                                                            startActivity(i);
                                                            KlbertjCache.my_att_list = result;
                                                        } else {
                                                            KlbertjCache.my_att_list = result;
                                                            i.putExtra("contentStr", result);
                                                            startActivity(i);
                                                        }

                                                    }
                                                }, Objects.requireNonNull(getActivity()));
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
            me_bargen_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Objects.requireNonNull(Me.this.getActivity()), MyAsk.class);
                    i.putExtra("title", "我的积分");
                    i.putExtra("itemType", 3);
                    i.putExtra("bargen", KlbertjCache.score +"分");

                    startActivity(i);


                }
            });
            me_setting_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Objects.requireNonNull(Me.this.getActivity()), MyAsk.class);
                    i.putExtra("title", "设置");
                    i.putExtra("itemType", 6);
                    startActivity(i);
                }
            });


        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Cons.REQUEST_CHANGE_BG:
                if (resultCode == -1) {
                    Uri headUri = data.getParcelableExtra("head_uri");
                    Uri bgUri = data.getParcelableExtra("bg_uri");
                    ToastUtils.showToast(Objects.requireNonNull(getActivity()), headUri.toString());

                }
                break;
        }
    }
}
