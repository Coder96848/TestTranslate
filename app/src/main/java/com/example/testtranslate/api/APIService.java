package com.example.testtranslate.api;

import com.example.testtranslate.models.TranslateResponse;

import io.reactivex.Single;

import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    @POST("api/v1.5/tr.json/translate")
    Single<TranslateResponse> translateResponse(@Query("key") String key,
                                                @Query("text") String text,
                                                @Query("lang") String lang,
                                                @Query("format") String format,
                                                @Query("options") int options);
}
