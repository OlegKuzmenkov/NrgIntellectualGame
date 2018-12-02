package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.News;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListHolder> {
    private final Context mContext;
    private List<News> mNews;

    NewsListAdapter(final Context context, List<News> news) {
        mContext = context;
        mNews = news;
    }

    @Override
    public NewsListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.news_view, parent, false);
        return new NewsListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsListHolder holder, int position) {
        News news = mNews.get(position);
        holder.bindNews(news, position);
    }

    @Override
    public int getItemCount() {
        return  mNews.size();
    }
}
