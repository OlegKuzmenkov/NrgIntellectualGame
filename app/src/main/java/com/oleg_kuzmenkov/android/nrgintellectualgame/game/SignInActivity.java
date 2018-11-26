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

public class SignInActivity extends AppCompatActivity {
    private static final String INTENT_CONTENT = "INTENT_CONTENT";
    private static final String BUNDLE_CONTENT = "BUNDLE_CONTENT";

    private EditText mUserLogin;
    private Button mSignInButton;
    private String mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mUserLogin = findViewById(R.id.login_edit_text);

        if (savedInstanceState != null) {
            mLogin = savedInstanceState.getString(BUNDLE_CONTENT);
            mUserLogin.setText(mLogin);
        }

        mSignInButton = findViewById(R.id.login_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLogin = mUserLogin.getText().toString();
                if (mLogin.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter login", Toast.LENGTH_SHORT).show();
                } else {
                    Intent startMenuIntent = new Intent(getApplicationContext(), MenuScreenActivity.class);
                    startMenuIntent.putExtra(INTENT_CONTENT, mLogin);
                    startActivity(startMenuIntent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_CONTENT, mLogin);
    }
}
