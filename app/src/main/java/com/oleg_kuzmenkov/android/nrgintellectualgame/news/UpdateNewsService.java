package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class UpdateNewsService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        new UpdateNewsTask(getApplicationContext()).execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
