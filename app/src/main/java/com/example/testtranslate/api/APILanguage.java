package com.example.testtranslate.api;


import com.example.testtranslate.models.LanguageResponse;

import io.reactivex.Single;

import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APILanguage {
    @POST("api/v1.5/tr.json/getLangs")
    Single<LanguageResponse> languageResponse(@Query("key") String key,
                                              @Query("ui") String ui);
}
