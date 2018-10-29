package com.zzu.zk.zhiwen.customed_ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.mylhyl.circledialog.BaseCircleDialog;
import com.zzu.zk.zhiwen.R;
import com.zzu.zk.zhiwen.beans.Label;
import com.zzu.zk.zhiwen.utils.ColorSuggestion;
import com.zzu.zk.zhiwen.utils.ColorWrapper;
import com.zzu.zk.zhiwen.utils.DataHelper;
import com.zzu.zk.zhiwen.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class ChooseLabelDia extends BaseCircleDialog {
    private String mLastQuery = "";
    private boolean mIsDarkSearchTheme = false;
   private static List<String>   selectedLabels = new ArrayList<>();
private static  OnItemClickListener onItemClickListener;
private static String ss = "";
private static int i;
    public static ChooseLabelDia getInstance(OnItemClickListener listener,List<String> ll,int num) {
        ChooseLabelDia dialogFragment = new ChooseLabelDia();
        dialogFragment.setCanceledBack(false);
        dialogFragment.setCanceledOnTouchOutside(false);
        dialogFragment.setGravity(Gravity.BOTTOM);
        dialogFragment.setWidth(1f);
        i=num;
        onItemClickListener = listener;
        selectedLabels = ll;
        if(ll!=null){
            StringBuilder sb = new StringBuilder();
            for (String s:ll){
                sb.append(s);
            }
            ss = sb.toString();
        }

        return dialogFragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View view = getView();
        final FloatingSearchView floatingSearchView = view.findViewById(R.id.floating_search_view);
        if(i==0){
            floatingSearchView.setSearchHint("搜索标签...");
        }else {
            floatingSearchView.setSearchHint("搜索课程...");
        }

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    floatingSearchView.clearSuggestions();
                } else {
                    mLastQuery = newQuery;
                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    floatingSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.

                    DataHelper.findLabel(view.getContext(), newQuery,ss,i,
                             new DataHelper.OnFindLabelListener() {

                                @Override
                                public void onResults(List<Label> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    floatingSearchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    floatingSearchView.hideProgress();
                                }
                            });

                }


            }
        });
        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                final Label colorSuggestion = (Label) searchSuggestion;
                DataHelper.findColors(view.getContext(), colorSuggestion.getBody(),
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(List<ColorWrapper> results) {

                                onItemClickListener.OnItemClick(colorSuggestion.getBody());
                                ChooseLabelDia.this.dismiss();
                            }

                        });



            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

//                DataHelper.findColors(ChooseLabel.this, query,
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<ColorWrapper> results) {
//                                mSearchResultsAdapter.swapData(results);
//                            }
//
//                        });

            }
        });



        floatingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

//                //show suggestions when search bar gains focus (typically history suggestions)
//                floatingSearchView.swapSuggestions(DataHelper.getHistory(view.getContext(), 3));


            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                floatingSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());


            }
        });
        floatingSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                ChooseLabelDia.this.dismiss();

            }
        });


        floatingSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                Label label = (Label) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#E91E63" : "#E91E63";

//                if (colorSuggestion.getIsHistory()) {
//                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
//                            R.drawable.ic_history_black_24dp, null));
//
//                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
//                    leftIcon.setAlpha(.36f);
//                } else {
//                    leftIcon.setAlpha(0.0f);
//                    leftIcon.setImageDrawable(null);
//                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = label.getBody()
                        .replaceFirst(floatingSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + floatingSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }

        });



    }

    @Override
    public View createView(Context context, LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.choose_label_dia, container, false);
    }
    public interface OnItemClickListener{
        void OnItemClick(String s);
        }
}
