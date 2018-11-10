package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Question;

public interface GameScreenView {
    void displayQuestion(Question question);
    void displayResultsOfGame(int countOfQuestions,int countWriteAnswers);
    void checkAnswer();
    void setEnableAnswerButtons();
    void setDisableAnswerButtons();
    void startGettingUserLocation();
    void stopGettingUserLocation();
}
