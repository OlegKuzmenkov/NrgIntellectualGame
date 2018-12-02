package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private String mId;
    private String mLogin;
    private int mRightAnswersCount;
    private int mAnswersCount;
    private double mLatitude;
    private double mLongitude;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String login, int rightAnswersCount, int answersCount, double latitude, double longitude) {
        mLogin = login;
        mRightAnswersCount = rightAnswersCount;
        mAnswersCount = answersCount;
        mLatitude = latitude;
        mLongitude = longitude;
    }
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    public int getRightAnswersCount() {
        return mRightAnswersCount;
    }

    public void setRightAnswersCount(int rightAnswersCount) {
        mRightAnswersCount = rightAnswersCount;
    }

    public int getAnswersCount() {
        return mAnswersCount;
    }

    public void setAnswersCount(int answersCount) {
        mAnswersCount = answersCount;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
