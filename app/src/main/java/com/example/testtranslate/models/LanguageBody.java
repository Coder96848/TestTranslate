package com.example.testtranslate.models;

public class LanguageBody {

    private String key;
    private String ui;

    public LanguageBody() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public LanguageBody(String key, String ui){
        this.key = key;
        this.ui = ui;

    }
}
