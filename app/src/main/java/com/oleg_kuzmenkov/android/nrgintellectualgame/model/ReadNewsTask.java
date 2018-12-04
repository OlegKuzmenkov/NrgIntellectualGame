package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

class ReadNewsTask extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = "READ_NEWS_TASK";

    private SQLiteDatabase mDatabase;
    private Repository.ReadNewsCallback mListener;
    private List<News> mNewsList;

    ReadNewsTask(final SQLiteDatabase database, final List<News> news, Repository.ReadNewsCallback listener) {
        mDatabase = database;
        mNewsList = news;
        mListener = listener;
    }

    protected Void doInBackground(Void... params) {
        readNews();
        return null;
    }

    protected void onPostExecute(Void result) {
        Log.d(LOG_TAG, "Loading News is finished");
        mListener.onFinished(mNewsList);
    }

    /**
     * Read news list from database
     */
    private void readNews() {
        Cursor c = mDatabase.query(Database.TABLE_NEWS, null, null,
                null, null, null, null);

        if (c.moveToFirst()) {
            int newsSourceColIndex = c.getColumnIndex(Database.COLUMN_NEWS_SOURCE);
            int newsTitleColIndex = c.getColumnIndex(Database.COLUMN_NEWS_TITLE);
            int newsDescriptionColIndex = c.getColumnIndex(Database.COLUMN_NEWS_DESCRIPTION);
            int newsURLColIndex = c.getColumnIndex(Database.COLUMN_NEWS_URL);
            int newsImageColIndex = c.getColumnIndex(Database.COLUMN_NEWS_IMAGE);

            do {
                News news = new News();

                try {
                    news.setSource(c.getString(newsSourceColIndex));
                    news.setTitle(c.getString(newsTitleColIndex));
                    news.setDescription(c.getString(newsDescriptionColIndex));
                    news.setUrl(c.getString(newsURLColIndex));
                    byte[] byteArray = c.getBlob(newsImageColIndex);

                    if (byteArray != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        news.setImage(bitmap);
                    }

                    mNewsList.add(news);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "News contains an error!");
                }

                Log.d(LOG_TAG, String.format("Count of news is - %d", mNewsList.size()));
            } while (c.moveToNext());
        }

        c.close();
    }
}

