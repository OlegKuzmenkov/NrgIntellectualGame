package com.oleg_kuzmenkov.android.nrgintellectualgame.menu;

import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

import java.util.List;

public interface MenuScreenView {
    void displayUserLogin(User user);
    void startGameActivity(User user);
    void startStatisticsActivity(User user);
    void startBestPlayersActivity(List<User> list);
    void startNewsActivity();
    void hideMenu();
    void displayMenu();
}
