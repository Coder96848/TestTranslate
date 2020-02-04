package com.example.testtranslate.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.testtranslate.R;
import com.example.testtranslate.YandexLangs;
import com.example.testtranslate.models.LanguageBody;
import com.example.testtranslate.models.LanguageResponse;

public class SplashActivity extends AppCompatActivity {

    private YandexLangs mYandexLangs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent = new Intent(this, MainActivity.class);
        mYandexLangs = new YandexLangs();
        LanguageBody languageBody = new LanguageBody(getString(R.string.key_yandex_api), "ru");

        mYandexLangs.setLanguageBody(languageBody);
        mYandexLangs.getResponse();
        mYandexLangs.setListener(languageResponse -> {
            intent.putExtra(LanguageResponse.class.getSimpleName(), languageResponse);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mYandexLangs.clearDisposable();
    }
}
