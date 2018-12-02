package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import java.util.List;

public class Question {
    private String mQuestionText;
    private List<String> mAnswersList;
    private String mRightAnswer;

    Question() { }

    public String getQuestionText() {
        return mQuestionText;
    }

    public void setQuestionText(String questionText) {
        mQuestionText = questionText;
    }

    public List<String> getAnswersList() {
        return mAnswersList;
    }

    public void setAnswersList(List<String> answersList) {
        mAnswersList = answersList;
    }

    public String getRightAnswer() {
        return mRightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        mRightAnswer = rightAnswer;
    }
}


