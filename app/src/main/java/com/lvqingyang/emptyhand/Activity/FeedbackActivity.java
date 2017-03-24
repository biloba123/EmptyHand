package com.lvqingyang.emptyhand.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.lvqingyang.emptyhand.R;
import com.lvqingyang.emptyhand.Tools.TypefaceUtils;


public class FeedbackActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private WebView webview;
    private static final String KEY_ADD = "ADD";

    public static void start(Context context,String add) {
        Intent starter = new Intent(context, FeedbackActivity.class);
        starter.putExtra(KEY_ADD,add);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        TypefaceUtils.setText((TextView) findViewById(R.id.title),getString(R.string.feedback));
        //Back
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.webview = (WebView) findViewById(R.id.web_view);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() { @Override public boolean shouldOverrideUrlLoading(WebView view, String
                url) {
            view.loadUrl(url); // 根据传入的参数再去加载新的网页
            return true; // 表示当前WebView可以处理打开新网页的请求，不用借助系统浏览器
        }
        });
        webview.loadUrl(getIntent().getStringExtra(KEY_ADD));
    }

}
