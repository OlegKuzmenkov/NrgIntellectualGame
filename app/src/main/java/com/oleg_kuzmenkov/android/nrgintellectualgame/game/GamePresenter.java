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

public class GamePresenter implements Repository.ReadQuestionsCallback, Serializable,
        QuestionTimerCallback, QuestionPauseCallback {

    static final int GAME_QUESTIONS_COUNT = 3;

    private static final String LOG_TAG = "GAME_PRESENTER";
    private static final int RED_INDICATOR_TIME = 3;
    private static final int QUESTION_AVAILABLE_TIME = 10;

    private GameView mGameView;
    private Repository mRepository;

    private User mCurrentUser;

    private List<Question> mGameQuestionsList;
    private boolean mIsAnswerDone;
    private int mQuestionIndex;
    private int mQuestionRemainTime;
    private int mRightAnswersCount;
    private QuestionTimer mQuestionTimer;
    private QuestionPause mQuestionPause;

    GamePresenter(@NonNull final Repository repository) {
        mRepository = repository;
    }

    void setView(final GameView gameView) {
        mGameView = gameView;
    }

    void setRepository(final Repository repository) {
        mRepository = repository;
    }

    /**
     * Detach View and presenter
     */
    void detach() {
        mQuestionTimer.cancel();
        mGameView = null;
        mRepository = null;
    }

    void startGame() {
            mRepository.getQuestionsList(this);
    }

    /**
     * Move to the next question from gameQuestionsList
     */
    private void moveToNextQuestion() {
        if (mQuestionIndex == (mGameQuestionsList.size() - 1)) {
            finishGame();
        } else {
            //get new question
            mIsAnswerDone = false;
            mQuestionIndex++;
            mQuestionRemainTime = QUESTION_AVAILABLE_TIME;
            mGameView.setGreenTimeIndicator();
            askQuestion();
        }
    }

    /**
     * Display the current question on the screen
     */
    private void askQuestion() {
            mQuestionTimer = new QuestionTimer(this);
            mGameView.displayQuestion(mGameQuestionsList.get(mQuestionIndex));
    }

    /**
     * Restore game
     */
    void restoreGame() {
        if (mIsAnswerDone || mQuestionRemainTime == 0) {
            moveToNextQuestion();
        } else {
            if (mQuestionRemainTime < 4) {
                mGameView.setRedTimeIndicator();
            }

            askQuestion();
        }
    }

    void setUser(final User user) {
        mCurrentUser = user;
    }

    /**
     * Check user's location. Start location service if user's data is not exist
     */
    void getUserLocation() {
        if (mCurrentUser.getLatitude() == 0) {
            Log.d(LOG_TAG, "It is a new User. We must get his location!!!");
            //get location
            mGameView.startLocationService();
        }
    }

    /**
     * Set user's location
     */
    void setUserLocation(double latitude, double longitude) {
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

    /**
     * Update user's statistics after game
     */
    private void updateUserStatistics() {
        mCurrentUser.setRightAnswersCount(mCurrentUser.getRightAnswersCount() + mRightAnswersCount);
        mCurrentUser.setAnswersCount(mCurrentUser.getAnswersCount() + mGameQuestionsList.size());
        mRepository.updateUser(mCurrentUser);
    }

    /**
     * Check user's answer
     */
    void checkAnswer(String answer) {
        mQuestionTimer.cancel();
        mIsAnswerDone = true;
        mGameView.enableAnswerButtons(false);

        String rightAnswer = mGameQuestionsList.get(mQuestionIndex).getRightAnswer();
        if (rightAnswer.equals(answer)) {
            //answer is true
            mRightAnswersCount++;
            mGameView.displayRightAnswerResult(answer);
        } else {
            //answer is wrong
            mGameView.displayWrongAnswerResult(rightAnswer, answer);
        }

        mQuestionPause = new QuestionPause(this);
        mQuestionPause.start();
    }

    /**
     * Choose list of random questions
     */
    private void chooseRandomQuestions(@NonNull final List<Question> list) {
        Random gen = new Random();
        mGameQuestionsList = new ArrayList<>();
        int max = list.size();
        int questionsCount = 0;

        while (questionsCount < GAME_QUESTIONS_COUNT) {
            int index = gen.nextInt(max);

            if (!mGameQuestionsList.contains(list.get(index))) {
                //question doesn't exist in the list
                mGameQuestionsList.add(list.get(index));
                questionsCount++;
            }
        }
    }

    /**
     * Change remain time for question in View
     */
    @Override
    public void changeRemainQuestionTime() {
        mQuestionRemainTime--;
        mGameView.setQuestionRemainTime(mQuestionRemainTime);

        if (mQuestionRemainTime == RED_INDICATOR_TIME) {
            mGameView.setRedTimeIndicator();
            return;
        }

        if (mQuestionRemainTime == 0) {
            mQuestionTimer.cancel();
            mGameView.displayRightAnswer(mGameQuestionsList.get(mQuestionIndex).getRightAnswer());
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
        moveToNextQuestion();
    }

    /**
     * This is callback from GameData. Start game after getting questions list
     */
    @Override
    public void onFinished(final List<Question> list) {
        chooseRandomQuestions(list);
        mQuestionIndex = 0;
        mRightAnswersCount = 0;
        //refresh timer
        mQuestionRemainTime = QUESTION_AVAILABLE_TIME;
        askQuestion();
    }
}
