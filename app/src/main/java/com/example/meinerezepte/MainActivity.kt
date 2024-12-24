package com.example.meinerezepte

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isFirstLoad = true
    private var isLoading = false

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                filePathCallback?.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
                )
            } else {
                filePathCallback?.onReceiveValue(null)
            }
            filePathCallback = null
        }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView: WebView = findViewById(R.id.webView)
        val loadingLayout: View = findViewById(R.id.loadingLayout)
        val webSettings = webView.settings

        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // Only show loading screen if it's the first load
                if (isFirstLoad) {
                    loadingLayout.visibility = View.VISIBLE
                    webView.visibility = View.GONE
                }
                isLoading = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Only hide loading screen after the first load
                if (isFirstLoad) {
                    loadingLayout.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                    isFirstLoad = false
                }
                isLoading = false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                setContentView(R.layout.activity_error)
                findViewById<View>(R.id.retryButton).setOnClickListener {
                    // Neustarten der Aktivität, um eine saubere Initialisierung zu gewährleisten
                    val intent = intent
                    finish()
                    startActivity(intent)
                }
                if (error?.errorCode != WebViewClient.ERROR_HOST_LOOKUP) {
                    setContentView(R.layout.undefined_error)
                    findViewById<View>(R.id.retryButton).setOnClickListener {
                        // Neustarten der Aktivität, um eine saubere Initialisierung zu gewährleisten
                        val intent = intent
                        finish()
                        startActivity(intent)
                    }
                }
            }
            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)

                // Check if the error is an internal server error (HTTP 500)
                if (errorResponse?.statusCode == 500) {
                    setContentView(R.layout.undefined_error)
                    findViewById<View>(R.id.retryButton).setOnClickListener {
                        // Neustarten der Aktivität, um eine saubere Initialisierung zu gewährleisten
                        val intent = intent
                        finish()
                        startActivity(intent)
                    }
                }
            }



            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                if (url.startsWith("https://api.whatsapp.com/")) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                this@MainActivity.filePathCallback = filePathCallback
                val intent = fileChooserParams.createIntent()
                try {
                    fileChooserLauncher.launch(intent)
                } catch (e: Exception) {
                    this@MainActivity.filePathCallback = null
                    return false
                }
                return true
            }
        }

        webView.loadUrl("https://flask-recipe-book-ureg.onrender.com/android")
    }

    override fun onBackPressed() {
        val webView: WebView = findViewById(R.id.webView)
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}