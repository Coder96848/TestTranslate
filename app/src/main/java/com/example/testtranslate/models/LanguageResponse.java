package com.example.testtranslate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageResponse implements Parcelable {

    @SerializedName("dirs")
    public List<String> dirs = null;
    @SerializedName("langs")
    public Map<String, String> langs = new HashMap<>();

    public List<String> getKeyLangs(){
        return new ArrayList<>(langs.keySet());
    }

    public List<String> getValueLangs(){
        return new ArrayList<>(langs.values());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.dirs);
        dest.writeInt(this.langs.size());
        for (Map.Entry<String, String> entry : this.langs.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public LanguageResponse() {
    }

    private LanguageResponse(Parcel in) {
        this.dirs = in.createStringArrayList();
        int langsSize = in.readInt();
        this.langs = new HashMap<>(langsSize);
        for (int i = 0; i < langsSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.langs.put(key, value);
        }
    }

    public static final Creator<LanguageResponse> CREATOR = new Creator<LanguageResponse>() {
        @Override
        public LanguageResponse createFromParcel(Parcel source) {
            return new LanguageResponse(source);
        }

        @Override
        public LanguageResponse[] newArray(int size) {
            return new LanguageResponse[size];
        }
    };
}
