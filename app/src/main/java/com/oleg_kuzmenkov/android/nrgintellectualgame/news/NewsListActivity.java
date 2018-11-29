package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.News;
import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.RepositoryImpl;

import java.util.List;

public class NewsListActivity extends AppCompatActivity implements NewsView {
    private static final String BROADCAST_ACTION = "download_news";
    private static final String BUNDLE_CONTENT = "content";
    private static final String LOG_TAG = "Message";

    private RecyclerView mRecyclerView;
    private NewsListAdapter mAdapter;
    private FloatingActionButton mFloatingActionButton;
    private BroadcastReceiver mBroadcastReceiver;
    private NewsPresenter mPresenter;
    private TextView mLoadingTextView;
    private boolean mIsStartLoadingNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        if (savedInstanceState != null) {
            mIsStartLoadingNews = savedInstanceState.getBoolean(BUNDLE_CONTENT);
        }

        mPresenter = new NewsPresenter(RepositoryImpl.get(this));
        mPresenter.setView(this);

        findViewById(R.id.floating_action_button).setVisibility(View.INVISIBLE);
        mRecyclerView = findViewById(R.id.news_recycler_view);
        mLoadingTextView = findViewById(R.id.loading_text_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    Log.d(LOG_TAG, "Scrolling up");
                    findViewById(R.id.floating_action_button).setVisibility(View.VISIBLE);
                    // Scrolling up
                } else {
                    Log.d(LOG_TAG, "Scrolling down");
                    findViewById(R.id.floating_action_button).setVisibility(View.INVISIBLE);
                    // Scrolling down
                }
            }
        });

        mFloatingActionButton = findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });

        // create and register BroadcastReceiver
        createBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);

        mPresenter.getNews();
    }

    @Override
    public void startNewsUpdating() {
        if (mIsStartLoadingNews == false) {
            mIsStartLoadingNews = true;
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName(NewsListActivity.this, NewsUpdatingService.class);
            JobInfo.Builder jobInfo = new JobInfo.Builder(101, componentName);
            //jobInfo.setPeriodic(5000);
            //jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
            jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            jobScheduler.schedule(jobInfo.build());
        }
    }

    @Override
    public void displayNews(List<News> newsList) {
        mLoadingTextView.setText("List of all news:");
        mIsStartLoadingNews = false;
        if (mAdapter == null) {
            mAdapter = new NewsListAdapter(this, newsList);
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "NewsListActivity:onSaveInstanceState");
        outState.putBoolean(BUNDLE_CONTENT, mIsStartLoadingNews);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
        // unregister BroadcastReceiver
        unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * Create BroadcastReceiver
     */
    private void createBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "OnReceive");
                mPresenter.getNews();
            }
        };
    }
}
