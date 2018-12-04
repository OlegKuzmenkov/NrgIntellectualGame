package com.oleg_kuzmenkov.android.nrgintellectualgame.menu;

import android.util.Log;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.Repository;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuPresenter implements Repository.ReadUsersCallback, Serializable {
    private static final int AUTHORIZATION_REQUEST_CODE = 1;
    private static final int BEST_PLAYERS_REQUEST_CODE = 2;
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
            mRepository.getUsersList(AUTHORIZATION_REQUEST_CODE,this);
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
        mRepository.getUsersList(BEST_PLAYERS_REQUEST_CODE,this);
    }

    /**
     * This is callback from GameData. Show the best players or authorize user depending
     * on the parameter
     */
    @Override
    public void onFinished(final List<User> userslist, int requestCode) {
        Log.d(LOG_TAG, "OnFinishedGettingUsers");
        Log.d(LOG_TAG, "Request code - "+requestCode);

        switch (requestCode) {
            case AUTHORIZATION_REQUEST_CODE:
                authorizeUser(userslist);
                break;

            case BEST_PLAYERS_REQUEST_CODE:
                showBestPlayers(userslist);
                break;

            default:
                Log.i(LOG_TAG, "Unknown request");
                break;
        }
    }

    /**
     * Authorize user
     */
    private void authorizeUser(final List<User> userslist) {
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

    /**
     * Show the best players
     */
    private void showBestPlayers(final List<User> userslist) {
        List<User> bestPlayersList = chooseBestPlayers(userslist);
        Log.d(LOG_TAG, "Best players count = " + bestPlayersList.size());
        mMenuView.startBestPlayersActivity(bestPlayersList);
    }

    /**
     * Choose the best players
     */
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

    /**
     * Calculate the percentage of user's correct answers
     */
    private int calculatePercentage(int answersCount, int rightAnswersCount) {
        return answersCount > 0 ? rightAnswersCount * 100 / answersCount : 0;
    }

    /**
     * Create a new user
     */
    private void createNewUser() {
        User newUser = new User();
        newUser.setLogin(mUserLogin);
        newUser.setAnswersCount(0);
        newUser.setRightAnswersCount(0);
        newUser.setLatitude(0);
        newUser.setLongitude(0);

        mRepository.addUser(newUser);
    }

    /**
     * Show menu for user
     */
    private void showMenu() {
        if (mMenuView != null) {
            mMenuView.enableMenu(true);
            mMenuView.displayUserLogin(mCurrentUser.getLogin());
        }
    }
}