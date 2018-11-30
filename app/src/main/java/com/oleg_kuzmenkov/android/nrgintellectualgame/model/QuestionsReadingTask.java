package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class QuestionsReadingTask extends AsyncTask<Void, Void, Void> {
    private static final String LOG_TAG = "Message";

    private SQLiteDatabase mDatabase;
    private Repository.QuestionOnFinishedListener mListener;
    private List<Question> mQuestionList;

    QuestionsReadingTask(final SQLiteDatabase database, List<Question> questions, Repository.QuestionOnFinishedListener listener) {
        mDatabase = database;
        mQuestionList = questions;
        mListener = listener;
    }

    protected Void doInBackground(Void... params) {
        getQuestionsFromDatabase();
        return null;
    }

    protected void onPostExecute(Void result) {
        Log.d(LOG_TAG, "Loading Questions is finished");
        mListener.onFinishedGettingQuestions(mQuestionList);
    }

    private void getQuestionsFromDatabase() {
        Cursor c = mDatabase.query(Database.TABLE_QUESTION, null, null, null,
                null, null, null);
        if (c.moveToFirst()) {
            int questionTextColIndex = c.getColumnIndex(Database.COLUMN_QUESTION_TEXT);
            int questionFirstAnswerColIndex = c.getColumnIndex(Database.COLUMN_ANSWER_FIRST);
            int questionSecondAnswerColIndex = c.getColumnIndex(Database.COLUMN_ANSWER_SECOND);
            int questionThirdAnswerColIndex = c.getColumnIndex(Database.COLUMN_ANSWER_THIRD);
            int questionFourthAnswerColIndex = c.getColumnIndex(Database.COLUMN_ANSWER_FOURTH);
            int questionRightAnswerColIndex = c.getColumnIndex(Database.COLUMN_RIGHT_ANSWER);
            do {
                Question question = new Question();
                question.setQuestionText(c.getString(questionTextColIndex));

                List<String> answersList = new ArrayList<>();
                answersList.add(c.getString(questionFirstAnswerColIndex));
                answersList.add(c.getString(questionSecondAnswerColIndex));
                answersList.add(c.getString(questionThirdAnswerColIndex));
                answersList.add(c.getString(questionFourthAnswerColIndex));
                question.setAnswersList(answersList);

                question.setRightAnswer(c.getString(questionRightAnswerColIndex));
                mQuestionList.add(question);
            } while (c.moveToNext());
        } else {
            // table is empty
            Log.d(LOG_TAG, "Count of questions is 0");
        }
        c.close();
    }
}
