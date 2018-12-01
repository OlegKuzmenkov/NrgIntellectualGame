package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import java.util.List;

public interface Repository {

    void readQuestions(QuestionsReadingCallback listener);
    void readNews(NewsReadingCallback listener);
    void readUsers(UsersReadingCallback listener);
    List<User> getUsersList();
    void updateUser(User user);
    void addNewUser(User user);

    interface QuestionsReadingCallback {
        void onFinishedGettingQuestions(List<Question> list);
    }

    interface UsersReadingCallback {
        void onFinishedGettingUsers(List<User> list);
    }

    interface NewsReadingCallback {
        void onFinishedGettingNews(List<News> list);
    }
}
