package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import android.util.Log;

public class QuestionPause extends Thread {
    private static final String LOG_TAG = "Message";
    private static final Long PAUSE_PERIOD = 2000L;

    private QuestionPauseCallback mQuestionPauseCallback;

    QuestionPause(QuestionPauseCallback questionPauseCallback) {
        mQuestionPauseCallback = questionPauseCallback;
    }

    @Override
    public void run() {
        try {
            Log.d(LOG_TAG, "QuestionPause!!!!");
            Thread.sleep(PAUSE_PERIOD);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mQuestionPauseCallback.finishPause();
    }

}
