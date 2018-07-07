package com.oleg_kuzmenkov.android.nrgintellectualgame;

import java.util.List;

public interface NewsView {
    void startNewsUpdating();
    void displayNews(List<News> list);
}
