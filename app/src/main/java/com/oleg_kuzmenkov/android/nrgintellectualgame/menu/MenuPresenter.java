package com.oleg_kuzmenkov.android.nrgintellectualgame.menu;

import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuPresenter implements Repository.UsersOnFinishedListener, Serializable {
    private static final String LOG_TAG = "Message";

    private User mCurrentUser;
    private String mUserLogin;

    private transient MenuView mMenuView;
    private transient Repository mRepository;


    public MenuPresenter() {
    }

    public void setView(MenuView menuView) {
        mMenuView = menuView;
    }

    public void setRepository(Repository repository) {
        mRepository = repository;
    }

    public void detach() {
        mMenuView = null;
        mRepository = null;
    }

    public void checkUsers(String userLogin) {
        mUserLogin = userLogin;
        if (mCurrentUser == null) {
            mMenuView.enableMenu(false);
            mRepository.getCurrentUserData(this);
        } else {
            Log.d(LOG_TAG, "Display user login");
            mMenuView.displayUserLogin(mCurrentUser);
        }
    }

    public void startGameActivity() {
        mMenuView.startGameActivity(mCurrentUser);
    }

    public void startStatisticsActivity() {
        mMenuView.startStatisticsActivity(mCurrentUser);
    }

    public void startNewsActivity() {
        mMenuView.startNewsActivity();
    }

    public void startBestPlayersActivity() {
        List<User> allPlayersList = mRepository.getAllUsers();
        List<User> bestPlayersList = new ArrayList();
        // select the best players
        for (int i = 0; i < allPlayersList.size(); i++) {
            if (allPlayersList.get(i).getCountAnswers() != 0) {
                int percentRightAnswers = (int) (allPlayersList.get(i).getCountRightAnswers() * 100.0f) / allPlayersList.get(i).getCountAnswers();
                if (percentRightAnswers > 50) {
                    bestPlayersList.add(allPlayersList.get(i));
                }
            }
        }
        Log.d(LOG_TAG, "Best players count = " + bestPlayersList.size());
        // send list of the best players
        mMenuView.startBestPlayersActivity(bestPlayersList);
    }

    @Override
    public void onFinishedGettingUsers(final List<User> list) {
        Log.d(LOG_TAG, "OnFinishedGettingUsers");

        for (int i = 0; i < list.size(); i++) {
            Log.d(LOG_TAG, "----------------------------------------");
            if (mUserLogin.equals(list.get(i).getUserLogin())) {
                mCurrentUser = list.get(i);
                break;
            }
        }
        if (mCurrentUser == null) {
            // add user to firebase
            mCurrentUser = new User();
            mCurrentUser.setUserLogin(mUserLogin);
            mCurrentUser.setCountAnswers(0);
            mCurrentUser.setCountRightAnswers(0);
            mCurrentUser.setLatitude(0);
            mCurrentUser.setLongitude(0);
            mRepository.addNewUserToDatabase(mCurrentUser);
        } else {
            // user is exist in firebase
            if (mMenuView != null) {
                mMenuView.enableMenu(true);
                mMenuView.displayUserLogin(mCurrentUser);
            }
        }
    }
}

