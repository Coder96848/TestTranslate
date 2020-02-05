package com.example.testtranslate.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface HistoryDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(HistoryResponse historyResponse);

    @Query("SELECT * FROM history_response_table")
    Flowable<List<HistoryResponse>> getHistory();

}
