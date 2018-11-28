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

    public void getUserData(String userLogin) {
        if (mCurrentUser == null) {
            mUserLogin = userLogin;
            mMenuView.enableMenu(false);
            mRepository.getCurrentUserData(this);
        } else {
            Log.d(LOG_TAG, "Display user login");
            mMenuView.displayUserLogin(mCurrentUser.getUserLogin());
        }
    }

    public void onClickSinglePlayerButton() {
        mMenuView.startGameActivity(mCurrentUser);
    }

    public void onClickStatisticsButton() {
        mMenuView.startStatisticsActivity(mCurrentUser);
    }

    public void onClickReadNewsButton() {
        mMenuView.startNewsActivity();
    }

    public void onClickBestPlayersButton() {
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
    public void onFinishedGettingUsers(final List<User> userslist) {
        Log.d(LOG_TAG, "OnFinishedGettingUsers");

        for (User user : userslist) {
            if (mUserLogin.equals(user.getUserLogin())) {
                mCurrentUser = user;
                break;
            }
        }

        if (mCurrentUser == null) {
            // create and save new user
            createNewUser();
        } else {
            // user is exist in firebase
            showMenu();
        }
    }

    private void createNewUser() {
        User newUser = new User();
        newUser.setUserLogin(mUserLogin);
        newUser.setCountAnswers(0);
        newUser.setCountRightAnswers(0);
        newUser.setLatitude(0);
        newUser.setLongitude(0);

        mRepository.addNewUserToDatabase(newUser);
    }

    private void showMenu() {
        if (mMenuView != null) {
            mMenuView.enableMenu(true);
            mMenuView.displayUserLogin(mCurrentUser.getUserLogin());
        }
    }
}