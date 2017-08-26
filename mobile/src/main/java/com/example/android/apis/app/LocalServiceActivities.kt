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

import com.example.android.apis.R

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.local_service_binding.*
import kotlinx.android.synthetic.main.local_service_controller.*

class LocalServiceActivities {
    /**
     *
     * Example of explicitly starting and stopping the local service.
     * This demonstrates the implementation of a service that runs in the same
     * process as the rest of the application, which is explicitly started and stopped
     * as desired.

     *
     * Note that this is implemented as an inner class only keep the sample
     * all together; typically this code would appear in some separate class.
     */
    class Controller : Activity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.local_service_controller)

            // Watch for button clicks.
            start.setOnClickListener(mStartListener)
            stop.setOnClickListener(mStopListener)
        }

        private val mStartListener = OnClickListener {
            // Make sure the service is started.  It will continue running
            // until someone calls stopService().  The Intent we use to find
            // the service explicitly specifies our service component, because
            // we want it running in our own process and don't want other
            // applications to replace it.
            startService(Intent(this@Controller,
                    LocalService::class.java))
        }

        private val mStopListener = OnClickListener {
            // Cancel a previous call to startService().  Note that the
            // service will not actually stop at this point if there are
            // still bound clients.
            stopService(Intent(this@Controller,
                    LocalService::class.java))
        }
    }

    // ----------------------------------------------------------------------

    /**
     * Example of binding and unbinding to the local service.
     * This demonstrates the implementation of a service which the client will
     * bind to, receiving an object through which it can communicate with the service.

     *
     * Note that this is implemented as an inner class only keep the sample
     * all together; typically this code would appear in some separate class.
     */
    class Binding : Activity() {
        private var mIsBound: Boolean = false

        // BEGIN_INCLUDE(bind)
        private var mBoundService: LocalService? = null

        private val mConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                mBoundService = (service as LocalService.LocalBinder).service

                // Tell the user about this for our demo.
                Toast.makeText(this@Binding, R.string.local_service_connected,
                        Toast.LENGTH_SHORT).show()
            }

            override fun onServiceDisconnected(className: ComponentName) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                mBoundService = null
                Toast.makeText(this@Binding, R.string.local_service_disconnected,
                        Toast.LENGTH_SHORT).show()
            }
        }

        internal fun doBindService() {
            // Establish a connection with the service.  We use an explicit
            // class name because we want a specific service implementation that
            // we know will be running in our own process (and thus won't be
            // supporting component replacement by other applications).
            bindService(Intent(this@Binding,
                    LocalService::class.java), mConnection, Context.BIND_AUTO_CREATE)
            mIsBound = true
        }

        internal fun doUnbindService() {
            if (mIsBound) {
                // Detach our existing connection.
                unbindService(mConnection)
                mIsBound = false
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            doUnbindService()
        }
        // END_INCLUDE(bind)

        private val mBindListener = OnClickListener { doBindService() }

        private val mUnbindListener = OnClickListener { doUnbindService() }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.local_service_binding)

            // Watch for button clicks.
            bind.setOnClickListener(mBindListener)
            unbind.setOnClickListener(mUnbindListener)
        }
    }
}
