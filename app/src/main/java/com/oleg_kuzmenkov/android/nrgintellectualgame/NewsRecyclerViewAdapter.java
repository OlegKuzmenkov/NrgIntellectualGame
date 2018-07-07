package com.oleg_kuzmenkov.android.nrgintellectualgame;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewHolder> {
    private final Context context;
    private List<News> mNews;

    public NewsRecyclerViewAdapter(Context context, List<News> news) {
        this.context = context;
        mNews = news;
    }

    @Override
    public NewsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.news_view, parent, false);
        return new NewsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsRecyclerViewHolder holder, int position) {
        News news = mNews.get(position);
        holder.bindNews(news, position);
    }

    @Override
    public int getItemCount() {
        return  mNews.size();
    }
}