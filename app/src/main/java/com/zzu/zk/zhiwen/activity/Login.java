package com.zzu.zk.zhiwen.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.mylhyl.circledialog.view.listener.OnInputCounterChangeListener;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.ClearableEditText;
import com.zzu.zk.zhiwen.utils.EncryptionUtils;
import com.zzu.zk.zhiwen.utils.PushHandler;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.StatusBarUtil;
import com.zzu.zk.zhiwen.utils.ToastUtils;
import com.zzu.zk.zhiwen.utils.UrlUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    Button login;
    ClearableEditText un;
    ClearableEditText pwd;
    TextView go_reg;
    TextView forget_pwd;
    CheckBox remeber_pwd;
    boolean is_remeber = false;
    DialogFragment dialogFragment;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login = findViewById(R.id.login);
        un = findViewById(R.id.un);
        pwd = findViewById(R.id.pwd);

        if (!SharePreferenceUtils.getString(Login.this, "pwd").equals("")) {
            pwd.setText(SharePreferenceUtils.getString(Login.this, "pwd"));
        }

        go_reg = findViewById(R.id.go_reg);
        forget_pwd = findViewById(R.id.forget_pwd);
        remeber_pwd = findViewById(R.id.remeber_pwd);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uname = un.getText().toString().trim();
                final String pass = pwd.getText().toString().trim();
                if (TextUtils.isEmpty(uname)) {
                    ToastUtils.showToast(Login.this, "用户名不能为空");
                } else {
                    if (TextUtils.isEmpty(pass)) {
                        ToastUtils.showToast(Login.this, "密码不能为空");
                    } else {
                        final String regex = "[\\u4e00-\\u9fa5]";
                        if (!Pattern.compile(regex).matcher(pass).find()) {
                            if (is_remeber) {
                                SharePreferenceUtils.putString(Login.this, "pwd", pass);
                            }

                            dialogFragment = new CircleDialog.Builder()
                                    .setProgressText("正在发送请求...")
                                    .setCanceledOnTouchOutside(false)
                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                    .show(getSupportFragmentManager());


                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        HttpURLConnection urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.login));
                                        OutputStream out = urlConnection.getOutputStream();
                                        JSONObject job = new JSONObject();
                                        job.put("un", uname);
                                        job.put("pw", pass);
                                        out.write(EncryptionUtils.decryptByByte(job.toJSONString().getBytes("UTF-8")));
                                        out.close();
                                        int responseCode = urlConnection.getResponseCode();
                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                            InputStream inputStream = urlConnection.getInputStream();
                                            String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。
                                            final JSONObject j = JSONObject.parseObject(result);
                                            final int errCode = j.getInteger("errCode");

                                            Login.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (dialogFragment != null) {
                                                        dialogFragment.dismiss();
                                                        dialogFragment = null;
                                                    }

                                                    ToastUtils.showloading("正在校验用户名密码...", "正在登录服务器...", new ToastUtils.OnLoadingCancelListener() {
                                                        @Override
                                                        public void OnLoadingCancel() {
                                                            if (-1 == errCode) {
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.IS_LOGIN, errCode);
                                                                SharePreferenceUtils.putString(Login.this, Cons.NAME, j.getString("name"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.COUNT_OF_MY_QUESTIONS, j.getInteger("count_of_my_questions"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.DISCUSS_NUM, j.getInteger("discuss_num"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.FANS_NUM, j.getInteger("fans_num"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.NUMS_OF_COLLECTION_QUESTIONS, j.getInteger("nums_of_collection_questions"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.ATTENTION_PEOPLE_NUM, j.getInteger("attention_people_num"));
                                                                SharePreferenceUtils.putString(Login.this, Cons.AVATOR_PATH, j.getString("avator_path"));
                                                                SharePreferenceUtils.putString(Login.this, Cons.BG_PATH, j.getString("bg_path"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.SCORE, j.getInteger("score"));
                                                                KlbertjCache.uid = errCode;
                                                                KlbertjCache.name = j.getString("name");
                                                                KlbertjCache.avator_path = j.getString("avator_path");
                                                                KlbertjCache.att_p_num = j.getInteger("attention_people_num");
                                                                KlbertjCache.coll_q_num = j.getInteger("nums_of_collection_questions");
                                                                KlbertjCache.att_p_num = j.getInteger("fans_num");
                                                                KlbertjCache.discuss_num = j.getInteger("discuss_num");
                                                                KlbertjCache.my_q_num = j.getInteger("count_of_my_questions");
                                                                KlbertjCache.score = j.getInteger("score");
//                                                                ToastUtils.showToast(Login.this,j.getString("name"));
                                                                new CircleDialog.Builder()
                                                                        .setTitle("操作结果")
                                                                        .setText("登录成功，但账号未激活，请及时激活！")
                                                                        .setPositive("确定", new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                Login.this.finish();
                                                                            }
                                                                        })

                                                                        .show(Login.this.getSupportFragmentManager());
                                                            } else if (-2 == errCode) {
                                                                new CircleDialog.Builder()
                                                                        .setTitle("操作结果")
                                                                        .setText("登录失败，用户名密码不匹配！")
                                                                        .setPositive("确定", null)

                                                                        .show(Login.this.getSupportFragmentManager());
                                                            } else {
                                                                ToastUtils.showToast(Login.this, "登录成功！");
                                                                new PushHandler().start();
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.IS_LOGIN, errCode);
                                                                SharePreferenceUtils.putString(Login.this, Cons.AVATOR_PATH, j.getString("avator_path"));
                                                                SharePreferenceUtils.putString(Login.this, Cons.COLLEGE, j.getString("college"));
                                                                SharePreferenceUtils.putString(Login.this, Cons.MAJOR, j.getString("major"));
                                                                SharePreferenceUtils.putString(Login.this, Cons.BG_PATH, j.getString("bg_path"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.COUNT_OF_MY_QUESTIONS, j.getInteger("count_of_my_questions"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.DISCUSS_NUM, j.getInteger("discuss_num"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.FANS_NUM, j.getInteger("fans_num"));
                                                                SharePreferenceUtils.putString(Login.this, Cons.NAME, j.getString("name"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.NUMS_OF_COLLECTION_QUESTIONS, j.getInteger("nums_of_collection_questions"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.ATTENTION_PEOPLE_NUM, j.getInteger("attention_people_num"));
                                                                SharePreferenceUtils.putInteger(Login.this, Cons.SCORE, j.getInteger("score"));

                                                                KlbertjCache.uid = errCode;
                                                                KlbertjCache.bg = j.getString("bg_path");
                                                                KlbertjCache.name = j.getString("name");
                                                                KlbertjCache.avator_path = j.getString("avator_path");
                                                                KlbertjCache.att_p_num = j.getInteger("attention_people_num");
                                                                KlbertjCache.coll_q_num = j.getInteger("nums_of_collection_questions");
                                                                KlbertjCache.att_p_num = j.getInteger("fans_num");
                                                                KlbertjCache.discuss_num = j.getInteger("discuss_num");
                                                                KlbertjCache.my_q_num = j.getInteger("count_of_my_questions");
                                                                KlbertjCache.college = j.getString("college");
                                                                KlbertjCache.major = j.getString("major");
                                                                KlbertjCache.score = j.getInteger("score");
                                                                Login.this.finish();
                                                            }
                                                        }
                                                    }, Login.this);

                                                }
                                            });

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.i("Login", "登陆有错");
                                    }
                                }
                            }).start();

                        } else {
                            ToastUtils.showToast(Login.this, "密码不能包含中文");
                        }
                    }
                }
            }
        });


        remeber_pwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                is_remeber = isChecked;
            }
        });

        forget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogFragment = new CircleDialog.Builder()
                        .setCanceledOnTouchOutside(false)
                        .setCancelable(true)
                        .setInputManualClose(false)
                        .setTitle("邮箱验证")

                        .setInputHint("请输入您的邮箱和用户名 格式：邮箱##用户名 我们会给您的绑定邮箱发送一封验证邮件")

                        .autoInputShowKeyboard()

                        .setInputCounter(80, new OnInputCounterChangeListener() {
                            @Override
                            public String onCounterChange(int maxLen, int currentLen) {
                                return maxLen - currentLen + "/" + maxLen;
                            }
                        })
                        .setInputCounterColor(getResources().getColor(R.color.colorAccent))
                        .setNegative("取消", null)

                        .setPositiveInput(find_label(), new OnInputClickListener() {
                            @Override
                            public void onClick(String text1, View v) {
                                if ("发送".equals(find_label())) {
                                    text1 = text1.trim();
                                    if (TextUtils.isEmpty(text1)) {
                                        ToastUtils.showToast(Login.this, "内容不能为空");
                                    } else {
                                        if (!text1.contains("##")) {
                                            ToastUtils.showToast(Login.this, "输入格式有误");
                                        } else {
                                            String[] info = text1.split("##");
                                            if (info.length < 2) {
                                                ToastUtils.showToast(Login.this, "输入格式有误");
                                            } else {
                                                final String email = info[0].trim();
                                                if (TextUtils.isEmpty(email)) {
                                                    ToastUtils.showToast(Login.this, "邮箱不能为空");
                                                } else {
                                                    String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                                                    Pattern regex = Pattern.compile(check);
                                                    Matcher matcher = regex.matcher(email);
                                                    if (matcher.matches()) {
                                                        final String un = info[1].trim();
                                                        if (TextUtils.isEmpty(un)) {
                                                            ToastUtils.showToast(Login.this, "用户名不能为空");
                                                        } else {
                                                            SharePreferenceUtils.putString(Login.this, "setPositiveInput", ToastUtils.getCurrentSeconds() + "");

                                                            dialogFragment = new CircleDialog.Builder()
                                                                    .setProgressText("正在获取数据...")
                                                                    .setCanceledOnTouchOutside(false)
                                                                    .setCancelable(false)
                                                                    .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                    .show(Objects.requireNonNull(Login.this).getSupportFragmentManager());


                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    HttpURLConnection urlConnection = null;
                                                                    try {
                                                                        urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.request_check_email));
                                                                        OutputStream out = urlConnection.getOutputStream();
                                                                        JSONObject object = new JSONObject();
                                                                        object.put("un", un);
                                                                        object.put("em", email);
                                                                        out.write(EncryptionUtils.decryptByByte(object.toJSONString().getBytes("UTF-8")));
                                                                        out.close();
                                                                        int responseCode = urlConnection.getResponseCode();
                                                                        if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                            final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                            Thread.sleep(1000);
                                                                            Objects.requireNonNull(Objects.requireNonNull(Login.this)).runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    if (dialogFragment != null) {
                                                                                        dialogFragment.dismiss();
                                                                                        dialogFragment = null;
                                                                                    }

                                                                                    if ("0".equals(result)) {

                                                                                        new CircleDialog.Builder()
                                                                                                .setTitle("重置密码")
                                                                                                .setCanceledOnTouchOutside(false)
                                                                                                .setCancelable(true)
                                                                                                .setInputManualClose(false)
                                                                                                .setInputHint("请输入您的邮箱验证码和新密码 格式：邮箱验证码##新密码 ")

                                                                                                .autoInputShowKeyboard()

                                                                                                .setInputCounter(80, new OnInputCounterChangeListener() {
                                                                                                    @Override
                                                                                                    public String onCounterChange(int maxLen, int currentLen) {
                                                                                                        return maxLen - currentLen + "/" + maxLen;
                                                                                                    }
                                                                                                })
                                                                                                .setInputCounterColor(getResources().getColor(R.color.colorAccent))
                                                                                                .setNegative("取消", null)
                                                                                                .setPositiveInput("发送", new OnInputClickListener() {
                                                                                                    @Override
                                                                                                    public void onClick(String text, View v) {
                                                                                                        //网络请求
//

                                                                                                        text = text.trim();
                                                                                                        if (text.contains("##")) {
                                                                                                            String[] info = text.split("##");
                                                                                                            if (info.length < 2) {
                                                                                                                ToastUtils.showToast(Login.this, "输入格式有误");
                                                                                                            } else {
                                                                                                                final String pwd = info[1].trim();
                                                                                                                final String code = info[0].trim();
                                                                                                                final String regex = "^[\\u4e00-\\u9fa5]*$";
                                                                                                                if (Pattern.compile(regex).matcher(pwd).find()) {
//网络请求
                                                                                                                    dialogFragment = new CircleDialog.Builder()
                                                                                                                            .setProgressText("正在获取数据...")
                                                                                                                            .setCanceledOnTouchOutside(false)
                                                                                                                            .setCancelable(false)
                                                                                                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                                                                                            .show(Objects.requireNonNull(Login.this).getSupportFragmentManager());
                                                                                                                    new Thread(new Runnable() {
                                                                                                                        @Override
                                                                                                                        public void run() {
                                                                                                                            HttpURLConnection urlConnection = null;
                                                                                                                            try {
                                                                                                                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.findBackPw));
                                                                                                                                OutputStream out = urlConnection.getOutputStream();

                                                                                                                                JSONObject object1 = new JSONObject();
                                                                                                                                object1.put("newpw", pwd);
                                                                                                                                object1.put("em", email);
                                                                                                                                object1.put("code", code);

                                                                                                                                out.write(EncryptionUtils.decryptByByte(object1.toJSONString().getBytes("UTF-8")));
                                                                                                                                out.close();
                                                                                                                                int responseCode = urlConnection.getResponseCode();
                                                                                                                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                                                                                                                    final String result1 = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                                                                                                                    Thread.sleep(1000);
                                                                                                                                    Objects.requireNonNull(Objects.requireNonNull(Login.this)).runOnUiThread(new Runnable() {
                                                                                                                                        @Override
                                                                                                                                        public void run() {
                                                                                                                                            if (dialogFragment != null) {
                                                                                                                                                dialogFragment.dismiss();
                                                                                                                                                dialogFragment = null;
                                                                                                                                            }

                                                                                                                                            if ("0".equals(result1)){
                                                                                                                                                ToastUtils.showToast(Login.this,"验证码有误或过期");
                                                                                                                                            }else if("there is something wrong with findUserByEmail".equals(result1)){

                                                                                                                                                ToastUtils.showToast(Login.this,"服务器出错");

                                                                                                                                            }else {
                                                                                                                                                ToastUtils.showToast(Login.this,"操作成功");
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
                                                                                                                    ToastUtils.showToast(Login.this, "密码不能包含中文");
                                                                                                                }
                                                                                                            }
                                                                                                        } else {
                                                                                                            ToastUtils.showToast(Login.this, "输入格式有误");
                                                                                                        }


                                                                                                    }
                                                                                                }).show(Login.this.getSupportFragmentManager());
                                                                                    }


                                                                                }
                                                                            });

                                                                        }
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }).start();


                                                            ToastUtils.showloading("正在校验输入...", "正在发送验证...", new ToastUtils.OnLoadingCancelListener() {
                                                                @Override
                                                                public void OnLoadingCancel() {

                                                                }
                                                            }, Login.this);

                                                        }
                                                    } else {
                                                        ToastUtils.showToast(Login.this, "邮箱格式有误");
                                                    }
                                                }
                                            }

                                        }
                                    }

                                } else {

                                }

//                                Toast.makeText(Login.this, "frdtfgdjsifgjdseijgdkirefjg", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .show(Login.this.getSupportFragmentManager());
            }
        });
        go_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Login.this, Register.class));
                Login.this.finish();
            }
        });


        StatusBarUtil.immersive(this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    private String find_label() {
        if (TextUtils.isEmpty(SharePreferenceUtils.getString(this, "setPositiveInput"))) {
            return "发送";
        } else {
            int lastmill = Integer.valueOf(SharePreferenceUtils.getString(this, "setPositiveInput"));
            long currmill = Long.valueOf(ToastUtils.getCurrentSeconds()) / 1000;
            if (currmill - lastmill > 5 * 60) {
                return "发送";
            } else {
                return "已经发送";
            }

        }
    }


}
