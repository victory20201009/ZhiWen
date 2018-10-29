package com.zzu.zk.zhiwen.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zzu.zk.zhiwen.activity.Home;
import com.zzu.zk.zhiwen.beans.Label;
import com.zzu.zk.zhiwen.constant.Cons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHelper {

    private static final String COLORS_FILE_NAME = "colors.json";

    private static List<ColorWrapper> sColorWrappers = new ArrayList<>();
    private static List<Label> labels = new ArrayList<>();
    private static List<ColorSuggestion> sColorSuggestions =
            new ArrayList<>(Arrays.asList(
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),

                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),

                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),

                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),

                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),

                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),

                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),

                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),

                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("green"),
                    new ColorSuggestion("blue"),
                    new ColorSuggestion("pink"),
                    new ColorSuggestion("purple"),
                    new ColorSuggestion("brown"),
                    new ColorSuggestion("gray"),
                    new ColorSuggestion("Granny Smith Apple"),
                    new ColorSuggestion("Indigo"),
                    new ColorSuggestion("Periwinkle"),
                    new ColorSuggestion("Mahogany"),
                    new ColorSuggestion("Maize"),
                    new ColorSuggestion("Mahogany"),
                    new ColorSuggestion("Outer Space"),
                    new ColorSuggestion("Melon"),
                    new ColorSuggestion("Yellow"),
                    new ColorSuggestion("Orange"),
                    new ColorSuggestion("Red"),
                    new ColorSuggestion("Orchid")));

    public interface OnFindColorsListener {
        void onResults(List<ColorWrapper> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<ColorSuggestion> results);
    }

    public interface OnFindLabelListener {
        void onResults(List<Label> results);
    }

    public static List<ColorSuggestion> getHistory(Context context, int count) {

        List<ColorSuggestion> suggestionList = new ArrayList<>();
        ColorSuggestion colorSuggestion;
        for (int i = 0; i < sColorSuggestions.size(); i++) {
            colorSuggestion = sColorSuggestions.get(i);
            colorSuggestion.setIsHistory(true);
            suggestionList.add(colorSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (ColorSuggestion colorSuggestion : sColorSuggestions) {
            colorSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<ColorSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (ColorSuggestion suggestion : sColorSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
//                            if (limit != -1 && suggestionList.size() == limit) {
//                                break;
//                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<ColorSuggestion>() {
                    @Override
                    public int compare(ColorSuggestion lhs, ColorSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<ColorSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public static void findColors(Context context, String query, final OnFindColorsListener listener) {
        initColorWrapperList(context);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                List<ColorWrapper> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {

                    for (ColorWrapper color : sColorWrappers) {
                        if (color.getName().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {


                            suggestionList.add(color);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<ColorWrapper>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initColorWrapperList(Context context) {

        if (sColorWrappers.isEmpty()) {
            String jsonString = loadJsonFromAssets(context);
            sColorWrappers = deserializeColors(jsonString);
        }
    }

    private static String loadJsonFromAssets(Context context) {

        String jsonString;

        try {
            InputStream is = context.getAssets().open(COLORS_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonString;
    }

    private static void loadLabelListFromFile(Context context,int ii) {
        try {
            if(ii==0){
                JSONArray jsonArray = JSONArray.parseArray(EncryptionUtils.inputstream2String(
                        new FileInputStream(new File(context.getFilesDir().
                                getAbsolutePath() + Cons.label))));
                for (int i = 0; i < jsonArray.size(); i++) {
                    labels.add(new Label((String) (jsonArray.get(i))));
                }
            }else {
                JSONArray jsonArray = JSONArray.parseArray(EncryptionUtils.inputstream2String(
                        new FileInputStream(new File(context.getFilesDir().
                                getAbsolutePath() + Cons.course))));
                for (int i = 0; i < jsonArray.size(); i++) {
                    labels.add(new Label((String) (jsonArray.get(i))));
                }
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }


    }

    public static void findLabel(final Context context, String query, final String ll, final int i,
                                 final OnFindLabelListener listener) {
        final List<Label> suggestionlabels = new ArrayList<>();
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (labels.isEmpty()) {
                    loadLabelListFromFile(context,i);
                }

                if (!(constraint == null || constraint.length() == 0)) {

                    for (Label label : labels) {
                        if(i==0){
                            if (label.getBody().contains(constraint.toString())&&!ll.contains(label.getBody())) {
//                                Log.i(Cons.debugLabel, label.getBody());
                                suggestionlabels.add(label);

                            }
                        }else {
                            if (label.getBody().contains(constraint.toString())) {
//                                Log.i(Cons.debugLabel, label.getBody());
                                suggestionlabels.add(label);

                            }
                        }

                    }
                }

//                FilterResults results = new FilterResults();
//
//                results.values = suggestionlabels;
//                results.count = suggestionlabels.size();

                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {

                    listener.onResults(suggestionlabels);
                }
            }
        }.filter(query);

    }

    private static List<ColorWrapper> deserializeColors(String jsonString) {

        Gson gson = new Gson();

        Type collectionType = new TypeToken<List<ColorWrapper>>() {
        }.getType();
        return gson.fromJson(jsonString, collectionType);
    }

}
