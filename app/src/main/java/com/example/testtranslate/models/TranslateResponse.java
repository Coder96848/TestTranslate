package com.example.testtranslate.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TranslateResponse {

    @SerializedName("code")
    public Integer code;
    @SerializedName("lang")
    public String lang;
    @SerializedName("text")
    public List<String> text = null;

}
