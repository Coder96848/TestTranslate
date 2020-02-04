package com.example.testtranslate;

import android.util.Log;

import com.example.testtranslate.api.APIService;
import com.example.testtranslate.models.TranslateBody;
import com.example.testtranslate.interfaces.OnTranslateFetchedListener;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class YandexTranslate {

    private static final String TAG = YandexTranslate.class.getSimpleName();
    private TranslateBody mTranslateBody;
    private static YandexTranslate instance = null;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private OnTranslateFetchedListener listener;

    public void setTranslateBody(TranslateBody mTranslateBody) {
        this.mTranslateBody = mTranslateBody;
    }

    public void setListener(OnTranslateFetchedListener listener) {
        this.listener = listener;
    }

    public static YandexTranslate getInstance() {
        if (instance == null) {
            instance = new YandexTranslate();
        }
        return instance;
    }

    public void getResponse(){

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")
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

        APIService mApi = retrofit.create(APIService.class);

        mDisposable.add(mApi.translateResponse(mTranslateBody.getKey(), mTranslateBody.getText(), mTranslateBody.getLang(),
                mTranslateBody.getFormat(), mTranslateBody.getOptions())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(translateResponse -> listener.getTranslate(translateResponse),
                        throwable -> Log.e(TAG, "Unable to get translate", throwable)));
    }

    public void clearDisposable(){
        mDisposable.clear();
    }
}
