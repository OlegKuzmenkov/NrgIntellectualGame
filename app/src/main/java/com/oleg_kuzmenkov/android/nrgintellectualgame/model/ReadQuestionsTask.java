package com.oleg_kuzmenkov.android.nrgintellectualgame.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

class ReadQuestionsTask extends AsyncTask<Void, Void, Void> {

    private SQLiteDatabase mDatabase;
    private Repository.ReadQuestionsCallback mListener;
    private List<Question> mQuestionList;

    ReadQuestionsTask(final SQLiteDatabase database, List<Question> questions,
                      Repository.ReadQuestionsCallback listener) {
        mDatabase = database;
        mQuestionList = questions;
        mListener = listener;
    }

    protected Void doInBackground(Void... params) {
        readQuestions();
        return null;
    }

    protected void onPostExecute(Void result) {
        mListener.onFinished(mQuestionList);
    }

    /**
     * Read questions list from database
     */
    private void readQuestions() {
        Cursor c = mDatabase.query(Database.TABLE_QUESTION, null, null,
                null, null, null, null);

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
                question.setRightAnswer(c.getString(questionRightAnswerColIndex));

                List<String> answersList = new ArrayList<>();
                answersList.add(c.getString(questionFirstAnswerColIndex));
                answersList.add(c.getString(questionSecondAnswerColIndex));
                answersList.add(c.getString(questionThirdAnswerColIndex));
                answersList.add(c.getString(questionFourthAnswerColIndex));
                question.setAnswersList(answersList);

                mQuestionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
    }
}
