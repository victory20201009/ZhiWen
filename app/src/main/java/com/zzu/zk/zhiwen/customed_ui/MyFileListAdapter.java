package com.zzu.zk.zhiwen.customed_ui;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.angads25.filepicker.R;
import com.github.angads25.filepicker.controller.NotifyItemChecked;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.model.FileListItem;
import com.github.angads25.filepicker.model.MarkedItemList;
import com.github.angads25.filepicker.widget.MaterialCheckbox;
import com.github.angads25.filepicker.widget.OnCheckedChangeListener;
import com.zzu.zk.zhiwen.constant.Cons;
import com.zzu.zk.zhiwen.utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyFileListAdapter extends BaseAdapter {
    private ArrayList<FileListItem> listItem;
    private Context context;
    private DialogProperties properties;
    private NotifyItemChecked notifyItemChecked;
    private int selected_f_num = 0;
    private int selected_f_length = 0;
    ArrayList<String> mpaths = new ArrayList();
    public MyFileListAdapter(ArrayList<FileListItem> listItem, Context context, DialogProperties properties) {
        this.listItem = listItem;
        this.context = context;
        this.properties = properties;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public FileListItem getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    @SuppressWarnings("deprecation")
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final MyFileListAdapter.ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_file_list_item, viewGroup, false);
            holder = new MyFileListAdapter.ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (MyFileListAdapter.ViewHolder) view.getTag();
        }
        final FileListItem item = listItem.get(i);
        if (MarkedItemList.hasItem(item.getLocation())) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.marked_item_animation);
            view.setAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.unmarked_item_animation);
            view.setAnimation(animation);
        }
        if (item.isDirectory()) {
            holder.type_icon.setImageResource(R.mipmap.ic_type_folder);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));
            } else {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            }
            if (properties.selection_type == DialogConfigs.FILE_SELECT) {
                holder.fmark.setVisibility(View.INVISIBLE);
            } else {
                holder.fmark.setVisibility(View.VISIBLE);
            }
        } else {
            holder.type_icon.setImageResource(R.mipmap.ic_type_file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorAccent, context.getTheme()));
            } else {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorAccent));
            }
            if (properties.selection_type == DialogConfigs.DIR_SELECT) {
                holder.fmark.setVisibility(View.INVISIBLE);
            } else {
                holder.fmark.setVisibility(View.VISIBLE);
            }
        }
        holder.type_icon.setContentDescription(item.getFilename());
        holder.name.setText(item.getFilename());
        SimpleDateFormat sdate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat stime = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date date = new Date(item.getTime());
        if (i == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
            holder.type.setText(R.string.label_parent_directory);
        } else {
            holder.type.setText(context.getString(R.string.last_edit) + sdate.format(date) + ", " + stime.format(date));
        }
        if (holder.fmark.getVisibility() == View.VISIBLE) {
            if (i == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
                holder.fmark.setVisibility(View.INVISIBLE);
            }
            if (MarkedItemList.hasItem(item.getLocation())) {
                holder.fmark.setChecked(true);
            } else {
                holder.fmark.setChecked(false);
            }
        }

        holder.fmark.setOnCheckedChangedListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MaterialCheckbox checkbox, boolean isChecked) {
                if(isChecked){

                    if(MarkedItemList.getFileCount() + selected_f_num +1> 5){
                        ToastUtils.showToast(context, "附件个数超过限制");
                        holder.fmark.setChecked(false);
                    }else {
                        item.setMarked(isChecked);

                        if (properties.selection_mode == DialogConfigs.MULTI_MODE) {
                            MarkedItemList.addSelectedItem(item);
                        } else {
                            MarkedItemList.addSingleFile(item);
                        }
                        String[] paths = MarkedItemList.getSelectedPaths();
                        long temp = 0;
                        for (String path : paths) {
                            temp += new File(path).length();
                        }
                        if (Cons.max_upload_file_length - selected_f_length - temp < 0) {
                            ToastUtils.showToast(context, "附件总大小超过限制");
                            holder.fmark.setChecked(false);
                            item.setMarked(false);
                            MarkedItemList.removeSelectedItem(item.getLocation());
                        }else if(mpaths.contains(item.getLocation())){
                            ToastUtils.showToast(context, "此附件已经被选择");
                            holder.fmark.setChecked(false);
                            item.setMarked(false);
                            MarkedItemList.removeSelectedItem(item.getLocation());
                        }
                    }


                }else {
                    item.setMarked(isChecked);

                    MarkedItemList.removeSelectedItem(item.getLocation());
                }
//                item.setMarked(isChecked);
//                if (item.isMarked()) {
//                    if (properties.selection_mode == DialogConfigs.MULTI_MODE) {
//                        MarkedItemList.addSelectedItem(item);
//                    } else {
//                        MarkedItemList.addSingleFile(item);
//                    }
//                } else {
//                    MarkedItemList.removeSelectedItem(item.getLocation());
//                }
                notifyItemChecked.notifyCheckBoxIsClicked();
            }
        });
        return view;
    }

    private class ViewHolder {
        ImageView type_icon;
        TextView name, type;
        MaterialCheckbox fmark;

        ViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.fname);
            type = (TextView) itemView.findViewById(R.id.ftype);
            type_icon = (ImageView) itemView.findViewById(R.id.image_type);
            fmark = (MaterialCheckbox) itemView.findViewById(R.id.file_mark);
        }
    }

    public void setNotifyItemCheckedListener(NotifyItemChecked notifyItemChecked) {
        this.notifyItemChecked = notifyItemChecked;
    }

    public void setInitialParams(int[] a,ArrayList<String> list) {
        selected_f_num = a[0];
        selected_f_length = a[1];
        mpaths  = list;
    }
}
