package net.oilchem.communication.sms.activity;

import net.oilchem.communication.sms.OilchemApplication;
import net.oilchem.communication.sms.R;
import net.oilchem.communication.sms.view.TitleBar;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.JsResult;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends OilActivityBase {
	public static String PARAMS_URL = "url";
	private WebView webView;
	private TitleBar titlebar;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_webview);
		url = getIntent().getExtras().getString(PARAMS_URL);
		webView = (WebView) this.findViewById(R.id.activity_webview_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setWebViewClient(new WebViewClient());
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}
		});
		titlebar = (TitleBar) this.findViewById(R.id.activity_webview_titlebar);
		webView.loadUrl(url);
		initTitlebar();
	}
	
	private void initTitlebar() {
		titlebar.setTitle(this, OilchemApplication.getResourceString(R.string.app_name));
		titlebar.setLeft(this, TitleBar.ACTION_BACK);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
}
