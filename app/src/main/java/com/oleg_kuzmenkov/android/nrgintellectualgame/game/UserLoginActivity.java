package com.oleg_kuzmenkov.android.nrgintellectualgame.game;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.menu.MenuScreenActivity;

public class UserLoginActivity extends AppCompatActivity {
    private static final String INTENT_CONTENT = "content";
    private static final String BUNDLE_CONTENT = "content";

    private EditText mUserLoginEditText;
    private Button mLoginButton;
    private String mUserLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mUserLoginEditText = findViewById(R.id.login_edit_text);

        if (savedInstanceState != null) {
            mUserLogin = savedInstanceState.getString(BUNDLE_CONTENT);
            mUserLoginEditText.setText(mUserLogin);
        }

        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserLogin = mUserLoginEditText.getText().toString();
                if (mUserLogin.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter login", Toast.LENGTH_SHORT).show();
                } else {
                    Intent startMenuIntent = new Intent(getApplicationContext(), MenuScreenActivity.class);
                    startMenuIntent.putExtra(INTENT_CONTENT, mUserLogin);
                    startActivity(startMenuIntent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_CONTENT, mUserLogin);
    }
}
