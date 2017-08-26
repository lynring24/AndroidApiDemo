/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.app

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.example.android.apis.R

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import kotlinx.android.synthetic.main.notification_background_service.*

/**
 * Example service that gets launched from a notification and runs in the background.
 */
class NotificationBackgroundService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .cancel(R.layout.notification_background_service)
        stopSelf(startId)
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Demo UI that allows the user to post the notification.
     */
    class Controller : Activity() {
        lateinit private var mNM: NotificationManager

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            setContentView(R.layout.notification_background_service)

            notify.setOnClickListener(mNotify)
        }

        private fun showNotification(text: CharSequence) {
            // The PendingIntent to launch our activity if the user selects this notification
            val contentIntent = PendingIntent.getService(this, 0,
                    Intent(this, NotificationBackgroundService::class.java), 0)

            // Set the info for the views that show in the notification panel.
            val notification = Notification.Builder(this)
                    .setSmallIcon(R.drawable.stat_sample)  // the status icon
                    .setTicker(text)  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle(getText(R.string.notification_background_label))  // the label of the entry
                    .setContentText(text)  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .build()

            // Send the notification.
            // We use a layout id because it is a unique number.  We use it later to cancel.
            mNM.notify(R.layout.notification_background_service, notification)
        }

        private val mNotify = OnClickListener { showNotification("Selecting this will cause a background service to run.") }
    }
}

