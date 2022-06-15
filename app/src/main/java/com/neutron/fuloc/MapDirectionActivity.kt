package com.neutron.fuloc

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import com.neutron.fuloc.databinding.ActivityMapDirectionBinding

class MapDirectionActivity : AppCompatActivity() {
    private lateinit var activityMapDirectionBinding: ActivityMapDirectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapDirectionBinding = ActivityMapDirectionBinding.inflate(layoutInflater)
        setContentView(activityMapDirectionBinding.root)

        if(isInternetConnected()){
            val url:String? = intent.extras?.getString("URL")
            displayMapWithDirections(url)
        }
    }

    private fun displayMapWithDirections(url: String?) {
        url?.let { link ->
            activityMapDirectionBinding.webView.apply {
                webViewClient = WebViewClient()
                loadUrl(link)
                settings.javaScriptEnabled = true
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun isInternetConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}