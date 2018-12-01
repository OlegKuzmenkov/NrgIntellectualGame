package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RepositoryImpl implements Repository {
    private static final String LOG_TAG = "Message";
    private static RepositoryImpl sRepository;

    private Database mLocalDatabase;
    private DatabaseReference mRemoteDatabase;

    private List<Question> mQuestionList;
    private List<User> mUserList;
    private List<News> mNewsList;
    private Context mContext;

    public static RepositoryImpl get(Context context) {
        if (sRepository == null) {
            sRepository = new RepositoryImpl(context);
        }
        return sRepository;
    }

    private RepositoryImpl(Context context) {
        mContext = context;
        mLocalDatabase = new Database(mContext);
        mRemoteDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void readQuestions(QuestionsReadingCallback listener) {
        if (mQuestionList == null) {
            mQuestionList = new ArrayList<>();
            Log.d(LOG_TAG, "Start loading questions.");
            SQLiteDatabase database = mLocalDatabase.getWritableDatabase();
            new QuestionsReadingTask(database, mQuestionList, listener).execute();
        } else {
            Log.d(LOG_TAG, "List of questions is exist. Loading is not started.");
            listener.onFinishedGettingQuestions(mQuestionList);
        }
    }

    @Override
    public void readNews(NewsReadingCallback listener) {
        if (mNewsList == null) {
            mNewsList = new ArrayList<>();
            Log.d(LOG_TAG, "Start loading news.");
            SQLiteDatabase database = mLocalDatabase.getWritableDatabase();
            new NewsReadingTask(database, mNewsList, listener).execute();
        } else {
            Log.d(LOG_TAG, "List of news is exist. Loading is not started.");
            listener.onFinishedGettingNews(mNewsList);
        }
    }

    @Override
    public void readUsers(UsersReadingCallback listener) {
        Log.d(LOG_TAG, "readUsers");
        if (mNewsList == null) {
            Log.d(LOG_TAG, "Start loading all users");
            readUsersList(listener);
        } else {
            Log.d(LOG_TAG, "List of users is exist. Loading is not started.");
            listener.onFinishedGettingUsers(mUserList);
        }
    }

    @Override
    public List<User> getUsersList() {
        Log.d(LOG_TAG, "Send all users");
        return mUserList;
    }

    @Override
    public void updateUser(User user) {
        User newUser = new User(user.getLogin(), user.getRightAnswersCount(),
                user.getAnswersCount(), user.getLatitude(), user.getLongitude());
        mRemoteDatabase.child("users").child(user.getId()).setValue(newUser);
    }

    @Override
    public void addNewUser(User user) {
        DatabaseReference postsRef = mRemoteDatabase.child("users");
        DatabaseReference newPostRef = postsRef.push();
        newPostRef.setValue(user);
    }

    private void writeNewUser(String userId, String userLogin, int countRightAnswers, int countAnswers, double latitude, double longitude) {
        User user = new User(userLogin, countRightAnswers, countAnswers, latitude, longitude);
        //mRemoteDatabase.child("users").child(userId).setValue(user);
        DatabaseReference postsRef = mRemoteDatabase.child("users");
        DatabaseReference newPostRef = postsRef.push();
        newPostRef.setValue(user);
    }

    private void readUsersList(final UsersReadingCallback listener) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserList = new ArrayList<>();
                dataSnapshot = dataSnapshot.child("users");

                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    User user = noteDataSnapshot.getValue(User.class);
                    user.setId(noteDataSnapshot.getKey());
                    mUserList.add(user);
                }

                Log.d(LOG_TAG, "Count of Users = " + mUserList.size());
                listener.onFinishedGettingUsers(mUserList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d(LOG_TAG, "loading is onCancelled", databaseError.toException());
            }
        };

        mRemoteDatabase.child("users");
        mRemoteDatabase.addValueEventListener(postListener);
    }
}
