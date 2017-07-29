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

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.SystemClock
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast

import java.util.Calendar

/**
 * Example of scheduling one-shot and repeating alarms.  See
 * [OneShotAlarm] for the code run when the one-shot alarm goes off, and
 * [RepeatingAlarm] for the code run when the repeating alarm goes off.
 * <h4>Demo</h4>
 * App/Service/Alarm Controller

 * <h4>Source files</h4>
 * <table class="LinkTable">
 * <tr>
 * <td class="LinkColumn">src/com.example.android.apis/app/AlarmController.java</td>
 * <td class="DescrColumn">The activity that lets you schedule alarms</td>
</tr> *
 * <tr>
 * <td class="LinkColumn">src/com.example.android.apis/app/OneShotAlarm.java</td>
 * <td class="DescrColumn">This is an intent receiver that executes when the
 * one-shot alarm goes off</td>
</tr> *
 * <tr>
 * <td class="LinkColumn">src/com.example.android.apis/app/RepeatingAlarm.java</td>
 * <td class="DescrColumn">This is an intent receiver that executes when the
 * repeating alarm goes off</td>
</tr> *
 * <tr>
 * <td class="LinkColumn">/res/any/layout/alarm_controller.xml</td>
 * <td class="DescrColumn">Defines contents of the screen</td>
</tr> *
</table> *

 */

