package com.example.testtranslate;

import android.app.Application;

import com.example.testtranslate.db.HistoryDAO;
import com.example.testtranslate.db.HistoryDB;

public class App extends Application {

    private static HistoryDB historyDB;
    private static HistoryDAO historyDAO;

    public static HistoryDAO getHistoryDAO() {
        return historyDAO;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            historyDB = HistoryDB.getInstance(getApplicationContext());
            historyDAO = historyDB.historyDAO();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void closeDb(){
        historyDB.close();
    }
}
