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

package com.example.android.apis.app;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.example.android.apis.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Toast;
import kotlinx.android.synthetic.main.alarm_service.*


class AlarmService :  Activity() {
     lateinit var  mAlarmSender : PendingIntent

    override fun onCreate( savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        // Create an IntentSender that will launch our service, to be scheduled
        // with the alarm manager.
        mAlarmSender = PendingIntent.getService(this@AlarmService,
                0, Intent(this@AlarmService, AlarmService_Service::class.java), 0)

        setContentView(R.layout.alarm_service)

        // Watch for button clicks.
        start_alarm.setOnClickListener(mStartAlarmListener)
        stop_alarm.setOnClickListener(mStopAlarmListener)
    }

    private val  mStartAlarmListener=  OnClickListener {
            // We want the alarm to go off 30 seconds from now.
            var firstTime : Long = SystemClock.elapsedRealtime()

            // Schedule the alarm!
            var am :AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    firstTime, 30*1000, mAlarmSender)

            // Tell the user about what we did.
            Toast.makeText(this@AlarmService, R.string.repeating_scheduled,
                    Toast.LENGTH_LONG).show()
    }

    private val  mStopAlarmListener = OnClickListener{
            // And cancel the alarm.
            var am :AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            am.cancel(mAlarmSender)

            // Tell the user about what we did.
            Toast.makeText(this@AlarmService, R.string.repeating_unscheduled,
                    Toast.LENGTH_LONG).show()


    }
}