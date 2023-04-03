package com.blogs.shiks.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blogs.shiks.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class ViewWeb extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;
    FloatingActionButton ChangeLink;
    String HomeLink = "https://google.com"; //"http://192.168.43.102/Shopping/";
    String previousPage = HomeLink;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_web);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        ChangeLink = findViewById(R.id.changeLink);

        ChangeLink.setVisibility(View.GONE);

        /*if (!getLink().isEmpty()){
            HomeLink = getLink();
        }*/

        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                setProgress(progress*100);

                if(progress == 100){
                    setTitle(R.string.app_name);
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        /*if(Build.VERSION.SDK_INT<=18){
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }else{
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }*/

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
                pullToRefresh.setRefreshing(false);
            }
        });
        resumeLoading();
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ChangeLink.setVisibility(View.VISIBLE);
                return false;
            }
        });
        ChangeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeLink.setVisibility(View.GONE);
                setUserDialog();
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void resumeLoading(){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);

                /*if(!isInternetAvailable()){
                    Intent intent = new Intent(new Intent(Home.this, NoInternet.class));
                    intent.putExtra("STAGE", "resume");
                    startActivity(intent);
                }*/
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                /*if(!Uri.parse(url).getHost().equals(HomeLink)){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    view.loadUrl(previousPage);
                    return false;
                }*/
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                previousPage = url;
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(HomeLink);
    }
    private void setUserDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewWeb.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(ViewWeb.this).inflate(R.layout.save_number, viewGroup, false);

        TextInputLayout Link = dialogView.findViewById(R.id.textInputLayout);
        TextView Save = dialogView.findViewById(R.id.textView6);

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Save.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View view) {
                if (Link.getEditText().getText().toString().isEmpty()){
                    Link.setError("Enter url");
                }else{
                    setLink(Link.getEditText().getText().toString());
                }
                alertDialog.dismiss();
            }
        });
    }
    private void setLink(String link){
        SharedPreferences sharedPreferences = getSharedPreferences("Link", Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Link", link);
        editor.apply();
    }
    private String getLink(){
        SharedPreferences sharedPreferences = getSharedPreferences("Link", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Link", "");
    }
}