package org.jellyfin.mobile

import android.app.Application
import android.webkit.WebView
import org.jellyfin.mobile.app.apiModule
import org.jellyfin.mobile.app.applicationModule
import org.jellyfin.mobile.data.databaseModule
import org.jellyfin.mobile.utils.JellyTree
import org.jellyfin.mobile.utils.isWebViewSupported
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused")
class JellyfinApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Setup logging
        Timber.plant(JellyTree())

        if (BuildConfig.DEBUG) {
            // Enable WebView debugging
            if (isWebViewSupported()) {
                WebView.setWebContentsDebuggingEnabled(true)
            }

            // Read the URL, username, and password from the shared preferences
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val url = sharedPreferences.getString("url", "https://jellyfin.dotscale.tech")
            val username = sharedPreferences.getString("username", "carnival")
            val password = sharedPreferences.getString("password", "121456")

            // If the URL, username, and password are not empty, then login to the Jellyfin server
            if (url.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                val loginManager = LoginManager(this)
                loginManager.login(url, username, password)
            }
        }

        startKoin {
            androidContext(this@JellyfinApplication)
            fragmentFactory()

            modules(
                applicationModule,
                apiModule,
                databaseModule,
            )
        }
    }
}