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
        void onFinishedReadingQuestions(List<Question> list);
    }

    interface UsersReadingCallback {
        void onFinishedReadingUsers(List<User> list);
    }

    interface NewsReadingCallback {
        void onFinishedReadingNews(List<News> list);
    }
}
