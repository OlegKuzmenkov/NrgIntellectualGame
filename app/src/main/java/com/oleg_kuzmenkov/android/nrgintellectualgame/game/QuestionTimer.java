package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import java.util.Timer;
import java.util.TimerTask;

public class QuestionTimer extends TimerTask {
    private static final Long TIMER_PERIOD = 1000L;

    private QuestionTimerCallback mQuestionTimerCallback;

    QuestionTimer(QuestionTimerCallback questionTimerCallback) {
        mQuestionTimerCallback = questionTimerCallback;
        Timer t = new Timer();
        t.scheduleAtFixedRate(this, 0, TIMER_PERIOD);
    }

    @Override
    public void run() {
        mQuestionTimerCallback.changeRemainQuestionTime();
    }
}
