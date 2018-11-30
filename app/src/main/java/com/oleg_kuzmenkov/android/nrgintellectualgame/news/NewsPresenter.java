package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.News;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;

import java.io.Serializable;
import java.util.List;

public class NewsPresenter implements Repository.NewsOnFinishedListener, Serializable {
    private static final String LOG_TAG = "Message";

    private transient NewsView mNewsView;
    private transient Repository mRepository;

    private boolean mIsServiceStart;

    NewsPresenter() { }

    public void setView(NewsView newsView) {
        mNewsView = newsView;
    }

    public void setRepository(Repository repository) {
        mRepository = repository;
    }

    public void detach() {
        mNewsView = null;
        mRepository = null;
    }

    public void getNews() {
        if (!mIsServiceStart) {
            //start updating of the news
            mIsServiceStart = true;
            mNewsView.startNewsUpdating();
        } else {
            mRepository.getNewsFromDatabase(this);
        }
    }

    @Override
    public void onFinishedGettingNews(List<News> list) {
        Log.d(LOG_TAG, "Count of news = " + list.size());
        mNewsView.displayNews(list);
    }
}
