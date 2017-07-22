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

package com.example.android.apis.text

import android.widget.TextView
import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.text.method.MovementMethod
import android.text.Editable
import android.util.AttributeSet

/**
 * This is a TextView that is Editable and by default scrollable,
 * like EditText without a cursor.

 *
 *
 * **XML attributes**
 *
 *
 * See
 * [TextView Attributes][android.R.styleable.TextView],
 * [View Attributes][android.R.styleable.View]
 */
class LogTextBox @JvmOverloads constructor(context: Context, attrs: AttributeSet ?= null, defStyle: Int = android.R.attr.textViewStyle) : TextView(context, attrs, defStyle) {

    override fun getDefaultMovementMethod(): MovementMethod {
        return ScrollingMovementMethod.getInstance()
    }

    override fun getText(): Editable {
        return super.getText() as Editable
    }

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        super.setText(text, TextView.BufferType.EDITABLE)
    }
}
