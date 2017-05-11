package com.example.kmj.week10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Boolean barshow=true;
    EditText et;
    WebView webview;
    Animation animTop;
    Animation animTopReveal;
    LinearLayout linear;
    ArrayList<Data> arrayList = new ArrayList<>();
    ArrayAdapter<Data> adapter;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void setListView(){
        lv=(ListView)findViewById(R.id.listview);
        adapter=new ArrayAdapter<Data>(this,android.R.layout.simple_list_item_1,arrayList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Anim2();
                lv.setVisibility(View.GONE);
                webview.loadUrl(arrayList.get(position).getUrl());
                webview.setVisibility(View.VISIBLE);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                final int pos = position;
                dlg.setTitle("삭제하시겠습니까?")
                        .setMessage("삭제확인")
                        .setPositiveButton("닫기",null)
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                arrayList.remove(pos);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    void init() {
        setListView();
        Data google = new Data("google","http://www.google.com");
        Data naver = new Data("naver","http://www.naver.com");
        arrayList.add(google);
        arrayList.add(naver);
        lv.setVisibility(View.GONE);
        et = (EditText)findViewById(R.id.url);
        linear = (LinearLayout)findViewById(R.id.linear);
        webview = (WebView) findViewById(R.id.webview);
        webview.addJavascriptInterface(new JavaScriptMethods(),"MyApp");
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
        webview.loadUrl("http://www.naver.com");
        animTop= AnimationUtils.loadAnimation(this,R.anim.translate_top);
        animTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        linear.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animTopReveal= AnimationUtils.loadAnimation(this,R.anim.translate_top_reveal);
        animTopReveal.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        linear.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                et.setText(url);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });

        final ProgressDialog dialog;
        dialog = new ProgressDialog(this);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int
                    newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) dialog.dismiss();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,"즐겨찾기추가");
        menu.add(0,2,0,"즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==1){
            if (barshow) {
                barshow = false;
                linear.startAnimation(animTop);
            }
            webview.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
            webview.loadUrl("file:///android_asset/www/urladd.html");
        }
        if (item.getItemId()==2){
            if (barshow) {
                barshow = false;
                linear.startAnimation(animTop);
            }
            webview.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    Handler myHandler = new Handler();



    class JavaScriptMethods {

        @JavascriptInterface
        public void add(String siteName, String siteUrl) {
            final String name = siteName;
            final String url = siteUrl;
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    boolean already = false;
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).getUrl().equals(url)) {
                            already = true;
                            break;
                        }
                    }
                    if (already) {
                        webview.loadUrl("javascript:displayMsg()");
                    } else {
                        arrayList.add(new Data(name,url));
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
        @JavascriptInterface
        public void Reveal() {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    Anim2();
                }
            });
        }
    }

    public void Anim2(){
        Toast.makeText(this,"called",Toast.LENGTH_SHORT).show();
        if (!barshow) {
            barshow = true;
            linear.startAnimation(animTopReveal);
        }
    }


    public void OnClick(View v){
        if (v.getId()==R.id.bt1){
            webview.loadUrl(et.getText().toString());
        }
    }
}
