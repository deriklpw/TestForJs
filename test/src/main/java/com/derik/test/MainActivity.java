package com.derik.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = findViewById(R.id.webview1);

        String ua = webView.getSettings().getUserAgentString();

        webView.getSettings().setUserAgentString(ua + "; 自定义标记");

        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", ua + "; 自定义标记");
        webView.loadUrl("http://www.useragents.com/");
        Log.d("000", "webview，设置自定义UA，ua=" + webView.getSettings().getUserAgentString());


        WebView webView2 = findViewById(R.id.webview2);
        webView2.loadUrl("http://www.useragents.com/");
        Log.d("111", "webview2，未设置自定义UA，ua=" + webView2.getSettings().getUserAgentString());

        // request("https://www.baidu.com/");
        request2("http://www.useragents.com/", ua + "; 自定义标记");

    }

    private void request2(final String url, final String ua) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                InputStream is = null;
                try {
                    URL requestUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
                    conn.setConnectTimeout(8 * 1000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", ua);
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoInput(true);

                    int state = conn.getResponseCode();

                    if (state == 200) {
                        is = conn.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);

                        Log.d("222", "run: Request header, ua= " + conn.getRequestProperty("User-Agent"));
                        Log.d("222", "run: Request header, Content-Type= " + conn.getHeaderField("Content-Type"));

                        char[] buffer = new char[1024];
                        int read;
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((read = isr.read(buffer)) > 0) {
                            stringBuilder.append(buffer, 0, read);
                        }

                        Log.d("111", "run Response: " + stringBuilder.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void request(String url) {
        OkHttpClientUtils.getClient().get(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("request", "onFailure: ");

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("request", "onResponse: " + response.body().string());

            }
        });

    }
}
