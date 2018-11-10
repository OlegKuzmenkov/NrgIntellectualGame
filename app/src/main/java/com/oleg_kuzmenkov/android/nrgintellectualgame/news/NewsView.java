package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.News;

import java.util.List;

public interface NewsView {
    void startNewsUpdating();
    void displayNews(List<News> list);
}
