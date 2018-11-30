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
    private static final String BROADCAST_ACTION = "DOWNLOAD_NEWS";
    private static final String BUNDLE_CONTENT = "BUNDLE_CONTENT";
    private static final String LOG_TAG = "NEWS_LIST_ACTIVITY";

    private RecyclerView mRecycler;
    private NewsListAdapter mAdapter;
    private FloatingActionButton mFab;
    private BroadcastReceiver mReceiver;
    private NewsPresenter mPresenter;
    private TextView mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initControls();
        
        createBroadcastReceiver();
        registerBroadcastReceiver();

        setupPresenter(savedInstanceState);
        mPresenter.getNews();
    }

    @Override
    public void startNewsUpdating() {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            ComponentName componentName = new ComponentName(NewsListActivity.this, NewsUpdatingService.class);
            JobInfo.Builder jobInfo = new JobInfo.Builder(101, componentName);
            jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            jobScheduler.schedule(jobInfo.build());
    }

    @Override
    public void displayNews(List<News> newsList) {
        mNotification.setText("List of all news:");

        if (mAdapter == null) {
            mAdapter = new NewsListAdapter(this, newsList);
        }

        mRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "NewsListActivity:onSaveInstanceState");
        outState.putSerializable(BUNDLE_CONTENT, mPresenter);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        // unregister BroadcastReceiver
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * Create BroadcastReceiver
     */
    private void createBroadcastReceiver() {
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "OnReceive");
                mPresenter.getNews();
            }
        };
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * Setup presenter. Create or restore it.
     */
    private void setupPresenter(final Bundle savedInstanceState){
        if (savedInstanceState == null) {
            mPresenter = new NewsPresenter();
        } else {
            mPresenter = (NewsPresenter) savedInstanceState.getSerializable(BUNDLE_CONTENT);
        }

        mPresenter.setView(this);
        mPresenter.setRepository(RepositoryImpl.get(this));
    }

    private void initControls() {
        mNotification = findViewById(R.id.loading_text_view);

        mFab = findViewById(R.id.floating_action_button);
        mFab.setVisibility(View.INVISIBLE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecycler.smoothScrollToPosition(0);
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecycler = findViewById(R.id.news_recycler_view);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // scrolling up
                    mFab.setVisibility(View.VISIBLE);
                } else {
                    // scrolling down
                    mFab.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
