/*
 * Copyright 2013 Niek Haarman
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

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.UNSPECIFIED
import android.widget.FrameLayout

class ToolTipContainer : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                getDefaultSize(suggestedMinimumHeight, heightMeasureSpec))
        //now the child is measured no matter what size the parent is
        measureChildren(MeasureSpec.makeMeasureSpec(0, UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, UNSPECIFIED))
    }

    /**
     * Shows a [ToolTipView] based on given [ToolTip] at the proper
     * location relative to given [View].
     *
     * @param toolTip
     * the ToolTip to show.
     * @param view
     * the View to position the ToolTipView relative to.
     *
     * @return the ToolTipView that was created.
     */
    fun showToolTipForView(toolTip: ToolTip, view: View): ToolTipView {
        val toolTipView = ToolTipView.showToolTipForView(toolTip, view)
        addView(toolTipView)
        return toolTipView
    }

}
