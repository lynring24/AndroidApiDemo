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

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process
import android.util.Log
import android.view.View.OnClickListener
import android.widget.Toast

import com.example.android.apis.R
import com.example.android.apis.R.id.*
import kotlinx.android.synthetic.main.service_start_arguments_controller.*

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The [Controller]
 * class shows how to interact with the service.

 *
 * Notice the use of the [NotificationManager] when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().

 *
 * For applications targeting Android 1.5 or beyond, you may want consider
 * using the [android.app.IntentService] class, which takes care of all the
 * work of creating the extra thread and dispatching commands to it.
 */

class ServiceStartArguments : Service() {
    lateinit private var mNM: NotificationManager
    lateinit private var mInvokeIntent: Intent
    @Volatile
    lateinit private var mServiceLooper: Looper
    @Volatile
    lateinit private var mServiceHandler: ServiceHandler

    private inner class ServiceHandler( looper:Looper ): Handler(looper) {
       init { }

               override fun handleMessage(msg: Message) {
            val arguments = msg.obj as Bundle

            var txt: String = arguments.getString("name")

            Log.i("ServiceStartArguments", "Message: " + msg + ", "
                    + arguments.getString("name"))

            if (msg.arg2 and Service.START_FLAG_REDELIVERY == 0) {
                txt = "New cmd #" + msg.arg1 + ": " + txt
            } else {
                txt = "Re-delivered #" + msg.arg1 + ": " + txt
            }

            showNotification(txt)

            // Normally we would do some work here...  for our sample, we will
            // just sleep for 5 seconds.
            val endTime = System.currentTimeMillis() + 5 * 1000
            while (System.currentTimeMillis() < endTime) {
                synchronized(this) {
                    try {
                        //wait(endTime - System.currentTimeMillis())
                    } catch (e: Exception) {
                    }

                }
            }

            hideNotification()

            Log.i("ServiceStartArguments", "Done with #" + msg.arg1)
            stopSelf(msg.arg1)
        }

    }

    override public fun onCreate() {
        mNM = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        Toast.makeText(this, R.string.service_created,
                Toast.LENGTH_SHORT).show()

        // This is who should be launched if the user selects our persistent
        // notification.
        mInvokeIntent = Intent(this, Controller::class.java)

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        var thread: HandlerThread = HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()

        mServiceLooper = thread.getLooper()
        mServiceHandler = ServiceHandler(mServiceLooper)
    }

    override public fun onStartCommand( intent:Intent,  flags:Int,  startId :Int):Int
    {
        Log.i("ServiceStartArguments",
                "Starting #" + startId + ": " + intent.getExtras())
        var msg = mServiceHandler . obtainMessage ()
        msg.arg1 = startId
        msg.arg2 = flags
        msg.obj = intent.getExtras()
        mServiceHandler.sendMessage(msg)
        Log.i("ServiceStartArguments", "Sending: " + msg);

        // For the start fail button, we will simulate the process dying
        // for some reason in onStartCommand().
        if (intent.getBooleanExtra("fail", false)) {
            // Don't do this if we are in a retry... the system will
            // eventually give up if we keep crashing.
            if ((flags and START_FLAG_RETRY) == 0) {
                // Since the process hasn't finished handling the command,
                // it will be restarted with the command again, regardless of
                // whether we return START_REDELIVER_INTENT.
                Process.killProcess(Process.myPid())
            }
        }

        // Normally we would consistently return one kind of result...
        // however, here we will select between these two, so you can see
        // how they impact the behavior.  Try killing the process while it
        // is in the middle of executing the different commands.
        var flag: Int= START_NOT_STICKY
         if (intent.getBooleanExtra("redeliver", false))
             flag = START_REDELIVER_INTENT

        return flag
    }

   override
    public fun onDestroy()
    {
        mServiceLooper.quit()

        hideNotification()

        // Tell the user we stopped.
        Toast.makeText(this@ServiceStartArguments, R.string.service_destroyed,
                Toast.LENGTH_SHORT).show()
    }

    override public fun onBind(intent:Intent):IBinder?
    {
        return null
    }

    //Show a notification while this service is running.

    private  fun showNotification(text:String)
    {
        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent :PendingIntent= PendingIntent . getActivity (this, 0,
         Intent (this, Controller::class.java), 0)

        // Set the info for the views that show in the notification panel.
        var noteBuilder =  Notification.Builder(this)
                .setSmallIcon(R.drawable.stat_sample)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.service_start_arguments_label))  // the label
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked

        // We show this for as long as our service is processing a command.
        noteBuilder.setOngoing(true)

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        mNM.notify(R.string.service_created, noteBuilder.build())
    }

    private fun hideNotification()
    {
        mNM.cancel(R.string.service_created)
    }


/**
 * Example of explicitly starting the [ServiceStartArguments].
 * Note that this is implemented as an inner class only keep the sample
 * all together; typically this code would appear in some separate class. */

    class Controller : Activity() {
        override protected fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.service_start_arguments_controller)

            start1.setOnClickListener(mStart1Listener)
            start2.setOnClickListener(mStart2Listener)
            start3.setOnClickListener(mStart3Listener)
            startfail.setOnClickListener(mStartFailListener)
            kill.setOnClickListener(mKillListener)
        }

        private val mStart1Listener = OnClickListener {
            startService(Intent(this@Controller,
                    ServiceStartArguments::class.java).putExtra("name", "One"))
        }

        private val mStart2Listener = OnClickListener {
            startService(Intent(this@Controller,
                    ServiceStartArguments::class.java).putExtra("name", "Two"))
        }
        private val mStart3Listener = OnClickListener {
            startService(Intent(this@Controller,
                    ServiceStartArguments::class.java).putExtra("name", "Three")
                    .putExtra("redeliver", true))
        }


        private val mStartFailListener = OnClickListener {
            startService(Intent(this@Controller,
                    ServiceStartArguments::class.java)
                    .putExtra("name", "Three")
                    .putExtra("name", "Failure")
                    .putExtra("fail", true))
        }

        private val mKillListener = OnClickListener {
            Process.killProcess(Process.myPid())
        }

    }
}
