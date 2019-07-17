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

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnPreDraw
import java.lang.ref.WeakReference
import kotlin.math.max

/**
 * A ViewGroup to visualize ToolTips. Use
 * ToolTipRelativeLayout.showToolTipForView() to show ToolTips.
 */
class ToolTipView(context: Context) : LinearLayout(context), View.OnClickListener {

    private lateinit var mTopPointerView: ImageView
    private lateinit var mTopFrame: View
    private lateinit var mBottomFrame: View
    private lateinit var mBottomPointerView: ImageView
    private lateinit var mShadowView: View
    private lateinit var mContentHolder: ViewGroup

    private var mView: WeakReference<View>? = null

    private var mRelativeMasterViewY: Int = 0

    private var mRelativeMasterViewX: Int = 0

    private var mListener: OnToolTipViewClickedListener? = null

    private var animationType: AnimationType = AnimationType.NONE


    init {
        init()
    }

    private fun init() {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.tooltip, this, true)

        mTopPointerView = findViewById<View>(R.id.tooltip_pointer_up) as ImageView
        mTopFrame = findViewById(R.id.tooltip_topframe)
        mContentHolder = findViewById<View>(R.id.tooltip_contentholder) as ViewGroup
        mBottomFrame = findViewById(R.id.tooltip_bottomframe)
        mBottomPointerView = findViewById<View>(R.id.tooltip_pointer_down) as ImageView
        mShadowView = findViewById(R.id.tooltip_shadow)

