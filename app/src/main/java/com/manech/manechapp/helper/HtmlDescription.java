package com.manech.manechapp.helper;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public final class HtmlDescription {
    private HtmlDescription() {
    }

    public static void render(WebView webView, String html) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        webView.setWebViewClient(new WebViewClient());
        webView.loadDataWithBaseURL(null, html == null ? "" : html,
                "text/html", "UTF-8", null);
    }
}
