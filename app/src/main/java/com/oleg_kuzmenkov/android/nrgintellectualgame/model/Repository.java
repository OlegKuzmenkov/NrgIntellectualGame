package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import java.util.List;

public interface Repository {

    void getQuestionsList(ReadQuestionsCallback listener);
    void getNewsList(ReadNewsCallback listener);
    void getUsersList(ReadUsersCallback listener);
    List<User> getUsers();
    void updateUser(User user);
    void addUser(User user);

    interface ReadQuestionsCallback {
        void onFinished(List<Question> list);
    }

    interface ReadUsersCallback {
        void onFinished(List<User> list);
    }

    interface ReadNewsCallback {
        void onFinished(List<News> list);
    }
}