        setOnClickListener(this)
    }


    fun setToolTip(toolTip: ToolTip, view: View) {
        mView = WeakReference(view)


        animationType = toolTip.animationType


        if (toolTip.color != 0) {
            setColor(toolTip.color)
        }

        if (toolTip.contentViewId != 0) {
            setContentView(toolTip.contentViewId)
        } else {
            val mToolTipTV = TextView(context)

            mToolTipTV.text = toolTip.text

            if (toolTip.typeface != null) {
                mToolTipTV.typeface = toolTip.typeface
            }

            if (toolTip.textColor != 0) {
                mToolTipTV.setTextColor(toolTip.textColor)
            }
            setContentView(mToolTipTV)
        }

        if (!toolTip.showShadow) {
            mShadowView.visibility = View.GONE
        }


        doOnPreDraw {
            applyToolTipPosition()
        }

    }

    private fun applyToolTipPosition() {
        val v = mView?.get() ?: return
        val masterViewScreenPosition = IntArray(2)
        v.getLocationOnScreen(masterViewScreenPosition)

        val viewDisplayFrame = Rect()
        v.getWindowVisibleDisplayFrame(viewDisplayFrame)

        val parentViewScreenPosition = IntArray(2)
        (parent as View).getLocationOnScreen(parentViewScreenPosition)

        val masterViewWidth = v.width
        val masterViewHeight = v.height

        mRelativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0]
        mRelativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1]
        val relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2

        val toolTipViewAboveY = mRelativeMasterViewY - height
        val toolTipViewBelowY = max(0, mRelativeMasterViewY + masterViewHeight)

        var toolTipViewX = max(0, relativeMasterViewCenterX - width / 2)
        if (toolTipViewX + width > viewDisplayFrame.right) {
            toolTipViewX = viewDisplayFrame.right - width
        }

        x = toolTipViewX.toFloat()
        setPointerCenterX(relativeMasterViewCenterX)

        val showBelow = toolTipViewAboveY < 0


        mTopPointerView.visibility = if (showBelow) View.VISIBLE else View.GONE
        mBottomPointerView.visibility = if (showBelow) View.GONE else View.VISIBLE


        val toolTipViewY: Int
        toolTipViewY = if (showBelow) {
            toolTipViewBelowY
        } else {
            toolTipViewAboveY
        }

        if (animationType == AnimationType.NONE) {
            translationY = toolTipViewY.toFloat()
            translationX = toolTipViewX.toFloat()
        } else {


            AnimatorSet().apply {
                playTogether(arrayListOf(
                        ObjectAnimator.ofFloat(this@ToolTipView, SCALE_X_COMPAT, 0f, 1f) as Animator,
                        ObjectAnimator.ofFloat(this@ToolTipView, SCALE_Y_COMPAT, 0f, 1f),
                        ObjectAnimator.ofFloat(this@ToolTipView, ALPHA_COMPAT, 0f, 1f)
                ).apply {
                    if (animationType == AnimationType.FROM_MASTER_VIEW) {
                        add(ObjectAnimator.ofFloat(this@ToolTipView, TRANSLATION_Y_COMPAT,
                                mRelativeMasterViewY + v.height / 2f - height / 2f, toolTipViewY.toFloat()))
                        add(ObjectAnimator.ofFloat(this@ToolTipView, TRANSLATION_X_COMPAT,
                                mRelativeMasterViewX + v.width / 2f - width / 2f, toolTipViewX.toFloat()))
                    } else if (animationType == AnimationType.FROM_TOP) {
                        add(ObjectAnimator.ofFloat(this@ToolTipView, TRANSLATION_Y_COMPAT,
                                0f, toolTipViewY.toFloat()))
                    }
                })
            }.start()


        }
    }

    private fun setPointerCenterX(pointerCenterX: Int) {
        val pointerWidth = max(mTopPointerView.measuredWidth, mBottomPointerView.measuredWidth)

        mTopPointerView.x = (pointerCenterX - pointerWidth / 2 - x.toInt()).toFloat()
        mBottomPointerView.x = (pointerCenterX - pointerWidth / 2 - x.toInt()).toFloat()
    }

    fun setOnToolTipViewClickedListener(listener: OnToolTipViewClickedListener) {
        mListener = listener
    }

    private fun setColor(color: Int) {
        mTopPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        mTopFrame.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        mBottomPointerView.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        mBottomFrame.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        mContentHolder.setBackgroundColor(color)
    }

    private fun setContentView(viewId: Int) {
        mContentHolder.removeAllViews()
        LayoutInflater.from(context).inflate(viewId, mContentHolder, true)
    }

    private fun setContentView(view: View) {
        mContentHolder.removeAllViews()
        mContentHolder.addView(view)
    }

    fun remove() {
        if (animationType == AnimationType.NONE) {
            parent?.run {
                this as ViewGroup
                this.removeView(this@ToolTipView)
            }
        } else {
            AnimatorSet().apply {
                playTogether(arrayListOf(
                        ObjectAnimator.ofFloat(this@ToolTipView, SCALE_X_COMPAT, 1f, 0f) as Animator,
                        ObjectAnimator.ofFloat(this@ToolTipView, SCALE_Y_COMPAT, 1f, 0f),
                        ObjectAnimator.ofFloat(this@ToolTipView, ALPHA_COMPAT, 1f, 0f)
                ).apply {
                    if (animationType == AnimationType.FROM_MASTER_VIEW) {
                        add(ObjectAnimator.ofFloat(this@ToolTipView, TRANSLATION_Y_COMPAT, y))
                        add(ObjectAnimator.ofFloat(this@ToolTipView, TRANSLATION_X_COMPAT, x))
                    } else {
                        add(ObjectAnimator.ofFloat(this@ToolTipView, TRANSLATION_Y_COMPAT, y, 0f))
                    }
                })
                doOnEnd {
                    parent?.run {
                        this as ViewGroup
                        this.removeView(this@ToolTipView)
                    }
                }
            }.start()
        }
    }

    override fun onClick(view: View) {
        mListener?.onToolTipViewClicked(this)
        remove()
    }


    interface OnToolTipViewClickedListener {
        fun onToolTipViewClicked(toolTipView: ToolTipView)
    }


    companion object {

        const val TRANSLATION_Y_COMPAT = "translationY"
        const val TRANSLATION_X_COMPAT = "translationX"
        const val SCALE_X_COMPAT = "scaleX"
        const val SCALE_Y_COMPAT = "scaleY"
        const val ALPHA_COMPAT = "alpha"

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
            val toolTipView = ToolTipView(view.context)
            toolTipView.setToolTip(toolTip, view)
            return toolTipView
        }
    }
}
