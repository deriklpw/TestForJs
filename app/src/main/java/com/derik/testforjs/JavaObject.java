package com.derik.testforjs;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by Derik on 2018/12/3.
 * Email: weilai0314@163.com
 */

public class JavaObject {

    @JavascriptInterface
    public void show(String msg){
        Log.d("222", "show: " +msg);
    }
}
