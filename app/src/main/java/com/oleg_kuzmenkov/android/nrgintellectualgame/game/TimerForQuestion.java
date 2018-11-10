package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import android.util.Log;
import android.widget.TextView;

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;

import java.util.Timer;
import java.util.TimerTask;

public class TimerForQuestion extends TimerTask {
    private static final String LOG_TAG = "Message";

    private int mRemainTime;
    private TextView mTimerTextView;
    private GameFragmentCallBacks mGameFragmentCallBacks;
    private boolean mIsRedIndicator;

    public TimerForQuestion(int timer, TextView timerTextView, GameFragmentCallBacks gameFragmentCallBacks) {
        Log.d(LOG_TAG,"Create Timer!!!!!!!!!!!!!!");
        mRemainTime = timer;
        mTimerTextView = timerTextView;
        mGameFragmentCallBacks = gameFragmentCallBacks;
        setIndicator();
        Timer t = new Timer();
        t.scheduleAtFixedRate(this,0,1000L);
    }

    @Override
    public void run() {
        if(mRemainTime == 0){
            Log.d(LOG_TAG,"Task is cancelled");
            mTimerTextView.post(new Runnable() {
                @Override
                public void run() {
                    mGameFragmentCallBacks.showRightAnswer();
                }
            });
            cancel();
        }
        Log.d(LOG_TAG,"mRemainTime = "+mRemainTime);
        mTimerTextView.post(new Runnable() {
            @Override
            public void run() {
                mTimerTextView.setText(Integer.toString(mRemainTime));

                if(mIsRedIndicator == false){
                    if(mRemainTime == 3){
                        mIsRedIndicator = true;
                        mTimerTextView.setBackgroundResource(R.drawable.time_red_indicator);
                    }
                }
                mRemainTime--;
            }
        });
    }

    private void setIndicator(){
        mTimerTextView.post(new Runnable() {
            @Override
            public void run() {
                if(mRemainTime > 3){
                    mIsRedIndicator = false;
                    mTimerTextView.setBackgroundResource(R.drawable.time_green_indicator);
                } else{
                    mIsRedIndicator = true;
                    mTimerTextView.setBackgroundResource(R.drawable.time_red_indicator);
                }
            }
        });
    }

    public int getRemainTime(){
        return mRemainTime;
    }
}
