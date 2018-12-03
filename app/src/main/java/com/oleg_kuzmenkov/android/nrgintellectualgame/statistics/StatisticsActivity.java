package com.oleg_kuzmenkov.android.nrgintellectualgame.statistics;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.model.User;

public class StatisticsActivity extends AppCompatActivity {
    private static final String INTENT_CONTENT = "1";

    private Button mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        User user = (User) getIntent().getSerializableExtra(INTENT_CONTENT);
        setUserStatistics(user);

        mExit = findViewById(R.id.go_to_main_menu_button);
        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        animateButton();
    }

    /**
     * Animate Button
     */
    private void animateButton() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(mExit, "rotationY", 0.0f, 360f);
        animation.setDuration(2000);
        animation.setStartDelay(1000);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setInterpolator(new AnticipateOvershootInterpolator());
        animation.start();
    }

    /**
     * Set the current user statistics
     */
    private void setUserStatistics(User user) {
        if (user != null) {
            TextView userId = findViewById(R.id.id_text_view);
            userId.setText(String.format("ID: %s", user.getId()));

            TextView userLogin = findViewById(R.id.login_text_view);
            userLogin.setText(String.format("Login: %s", user.getLogin()));

            TextView userAnswersCount = findViewById(R.id.count_answers_text_view);
            int answersCount = user.getAnswersCount();
            userAnswersCount.setText(String.format("Count answers: %d", answersCount));

            TextView userRightAnswersCount = findViewById(R.id.count_right_answers_text_view);
            int rightAnswersCount = user.getRightAnswersCount();
            userRightAnswersCount.setText(String.format("Count right answers: %d", rightAnswersCount));

            TextView userRightAnswersPercent = findViewById(R.id.percent_right_answers_text_view);
            int rightAnswersPercent = calculatePercentage(answersCount,rightAnswersCount);
            userRightAnswersPercent.setText(String.format("%d", rightAnswersPercent));
        }
    }

    private int calculatePercentage(int answersCount, int rightAnswersCount) {
        return answersCount > 0 ? rightAnswersCount * 100 / answersCount : 0;
    }
}
