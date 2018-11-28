package com.oleg_kuzmenkov.android.nrgintellectualgame.menu;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.util.List;

public interface MenuView {
    void displayUserLogin(String userLogin);
    void startGameActivity(User user);
    void startStatisticsActivity(User user);
    void startBestPlayersActivity(List<User> list);
    void startNewsActivity();
    void enableMenu(boolean isEnable);
}
