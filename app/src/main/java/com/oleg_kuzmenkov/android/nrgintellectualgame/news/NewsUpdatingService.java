package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class NewsUpdatingService extends JobService {
    private static final String LOG_TAG = "Message";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(LOG_TAG, "onStartJob ");
        new NewsUpdatingTask(getApplicationContext()).execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
