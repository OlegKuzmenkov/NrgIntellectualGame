package com.oleg_kuzmenkov.android.nrgintellectualgame;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class RequestTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = "Message";
    private final String BROADCAST_ACTION = "download_news";
    private static final String TABLE = "news";

    private Context mContext;
    private QuestionsDatabase mDatabase;

    public RequestTask(Context context) {
        mContext = context;
        mDatabase = new QuestionsDatabase(mContext);
    }

    protected Void doInBackground(Void... params) {
        String response = getResponseFromAPI();
        parseJSON(response);
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
    private String parseJSON(String json){
        SQLiteDatabase db = mDatabase.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //clear table
        db.delete(TABLE,null,null);
        String result="";

        try {
            final JSONObject object = new JSONObject(json);
            final JSONArray jsonArray = object.getJSONArray("articles");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    // save only ten news
                    if(i == 10){
                        break;
                    }
                    JSONObject newsObj = jsonArray.getJSONObject(i);
                    final JSONObject sourceObj = newsObj.getJSONObject("source");
                    cv.put(QuestionsDatabase.COLUMN_NEWS_SOURCE,sourceObj.getString("name"));
                    cv.put(QuestionsDatabase.COLUMN_NEWS_TITLE,newsObj.getString("title"));
                    cv.put(QuestionsDatabase.COLUMN_NEWS_DESCRIPTION,newsObj.getString("description"));
                    cv.put(QuestionsDatabase.COLUMN_NEWS_URL,newsObj.getString("url"));

                    Bitmap bitmap = getImageByURL(newsObj.getString("urlToImage"));
                    //convert bitmap into blob
                    if(bitmap!= null) {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] bArray = bos.toByteArray();
                        cv.put(QuestionsDatabase.COLUMN_NEWS_IMAGE, bArray);
                    } else{
                        byte[] bArray = null;
                        cv.put(QuestionsDatabase.COLUMN_NEWS_IMAGE, bArray);
                    }
                    db.insert(TABLE, null, cv);
                }
            }
        }
        catch(Exception e){
            Log.e("Error of parcing", e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get Json from API
     */
    private String getResponseFromAPI(){
        String serverResponse="";
        final OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://newsapi.org/v2/top-headlines").newBuilder();
        urlBuilder.addQueryParameter("country", "ru");
        urlBuilder.addQueryParameter("apikey", "cbf087cac2c449ada1880fdf9a4587ab");
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();
            serverResponse = response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return serverResponse;
    }

    /**
     * Get Image by URL and set into bitmap
     */
    private Bitmap getImageByURL(String url){
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
}
