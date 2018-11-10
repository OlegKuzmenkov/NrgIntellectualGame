package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private String mUserId;
    private String mUserLogin;
    private int mCountRightAnswers;
    private int mCountAnswers;
    private double mLatitude;
    private double mLongitude;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userLogin, int countRightAnswers, int countAnswers, double latitude,double longitude) {
        mUserLogin = userLogin;
        mCountRightAnswers = countRightAnswers;
        mCountAnswers = countAnswers;
        mLatitude = latitude;
        mLongitude = longitude;
    }
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getUserLogin() {
        return mUserLogin;
    }

    public void setUserLogin(String userLogin) {
        mUserLogin = userLogin;
    }

    public int getCountRightAnswers() {
        return mCountRightAnswers;
    }

    public void setCountRightAnswers(int countRightAnswers) {
        mCountRightAnswers = countRightAnswers;
    }

    public int getCountAnswers() {
        return mCountAnswers;
    }

    public void setCountAnswers(int countAnswers) {
        mCountAnswers = countAnswers;
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
