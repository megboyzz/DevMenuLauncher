package ru.megboyzz.devmenu.launcher

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import fi.iki.elonen.NanoHTTPD

data class DevMenuApp(
    val name: String,
    val icon: Drawable,
    val packageName: String
){
    fun start(port: Int, context: Context){
        val intent = Intent()
        intent.putExtra("port", port)
        intent.component = ComponentName(packageName, devMenuServiceClassName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context.startForegroundService(intent)
        else
            context.startService(intent)
    }

    fun stop(context: Context){
        val intent = Intent()
        intent.component = ComponentName(packageName, devMenuServiceClassName)
        context.stopService(intent)
    }

}