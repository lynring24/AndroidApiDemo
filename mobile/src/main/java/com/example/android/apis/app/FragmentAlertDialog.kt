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
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView

/**
 * Demonstrates how to show an AlertDialog that is managed by a Fragment.
 */

class FragmentAlertDialog : Activity() {

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dialog)

        val tv = findViewById<TextView>(R.id.text)
        tv.setText("Example of displaying an alert dialog with a DialogFragment")

        // Watch for button clicks.
        val button = findViewById<Button>(R.id.show)
        button.setOnClickListener(OnClickListener {
            showDialog()
        })
    }

    //BEGIN_INCLUDE(activity)
    fun showDialog() {
        val newFragment: DialogFragment = MyAlertDialogFragment.newInstance(
                R.string.alert_dialog_two_buttons_title)
        newFragment.show(getFragmentManager(), "dialog")
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
    //Question :: static class 는 어떻게 처리하는지 , static method const
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

            return AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.alert_dialog_icon)
                    .setTitle(title)
                    .setPositiveButton(R.string.alert_dialog_ok,
                            {dialog, whichButton ->FragmentAlertDialog::doPositiveClick}
                    )
                    .setNegativeButton(R.string.alert_dialog_cancel,
                            {dialog, whichButton ->FragmentAlertDialog::doNegativeClick }
                    )
                    .create()
        }
    }
//END_INCLUDE(dialog)
}

