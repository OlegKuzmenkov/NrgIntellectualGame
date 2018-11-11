package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
    //local database
    private QuestionsDatabase mDatabase;
    //remote database
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
        mDatabase = new QuestionsDatabase(mContext);
        mRemoteDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void getQuestionsFromDatabase(QuestionOnFinishedListener listener) {
        if (mQuestionList == null) {
            mQuestionList = new ArrayList<>();
            Log.d(LOG_TAG, "Start loading questions.");
            SQLiteDatabase db = mDatabase.getWritableDatabase();
            new GetQuestionsFromLocalDatabaseAsyncTask(db, mQuestionList, listener).execute();
        } else {
            Log.d(LOG_TAG, "List of questions is exist. Loading is not started.");
            Log.d(LOG_TAG, "List of questions size is - " + mQuestionList.size());
            listener.onFinishedGettingQuestions(mQuestionList);
        }
    }

    @Override
    public void getNewsFromDatabase(NewsOnFinishedListener listener) {
        if (mNewsList == null) {
            //mNewsList = new ArrayList<>();
            Log.d(LOG_TAG, "Start loading news.");
            SQLiteDatabase db = mDatabase.getWritableDatabase();
            new GetNewsFromLocalDatabaseAsyncTask(db, mNewsList, listener).execute();
        } else {
            Log.d(LOG_TAG, "List of news is exist. Loading is not started.");
            listener.onFinishedGettingNews(mNewsList);
        }
    }

    @Override
    public void getCurrentUserData(UsersOnFinishedListener listener) {
        Log.d(LOG_TAG, "getCurrentUserData");
        if (mNewsList == null) {
            Log.d(LOG_TAG, "Start loading all users");
            readFromRemoteDatabase(listener);
        } else {
            Log.d(LOG_TAG, "List of users is exist. Loading is not started.");
            listener.onFinishedGettingUsers(mUserList);
        }
    }

    @Override
    public List<User> getAllUsers() {
        Log.d(LOG_TAG, "Send all users");
        return mUserList;
    }

    private void writeNewUser(String userId, String userLogin, int countRightAnswers, int countAnswers, double latitude, double longitude) {
        User user = new User(userLogin, countRightAnswers, countAnswers, latitude, longitude);
        //mRemoteDatabase.child("users").child(userId).setValue(user);
        DatabaseReference postsRef = mRemoteDatabase.child("users");
        DatabaseReference newPostRef = postsRef.push();
        newPostRef.setValue(user);
    }

    @Override
    public void updateUserData(User user) {
        User newUser = new User(user.getUserLogin(), user.getCountRightAnswers(), user.getCountAnswers(),
                user.getLatitude(), user.getLongitude());
        mRemoteDatabase.child("users").child(user.getUserId()).setValue(newUser);
    }

    @Override
    public void addNewUserToDatabase(User user) {
        DatabaseReference postsRef = mRemoteDatabase.child("users");
        DatabaseReference newPostRef = postsRef.push();
        newPostRef.setValue(user);
    }

    private void readFromRemoteDatabase(final UsersOnFinishedListener listener) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserList = new ArrayList<>();
                dataSnapshot = dataSnapshot.child("users");
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    //Log.d(LOG_TAG, "Key is: " + noteDataSnapshot.getKey());
                    User user = noteDataSnapshot.getValue(User.class);
                    user.setUserId(noteDataSnapshot.getKey());
                    mUserList.add(user);
                }
                Log.d(LOG_TAG, "Count of Users = " + mUserList.size());
                listener.onFinishedGettingUsers(mUserList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d(LOG_TAG, "loading is onCancelled", databaseError.toException());
            }
        };
        mRemoteDatabase.child("users");
        mRemoteDatabase.addValueEventListener(postListener);
    }
}
