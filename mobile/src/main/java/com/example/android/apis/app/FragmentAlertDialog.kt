/*
 * Copyright (C) 2010 The Android Open Source Project
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
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_dialog.*

/** Demonstrates how to show an AlertDialog that is managed by a Fragment.*/

class FragmentAlertDialog : Activity() {

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dialog)

        //Display text on Screen
        text.setText(R.string.fragment_alert_dialog_msg)

        // Watch for button clicks.
        show.setOnClickListener {
            showDialog()
        }
    }

    //BEGIN_INCLUDE(activity)
    fun showDialog() {
        val newFragment: DialogFragment = MyAlertDialogFragment.newInstance(
                R.string.alert_dialog_two_buttons_title)
        newFragment.show(fragmentManager, "dialog")
    }

    fun doPositiveClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Positive click!")
    }

    fun doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!")
    }
//END_INCLUDE(activity)

    //BEGIN_INCLUDE(dialog)
    //-> static class , static method const
    public class MyAlertDialogFragment : DialogFragment() {
        companion object {
            @JvmStatic
            fun newInstance(title: Int): MyAlertDialogFragment {
                val frag = MyAlertDialogFragment()
                val args = Bundle()
                args.putInt("title", title)
                frag.arguments = args
                return frag
            }
        }

        override
        public fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val title: Int = arguments.getInt("title")

            return AlertDialog.Builder(activity)
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle(title)
                    .setPositiveButton(R.string.alert_dialog_ok
                    ) { dialog, whichButton -> (activity as FragmentAlertDialog).doPositiveClick() }
                    .setNegativeButton(R.string.alert_dialog_cancel
                    ) { dialog, whichButton -> (activity as FragmentAlertDialog).doNegativeClick() }
                    .create()
        }
    }
//END_INCLUDE(dialog)
}
