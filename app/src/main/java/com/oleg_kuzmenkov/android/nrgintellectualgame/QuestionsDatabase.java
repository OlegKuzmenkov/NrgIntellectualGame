package com.oleg_kuzmenkov.android.nrgintellectualgame;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuestionsDatabase extends SQLiteOpenHelper {
    private static final String LOG_TAG = "Message";
    private static final String DB_NAME = "questions.db";
    private static final int DATABASE_VERSION = 1;

    // Question table
    public static final String TABLE_QUESTION = "question";
    public static final String COLUMN_ID_QUESTION = "_id";
    public static final String COLUMN_QUESTION_TEXT = "question_text";
    public static final String COLUMN_ANSWER_FIRST = "question_answer_first";
    public static final String COLUMN_ANSWER_SECOND = "question_answer_second";
    public static final String COLUMN_ANSWER_THIRD = "question_answer_third";
    public static final String COLUMN_ANSWER_FOURTH = "question_answer_fourth";
    public static final String COLUMN_RIGHT_ANSWER = "right_answer";
    public static final String COLUMN_DETAILS = "details";

    // Question table creation SQL statement
    private static final String TABLE_QUESTION_CREATE = "create table "
            + TABLE_QUESTION
            + "("
            + COLUMN_ID_QUESTION + " integer primary key autoincrement, "
            + COLUMN_QUESTION_TEXT + " text, "
            + COLUMN_ANSWER_FIRST + " text,"
            + COLUMN_ANSWER_SECOND + " text,"
            + COLUMN_ANSWER_THIRD + " text,"
            + COLUMN_ANSWER_FOURTH + " text,"
            + COLUMN_RIGHT_ANSWER + " text,"
            + COLUMN_DETAILS + " text"
            + ");";

    // News table
    public static final String TABLE_NEWS = "news";
    public static final String COLUMN_ID_NEWS = "_id";
    public static final String COLUMN_NEWS_SOURCE = "news_source";
    public static final String COLUMN_NEWS_TITLE  = "news_title";
    public static final String COLUMN_NEWS_DESCRIPTION = "news_description";
    public static final String COLUMN_NEWS_URL = "news_url";
    public static final String COLUMN_NEWS_IMAGE = "news_image";

    // News table creation SQL statement
    private static final String TABLE_NEWS_CREATE = "create table "
            + TABLE_NEWS
            + "("
            + COLUMN_ID_NEWS + " integer primary key autoincrement, "
            + COLUMN_NEWS_SOURCE + " text, "
            + COLUMN_NEWS_TITLE + " text,"
            + COLUMN_NEWS_DESCRIPTION + " text,"
            + COLUMN_NEWS_URL + " text,"
            + COLUMN_NEWS_IMAGE + " blob"
            + ");";

    public QuestionsDatabase(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "onCreate database");
        sqLiteDatabase.execSQL(TABLE_QUESTION_CREATE);
        sqLiteDatabase.execSQL(TABLE_NEWS_CREATE);
        addSomeDataToDatabase(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void addSomeDataToDatabase(SQLiteDatabase sqLiteDatabase) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QUESTION_TEXT, "How much people live in Belarus?");
        cv.put(COLUMN_ANSWER_FIRST , "4 millions");
        cv.put(COLUMN_ANSWER_SECOND , "7 millions");
        cv.put(COLUMN_ANSWER_THIRD , "9 millions");
        cv.put(COLUMN_ANSWER_FOURTH , "11 millions");
        cv.put(COLUMN_RIGHT_ANSWER , "9 millions");
        cv.put(COLUMN_DETAILS,"Geography");
        sqLiteDatabase.insert(TABLE_QUESTION, null, cv);

        cv.put(COLUMN_QUESTION_TEXT, "How much people live in Russia?");
        cv.put(COLUMN_ANSWER_FIRST , "50 millions");
        cv.put(COLUMN_ANSWER_SECOND , "80 millions");
        cv.put(COLUMN_ANSWER_THIRD , "110 millions");
        cv.put(COLUMN_ANSWER_FOURTH , "140 millions");
        cv.put(COLUMN_RIGHT_ANSWER , "140 millions");
        cv.put(COLUMN_DETAILS,"Geography");
        sqLiteDatabase.insert(TABLE_QUESTION, null, cv);

        cv.put(COLUMN_QUESTION_TEXT, "What type of the Irish Guinness beer?");
        cv.put(COLUMN_ANSWER_FIRST , "Light");
        cv.put(COLUMN_ANSWER_SECOND , "Dark");
        cv.put(COLUMN_ANSWER_THIRD , "Red");
        cv.put(COLUMN_ANSWER_FOURTH , "Pink");
        cv.put(COLUMN_RIGHT_ANSWER , "Dark");
        sqLiteDatabase.insert(TABLE_QUESTION, null, cv);

        cv.put(COLUMN_QUESTION_TEXT, "What color is included in the RGB color model, in addition to red and blue?");
        cv.put(COLUMN_ANSWER_FIRST , "Yellow");
        cv.put(COLUMN_ANSWER_SECOND , "Gray");
        cv.put(COLUMN_ANSWER_THIRD , "Green");
        cv.put(COLUMN_ANSWER_FOURTH , "White");
        cv.put(COLUMN_RIGHT_ANSWER , "Green");
        sqLiteDatabase.insert(TABLE_QUESTION, null, cv);

        cv.put(COLUMN_QUESTION_TEXT, "In what year did Ankara become the capital of Turkey?");
        cv.put(COLUMN_ANSWER_FIRST , "1923");
        cv.put(COLUMN_ANSWER_SECOND , "1900");
        cv.put(COLUMN_ANSWER_THIRD , "1854");
        cv.put(COLUMN_ANSWER_FOURTH , "1932");
        cv.put(COLUMN_RIGHT_ANSWER , "1923");
        sqLiteDatabase.insert(TABLE_QUESTION, null, cv);

        cv.put(COLUMN_NEWS_SOURCE, "2");
        cv.put(COLUMN_NEWS_TITLE , "2");
        cv.put(COLUMN_NEWS_DESCRIPTION , "2");
        cv.put(COLUMN_NEWS_URL , "2");
        cv.put(COLUMN_NEWS_IMAGE , "2");
        sqLiteDatabase.insert(TABLE_NEWS, null, cv);


    }
}