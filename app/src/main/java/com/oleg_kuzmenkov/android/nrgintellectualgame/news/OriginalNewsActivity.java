package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;

public class OriginalNewsActivity extends AppCompatActivity {
    private final String INTENT_CONTENT = "url";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original_news);

        Intent intent = getIntent();
        String  url = intent.getStringExtra(INTENT_CONTENT);

        mWebView = findViewById(R.id.news_web_view);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView .getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
    }
}
