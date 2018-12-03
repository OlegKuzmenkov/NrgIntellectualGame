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

public class GameData implements Repository {
    private static final String LOG_TAG = "GAME_DATA";
    private static GameData sRepository;

    private Database mLocalDatabase;
    private DatabaseReference mRemoteDatabase;

    private List<Question> mQuestionList;
    private List<User> mUserList;
    private List<News> mNewsList;

    public static GameData get(Context context) {
        if (sRepository == null) {
            sRepository = new GameData(context);
        }

        return sRepository;
    }

    private GameData(Context context) {
        mLocalDatabase = new Database(context);
        mRemoteDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Read questions list from local database
     */
    @Override
    public void getQuestionsList(ReadQuestionsCallback listener) {
        if (mQuestionList == null) {
            mQuestionList = new ArrayList<>();
            Log.d(LOG_TAG, "Start loading questions.");
            SQLiteDatabase database = mLocalDatabase.getWritableDatabase();
            new ReadQuestionsTask(database, mQuestionList, listener).execute();
        } else {
            Log.d(LOG_TAG, "List of questions is exist. Loading is not started.");
            listener.onFinished(mQuestionList);
        }
    }

    /**
     * Read news list from local database
     */
    @Override
    public void getNewsList(ReadNewsCallback listener) {
        if (mNewsList == null) {
            mNewsList = new ArrayList<>();
            Log.d(LOG_TAG, "Start loading news.");
            SQLiteDatabase database = mLocalDatabase.getWritableDatabase();
            new ReadNewsTask(database, mNewsList, listener).execute();
        } else {
            Log.d(LOG_TAG, "List of news is exist. Loading is not started.");
            listener.onFinished(mNewsList);
        }
    }

    /**
     * Read users list from remote database
     */
    @Override
    public void getUsersList(ReadUsersCallback listener) {
        if (mNewsList == null) {
            Log.d(LOG_TAG, "Start loading all users");
            readRemoteDatabase(listener);
        } else {
            Log.d(LOG_TAG, "List of users is exist. Loading is not started.");
            listener.onFinished(mUserList);
        }
    }

    /**
     * Get users list
     */
    @Override
    public List<User> getUsers() {
        Log.d(LOG_TAG, "Send all users");
        return mUserList;
    }

    /**
     * Update user data
     */
    @Override
    public void updateUser(User user) {
        User newUser = new User(user.getLogin(), user.getRightAnswersCount(),
                user.getAnswersCount(), user.getLatitude(), user.getLongitude());
        mRemoteDatabase.child("users").child(user.getId()).setValue(newUser);
    }

    /**
     * Add new user to remote database
     */
    @Override
    public void addUser(User user) {
        DatabaseReference postsRef = mRemoteDatabase.child("users");
        DatabaseReference newPostRef = postsRef.push();
        newPostRef.setValue(user);
    }

    /**
     * Read users list from remote database
     */
    private void readRemoteDatabase(final ReadUsersCallback listener) {
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

                Log.d(LOG_TAG, String.format("Count of users - %d", mUserList.size()));
                listener.onFinished(mUserList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG, "loading is onCancelled", databaseError.toException());
            }
        };

        mRemoteDatabase.child("users");
        mRemoteDatabase.addValueEventListener(postListener);
    }
}
