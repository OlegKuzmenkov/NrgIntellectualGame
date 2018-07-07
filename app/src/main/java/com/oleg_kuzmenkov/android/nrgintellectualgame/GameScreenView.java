package com.oleg_kuzmenkov.android.nrgintellectualgame;

public interface GameScreenView {
    void displayQuestion(Question question);
    void displayResultsOfGame(int countOfQuestions,int countWriteAnswers);
    void checkAnswer();
    void setEnableAnswerButtons();
    void setDisableAnswerButtons();
    void startGettingUserLocation();
    void stopGettingUserLocation();
}
