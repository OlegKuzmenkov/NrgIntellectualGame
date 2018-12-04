package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.News;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;

import java.io.Serializable;
import java.util.List;

public class ReadNewsPresenter implements Repository.ReadNewsCallback, Serializable {
    private transient NewsView mNewsView;
    private transient Repository mRepository;

    private boolean mIsServiceStart;

    ReadNewsPresenter() { }

    public void setView(NewsView newsView) {
        mNewsView = newsView;
    }

    void setRepository(Repository repository) {
        mRepository = repository;
    }

    /**
     * Detach presenter with view and repository
     */
    void detach() {
        mNewsView = null;
        mRepository = null;
    }

    /**
     * Get list of news from Internet or local database
     */
     void getNews() {
        if (!mIsServiceStart) {
            //start updating of the news
            mIsServiceStart = true;
            mNewsView.startNewsUpdating();
        } else {
            mRepository.getNewsList(this);
        }
    }

    /**
     * This is callback from GameData. Show list of available news
     */
    @Override
    public void onFinished(List<News> list) {
        mNewsView.displayNews(list);
    }
}
