package com.oleg_kuzmenkov.android.nrgintellectualgame.menu;

import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuPresenter implements Repository.ReadUsersCallback, Serializable {
    private static final String LOG_TAG = "Message";

    private User mCurrentUser;
    private String mUserLogin;

    private transient MenuView mMenuView;
    private transient Repository mRepository;

    MenuPresenter() { }

    void setView(MenuView menuView) {
        mMenuView = menuView;
    }

    void setRepository(Repository repository) {
        mRepository = repository;
    }

    void detach() {
        mMenuView = null;
        mRepository = null;
    }

    void getUserData(String userLogin) {
        if (mCurrentUser == null) {
            mUserLogin = userLogin;
            mMenuView.enableMenu(false);
            mRepository.getUsersList(this);
        } else {
            Log.d(LOG_TAG, "Display user login");
            mMenuView.displayUserLogin(mCurrentUser.getLogin());
        }
    }

    void onClickSinglePlayerButton() {
        mMenuView.startGameActivity(mCurrentUser);
    }

    void onClickStatisticsButton() {
        mMenuView.startStatisticsActivity(mCurrentUser);
    }

    void onClickReadNewsButton() {
        mMenuView.startNewsActivity();
    }

    void onClickBestPlayersButton() {
        List<User> bestPlayersList = chooseBestPlayers(mRepository.getUsers());
        Log.d(LOG_TAG, "Best players count = " + bestPlayersList.size());
        // send list of the best players
        mMenuView.startBestPlayersActivity(bestPlayersList);
    }

    @Override
    public void onFinished(final List<User> userslist) {
        Log.d(LOG_TAG, "OnFinishedGettingUsers");

        for (User user : userslist) {
            if (mUserLogin.equals(user.getLogin())) {
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

    private List<User> chooseBestPlayers(List<User> playersList) {
        List<User> bestPlayersList = new ArrayList<>();

        for (User user : playersList) {
            int rightAnswersPercent = calculatePercentage(user.getAnswersCount(),
                    user.getRightAnswersCount());

            if (rightAnswersPercent > 50) {
                bestPlayersList.add(user);
            }
        }

        return bestPlayersList;
    }

    private int calculatePercentage(int answersCount, int rightAnswersCount) {
        return answersCount > 0 ? rightAnswersCount * 100 / answersCount : 0;
    }

    private void createNewUser() {
        User newUser = new User();
        newUser.setLogin(mUserLogin);
        newUser.setAnswersCount(0);
        newUser.setRightAnswersCount(0);
        newUser.setLatitude(0);
        newUser.setLongitude(0);

        mRepository.addUser(newUser);
    }

    private void showMenu() {
        if (mMenuView != null) {
            mMenuView.enableMenu(true);
            mMenuView.displayUserLogin(mCurrentUser.getLogin());
        }
    }
}