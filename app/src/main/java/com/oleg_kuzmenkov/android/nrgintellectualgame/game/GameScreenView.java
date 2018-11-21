package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Question;

public interface GameScreenView {
    void displayQuestion(Question question);
    void displayResultsOfGame(int countOfQuestions, int countWriteAnswers);
    void displayRightAnswerResult(String rightAnswer);
    void displayWrongAnswerResult(String rightAnswer, String wrongAnswer);
    void displayRightAnswer(String rightAnswer);
    void enableAnswerButtons(boolean isEnable);
    void startGettingUserLocation();
    void stopGettingUserLocation();
    void clearButtons();
    void setGreenTimeIndicator();
    void setRedTimeIndicator();
    void setQuestionRemainTime(int remainTime);
}
