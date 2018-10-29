package com.zzu.zk.zhiwen.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.params.ProgressParams;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.customed_ui.ClearableEditText;
import com.zzu.zk.zhiwen.utils.EncryptionUtils;
import com.zzu.zk.zhiwen.utils.StatusBarUtil;
import com.zzu.zk.zhiwen.utils.ToastUtils;
import com.zzu.zk.zhiwen.utils.UrlUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    Button register;
    ClearableEditText un;
    ClearableEditText pwd;
    ClearableEditText email;
    private DialogFragment dialogFragment;
    TextView go_login;
    ClearableEditText cpwd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uname = un.getText().toString().trim();
                final String pass = pwd.getText().toString().trim();
                String cpass = cpwd.getText().toString().trim();
                final String e_mail = email.getText().toString().trim();
                if(TextUtils.isEmpty(uname)){
                    ToastUtils.showToast(Register.this,"用户名不能为空");
                }else if(TextUtils.isEmpty(pass)){
                    ToastUtils.showToast(Register.this,"密码不能为空");
                }else if(TextUtils.isEmpty(e_mail)){
                    ToastUtils.showToast(Register.this,"邮箱不能为空");
                }else if(TextUtils.isEmpty(cpass)){
                    ToastUtils.showToast(Register.this,"确认密码不能为空");
                }else {
                    final String regex = "[\\u4e00-\\u9fa5]";
                    String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
                    if (Pattern.compile(regex).matcher(pass).find()) {
                        ToastUtils.showToast(Register.this, "密码不能包含中文");
                    }else if(Pattern.compile(regex).matcher(cpass).find()){
                        ToastUtils.showToast(Register.this, "确认密码不能包含中文");
                    }else if(!pass.equals(cpass)){
                        ToastUtils.showToast(Register.this, "两次密码输入不一致");
                    }else if(!Pattern.compile(check).matcher(e_mail).matches()){
                        ToastUtils.showToast(Register.this, "邮箱格式有误");
                    }else {
                        dialogFragment = new CircleDialog.Builder()
                                .setProgressText("正在发送请求...")
                                .setCanceledOnTouchOutside(false)
                                .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                .setCancelable(false)
                                .show(getSupportFragmentManager());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    HttpURLConnection urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.register));

                                    OutputStream out = urlConnection.getOutputStream();
                                    JSONObject job = new JSONObject();
                                    job.put("un", uname);
                                    job.put("pw", pass);
                                    job.put("em", e_mail);
                                    out.write(EncryptionUtils.decryptByByte(job.toJSONString().getBytes("UTF-8")));
                                    out.close();

                                    int responseCode = urlConnection.getResponseCode();
                                    Log.i("zhangkai",EncryptionUtils.decryptByChar(Cons.register));
                                    if (responseCode == HttpURLConnection.HTTP_OK) {
                                        InputStream inputStream = urlConnection.getInputStream();
                                        final String result = EncryptionUtils.inputstream2String(inputStream);//将流转换为字符串。
                                        Register.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialogFragment != null) {
                                                    dialogFragment.dismiss();
                                                    dialogFragment = null;
                                                }

                                                ToastUtils.showloading("正在校验注册信息", "正在注册账号", new ToastUtils.OnLoadingCancelListener() {
                                                    @Override
                                                    public void OnLoadingCancel() {
                                                        if("-1".equals(result)){
                                                            new CircleDialog.Builder()
                                                                    .setTitle("操作结果")
                                                                    .setText("注册失败，用户名被占用！")
                                                                    .setPositive("确定", null)

                                                                    .show(Register.this.getSupportFragmentManager());
                                                        }else if("1".equals(result)){
                                                            new CircleDialog.Builder()
                                                                    .setTitle("操作结果")
                                                                    .setText("注册失败，邮箱被占用！")
                                                                    .setPositive("确定", null)

                                                                    .show(Register.this.getSupportFragmentManager());
                                                        }else if("2".equals(result)){
                                                            new CircleDialog.Builder()
                                                                    .setTitle("操作结果")
                                                                    .setText("注册失败，用户名，邮箱均被占用！")
                                                                    .setPositive("确定", null)

                                                                    .show(Register.this.getSupportFragmentManager());
                                                        }else {
                                                            ToastUtils.showToast(Register.this,"注册成功");
                                                            startActivity(new Intent(Register.this,Login.class));
                                                            Register.this.finish();
                                                        }
                                                    }
                                                },Register.this);
                                            }
                                        });


                                         }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.i("Register","注册有错");
                                }






                            }
                        }).start();
                    }

                }




            }
        });
        un = findViewById(R.id.r_un);
        pwd= findViewById(R.id.r_pwd);
        cpwd =findViewById(R.id.r_cpwd);
        email =findViewById(R.id.r_email);
        go_login=findViewById(R.id.go_login);
        go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
                Register.this.finish();
            }
        });
        StatusBarUtil.immersive(this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
