package com.example.testtranslate;

import android.util.Log;

import com.example.testtranslate.api.APILanguage;
import com.example.testtranslate.models.LanguageBody;
import com.example.testtranslate.interfaces.OnLanguageResponseListener;
import com.example.testtranslate.models.LanguageResponse;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class YandexLangs {

    private static final String TAG = YandexLangs.class.getSimpleName();
    private LanguageBody mLanguageBody;
    private OnLanguageResponseListener listener;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public void setLanguageBody(LanguageBody mLanguageBody) {
        this.mLanguageBody = mLanguageBody;
    }

    public void getResponse() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-type", "application/json")
                    .build();
            return chain.proceed(request);
        });

        String url = "https://translate.yandex.net/";
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .client(httpClient.build())
                .build();
        APILanguage mApi = retrofit.create(APILanguage.class);

        mDisposable.add(mApi.languageResponse(mLanguageBody.getKey(), mLanguageBody.getUi())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(languageResponse -> listener.getLangs(languageResponse),
                        throwable -> Log.e(TAG, "Unable to get language", throwable)));
    }

    public void setListener(OnLanguageResponseListener listener) {
        this.listener = listener;
    }

    public void clearDisposable(){
        mDisposable.clear();
    }
}
