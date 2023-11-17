package com.absolute.bnj;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.net.URLDecoder;

public class MyWebView extends WebView {
    private static final String TAG = "MyWebView";
    Context context;
    Session session;

    @SuppressLint("JavascriptInterface")
    public MyWebView(@NonNull Context context) {

        super(context);
        this.context = context;
        session = new Session(context);

    }

    public MyWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressLint("JavascriptInterface")
    private void init(Context context) {
        this.context = context;

        getSettings().setJavaScriptEnabled(true);
        getSettings().setUseWideViewPort(true);

        getSettings().setAllowFileAccessFromFileURLs(true);
        getSettings().setAllowUniversalAccessFromFileURLs(true);
        getSettings().setDomStorageEnabled(true);

        getSettings().setPluginState(WebSettings.PluginState.ON);
//        getSettings().setAppCacheEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //load no cache will stop loading cache
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
        getSettings().setDatabaseEnabled(true);

//        getSettings().setAllowFileAccess(true);
//        getSettings().setAllowContentAccess(true);

        Log.d(TAG, "init: mywebviewloading");

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            getSettings().setDatabasePath("/data/data/" + getContext().getPackageName() + "/databases/");
//        }
        getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        setWebContentsDebuggingEnabled(true);

        //to disable select text
        setLongClickable(false);




//        getSettings().setJavaScriptEnabled(true);
//        getSettings().setUseWideViewPort(true);
//
//        getSettings().setAllowFileAccess(true);
//        getSettings().setAllowFileAccessFromFileURLs(true);
//        getSettings().setAllowUniversalAccessFromFileURLs(true);
//        getSettings().setDomStorageEnabled(true);
//
//        getSettings().setPluginState(WebSettings.PluginState.ON);
//        getSettings().setAppCacheEnabled(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
////            getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        }
//        getSettings().setDatabaseEnabled(true);
//
//        getSettings().setAllowFileAccess(true);
//        getSettings().setAllowContentAccess(true);
//
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            getSettings().setDatabasePath("/data/data/" + getContext().getPackageName() + "/databases/");
//        }
//        getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        setWebContentsDebuggingEnabled(true);
//
//        //to disable select text
//        setLongClickable(false);






        getSettings().setMediaPlaybackRequiresUserGesture(false);
        setWebViewClient(new WebViewClient(){
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
                saveTokenDataToLocalStorage();
            }
        });

        setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView wv, String url) {

                Uri query_string= Uri.parse(url);
                String query_scheme=query_string.getScheme();
                String query_host=query_string.getHost();
                if ((query_scheme.equalsIgnoreCase("https") || query_scheme.equalsIgnoreCase("http"))
                        && query_host!=null && query_host.equalsIgnoreCase(Uri.parse(url).getHost())
                        && query_string.getQueryParameter("new_window")==null
                ) {
                    return false;//handle the load by webview
                }
                if(url.startsWith("tel:") || url.startsWith("whatsapp:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    context.startActivity(intent);
                    return true;
                }
                try {
                    Intent intent=new Intent(Intent.ACTION_VIEW, query_string);
                    String[] body=url.split("\\?body=");
                    if (query_scheme.equalsIgnoreCase("sms") && body.length>1)
                    {intent=new Intent(Intent.ACTION_VIEW, Uri.parse(body[0]));
                        intent.putExtra("sms_body", URLDecoder.decode(body[1]));
                    }
                    context.startActivity(intent);//handle the load by os
                }
                catch (Exception e) {}
                return true;


            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: "+url);
                saveTokenDataToLocalStorage();

            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
//                Log.d(TAG, "onReceivedHttpError: "+errorResponse.getStatusCode());

//                if (errorResponse.getStatusCode()!=200) {
//                    loadUrl("file:///android_asset/html/error.html");
//                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
//                loadUrl("file:///android_asset/html/error.html");
                invalidate();

            }
        });

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });



        addJavascriptInterface(new MyWebView(context), "Android");


    }

    public void saveTokenDataToLocalStorage() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            evaluateJavascript("window.localStorage.setItem('utoken','tokenvalue');", null);
//            evaluateJavascript("window.localStorage.setItem('umisid','tokenvalue');", null);
//            evaluateJavascript("window.localStorage.setItem('umsdisid','tokenvalue');", null);
//            Log.d(TAG, "saveTokenDataToLocalStorage: setdata");
//        } else {
//            evaluateJavascript("javascript:localStorage.setItem('utoken','tokenvalue');", null);
//            evaluateJavascript("javascript:localStorage.setItem('umisid','tokenvalue');", null);
//            evaluateJavascript("javascript:localStorage.setItem('umsdisid','tokenvalue');", null);
//            Log.d(TAG, "saveTokenDataToLocalStorage: setdata 1");
//        }

    }


    @JavascriptInterface
    public void WebAction(String url , String newActivity, String openBrowser ){
        Log.d("TAG", "from webview msg to mywebview "+url);
        Log.d(TAG, "WebAction: newActivity:"+newActivity);
        Log.d(TAG, "WebAction: openBrowser:"+openBrowser);
        if(newActivity!=null && newActivity.equals("true")){
            Log.d(TAG, "WebAction: newactivity");
            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);
        }else if(openBrowser!=null && openBrowser.equals("true")){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
            Log.d(TAG, "WebAction: openbrowsser");
        }else{
            //default open in new intent
            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);
            Log.d(TAG, "WebAction: ndewactivity");
        }
    }

    @JavascriptInterface
    public void saveLogin(String mobile, String userid, String token){
        Log.d(TAG, "saveLogin: Mobile:"+mobile+" | uid:"+userid+" | token:"+token);
        session.saveLoginInfo(mobile, userid, token);
    }
    @JavascriptInterface
    public void logout(){
        Log.d(TAG, "Logout method called");
        session.logoutSession();
    }
    @JavascriptInterface
    public String getDeviceInfo(){
        String device_info="";
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String model = Build.MODEL;

        String device = "Android";
        device += ","+ Build.VERSION.RELEASE;

//        String brand = Build;
        device_info = manufacturer+","+model+","+brand+","+device;
        return device_info;
    }
    @JavascriptInterface
    public String getAppVersion(){
        return String.valueOf(BuildConfig.VERSION_CODE);
    }

    public void loadMyURL(String url, SwipeRefreshLayout srl, RelativeLayout rtl){
//        String postData = "token="+token+"&mobile="+mobile+"&userid="+userid;
        ////String urlData = "?token="+token+"&mobile="+mobile+"&userid="+userid;

//        postUrl(url, postData.getBytes());
//        loadUrl(url+urlData);


//        loadUrl(url);

        if(Libs.isNetworkAvailable(context)){
            srl.setVisibility(VISIBLE);
            rtl.setVisibility(GONE);
            loadUrl(url);
        }else{
            srl.setVisibility(GONE);
            rtl.setVisibility(VISIBLE);

//            loadUrl("file:///android_asset/html/error.html");
        }
    }


    @JavascriptInterface
    public void BackPress(){
        Activity activity = (Activity) context;
        activity.finish();
    }

}
