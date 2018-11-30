package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.QuestionsDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class NewsUpdatingTask extends AsyncTask<Void, Void, Void> {
    private static final String BROADCAST_ACTION = "DOWNLOAD_NEWS";
    private static final String LOG_TAG = "NEWS_UPDATING_TASK";
    private static final int REQUIRED_NEWS_COUNT = 10;

    private Context mContext;
    private QuestionsDatabase mDatabase;

    NewsUpdatingTask(final Context context) {
        mContext = context;
        mDatabase = new QuestionsDatabase(mContext);
    }

    protected Void doInBackground(Void... params) {
        String response = getJson();
        saveNews(response);
        return null;
    }

    protected void onPostExecute(Void result) {
        Log.d(LOG_TAG, "Loading News is finished");
        Intent intent = new Intent(BROADCAST_ACTION);
        mContext.sendBroadcast(intent);
    }

    /**
     * Parse json from API
     */
    private void saveNews(String json) {
        SQLiteDatabase database = mDatabase.getWritableDatabase();
        //clear table
        database.delete(QuestionsDatabase.TABLE_NEWS, null, null);

        ContentValues cv = new ContentValues();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("articles");

            if (jsonArray != null && jsonArray.length() > 0) {
                // save only ten news
                for (int i = 0; i < REQUIRED_NEWS_COUNT; i++) {
                    JSONObject newsObj = jsonArray.getJSONObject(i);
                    JSONObject sourceObj = newsObj.getJSONObject("source");
                    cv.put(QuestionsDatabase.COLUMN_NEWS_SOURCE, sourceObj.getString("name"));
                    cv.put(QuestionsDatabase.COLUMN_NEWS_TITLE, newsObj.getString("title"));
                    cv.put(QuestionsDatabase.COLUMN_NEWS_DESCRIPTION, newsObj.getString("description"));
                    cv.put(QuestionsDatabase.COLUMN_NEWS_URL, newsObj.getString("url"));

                    Bitmap bitmap = downloadImage(newsObj.getString("urlToImage"));
                    //convert bitmap into blob
                    byte[] byteArray = getBitmapBlob(bitmap);
                    cv.put(QuestionsDatabase.COLUMN_NEWS_IMAGE, byteArray);

                    database.insert(QuestionsDatabase.TABLE_NEWS, null, cv);
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error of news saving");
            e.printStackTrace();
        }

        database.close();
    }

    /**
     * Get Json from API
     */
    private String getJson() {
        String serverResponse = "";

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://newsapi.org/v2/top-headlines").newBuilder();
        urlBuilder.addQueryParameter("country", "ru");
        urlBuilder.addQueryParameter("apikey", "cbf087cac2c449ada1880fdf9a4587ab");
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();

        try {
            Response response = client.newCall(request).execute();
            serverResponse = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serverResponse;
    }

    /**
     * Get Image by URL and set into bitmap
     */
    private Bitmap downloadImage(final String url) {
        Bitmap bitmap = null;

        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Convert bitmap to blob
     */
    private byte[] getBitmapBlob(Bitmap bitmap) {
        byte[] byteArray = null;

        if (bitmap != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byteArray = bos.toByteArray();
        }

        return byteArray;
    }
}