class AlarmController : Activity (){
    var mToast: Toast? = null
    override fun onCreate(savedInstanceState:Bundle?) : Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_controller)

        // Watch for button clicks.
        var button = findViewById<View>(R.id.one_shot) as (Button)
        button.setOnClickListener(mOneShotListener)
        button = findViewById<View>(R.id.start_repeating) as (Button)
        button.setOnClickListener(mStartRepeatingListener);
        button = findViewById<View>(R.id.stop_repeating) as (Button)
        button.setOnClickListener(mStopRepeatingListener);
    }
   val mOneShotListener = OnClickListener{
            // When the alarm goes off, we want to broadcast an Intent to our
            // BroadcastReceiver.  Here we make an Intent with an explicit class
            // name to have our own receiver (which has been published in
            // AndroidManifest.xml) instantiated and called, and then create an
            // IntentSender to have the intent executed as a broadcast.
            val intent:Intent = Intent(this@AlarmController, OneShotAlarm::class.java)
            val sender:PendingIntent = PendingIntent.getBroadcast(this@AlarmController, 0, intent, 0)

            // We want the alarm to go off 30 seconds from now.
            var calendar:Calendar  = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis())
            calendar.add(Calendar.SECOND, 30);

            // Schedule the alarm!
             val am:AlarmManager = getSystemService(ALARM_SERVICE) as (AlarmManager)
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender)

            // Tell the user about what we did.
            if (mToast != null) {
                mToast!!.cancel()
            }
            mToast = Toast.makeText(this@AlarmController, R.string.one_shot_scheduled,Toast.LENGTH_LONG)
            mToast!!.show()

    };
   val mStartRepeatingListener = OnClickListener{
            // When the alarm goes off, we want to broadcast an Intent to our
            // BroadcastReceiver.  Here we make an Intent with an explicit class
            // name to have our own receiver (which has been published in
            // AndroidManifest.xml) instantiated and called, and then create an
            // IntentSender to have the intent executed as a broadcast.
            // Note that unlike above, this IntentSender is configured to
            // allow itself to be sent multiple times.
            val intent:Intent = Intent(this@AlarmController, RepeatingAlarm::class.java)
            val sender:PendingIntent = PendingIntent.getBroadcast(this@AlarmController,0, intent, 0)

            // We want the alarm to go off 30 seconds from now.
           var  firstTime : Long = SystemClock.elapsedRealtime()
            firstTime += 15*1000L

            // Schedule the alarm!
            val am:AlarmManager  = getSystemService(ALARM_SERVICE) as AlarmManager
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,firstTime, 15*1000, sender)

            // Tell the user about what we did.
            if (mToast != null) {
                mToast!!.cancel()
            }
            mToast = Toast.makeText(this@AlarmController, R.string.repeating_scheduled, Toast.LENGTH_LONG)
            mToast!!.show()

    };
    val mStopRepeatingListener = OnClickListener {
            // Create the same intent, and thus a matching IntentSender, for
            // the one that was scheduled.
            val intent:Intent  = Intent(this@AlarmController, RepeatingAlarm::class.java)
             val sender:PendingIntent  = PendingIntent.getBroadcast(this@AlarmController, 0, intent, 0)

            // And cancel the alarm.
            val am :AlarmManager= getSystemService(ALARM_SERVICE) as (AlarmManager)
            am.cancel(sender)

            // Tell the user about what we did.
            if (mToast != null) {
                mToast!!.cancel();
            }
            mToast = Toast.makeText(this@AlarmController, R.string.repeating_unscheduled,Toast.LENGTH_LONG)
            mToast!!.show()

    };
}
/*

class AlarmController : Activity() {
    internal var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.alarm_controller)

        // Watch for button clicks.
        var button = findViewById<View>(R.id.one_shot) as Button
        button.setOnClickListener(mOneShotListener)
        button = findViewById<View>(R.id.start_repeating) as Button
        button.setOnClickListener(mStartRepeatingListener)
        button = findViewById<View>(R.id.stop_repeating) as Button
        button.setOnClickListener(mStopRepeatingListener)
    }

    private val mOneShotListener = OnClickListener {
        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver.  Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        val intent = Intent(this@AlarmController, OneShotAlarm::class.java)
        val sender = PendingIntent.getBroadcast(this@AlarmController,
                0, intent, 0)

        // We want the alarm to go off 30 seconds from now.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, 30)

        // Schedule the alarm!
        val am = getSystemService(ALARM_SERVICE) as (AlarmManager)
        am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, sender)

        // Tell the user about what we did.
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(this@AlarmController, R.string.one_shot_scheduled,
                Toast.LENGTH_LONG)
        mToast!!.show()
    }

    private val mStartRepeatingListener = OnClickListener {
        // When the alarm goes off, we want to broadcast an Intent to our
        // BroadcastReceiver.  Here we make an Intent with an explicit class
        // name to have our own receiver (which has been published in
        // AndroidManifest.xml) instantiated and called, and then create an
        // IntentSender to have the intent executed as a broadcast.
        // Note that unlike above, this IntentSender is configured to
        // allow itself to be sent multiple times.
        val intent = Intent(this@AlarmController, RepeatingAlarm::class.java)
        val sender = PendingIntent.getBroadcast(this@AlarmController,
                0, intent, 0)

        // We want the alarm to go off 30 seconds from now.
        var firstTime = SystemClock.elapsedRealtime()
        firstTime += (15 * 1000).toLong()

        // Schedule the alarm!
        val am = getSystemService(ALARM_SERVICE) as (AlarmManager)
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                firstTime, (15 * 1000).toLong(), sender)

        // Tell the user about what we did.
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(this@AlarmController, R.string.repeating_scheduled,
                Toast.LENGTH_LONG)
        mToast!!.show()
    }

    private val mStopRepeatingListener = OnClickListener {
        // Create the same intent, and thus a matching IntentSender, for
        // the one that was scheduled.
        val intent = Intent(this@AlarmController, RepeatingAlarm::class.java)
        val sender = PendingIntent.getBroadcast(this@AlarmController,
                0, intent, 0)

        // And cancel the alarm.
        val am = getSystemService(ALARM_SERVICE) as (AlarmManager)
        am.cancel(sender)

        // Tell the user about what we did.
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(this@AlarmController, R.string.repeating_unscheduled,
                Toast.LENGTH_LONG)
        mToast!!.show()
    }
}
*/

