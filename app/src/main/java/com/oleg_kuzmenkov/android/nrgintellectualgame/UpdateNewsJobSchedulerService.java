package com.oleg_kuzmenkov.android.nrgintellectualgame;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class UpdateNewsJobSchedulerService extends JobService {
    private final String LOG_TAG = "Message";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(LOG_TAG, "onStartJob ");
        new RequestTask(getApplicationContext()).execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
