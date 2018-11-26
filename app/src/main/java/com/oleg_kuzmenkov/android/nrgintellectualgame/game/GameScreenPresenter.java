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

public class GameScreenPresenter implements Repository.QuestionOnFinishedListener, Serializable,
        QuestionTimerCallback, QuestionPauseCallback {

    public static final int COUNT_QUESTIONS_FOR_GAME = 3;
    private static final String LOG_TAG = "GameScreenPresenter";
    private static final int COUNT_SECONDS_FOR_RED_INDICATOR = 3;
    private static final int COUNT_SECONDS_FOR_QUESTION = 10;

    private int mCurrentQuestionIndex;
    private int mRightAnswersCount;
    private int mQuestionRemainTime;
    private boolean mAnswerIsDone;
    private QuestionTimer mQuestionTimer;
    private QuestionPause mQuestionPause;
    private List<Question> mGameQuestionsList;
    private User mCurrentUser;

    private GameScreenView mGameScreenView;
    private Repository mRepository;

    GameScreenPresenter(@NonNull final Repository repository) {
        mRepository = repository;
    }

    public void setView(final GameScreenView gameScreenView) {
        mGameScreenView = gameScreenView;
    }

    public void setRepository(final Repository repository) {
        mRepository = repository;
    }

    /**
     * Detach View and presenter
     */
    public void detach() {
        mQuestionTimer.cancel();
        mGameScreenView = null;
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
            mQuestionRemainTime = COUNT_SECONDS_FOR_QUESTION;
            mGameScreenView.setGreenTimeIndicator();
            getCurrentQuestion();
        }
    }

    private void getCurrentQuestion() {
            mQuestionTimer = new QuestionTimer(this);
            mGameScreenView.displayQuestion(mGameQuestionsList.get(mCurrentQuestionIndex));
    }

    public void restoreQuestion() {
        if (mAnswerIsDone) {
            getNextQuestion();
            return;
        }

        if (mQuestionRemainTime == 0) {
            //time is left
            //mGameScreenView.
            return;
        }

        if (mQuestionRemainTime < 4) {
            mGameScreenView.setRedTimeIndicator();
        }
        getCurrentQuestion();
        //mGameScreenView.displayQuestion(mQuestionListForGame.get(mNumberOfCurrentQuestion));
        //mQuestionTimer = new QuestionTimer(this);
    }

    public void setUser(final User user) {
        mCurrentUser = user;
    }

    public void getUserLocation() {
        if (mCurrentUser.getLatitude() == 0) {
            Log.d(LOG_TAG, "It is a new User. We must get his location!!!");
            //get location
            mGameScreenView.startLocationService();
        }
    }

    /**
     * Set user's location
     */
    public void setUserLocation(double latitude, double longitude) {
        mCurrentUser.setLatitude(latitude);
        mCurrentUser.setLongitude(longitude);
        // stop getting user's location
        mGameScreenView.stopLocationService();
    }

    /**
     * Show result of the game
     */
    private void finishGame() {
        updateUserStatistics();
        mGameScreenView.displayResultsOfGame(mRightAnswersCount);
    }

    private void updateUserStatistics() {
        mCurrentUser.setCountRightAnswers(mCurrentUser.getCountRightAnswers() + mRightAnswersCount);
        mCurrentUser.setCountAnswers(mCurrentUser.getCountAnswers() + mGameQuestionsList.size());
        mRepository.updateUserData(mCurrentUser);
    }

    /**
     * Check user's answer
     */
    public void checkAnswer(String answer) {
        Log.d(LOG_TAG, "Answer is = " + answer);
        mQuestionTimer.cancel();
        mAnswerIsDone = true;
        mGameScreenView.enableAnswerButtons(false);

        if (mGameQuestionsList.get(mCurrentQuestionIndex).getRightAnswer().equals(answer)) {
            //answer is true
            Log.d(LOG_TAG, "Answer is true = " + answer);
            mRightAnswersCount++;
            mGameScreenView.displayRightAnswerResult(answer);
        } else {
            //answer is wrong
            Log.d(LOG_TAG, "Answer is wrong = " + answer);
            mGameScreenView.displayWrongAnswerResult(mGameQuestionsList.get(mCurrentQuestionIndex).getRightAnswer(), answer);
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

        while (mGameQuestionsList.size() < COUNT_QUESTIONS_FOR_GAME) {
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
        mGameScreenView.setQuestionRemainTime(mQuestionRemainTime);

        if (mQuestionRemainTime == COUNT_SECONDS_FOR_RED_INDICATOR) {
            mGameScreenView.setRedTimeIndicator();
            return;
        }

        if (mQuestionRemainTime == 0) {
            mQuestionTimer.cancel();
            mGameScreenView.displayRightAnswer(mGameQuestionsList.get(mCurrentQuestionIndex).getRightAnswer());
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
        mGameScreenView.clearButtons();
        mGameScreenView.enableAnswerButtons(true);
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
        mQuestionRemainTime = COUNT_SECONDS_FOR_QUESTION;
        getCurrentQuestion();
    }
}
