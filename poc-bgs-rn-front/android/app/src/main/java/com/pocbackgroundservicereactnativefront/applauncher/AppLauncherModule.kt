package com.pocbackgroundservicereactnativefront.applauncher

import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class AppLauncherModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName() = "AppLauncher"

    @ReactMethod
    fun openApp(packageName: String, activityName: String) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.setClassName(packageName, activityName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val context = reactApplicationContext
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}
