package com.zzu.zk.zhiwen.beans;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

@SuppressLint("ParcelCreator")
public class Label  implements SearchSuggestion {

    String label;

    public Label(Parcel source) {
        this.label = source.readString();

    }
    public Label(String suggestion) {
        this.label = suggestion;
    }
    @Override
    public String getBody() {
        return label;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Parcelable.Creator<Label> CREATOR = new Parcelable.Creator<Label>() {
        @Override
        public Label createFromParcel(Parcel in) {
            return new Label(in);
        }

        @Override
        public Label[] newArray(int size) {
            return new Label[size];
        }
    };
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
    }
}
