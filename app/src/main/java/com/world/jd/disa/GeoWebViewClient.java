package com.world.jd.disa;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GeoWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // When user clicks a hyperlink, load in the existing WebView
        view.loadUrl(url);
        return true;
    }
}
