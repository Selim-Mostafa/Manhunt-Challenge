package app.manhunt.challenge

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var pendingAudioRequest: PermissionRequest? = null
    private var pendingLocationOrigin: String? = null
    private var pendingLocationCallback: android.webkit.GeolocationPermissions.Callback? = null
    private val preferences by lazy { getSharedPreferences("manhunt_settings", Context.MODE_PRIVATE) }
    private val homeUrl = "https://selim-mostafa.github.io/Manhunt-Challenge/"
    private val audioPermissionRequest = 1001
    private val notificationPermissionRequest = 1002
    private val locationPermissionRequest = 1003
    private val notificationChannelId = "manhunt_game_events"

    private inner class ManhuntBridge {
        @JavascriptInterface
        fun requestNotificationPermission() {
            runOnUiThread { requestGameNotifications() }
        }

        @JavascriptInterface
        fun disableNotifications() {
            preferences.edit().putBoolean("notifications_enabled", false).apply()
            runOnUiThread { notifyPagePermission(false) }
        }

        @JavascriptInterface
        fun notificationsEnabled(): Boolean = isGameNotificationsEnabled()

        @JavascriptInterface
        fun showNotification(title: String, body: String) {
            runOnUiThread { showGameNotification(title, body) }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.addJavascriptInterface(ManhuntBridge(), "AndroidManhunt")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                return if (uri.scheme == "https" && uri.host == "selim-mostafa.github.io") {
                    false
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                    true
                }
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: android.webkit.GeolocationPermissions.Callback) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    callback.invoke(origin, true, false)
                } else {
                    pendingLocationOrigin = origin
                    pendingLocationCallback = callback
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), locationPermissionRequest)
                }
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                val audioOnly = request.resources.isNotEmpty() &&
                    request.resources.all { it == PermissionRequest.RESOURCE_AUDIO_CAPTURE }
                if (!audioOnly) {
                    request.deny()
                    return
                }
                runOnUiThread {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                        checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        request.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                    } else {
                        pendingAudioRequest?.deny()
                        pendingAudioRequest = request
                        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), audioPermissionRequest)
                    }
                }
            }
        }

        if (savedInstanceState == null) webView.loadUrl(homeUrl)
        else webView.restoreState(savedInstanceState)
    }

    private fun requestGameNotifications() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), notificationPermissionRequest)
        } else {
            preferences.edit().putBoolean("notifications_enabled", true).apply()
            createNotificationChannel()
            notifyPagePermission(true)
        }
    }

    private fun notifyPagePermission(granted: Boolean) {
        if (::webView.isInitialized) {
            webView.evaluateJavascript("window.onManhuntNotificationPermission?.($granted)", null)
        }
    }

    private fun isGameNotificationsEnabled(): Boolean {
        val permissionGranted = Build.VERSION.SDK_INT < 33 ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        return permissionGranted && preferences.getBoolean("notifications_enabled", false)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(notificationChannelId, "Manhunt game events", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }

    private fun showGameNotification(title: String, body: String) {
        if (!isGameNotificationsEnabled()) return
        createNotificationChannel()
        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, notificationChannelId)
        } else {
            Notification.Builder(this)
        }
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title.take(80))
            .setContentText(body.take(180))
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_DEFAULT)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), builder.build())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            audioPermissionRequest -> {
                val request = pendingAudioRequest
                pendingAudioRequest = null
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                    request?.grant(arrayOf(PermissionRequest.RESOURCE_AUDIO_CAPTURE))
                } else {
                    request?.deny()
                }
            }
            notificationPermissionRequest -> {
                val granted = Build.VERSION.SDK_INT < 33 ||
                    grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
                preferences.edit().putBoolean("notifications_enabled", granted).apply()
                if (granted) createNotificationChannel()
                notifyPagePermission(granted)
            }
            locationPermissionRequest -> {
                val granted = grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
                val origin = pendingLocationOrigin
                val callback = pendingLocationCallback
                pendingLocationOrigin = null
                pendingLocationCallback = null
                if (origin != null && callback != null) callback.invoke(origin, granted, false)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        pendingAudioRequest?.deny()
        pendingAudioRequest = null
        pendingLocationCallback?.invoke(pendingLocationOrigin ?: "", false, false)
        pendingLocationCallback = null
        pendingLocationOrigin = null
        if (::webView.isInitialized) {
            webView.removeJavascriptInterface("AndroidManhunt")
            webView.destroy()
        }
        super.onDestroy()
    }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}
