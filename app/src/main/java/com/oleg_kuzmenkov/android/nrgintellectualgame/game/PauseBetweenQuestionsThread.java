package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import android.util.Log;

public class PauseBetweenQuestionsThread extends Thread {
    private static final String LOG_TAG = "Message";
    private static final Long PAUSE_PERIOD = 2000L;

    private PauseBetweenQuestionsThreadCallbacks mPauseBetweenQuestionsThreadCallbacks;

    public PauseBetweenQuestionsThread(PauseBetweenQuestionsThreadCallbacks pauseBetweenQuestionsThreadCallbacks) {
        mPauseBetweenQuestionsThreadCallbacks = pauseBetweenQuestionsThreadCallbacks;
    }

    @Override
    public void run() {
        try {
            Log.d(LOG_TAG, "PauseBetweenQuestionsThread!!!!");
            Thread.sleep(PAUSE_PERIOD);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mPauseBetweenQuestionsThreadCallbacks.finishPause();
    }

}
