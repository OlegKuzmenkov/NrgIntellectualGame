package com.oleg_kuzmenkov.android.nrgintellectualgame;

public class Question {
    private  boolean mAnswerTrue;
    private String mQuestionText;
    private String mFirstCaseAnswer;
    private String mSecondCaseAnswer;
    private String mThirdCaseAnswer;
    private String mFourthCaseAnswer;
    private String mRightAnswer;

    public Question() {
    }

    public String getQuestionText() {
        return mQuestionText;
    }

    public void setQuestionText(String questionText) {
        mQuestionText = questionText;
    }

    public String getFirstCaseAnswer() {
        return mFirstCaseAnswer;
    }

    public void setFirstCaseAnswer(String firstCaseAnswer) {
        mFirstCaseAnswer = firstCaseAnswer;
    }

    public String getSecondCaseAnswer() {
        return mSecondCaseAnswer;
    }

    public void setSecondCaseAnswer(String secondCaseAnswer) {
        mSecondCaseAnswer = secondCaseAnswer;
    }

    public String getThirdCaseAnswer() {
        return mThirdCaseAnswer;
    }

    public void setThirdCaseAnswer(String thirdCaseAnswer) {
        mThirdCaseAnswer = thirdCaseAnswer;
    }

    public String getFourthCaseAnswer() {
        return mFourthCaseAnswer;
    }

    public void setFourthCaseAnswer(String fourthCaseAnswer) {
        mFourthCaseAnswer = fourthCaseAnswer;
    }

    public String getRightAnswer() {
        return mRightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        mRightAnswer = rightAnswer;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}


