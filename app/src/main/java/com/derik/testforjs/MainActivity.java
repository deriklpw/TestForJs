package com.derik.testforjs;


import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private String js = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <title>Carson_Ho</title>\n" +
            "\n" +
            "    <script>\n" +
            "    function callJS(msg){\n" +
            "        javaobject.show(msg);\n" +
            "    }\n" +
            "\n" +
            "    </script>\n" +
            "\n" +
            "</head>\n" +
            "\n" +
            "</html>";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText input = findViewById(R.id.et_url);

        webView = findViewById(R.id.web_url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        // 设置允许执行JS脚本
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗，实际不会弹出，只是允许alert()等方法生效，弹窗需要在WebChromeClient中自定义实现
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua+"; 自定义标记");

        /**
         * JS通过WebView调用Android中的方法
         */
        //方式一：addJavascriptInterface
        webView.addJavascriptInterface(new JavaObject(), "javaobject"); //接口对象，Js中使用"javaobject"对象访问其方法，4.4以后，方法需要增加注释，否则无效
        //方式二：重写WebViewClient的shouldOverrideUrlLoading 拦截url
        //加载url时，解析url，是自定义的，则解析执行Android中的方法
        //只能通过loadUrl()传递返回值

        //方式三：WebChromeClient
        //比较灵活的方式

        Button load = findViewById(R.id.btn_load);
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入框加载原始url
//                String url = input.getText().toString().trim();
//                webView.loadUrl(url);
                /**
                 * webView加载html方法
                 */
                //方式一：加载本地js文件
                webView.loadUrl("file:///android_asset/javascript.html");
                //方式二：加载HTML String
//                webView.loadData(js,"text/html", "utf-8");

            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                /**
                 * 加载完js页之后，Android调用js中的方法
                 */
                int version = Build.VERSION.SDK_INT;
                if (version < 19 ){
                    //方式一：通过webView.loadUrl("javascript:jsMethod(params)")
                    view.loadUrl("javascript:callJS('Hello World')");
                } else {
                    //方式二：webView.evaluateJavaScript("javascript:jsMethod(params)", new ValueCallback<T>{
                    // public void onReceiveValue(T t){}})
                    //适用Android 4.4以上
                    view.evaluateJavascript("javascript:callJS('Hello, How are you')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("webView", "onReceiveValue: " + s);
                        }
                    });
                }

            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

        });

        Log.d(TAG, "onCreate: webView ua=" + webView.getSettings().getUserAgentString());

//        WebView webView1 = new WebView(this);
//        Log.d(TAG, "onCreate: webView1 ua=" + webView1.getSettings().getUserAgentString());

    }

}
