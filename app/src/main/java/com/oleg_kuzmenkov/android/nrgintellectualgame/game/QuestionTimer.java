package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class QuestionTimer extends TimerTask {
    private static final String LOG_TAG = "Message";
    private static final Long TIMER_PERIOD = 1000L;

    private int mRemainTime;
    private QuestionTimerCallback mQuestionTimerCallback;

    QuestionTimer(QuestionTimerCallback questionTimerCallback) {
        Log.d(LOG_TAG, "Create Timer!!!!!!!!!!!!!!");
        mQuestionTimerCallback = questionTimerCallback;
        Timer t = new Timer();
        t.scheduleAtFixedRate(this, 0, TIMER_PERIOD);
    }

    @Override
    public void run() {
            //cancel();
        mQuestionTimerCallback.changeRemainQuestionTime();
    }
}
