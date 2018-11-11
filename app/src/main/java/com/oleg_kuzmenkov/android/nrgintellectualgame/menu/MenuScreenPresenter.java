package com.oleg_kuzmenkov.android.nrgintellectualgame.menu;

import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuScreenPresenter implements Repository.UsersOnFinishedListener, Serializable {
    private static final String LOG_TAG = "Message";

    private User mCurrentUser;
    private String mUserLogin;

    private transient MenuScreenView mMenuScreenView;
    private transient Repository mRepository;


    public MenuScreenPresenter() {
    }

    public void setView(MenuScreenView menuScreenView) {
        mMenuScreenView = menuScreenView;
    }

    public void setRepository(Repository repository) {
        mRepository = repository;
    }

    public void detach() {
        mMenuScreenView = null;
        mRepository = null;
    }

    public void checkUsers(String userLogin) {
        mUserLogin = userLogin;
        if (mCurrentUser == null) {
            mMenuScreenView.hideMenu();
            mRepository.getCurrentUserData(this);
        } else {
            Log.d(LOG_TAG, "Display user login");
            mMenuScreenView.displayUserLogin(mCurrentUser);
        }
    }

    public void startGameActivity() {
        mMenuScreenView.startGameActivity(mCurrentUser);
    }

    public void startStatisticsActivity() {
        mMenuScreenView.startStatisticsActivity(mCurrentUser);
    }

    public void startNewsActivity() {
        mMenuScreenView.startNewsActivity();
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
        mMenuScreenView.startBestPlayersActivity(bestPlayersList);
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
            if (mMenuScreenView != null) {
                mMenuScreenView.displayMenu();
                mMenuScreenView.displayUserLogin(mCurrentUser);
            }
        }
    }
}

