package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import android.support.annotation.NonNull;
import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Question;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePresenter implements Repository.QuestionOnFinishedListener, Serializable,
        QuestionTimerCallback, QuestionPauseCallback {

    public static final int GAME_QUESTIONS_COUNT = 3;
    private static final String LOG_TAG = "GamePresenter";
    private static final int RED_INDICATOR_TIME = 3;
    private static final int QUESTION_AVAILABLE_TIME = 10;

    private int mCurrentQuestionIndex;
    private int mRightAnswersCount;
    private int mQuestionRemainTime;
    private boolean mAnswerIsDone;
    private QuestionTimer mQuestionTimer;
    private QuestionPause mQuestionPause;
    private List<Question> mGameQuestionsList;
    private User mCurrentUser;

    private GameView mGameView;
    private Repository mRepository;

    GamePresenter(@NonNull final Repository repository) {
        mRepository = repository;
    }

    public void setView(final GameView gameView) {
        mGameView = gameView;
    }

    public void setRepository(final Repository repository) {
        mRepository = repository;
    }

    /**
     * Detach View and presenter
     */
    public void detach() {
        mQuestionTimer.cancel();
        mGameView = null;
        mRepository = null;
    }

    public void startGame() {
            mRepository.getQuestionsFromDatabase(this);
    }

    private void getNextQuestion() {
        if (mCurrentQuestionIndex == (mGameQuestionsList.size() - 1)) {
            finishGame();
        } else {
            //get new question
            mCurrentQuestionIndex++;
            mQuestionRemainTime = QUESTION_AVAILABLE_TIME;
            mGameView.setGreenTimeIndicator();
            getCurrentQuestion();
        }
    }

    private void getCurrentQuestion() {
            mQuestionTimer = new QuestionTimer(this);
            mGameView.displayQuestion(mGameQuestionsList.get(mCurrentQuestionIndex));
    }

    public void restoreQuestion() {
        if (mAnswerIsDone) {
            getNextQuestion();
            return;
        }

        if (mQuestionRemainTime == 0) {
            //time is left
            //mGameView.
            return;
        }

        if (mQuestionRemainTime < 4) {
            mGameView.setRedTimeIndicator();
        }
        getCurrentQuestion();
        //mGameView.displayQuestion(mQuestionListForGame.get(mNumberOfCurrentQuestion));
        //mQuestionTimer = new QuestionTimer(this);
    }

    public void setUser(final User user) {
        mCurrentUser = user;
    }

    public void getUserLocation() {
        if (mCurrentUser.getLatitude() == 0) {
            Log.d(LOG_TAG, "It is a new User. We must get his location!!!");
            //get location
            mGameView.startLocationService();
        }
    }

    /**
     * Set user's location
     */
    public void setUserLocation(double latitude, double longitude) {
        mCurrentUser.setLatitude(latitude);
        mCurrentUser.setLongitude(longitude);
        // stop getting user's location
        mGameView.stopLocationService();
    }

    /**
     * Show result of the game
     */
    private void finishGame() {
        updateUserStatistics();
        mGameView.displayResultsOfGame(mRightAnswersCount);
    }

    private void updateUserStatistics() {
        mCurrentUser.setRightAnswersCount(mCurrentUser.getRightAnswersCount() + mRightAnswersCount);
        mCurrentUser.setAnswersCount(mCurrentUser.getAnswersCount() + mGameQuestionsList.size());
        mRepository.updateUserData(mCurrentUser);
    }

    /**
     * Check user's answer
     */
    public void checkAnswer(String answer) {
        Log.d(LOG_TAG, "Answer is = " + answer);
        mQuestionTimer.cancel();
        mAnswerIsDone = true;
        mGameView.enableAnswerButtons(false);

        if (mGameQuestionsList.get(mCurrentQuestionIndex).getRightAnswer().equals(answer)) {
            //answer is true
            Log.d(LOG_TAG, "Answer is true = " + answer);
            mRightAnswersCount++;
            mGameView.displayRightAnswerResult(answer);
        } else {
            //answer is wrong
            Log.d(LOG_TAG, "Answer is wrong = " + answer);
            mGameView.displayWrongAnswerResult(mGameQuestionsList.get(mCurrentQuestionIndex).getRightAnswer(), answer);
        }


        mQuestionPause = new QuestionPause(this);
        mQuestionPause.start();
    }

    /**
     * Choose list of random questions
     */
    private void chooseRandomQuestions(@NonNull final List<Question> list) {
        Random gen = new Random();
        int max = list.size();
        mGameQuestionsList = new ArrayList();

        while (mGameQuestionsList.size() < GAME_QUESTIONS_COUNT) {
            int index = gen.nextInt(max);
            if (mGameQuestionsList.contains(list.get(index)) == false) {
                mGameQuestionsList.add(list.get(index));
            }
        }
        Log.d(LOG_TAG, "Count of questions = " + mGameQuestionsList.size());
    }

    /**
     * Change remain time for question in View
     */
    @Override
    public void changeRemainQuestionTime() {
        mQuestionRemainTime--;
        Log.d(LOG_TAG, "Remain time = " + mQuestionRemainTime);
        mGameView.setQuestionRemainTime(mQuestionRemainTime);

        if (mQuestionRemainTime == RED_INDICATOR_TIME) {
            mGameView.setRedTimeIndicator();
            return;
        }

        if (mQuestionRemainTime == 0) {
            mQuestionTimer.cancel();
            mGameView.displayRightAnswer(mGameQuestionsList.get(mCurrentQuestionIndex).getRightAnswer());
            mQuestionPause = new QuestionPause(this);
            mQuestionPause.start();
        }
    }

    /**
     * Finish pause between questions
     */
    @Override
    public void finishPause() {
        //refresh game board
        mGameView.clearButtons();
        mGameView.enableAnswerButtons(true);
        getNextQuestion();
    }

    /**
     * Start the Game
     */
    @Override
    public void onFinishedGettingQuestions(final List<Question> list) {
        chooseRandomQuestions(list);
        mCurrentQuestionIndex = 0;
        mRightAnswersCount = 0;
        //refresh timer
        mQuestionRemainTime = QUESTION_AVAILABLE_TIME;
        getCurrentQuestion();
    }
}
