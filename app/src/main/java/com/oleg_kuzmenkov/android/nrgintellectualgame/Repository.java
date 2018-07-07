package com.oleg_kuzmenkov.android.nrgintellectualgame;

import java.util.List;

public interface Repository {

    interface QuestionOnFinishedListener {
        void onFinishedGettingQuestions(List<Question> list);
    }

    interface UsersOnFinishedListener{
        void onFinishedGettingUsers(List<User> list);
    }

    interface NewsOnFinishedListener{
        void onFinishedGettingNews(List<News> list);
    }

    void getQuestionsFromDatabase(QuestionOnFinishedListener listener);
    void getNewsFromDatabase(NewsOnFinishedListener listener);
    void getCurrentUserData(UsersOnFinishedListener listener);
    List<User> getAllUsers();
    void updateUserData(User user);
    void addNewUserToDatabase(User user);
}
