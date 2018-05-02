package com.example.ktforlist.webviewcatchimgs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    WebView mWebView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) this.findViewById(R.id.survey_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.loadUrl("https://pages.tmall.com/wow/huanxin/act/refreshment-pc?spm=a21bo.2017.201863-1.d1.5af911d9rA1Vwv&pos=1&acm=201602246-1.1003.2.837482&scm=1003.2.201602246-1.OTHER_1509275436065_837482");
        mWebView.addJavascriptInterface(new ImagePreviewJavascript(MainActivity.this), "imagelist");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 在APP内部打开链接，不要调用系统浏览器
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                addCssUrl();
            }

        });
    }

    List<String> mTempImgUrls = new ArrayList<>();

    // js通信接口
    public class ImagePreviewJavascript {

        private Context context;

        public ImagePreviewJavascript(Context context) {
            this.context = context;
        }

        // TARGET>17的时候一定要加@JS注解
        @JavascriptInterface
        public void getImages(final String[] imgUrl) {
            try {
                List list = Arrays.asList(imgUrl);
                Set set = new HashSet(list);
                List tmpList = new ArrayList();
                tmpList.addAll(set);//把set的
//                String[] mTempImgUrls = (String [])set.toArray(new String[0]);
                if (tmpList.size() > 0) {
                    if (mTempImgUrls != null) {
                        mTempImgUrls.clear();
                    } else {
                        mTempImgUrls = new ArrayList<>();
                    }
                    if (tmpList.size() > 40) {
                        mTempImgUrls.addAll(tmpList.subList(0, 40));
                    } else {
                        mTempImgUrls.addAll(tmpList);
                    }
                    for (String img :mTempImgUrls) {
                        Log.i("imgheng","img==="+img);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void addCssUrl() {
//        目前部署在it1地址：
//        http://cn.it1.dealmoon.net/assets/js/fetchImg.js
//
//        今天下午会正式上线服务器，
//        届时地址将会是：
//        http://cn.dealmoon.com/assets/js/fetchImg.js
//        请周知~


//        mWebView.loadUrl("javascript:(alert(111);" +
//                "             var d=document; " +
//                "             var s=d.createElement('link');" +
//                "             s.setAttribute('rel','stylesheet');" +
//                "             s.setAttribute('href','http://cn.it1.dealmoon.net/assets/js/fetchImg.js');" +
//                "             d.head.appendChild(s);)");

        if (mTempImgUrls != null) {
            mTempImgUrls.clear();
        } else {
            mTempImgUrls = new ArrayList<>();
        }
        mWebView.loadUrl("javascript:(function getImgs(){"
                + "var imgs = document.getElementsByTagName(\"img\"); "
                + "console.log('all imgs get imgs' + imgs);\n"
                + "var imgUrls = [], tmpImgUrls = [], tmpUrl, count = 0, urlPrefix = location.protocol + '://' + location.host + '/';\n"
                + "var MIN_IMG_WIDTH = 90, MIN_IMG_HEIGHT = 90;\n"
                + "console.log('all imgs get imgUrls' + imgUrls);\n"
                + "function imgSel(src, callback){\n"
                + "   console.log('all imgs get imgSel' + src);\n"
                + "   var img = new Image();\n"
                + "   img.src = src;\n"
                + "   console.log('all imgs get img.src' + src);\n"
                + "   img.onload = function () {\n"
                + "     setTimeout(function() {\n"
                + "         callback && callback({\n"
                + "             width: img.width,\n"
                + "             height: img.height\n"
                + "         });\n"
                + "     }, 0);\n"
                + "   };\n"
                + "   img.onerror = function () {\n"
                + "     setTimeout(function() {\n"
                + "         console.log('all imgs get error');\n"
                + "         callback && callback();\n"
                + "     }, 0);\n"
                + "   }\n"
                + "}\n"
                + "for(var i = 0; i < imgs.length; i++){\n"
                + "    console.log('all imgs get for' + imgs);\n"
                + "    tmpUrl = imgs[i].src || imgs[i].getAttribute('data-src');\n"
                + "    if(tmpUrl && tmpUrl.indexOf('http') != 0) {\n"
                + "          console.log('all imgs get tmpUrl' + tmpUrl);\n"
                + "          tmpUrl = urlPrefix + tmpUrl;\n"
                + "    }\n"
                + "    tmpUrl && tmpImgUrls.push(tmpUrl);\n"
                + "    console.log('all imgs get tmpUrl' + tmpUrl);\n"
                + "}\n"
                + "tmpImgUrls.forEach(function (url, index) {\n"
                + "    console.log('all imgs get forEach' + url);\n"
                + "    imgSel(url, function (data) {\n"
                + "          count++;\n"
                + "          if(data && data.width > MIN_IMG_WIDTH && data.height > MIN_IMG_HEIGHT) {\n"
                + "                 imgUrls.push({url: url, width: data.width, height: data.height});\n"
                + "          }\n"
                + "          if(count == tmpImgUrls.length) {\n"
                + "                 console.log('all imgs get done');\n"
                + "                 window.imagelist.getImages(tmpImgUrls);"
                + "           }\n"
                + "      })\n"
                + "});\n"
                + "})()");
    }

}
