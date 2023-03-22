package com.absolute.bnj;

import static com.absolute.bnj.Constants.FEED_COMPLIANCE_URL;
import static com.absolute.bnj.Constants.FEED_HOME_URL;
import static com.absolute.bnj.Constants.FEED_PROFILE_URL;
import static com.absolute.bnj.Constants.FEED_SERVICES_URL;
import static com.absolute.bnj.Constants.LOGIN_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class WebviewActivity extends Activity {
    private static final String TAG = "MainActivity";
    MyWebView myWebView;
    SwipeRefreshLayout srl;
    Context context = this;
    LottieAnimationView lottie_graph;
    LinearLayout loading_layout;
    RelativeLayout rtl;
    TextView reload;

    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            url = extras.getString("url");
            Log.d(TAG, "onCreate: webviewnew "+url);
        }
        initVars();

    }

    private void initVars() {
        myWebView = findViewById(R.id.myWebView);
        lottie_graph = findViewById(R.id.lottie_graph);
        loading_layout = findViewById(R.id.loading_layout);
        rtl = findViewById(R.id.rtl);
        reload = findViewById(R.id.reload);

        TextView loading_text;
        loading_text = findViewById(R.id.loading_text);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/poppins_regular.ttf");
        loading_text.setTypeface(typeface);

        srl = findViewById(R.id.srl);
        myWebView.loadMyURL(url, srl, rtl);


        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myWebView.loadMyURL(myWebView.getUrl(), srl, rtl);
                setVloading(true);

            }
        });

        myWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                setVloading(true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {
                if(url.startsWith("tel:") || url.startsWith("whatsapp:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: "+url);
                setVloading(false);
            }

        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWebView.loadMyURL(myWebView.getUrl(), srl, rtl );
            }
        });

        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(myWebView.canGoBack()){
            if(myWebView.getUrl().contains(FEED_HOME_URL)){
                super.onBackPressed();
            }else if(myWebView.getUrl().contains(FEED_SERVICES_URL)
                    || myWebView.getUrl().contains(FEED_COMPLIANCE_URL)
                    || myWebView.getUrl().contains(FEED_PROFILE_URL)
            ){
                myWebView.loadMyURL(FEED_HOME_URL, srl, rtl);
            }
            else{
                myWebView.goBack();
            }

        }else {
            super.onBackPressed();
        }
    }

    public void setVloading(boolean showState){
        srl.setRefreshing(false);
        if(showState){
            lottie_graph.setVisibility(View.VISIBLE);
            loading_layout.setVisibility(View.VISIBLE);
            lottie_graph.loop(true);
            myWebView.setVisibility(View.GONE);
        }else{
            lottie_graph.setVisibility(View.GONE);
            loading_layout.setVisibility(View.GONE);
            lottie_graph.loop(false);
            myWebView.setVisibility(View.VISIBLE);

        }
    }
}