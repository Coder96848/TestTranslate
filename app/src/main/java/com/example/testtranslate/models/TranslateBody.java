package com.example.testtranslate.models;


public class TranslateBody{

    private String key;
    private String text;
    private String lang;
    private final String format = "plain";
    private final int options = 1;

    public TranslateBody(String key, String text, String lang){
        this.key = key;
        this.text = text;
        this.lang = lang;
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public String getLang() {
        return lang;
    }

    public String getFormat() {
        return format;
    }

    public int getOptions() {
        return options;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }



}
