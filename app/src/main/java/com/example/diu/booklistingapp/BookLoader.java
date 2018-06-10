package com.example.diu.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    /** Tag for log messages */
    private static final String LOG_TAG = BookLoader.class.getName();
    /** Query URL */
    private String mUrl;

    public BookLoader(Context context, String mUrl){
        super(context);
        this.mUrl=mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.v(LOG_TAG,"onStartLoading");
    }

    @Override
    public List<Book> loadInBackground() {
        if(mUrl==null){
            return null;
        }
        List<Book> books=QueryUtils.fetchBookData(mUrl);
        Log.v(LOG_TAG,"loadInBackground"+books);
        return books;
    }
}
