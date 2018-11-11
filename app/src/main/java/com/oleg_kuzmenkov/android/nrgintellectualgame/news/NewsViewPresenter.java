package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.support.annotation.NonNull;
import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.News;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;

import java.io.Serializable;
import java.util.List;

public class NewsViewPresenter implements Repository.NewsOnFinishedListener, Serializable {
    private static final String LOG_TAG = "Message";

    private NewsView mNewsView;
    private Repository mRepository;

    public NewsViewPresenter(@NonNull Repository repository) {
        mRepository = repository;
    }

    public void setView(NewsView newsView) {
        mNewsView = newsView;
    }

    public void detach() {
        mNewsView = null;
        mRepository = null;
    }

    public void getNews() {
        mRepository.getNewsFromDatabase(this);
    }

    @Override
    public void onFinishedGettingNews(List<News> list) {
        Log.d(LOG_TAG, "Count of news = " + list.size());
        if (list.size() < 10) {
            //start updating of the news
            mNewsView.startNewsUpdating();
        } else {
            if (list.size() == 10) {
                //display news
                mNewsView.displayNews(list);
            }
        }
    }
}
