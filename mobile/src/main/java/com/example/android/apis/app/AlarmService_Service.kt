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
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import android.widget.Toast

/**
 * This is an example of implementing an application service that will run in
 * response to an alarm, allowing us to move long duration work out of an
 * intent receiver.

 * @see AlarmService

 * @see AlarmService_Alarm
 */
class AlarmService_Service : Service() {
    lateinit var mNM: NotificationManager

    override fun onCreate() {
        mNM = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // show the icon in the status bar
        showNotification()

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.
        var thr: Thread = Thread(null, mTask, "AlarmService_Service")
        thr.start()
    }

    override fun onDestroy() {
        // Cancel the notification -- we use the same ID that we had used to start it
        mNM.cancel(R.string.alarm_service_started)

        // Tell the user we stopped.
        Toast.makeText(this, R.string.alarm_service_finished, Toast.LENGTH_SHORT).show()
    }


    // The function that runs in our worker thread

    val mTask: Runnable = Runnable {
        // Normally we would do some work here...  for our sample, we will
        // just sleep for 30 seconds.
        var endTime: Long = System.currentTimeMillis() + 15 * 1000
        while (System.currentTimeMillis() < endTime) {
            synchronized(mBinder) {
                try {
                    // mBinder.wait(endTime - System.currentTimeMillis())
                } catch (e: Exception) {
                }

            }
        }

        // Done with our work...  stop the service!
        this@AlarmService_Service.stopSelf()
    }

    override public fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    // Show a notification while this service is running.

    private fun showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        val text: CharSequence = getText(R.string.alarm_service_started)

        // The PendingIntent to launch our activity if the user selects this notification
        var contentIntent: PendingIntent = PendingIntent.getActivity(this, 0,
                Intent(applicationContext, AlarmService::class.java), 0)

        // Set the info for the views that show in the notification panel.
        var notification: Notification = Notification.Builder(this)
                .setSmallIcon(R.drawable.stat_sample)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.alarm_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build()

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.alarm_service_started, notification)
    }

    /* * This is the object that receives interactions from clients.  See RemoteService
     * for a more complete example.*/
    
    private val mBinder = object : Binder() {
        @Throws(RemoteException::class)
        override fun onTransact(code: Int, data: Parcel, reply: Parcel,
                                flags: Int): Boolean {
            return super.onTransact(code, data, reply, flags)
        }
    }
}
