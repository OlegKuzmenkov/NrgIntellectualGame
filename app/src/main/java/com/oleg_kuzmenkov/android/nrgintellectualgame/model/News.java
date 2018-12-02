package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import android.graphics.Bitmap;

public class News {
    private String mSource;
    private String mTitle;
    private String mDescription;
    private String mUrl;
    private Bitmap mImage;

    News() { }

    public String getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource = source;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }
}
