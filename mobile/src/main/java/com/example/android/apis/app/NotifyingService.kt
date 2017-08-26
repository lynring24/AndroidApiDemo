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

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.example.android.apis.R

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.ConditionVariable
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException

/**
 * This is an example of service that will update its status bar balloon
 * every 5 seconds for a minute.

 */
class NotifyingService : Service() {

    // variable which controls the notification thread
   lateinit private var mCondition: ConditionVariable

    override fun onCreate() {
        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        val notifyingThread = Thread(null, mTask, "NotifyingService")
        mCondition = ConditionVariable(false)
        notifyingThread.start()
    }

    override fun onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(MOOD_NOTIFICATIONS)
        // Stop the thread from generating further notifications
        mCondition.open()
    }

    private val mTask = Runnable {
        for (i in 0..3) {
            showNotification(R.drawable.stat_happy,
                    R.string.status_bar_notifications_happy_message)
            if (mCondition.block((5 * 1000).toLong()))
                break
            showNotification(R.drawable.stat_neutral,
                    R.string.status_bar_notifications_ok_message)
            if (mCondition.block((5 * 1000).toLong()))
                break
            showNotification(R.drawable.stat_sad,
                    R.string.status_bar_notifications_sad_message)
            if (mCondition.block((5 * 1000).toLong()))
                break
        }
        // Done with our work...  stop the service!
        this@NotifyingService.stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    private fun showNotification(moodId: Int, textId: Int) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        val text = getText(textId)

        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent = PendingIntent.getActivity(this, 0,
                Intent(this, NotifyingController::class.java), 0)

        // Set the icon and timestamp.
        // Note that in this example, we do not set the tickerText.  We update the icon enough that
        // it is distracting to show the ticker text every time it changes.  We strongly suggest
        // that you do this as well.  (Think of of the "New hardware found" or "Network connection
        // changed" messages that always pop up)
        // Set the info for the views that show in the notification panel.
        val notification = Notification.Builder(this)
                .setSmallIcon(moodId)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.status_bar_notifications_mood_title))
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build()

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(MOOD_NOTIFICATIONS, notification)
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private val mBinder = object : Binder() {
        @Throws(RemoteException::class)
        override fun onTransact(code: Int, data: Parcel, reply: Parcel,
                                flags: Int): Boolean {
            return super.onTransact(code, data, reply, flags)
        }
    }

    lateinit private var mNM: NotificationManager

    companion object {
        // Use a layout id for a unique identifier
        private val MOOD_NOTIFICATIONS = R.layout.status_bar_notifications
    }
}
