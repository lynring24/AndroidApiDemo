/*
 * Copyright (C) 2012 The Android Open Source Project
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
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.RemoteException
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.example.android.apis.R

/**
 * This is an example if implementing a Service that uses android:isolatedProcess.
 */
open class IsolatedService : Service() {
    /**
     * This is a list of callbacks that have been registered with the
     * service.  Note that this is package scoped (instead of private) so
     * that it can be accessed more efficiently from inner classes.
     */
    internal val mCallbacks = RemoteCallbackList<IRemoteServiceCallback>()

    internal var mValue = 0

    override fun onCreate() {
        Log.i("IsolatedService", "Creating IsolatedService: " + this)
    }

    override fun onDestroy() {
        Log.i("IsolatedService", "Destroying IsolatedService: " + this)
        // Unregister all callbacks.
        mCallbacks.kill()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    /**
     * The IRemoteInterface is defined through IDL
     */
    private val mBinder = object : IRemoteService.Stub() {
        override fun registerCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.register(cb)
        }

        override fun unregisterCallback(cb: IRemoteServiceCallback?) {
            if (cb != null) mCallbacks.unregister(cb)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.i("IsolatedService", "Task removed in " + this + ": " + rootIntent)
        stopSelf()
    }

    private fun broadcastValue(value: Int) {
        // Broadcast to all clients the new value.
        val N = mCallbacks.beginBroadcast()
        for (i in 0..N - 1) {
            try {
                mCallbacks.getBroadcastItem(i).valueChanged(value)
            } catch (e: RemoteException) {
                // The RemoteCallbackList will take care of removing
                // the dead object for us.
            }

        }
        mCallbacks.finishBroadcast()
    }

    // ----------------------------------------------------------------------

    class Controller : Activity() {
        internal class ServiceInfo(val mActivity: Activity, val mClz: Class<*>,
                                   start: Int, stop: Int, bind: Int, status: Int) {
            lateinit var mStatus: TextView
            var mServiceBound: Boolean = false
            var mService: IRemoteService? = null



            fun destroy() {
                if (mServiceBound) {
                    mActivity.unbindService(mConnection)
                }
            }

            private val mStartListener = OnClickListener { mActivity.startService(Intent(mActivity, mClz)) }

            private val mStopListener = OnClickListener { mActivity.stopService(Intent(mActivity, mClz)) }

            private val mBindListener = OnClickListener { v ->
                if ((v as CheckBox).isChecked) {
                    if (!mServiceBound) {
                        if (mActivity.bindService(Intent(mActivity, mClz),
                                mConnection, Context.BIND_AUTO_CREATE)) {
                            mServiceBound = true
                            mStatus.text = "BOUND"
                        }
                    }
                } else {
                    if (mServiceBound) {
                        mActivity.unbindService(mConnection)
                        mServiceBound = false
                        mStatus.text = ""
                    }
                }
            }
            init {
                var button = mActivity.findViewById<View>(start) as Button
                button.setOnClickListener(mStartListener)
                button = mActivity.findViewById<View>(stop) as Button
                button.setOnClickListener(mStopListener)
                val cb = mActivity.findViewById<View>(bind) as CheckBox
                cb.setOnClickListener(mBindListener)
                mStatus = mActivity.findViewById<View>(status) as TextView
            }

            private val mConnection = object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName,
                                                service: IBinder) {
                    mService = IRemoteService.Stub.asInterface(service)
                    if (mServiceBound) {
                        mStatus.text = "CONNECTED"
                    }
                }

                override fun onServiceDisconnected(className: ComponentName) {
                    // This is called when the connection with the service has been
                    // unexpectedly disconnected -- that is, its process crashed.
                    mService = null
                    if (mServiceBound) {
                        mStatus.text = "DISCONNECTED"
                    }
                }
            }
        }

        internal lateinit var mService1: ServiceInfo
        internal lateinit var mService2: ServiceInfo

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            setContentView(R.layout.isolated_service_controller)

            mService1 = ServiceInfo(this, IsolatedService::class.java, R.id.start1, R.id.stop1,
                    R.id.bind1, R.id.status1)
            mService2 = ServiceInfo(this, IsolatedService2::class.java, R.id.start2, R.id.stop2,
                    R.id.bind2, R.id.status2)
        }

        override fun onDestroy() {
            super.onDestroy()
            mService1.destroy()
            mService2.destroy()
        }
    }
}
