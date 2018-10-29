package com.zzu.zk.zhiwen.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.goyourfly.multi_picture.MultiPictureView;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.ProgressParams;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.beans.ProblemDigest;
import com.zzu.zk.zhiwen.cache.KlbertjCache;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.utils.EncryptionUtils;
import com.zzu.zk.zhiwen.utils.SFTPUtils;
import com.zzu.zk.zhiwen.utils.SharePreferenceUtils;
import com.zzu.zk.zhiwen.utils.StatusBarUtil;
import com.zzu.zk.zhiwen.utils.StorageUtils;
import com.zzu.zk.zhiwen.utils.ToastUtils;
import com.zzu.zk.zhiwen.utils.UrlUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddReply extends AppCompatActivity {
    SmartRefreshLayout smartRefreshLayout;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Uri temp_imgUri;
    private DialogFragment dialogFragment;
    EditText reply_con;
    TextView watcher, cancel, publish;
    MultiPictureView multiple_image;
    final int max_length = 800;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reply);
        Intent i = getIntent();
        final int qid = i.getIntExtra("qid",-1);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AddReplyQuickAdapter addReplyQuickAdapter;
        recyclerView.setAdapter(addReplyQuickAdapter = new AddReplyQuickAdapter());
        addReplyQuickAdapter.replaceData(new ArrayList<ProblemDigest>() {{
            add(new ProblemDigest());
        }});
        cancel = findViewById(R.id.cancel);
        publish = findViewById(R.id.publish);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CircleDialog.Builder()
                        .setTitle("提醒")
                        .setText("你确定要退出本次编辑吗？")
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reply_con.setText("");
                                watcher.setText("还可输入：800字");
                                multiple_image.clearItem();
                                finish();

                            }
                        })
                        .setNegative("取消", null)
                        .show(AddReply.this.getSupportFragmentManager());
            }
        });
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = reply_con.getText().toString();
                if(TextUtils.isEmpty(content)){
                    ToastUtils.showToast(AddReply.this,"回复内容不能为空");
                }else {
                    dialogFragment = new CircleDialog.Builder()
                            .setProgressText("正在发送请求...")
                            .setCancelable(false)
                            .setCanceledOnTouchOutside(false)
                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                            .show(getSupportFragmentManager());
                    final List<Uri> list = multiple_image.getList();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Map<Uri,String> uri_name = new HashMap<>();
                            String name;
                            Uri uri;
                            JSONArray pics = new JSONArray();
                            if(!list.isEmpty()){
                                for(int i = 0;i<list.size();i++){
                                    uri = list.get(i);
                                    name  = ToastUtils.getCurrentSeconds()+".jpg";
                                    uri_name.put(uri,name);
                                    pics.add(name);
                                    try {
                                        Thread.sleep(2);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @SuppressLint("SimpleDateFormat")
                            String c_t = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date().getTime());
                            JSONObject job = new JSONObject();
                            job.put("id", KlbertjCache.uid);
                            job.put("qid",qid);

                            JSONObject reply = new JSONObject();
                            reply.put("belt_user_id",KlbertjCache.uid);
                            reply.put("belt_user_uname", SharePreferenceUtils.getString(AddReply.this,Cons.NAME));
                            reply.put("belt_user_head_url", SharePreferenceUtils.getString(AddReply.this,Cons.AVATOR_PATH));
                            reply.put("belt_ques_id", qid);
                            reply.put("is_adopted", 0);
                            reply.put("pic_urls", pics);
                            reply.put("content", content);
                            reply.put("create_time", c_t);
                            job.put("reply", reply);
                            HttpURLConnection urlConnection = null;
                            try {
                                urlConnection = UrlUtils.init(EncryptionUtils.decryptByChar(Cons.insertReply));
                                OutputStream out = urlConnection.getOutputStream();
                                out.write(EncryptionUtils.decryptByByte(job.toJSONString().getBytes("UTF-8")));
                                out.close();
                                int responseCode = urlConnection.getResponseCode();
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    final String result = EncryptionUtils.inputstream2String(urlConnection.getInputStream());//将流转换为字符串。
                                    Thread.sleep(1000);
                                    Objects.requireNonNull(AddReply.this).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialogFragment != null) {
                                                dialogFragment.dismiss();
                                                dialogFragment = null;
                                            }

                                            if ("f".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(AddReply.this), "登录已经过期");

                                            } else if ("bp".equals(result)) {
                                                ToastUtils.showToast(Objects.requireNonNull(AddReply.this), "参数错误");
                                            } else {
                                                if(uri_name.isEmpty()){
                                                    ToastUtils.showToast(Objects.requireNonNull(AddReply.this), "操作成功");
                                                    AddReply.this.finish();
                                                }else {
                                                    dialogFragment = new CircleDialog.Builder()
                                                            .setProgressText("正在保存您的输入...")
                                                            .setCancelable(false)
                                                            .setCanceledOnTouchOutside(false)
                                                            .setProgressStyle(ProgressParams.STYLE_SPINNER)
                                                            .show(getSupportFragmentManager());

                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            SFTPUtils.upLoadPics(AddReply.this,uri_name,Cons.REPLY_PICS_PATH_IN_SERVER);
                                                            try {
                                                                Thread.sleep(500);

                                                            } catch (InterruptedException e) {
                                                                e.printStackTrace();
                                                            }
                                                            Objects.requireNonNull(AddReply.this).runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if (dialogFragment != null) {
                                                                        dialogFragment.dismiss();
                                                                        dialogFragment = null;
                                                                    }
                                                                    ToastUtils.showloading("正在保存图片", "正在做最后的更改", new ToastUtils.OnLoadingCancelListener() {
                                                                        @Override
                                                                        public void OnLoadingCancel() {
                                                                            ToastUtils.showToast(Objects.requireNonNull(AddReply.this), "操作成功");
                                                                        }
                                                                    },AddReply.this);

                                                                    AddReply.this.finish();

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


            }
        });


        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, toolbar);
        StatusBarUtil.setPaddingSmart(this, recyclerView);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurView));
    }


    private class AddReplyQuickAdapter extends BaseQuickAdapter<ProblemDigest, BaseViewHolder> {


        private AddReplyQuickAdapter() {
            super(R.layout.add_reply_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, ProblemDigest item) {
            reply_con = helper.getView(R.id.reply_con);

            watcher = helper.getView(R.id.watcher);
            multiple_image = helper.getView(R.id.multiple_image);
            watcher.setText("还可输入：800字");
            reply_con.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    watcher.setText("还可输入：" + (max_length - s.length()) + "字");
                }
            });

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
                            .show(AddReply.this.getSupportFragmentManager());
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

                                        if ((ContextCompat.checkSelfPermission(AddReply.this,
                                                Manifest.permission.CAMERA)
                                                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(AddReply.this,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED)) {

                                            ActivityCompat.requestPermissions(AddReply.this,
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
//                                    ToastUtils.showToast(AddReply.this, position + "");
                                }
                            })
                            .setNegative("取消", null)
                            .show(AddReply.this.getSupportFragmentManager());

                }
            });

        }
    }

    private void addImage() {

        if (ContextCompat.checkSelfPermission(AddReply.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Cons.REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Matisse.from(AddReply.this)
                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                    .maxSelectable(6 - multiple_image.getCount())
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


    private Intent get_take_photo_intent(Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Cons.REQUEST_ADD_IMAGE && resultCode == RESULT_OK) {
            // ProblemDetail.ulist = Matisse.obtainResult(data);
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
}
