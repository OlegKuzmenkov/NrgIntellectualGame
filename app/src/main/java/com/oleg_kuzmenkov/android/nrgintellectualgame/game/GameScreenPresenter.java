package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import android.support.annotation.NonNull;
import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.game.GameScreenView;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Question;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreenPresenter implements Repository.QuestionOnFinishedListener, Serializable, QuestionTimerCallBacks, PauseBetweenQuestionsThreadCallbacks {
    private static final String LOG_TAG = "Message";
    private static final int COUNT_QUESTIONS_FOR_GAME = 3;
    private static final int COUNT_SECONDS_FOR_QUESTION = 10;

    private int mNumberOfCurrentQuestion;
    private int mCountRightAnswers;
    private int mQuestionRemainTime;
    private boolean mAnswerIsDone;
    private QuestionTimer mQuestionTimer;
    private PauseBetweenQuestionsThread mPauseBetweenQuestionsThread;
    private List<Question> mQuestionListForGame;
    private User mCurrentUser;

    private GameScreenView mGameScreenView;
    private Repository mRepository;

    public GameScreenPresenter(@NonNull Repository repository) {
        mRepository = repository;
    }

    public void setView(GameScreenView gameScreenView) {
        mGameScreenView = gameScreenView;
    }

    public void setRepository(Repository repository) {
        mRepository = repository;
    }

    public void detach() {
        mQuestionTimer.cancel();
        mGameScreenView = null;
        mRepository = null;
    }

    public void onClickSinglePlayerButton() {
            mRepository.getQuestionsFromDatabase(this);
    }

    public void getNextQuestion() {
        if (isLastQuestion()) {
            showResultsOfTheGame();
        } else {
            //get new question
            mNumberOfCurrentQuestion++;
            mQuestionRemainTime = COUNT_SECONDS_FOR_QUESTION;
            mGameScreenView.setGreenTimeIndicator();
            getCurrentQuestion();
        }
    }

    public void getCurrentQuestion() {
        if (mGameScreenView != null) {
            mGameScreenView.displayQuestion(mQuestionListForGame.get(mNumberOfCurrentQuestion));
            mQuestionTimer = new QuestionTimer(this);

        }
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

    public boolean isLastQuestion() {
        if (mNumberOfCurrentQuestion == mQuestionListForGame.size() - 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Start the Game
     */
    public void startGame(final List<Question> list) {
        chooseRandomQuestions(list);
        mNumberOfCurrentQuestion = 0;
        mCountRightAnswers = 0;
        //refresh timer
        mQuestionRemainTime = COUNT_SECONDS_FOR_QUESTION;
        getCurrentQuestion();
    }

    public void setUser(User user) {
        mCurrentUser = user;
    }

    public void checkIsExistUserLocation() {
        if (mCurrentUser.getLatitude() == 0) {
            Log.d(LOG_TAG, "It is a new User. We must get his location!!!");
            //get location
            mGameScreenView.startGettingUserLocation();
        }
    }

    /**
     * Set User's location
     */
    public void setUserLocation(double latitude, double longitude) {
        mCurrentUser.setLatitude(latitude);
        mCurrentUser.setLongitude(longitude);
        // stop getting user's location
        mGameScreenView.stopGettingUserLocation();
    }

    /**
     * Show result of the game
     */
    public void showResultsOfTheGame() {
        Log.d(LOG_TAG, "Count of write answers = " + mCountRightAnswers);
        int countOfQuestions = mQuestionListForGame.size();
        int countOfRightAnswers = mCountRightAnswers;
        mCurrentUser.setCountRightAnswers(mCurrentUser.getCountRightAnswers() + countOfRightAnswers);
        mCurrentUser.setCountAnswers(mCurrentUser.getCountAnswers() + countOfQuestions);
        mRepository.updateUserData(mCurrentUser);
        mGameScreenView.displayResultsOfGame(countOfQuestions, countOfRightAnswers);
    }

    /**
     * Show result of the game
     *
     */
    public void checkAnswer(String answer) {
        Log.d(LOG_TAG, "Answer is = " + answer);
        mQuestionTimer.cancel();
        mAnswerIsDone = true;
        mGameScreenView.setDisableAnswerButtons();

        if (mQuestionListForGame.get(mNumberOfCurrentQuestion).getRightAnswer().equals(answer)) {
            //answer is true
            Log.d(LOG_TAG, "Answer is true = " + answer);
            mCountRightAnswers ++;
            mGameScreenView.displayRightAnswerResult(answer);
        } else {
            //answer is wrong
            Log.d(LOG_TAG, "Answer is wrong = " + answer);
            mGameScreenView.displayWrongAnswerResult(mQuestionListForGame.get(mNumberOfCurrentQuestion).getRightAnswer(), answer);
        }


        mPauseBetweenQuestionsThread = new PauseBetweenQuestionsThread(this);
        mPauseBetweenQuestionsThread.start();
    }

    /**
     * Choose list of random questions
     */
    private void chooseRandomQuestions(@NonNull final List<Question> list) {
        Random gen = new Random();
        int max = list.size();
        mQuestionListForGame = new ArrayList();

        while (mQuestionListForGame.size() < COUNT_QUESTIONS_FOR_GAME) {
            int index = gen.nextInt(max);
            if (mQuestionListForGame.contains(list.get(index)) == false) {
                mQuestionListForGame.add(list.get(index));
            }
        }
        Log.d(LOG_TAG, "Count of questions = " + mQuestionListForGame.size());
    }

    @Override
    public void changeRemainQuestionTime() {
        mQuestionRemainTime --;
        mGameScreenView.setQuestionRemainTime(mQuestionRemainTime);
        if(mQuestionRemainTime == 3) {
            mGameScreenView.setRedTimeIndicator();
        }
        if(mQuestionRemainTime == 0) {
            mQuestionTimer.cancel();
            mGameScreenView.displayRightAnswer(mQuestionListForGame.get(mNumberOfCurrentQuestion).getRightAnswer());
            mPauseBetweenQuestionsThread = new PauseBetweenQuestionsThread(this);
            mPauseBetweenQuestionsThread.start();
        }
        Log.d(LOG_TAG, "Remain time = " + mQuestionRemainTime);
    }

    @Override
    public void finishPause() {
        mGameScreenView.continueGame();
    }

    @Override
    public void onFinishedGettingQuestions(List<Question> list) {
        startGame(list);
    }
}
