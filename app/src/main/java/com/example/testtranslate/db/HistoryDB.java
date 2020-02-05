package com.example.testtranslate.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {HistoryResponse.class}, version = 1, exportSchema = false)
public abstract class HistoryDB extends RoomDatabase {

    public abstract HistoryDAO historyDAO();

    private static volatile HistoryDB INSTANCE;

    public static HistoryDB getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (HistoryDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HistoryDB.class, "product_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
