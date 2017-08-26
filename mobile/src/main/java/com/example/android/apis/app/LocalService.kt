/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.example.android.apis.R

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The [LocalServiceActivities.Controller]
 * and [LocalServiceActivities.Binding] classes show how to interact with the
 * service.

 *
 * Notice the use of the [NotificationManager] when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
//BEGIN_INCLUDE(service)
class LocalService : Service() {
    private var mNM: NotificationManager? = null

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private val NOTIFICATION = R.string.local_service_started

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    inner class LocalBinder : Binder() {
        internal val service: LocalService
            get() = this@LocalService
    }

    override fun onCreate() {
        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("LocalService", "Received start id $startId: $intent")
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        // Cancel the persistent notification.
        mNM!!.cancel(NOTIFICATION)

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private val mBinder = LocalBinder()

    /**
     * Show a notification while this service is running.
     */
    private fun showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        val text = getText(R.string.local_service_started)

        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent = PendingIntent.getActivity(this, 0,
                Intent(this, LocalServiceActivities.Controller::class.java), 0)

        // Set the info for the views that show in the notification panel.
        val notification = Notification.Builder(this)
                .setSmallIcon(R.drawable.stat_sample)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build()

        // Send the notification.
        mNM!!.notify(NOTIFICATION, notification)
    }
}
//END_INCLUDE(service)
