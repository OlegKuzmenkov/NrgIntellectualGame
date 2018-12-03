package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import java.util.List;

public interface Repository {

    void getQuestionsList(ReadQuestionsCallback listener);
    void getNewsList(ReadNewsCallback listener);
    void getUsersList(int requestCode, ReadUsersCallback listener);
    void updateUser(User user);
    void addUser(User user);

    interface ReadQuestionsCallback {
        void onFinished(List<Question> list);
    }

    interface ReadUsersCallback {
        void onFinished(List<User> list, int requestCode);
    }

    interface ReadNewsCallback {
        void onFinished(List<News> list);
    }
}
