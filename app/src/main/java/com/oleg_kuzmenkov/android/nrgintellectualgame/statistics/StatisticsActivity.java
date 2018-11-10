package com.oleg_kuzmenkov.android.nrgintellectualgame.statistics;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
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

    private TextView mUserIdTextView;
    private TextView mUserLoginTextView;
    private TextView mUserCountAnswersTextView;
    private TextView mUserCountRightAnswersTextView;
    private TextView mUserPercentRightAnswersTextView;
    private Button mGoToMainMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        mUserIdTextView = findViewById(R.id.id_text_view);
        mUserLoginTextView = findViewById(R.id.login_text_view);
        mUserCountAnswersTextView = findViewById(R.id.count_answers_text_view);
        mUserCountRightAnswersTextView = findViewById(R.id.count_right_answers_text_view);
        mUserPercentRightAnswersTextView = findViewById(R.id.percent_right_answers_text_view);
        mGoToMainMenuButton = findViewById(R.id.go_to_main_menu_button);

        Intent intent = getIntent();
        User user = (User)intent.getSerializableExtra(INTENT_CONTENT );

        setContent(user);

        mGoToMainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent startMainMenuIntent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                //startActivity(startMainMenuIntent);
                finish();
            }
        });

        animateButton();
    }

    /**
     * Animate Button
     */
    private void animateButton(){
        ObjectAnimator animation = ObjectAnimator.ofFloat(mGoToMainMenuButton, "rotationY", 0.0f, 360f);
        animation.setDuration(2000);
        animation.setStartDelay(1000);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setInterpolator(new AnticipateOvershootInterpolator());
        animation.start();
    }

    /**
     * Set the received content in the game
     */
    private void setContent(User user) {
        if (user != null) {
            mUserIdTextView.setText("ID: "+user.getUserId());
            mUserLoginTextView.setText("Login: "+user.getUserLogin());
            mUserCountAnswersTextView.setText("Count answers: "+Integer.toString(user.getCountAnswers()));
            mUserCountRightAnswersTextView.setText("Count right answers: "+Integer.toString(user.getCountRightAnswers()));
            if(user.getCountAnswers() != 0) {
                //float percent2 = (user.getCountRightAnswers() * 100.0f) / user.getCountAnswers();
                int percent = (int) (user.getCountRightAnswers() * 100.0f) / user.getCountAnswers();
                mUserPercentRightAnswersTextView.setText(Integer.toString(percent));
            } else{
                mUserPercentRightAnswersTextView.setText("0");
            }
        }
    }
}
