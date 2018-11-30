package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import java.util.List;

public interface Repository {
    
    void getQuestionsList(QuestionsReadingCallback listener);
    void getNewsList(NewsReadingCallback listener);
    void getCurrentUser(UsersReadingCallback listener);
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
