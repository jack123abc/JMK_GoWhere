package com.example.jmk2018.jmk_gowhere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class HtmlLogoActivity extends AppCompatActivity {

    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_logo);

        webview = findViewById(R.id.webViewLogo);
        webview.loadUrl("file:///android_asset/index.html");
        //webview.loadUrl("http://www.google.com/");
    }

}
