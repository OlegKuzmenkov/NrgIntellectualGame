package com.oleg_kuzmenkov.android.nrgintellectualgame.news;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.oleg_kuzmenkov.android.nrgintellectualgame.R;

public class NewsActivity extends AppCompatActivity {
    private static final String INTENT_CONTENT = "URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original_news);

        String  url = getIntent().getStringExtra(INTENT_CONTENT);

        WebView web = findViewById(R.id.news_web_view);
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(url);
    }
}
