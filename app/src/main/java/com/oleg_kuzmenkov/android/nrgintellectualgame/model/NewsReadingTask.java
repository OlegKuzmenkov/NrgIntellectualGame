package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class NewsReadingTask extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = "Message";

    private SQLiteDatabase mDatabase;
    private Repository.NewsOnFinishedListener mListener;
    private List<News> mNewsList;

    NewsReadingTask(final SQLiteDatabase database, final List<News> news, Repository.NewsOnFinishedListener listener) {
        mDatabase = database;
        mNewsList = news;
        mListener = listener;
    }

    protected Void doInBackground(Void... params) {
        getNewsFromDatabase();
        return null;
    }

    protected void onPostExecute(Void result) {
        Log.d(LOG_TAG, "Loading News is finished");
        mListener.onFinishedGettingNews(mNewsList);
    }

    private void getNewsFromDatabase() {
        mNewsList = new ArrayList<>();
        Cursor c = mDatabase.query(Database.TABLE_NEWS, null, null, null,
                null, null, null);
        if (c.moveToFirst()) {
            int newsSourceColIndex = c.getColumnIndex(Database.COLUMN_NEWS_SOURCE);
            int newsTitleColIndex = c.getColumnIndex(Database.COLUMN_NEWS_TITLE);
            int newsDescriptionColIndex = c.getColumnIndex(Database.COLUMN_NEWS_DESCRIPTION);
            int newsURLColIndex = c.getColumnIndex(Database.COLUMN_NEWS_URL);
            int newsImageColIndex = c.getColumnIndex(Database.COLUMN_NEWS_IMAGE);
            do {
                News news = new News();
                try {
                    news.setSourceName(c.getString(newsSourceColIndex));
                    news.setTitle(c.getString(newsTitleColIndex));
                    news.setDescription(c.getString(newsDescriptionColIndex));
                    news.setUrl(c.getString(newsURLColIndex));
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Error");
                }

                byte[] byteArray = c.getBlob(newsImageColIndex);
                if (byteArray != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    news.setImage(bitmap);
                }
                mNewsList.add(news);
                Log.d(LOG_TAG, "Count of news is " + mNewsList.size());
            } while (c.moveToNext());
        } else {
            // table is empty
            Log.d(LOG_TAG, "Count of news is 0");
        }
        c.close();
    }
}
