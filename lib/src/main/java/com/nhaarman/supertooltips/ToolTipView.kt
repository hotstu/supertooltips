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
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import java.util.*
import kotlin.math.max

/**
 * A ViewGroup to visualize ToolTips. Use
 * ToolTipRelativeLayout.showToolTipForView() to show ToolTips.
 */
class ToolTipView(context: Context) : LinearLayout(context), View.OnClickListener {

    private lateinit var mTopPointerView: ImageView
    private lateinit var mTopFrame: View
    private lateinit var mToolTipTV: TextView
    private lateinit var mBottomFrame: View
    private lateinit var mBottomPointerView: ImageView
    private lateinit var mShadowView: View
    private lateinit var mContentHolder: ViewGroup

    private var mView: View? = null

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
        mToolTipTV = findViewById<View>(R.id.tooltip_contenttv) as TextView
        mBottomFrame = findViewById(R.id.tooltip_bottomframe)
        mBottomPointerView = findViewById<View>(R.id.tooltip_pointer_down) as ImageView
        mShadowView = findViewById(R.id.tooltip_shadow)

        setOnClickListener(this)
    }


    fun setToolTip(toolTip: ToolTip, view: View) {
        mView = view

        mToolTipTV.text = toolTip.text

        animationType = toolTip.animationType

        if (toolTip.typeface != null) {
            mToolTipTV.typeface = toolTip.typeface
        }

        if (toolTip.textColor != 0) {
            mToolTipTV.setTextColor(toolTip.textColor)
        }

        if (toolTip.color != 0) {
            setColor(toolTip.color)
        }

        if (toolTip.contentViewId != 0) {
            setContentView(toolTip.contentViewId)
        }

        if (!toolTip.showShadow) {
            mShadowView.visibility = View.GONE
        }


        doOnPreDraw {
            applyToolTipPosition()
        }
        postInvalidate()

    }

    private fun applyToolTipPosition() {
        val masterViewScreenPosition = IntArray(2)
        mView!!.getLocationOnScreen(masterViewScreenPosition)

        val viewDisplayFrame = Rect()
        mView!!.getWindowVisibleDisplayFrame(viewDisplayFrame)

        val parentViewScreenPosition = IntArray(2)
        (parent as View).getLocationOnScreen(parentViewScreenPosition)

        val masterViewWidth = mView!!.width
        val masterViewHeight = mView!!.height

        mRelativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0]
        mRelativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1]
        val relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2

        val toolTipViewAboveY = mRelativeMasterViewY - height
        val toolTipViewBelowY = Math.max(0, mRelativeMasterViewY + masterViewHeight)

        var toolTipViewX = Math.max(0, relativeMasterViewCenterX - width / 2)
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
            val animators = ArrayList<Animator>(5)

            if (animationType == AnimationType.FROM_MASTER_VIEW) {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, mRelativeMasterViewY + mView!!.height / 2f - height / 2f, toolTipViewY.toFloat()))
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_X_COMPAT, mRelativeMasterViewX + mView!!.width / 2f - width / 2f, toolTipViewX.toFloat()))
            } else if (animationType == AnimationType.FROM_TOP) {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, 0f, toolTipViewY.toFloat()))
            }

            animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 0f, 1f))
            animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 0f, 1f))

            animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 0f, 1f))

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animators)
            animatorSet.start()
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

    fun remove() {

        if (animationType == AnimationType.NONE) {
            if (parent != null) {
                (parent as ViewManager).removeView(this)
            }
        } else {
            val animators = ArrayList<Animator>(5)
            if (animationType == AnimationType.FROM_MASTER_VIEW) {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, y, mRelativeMasterViewY + mView!!.height / 2f - height / 2f))
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_X_COMPAT, x, mRelativeMasterViewX + mView!!.width / 2f - width / 2f))
            } else {
                animators.add(ObjectAnimator.ofFloat(this, TRANSLATION_Y_COMPAT, y, 0f))
            }

            animators.add(ObjectAnimator.ofFloat(this, SCALE_X_COMPAT, 1f, 0f))
            animators.add(ObjectAnimator.ofFloat(this, SCALE_Y_COMPAT, 1f, 0f))

            animators.add(ObjectAnimator.ofFloat(this, ALPHA_COMPAT, 1f, 0f))
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(animators)
            animatorSet.addListener(DisappearanceAnimatorListener())
            animatorSet.start()
        }
    }

    override fun onClick(view: View) {
        remove()

        if (mListener != null) {
            mListener!!.onToolTipViewClicked(this)
        }
    }


    interface OnToolTipViewClickedListener {
        fun onToolTipViewClicked(toolTipView: ToolTipView)
    }


    private inner class DisappearanceAnimatorListener : AnimatorListenerAdapter() {

        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            if (parent != null) {
                (parent as ViewManager).removeView(this@ToolTipView)
            }
        }

        override fun onAnimationCancel(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}
    }

    companion object {

        val TRANSLATION_Y_COMPAT = "translationY"
        val TRANSLATION_X_COMPAT = "translationX"
        val SCALE_X_COMPAT = "scaleX"
        val SCALE_Y_COMPAT = "scaleY"
        val ALPHA_COMPAT = "alpha"
    }
}
