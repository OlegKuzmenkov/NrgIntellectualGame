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

public class GameScreenPresenter implements Repository.QuestionOnFinishedListener, Serializable {
    private static final String LOG_TAG = "Message";

    private int mNumberOfCurrentQuestion;
    private int mCountRightAnswers;
    private List<Question> mQuestionListForGame;
    private User mCurrentUser;

    private GameScreenView mGameScreenView;
    private Repository mRepository;

    public GameScreenPresenter(@NonNull Repository repository) {
        mRepository = repository;
    }

    public void setView(GameScreenView gameScreenView){
        mGameScreenView = gameScreenView;
    }

    public void setRepository(Repository repository){
        mRepository = repository;
    }

    public void detach(){
        mGameScreenView = null;
        mRepository = null;
    }

    public void onClickSinglePlayerButton(){
            mRepository.getQuestionsFromDatabase(this);
    }

    public void getNextQuestion(){
        if(mNumberOfCurrentQuestion < mQuestionListForGame.size()-1) {
            mNumberOfCurrentQuestion++;
        }
        getQuestion();
    }

    public void getQuestion(){
        if (mGameScreenView != null) {
            mGameScreenView.displayQuestion(mQuestionListForGame.get(mNumberOfCurrentQuestion));
        }
    }

    public boolean isLastQuestion(){
        if(mNumberOfCurrentQuestion == mQuestionListForGame.size()-1){
            return true;
        } else{
            return false;
        }
    }

    /**
     * Start the Game
     */
    public void startGame(List<Question> list){
        chooseRandomQuestions(list);
        mNumberOfCurrentQuestion = 0;
        mCountRightAnswers = 0;
        getQuestion();
    }

    public void sendAnswerResult(boolean answer){
        if(answer == true){
            mCountRightAnswers++;
        }
    }

    public void setUser(User user){
        mCurrentUser = user;
    }

    public void checkIsExistUserLocation(){
        if(mCurrentUser.getLatitude() == 0){
            Log.d(LOG_TAG,"It is a new User. We must get his location!!!");
            //get location
            mGameScreenView.startGettingUserLocation();
        }
    }

    /**
     * Set User's location
     */
    public void setUserLocation(double latitude,double longitude){
        mCurrentUser.setLatitude(latitude);
        mCurrentUser.setLongitude(longitude);
        // stop getting user's location
        mGameScreenView.stopGettingUserLocation();
    }

    /**
     * Show result of the game
     */
    public void showResultsOfTheGame(){
        Log.d(LOG_TAG,"Count of write answers = "+mCountRightAnswers);
        int countOfQuestions = mQuestionListForGame.size();
        int countOfRightAnswers = mCountRightAnswers;
        mCurrentUser.setCountRightAnswers(mCurrentUser.getCountRightAnswers()+countOfRightAnswers);
        mCurrentUser.setCountAnswers(mCurrentUser.getCountAnswers()+countOfQuestions);
        mRepository.updateUserData(mCurrentUser);
        mGameScreenView.displayResultsOfGame(countOfQuestions,countOfRightAnswers);
    }

    /**
     * Choose list of random questions
     */
    private void chooseRandomQuestions(@NonNull List<Question> list){
        Random gen = new Random();
        int max = list.size();
        mQuestionListForGame = new ArrayList();

        while (mQuestionListForGame.size()<3){
            int index = gen.nextInt(max);
            if(mQuestionListForGame.contains(list.get(index)) == false){
                mQuestionListForGame.add(list.get(index));
            }
        }
        Log.d(LOG_TAG,"Count of questions = "+mQuestionListForGame.size());
    }

    @Override
    public void onFinishedGettingQuestions(List<Question> list) {
        startGame(list);
    }
}
