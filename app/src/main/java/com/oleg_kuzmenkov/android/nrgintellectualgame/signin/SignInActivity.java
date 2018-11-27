package com.oleg_kuzmenkov.android.nrgintellectualgame.signin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;
import com.oleg_kuzmenkov.android.nrgintellectualgame.menu.MenuScreenActivity;

public class SignInActivity extends AppCompatActivity implements SignInView{
    private static final String BUNDLE_CONTENT = "BUNDLE_CONTENT";
    private static final String INTENT_CONTENT = "INTENT_CONTENT";

    private SignInPresenter mPresenter;

    private EditText mUserLogin;
    private Button mSignInButton;

    @Override
    public void displayNotification() {
        Toast.makeText(getApplicationContext(), "Please enter login", Toast.LENGTH_SHORT).show();
    }

    /**
     * Start MenuActivity. Send user login in it.
     */
    @Override
    public void displayMenu(String userLogin) {
        Intent startMenu = new Intent(getApplicationContext(), MenuScreenActivity.class);
        startMenu.putExtra(INTENT_CONTENT, userLogin);
        startActivity(startMenu);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        setupPresenter(savedInstanceState);

        mUserLogin = findViewById(R.id.login_edit_text);
        mSignInButton = findViewById(R.id.login_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.checkUserLogin(mUserLogin.getText().toString());
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(BUNDLE_CONTENT, mPresenter);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detach();
        super.onDestroy();
    }

    /**
     * Setup presenter. Create or restore it.
     */
    private void setupPresenter(final Bundle savedInstanceState){
        if (savedInstanceState == null) {
            mPresenter = new SignInPresenter();
        } else {
            mPresenter = (SignInPresenter) savedInstanceState.getSerializable(BUNDLE_CONTENT);
        }
        mPresenter.setView(this);
    }
}
