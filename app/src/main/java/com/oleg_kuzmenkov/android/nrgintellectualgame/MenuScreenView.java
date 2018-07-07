package com.oleg_kuzmenkov.android.nrgintellectualgame;

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
