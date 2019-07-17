/*
 * Copyright 2013 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nhaarman.supertooltips

import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes

enum class AnimationType {
    FROM_MASTER_VIEW,
    FROM_TOP,
    NONE
}

data class ToolTip(
        var text: CharSequence = "",
        @LayoutRes var contentViewId: Int = 0,
        @ColorInt var color: Int = 0,
        @ColorInt var textColor: Int = 0,
        var animationType: AnimationType = AnimationType.NONE,
        var showShadow: Boolean = false,
        var typeface: Typeface? = null

)

