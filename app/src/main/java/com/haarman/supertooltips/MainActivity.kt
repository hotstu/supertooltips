package com.haarman.supertooltips

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import com.nhaarman.supertooltips.AnimationType
import com.nhaarman.supertooltips.ToolTip
import com.nhaarman.supertooltips.ToolTipRelativeLayout
import com.nhaarman.supertooltips.ToolTipView

class MainActivity : Activity(), View.OnClickListener, ToolTipView.OnToolTipViewClickedListener {

    private var mRedToolTipView: ToolTipView? = null
    private var mGreenToolTipView: ToolTipView? = null
    private var mBlueToolTipView: ToolTipView? = null
    private var mPurpleToolTipView: ToolTipView? = null
    private var mOrangeToolTipView: ToolTipView? = null
    private lateinit var mToolTipFrameLayout: ToolTipRelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mToolTipFrameLayout = findViewById<View>(R.id.activity_main_tooltipframelayout) as ToolTipRelativeLayout
        findViewById<View>(R.id.activity_main_redtv).setOnClickListener(this)
        findViewById<View>(R.id.activity_main_greentv).setOnClickListener(this)
        findViewById<View>(R.id.activity_main_bluetv).setOnClickListener(this)
        findViewById<View>(R.id.activity_main_purpletv).setOnClickListener(this)
        findViewById<View>(R.id.activity_main_orangetv).setOnClickListener(this)

        findViewById<View>(R.id.container).doOnPreDraw {
            addRedToolTipView()
            addGreenToolTipView()
            addOrangeToolTipView()
            addBlueToolTipView()
            addPurpleToolTipView()
        }
    }

    private fun addRedToolTipView() {
        mRedToolTipView = mToolTipFrameLayout.showToolTipForView(
                ToolTip().apply {
                    text = "A beautiful Button"
                    color = ContextCompat.getColor(this@MainActivity, R.color.holo_red)
                    showShadow = true
                }, findViewById(R.id.activity_main_redtv))
        mRedToolTipView!!.setOnToolTipViewClickedListener(this)
    }

    private fun addGreenToolTipView() {
        val anchorView = findViewById<View>(R.id.activity_main_greentv)
        val toolTip = ToolTip(
                "Another beautiful Button!"
        ).apply {
            color = (resources.getColor(R.color.holo_green))

        }

        mGreenToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, anchorView)
        mGreenToolTipView!!.setOnToolTipViewClickedListener(this)
    }

    private fun addBlueToolTipView() {
        val toolTip = ToolTip().apply {
            text = ("Moarrrr buttons!")
            color = (resources.getColor(R.color.holo_blue))
            animationType = (AnimationType.FROM_TOP)
        }


        mBlueToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_bluetv))
        mBlueToolTipView!!.setOnToolTipViewClickedListener(this)
    }

    private fun addPurpleToolTipView() {
        val toolTip = ToolTip().apply {
            contentViewId = R.layout.custom_tooltip
            color = resources.getColor(R.color.holo_purple)
            animationType = AnimationType.NONE
        }


        mPurpleToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_purpletv))
        mPurpleToolTipView!!.setOnToolTipViewClickedListener(this)
    }

    private fun addOrangeToolTipView() {
        val toolTip = ToolTip().apply {
            text = "Tap me!"
            color = resources.getColor(R.color.holo_orange)
        }


        mOrangeToolTipView = mToolTipFrameLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_orangetv))
        mOrangeToolTipView!!.setOnToolTipViewClickedListener(this)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.activity_main_redtv) {
            if (mRedToolTipView == null) {
                addRedToolTipView()
            } else {
                mRedToolTipView!!.remove()
                mRedToolTipView = null
            }

        } else if (id == R.id.activity_main_greentv) {
            if (mGreenToolTipView == null) {
                addGreenToolTipView()
            } else {
                mGreenToolTipView!!.remove()
                mGreenToolTipView = null
            }

        } else if (id == R.id.activity_main_bluetv) {
            if (mBlueToolTipView == null) {
                addBlueToolTipView()
            } else {
                mBlueToolTipView!!.remove()
                mBlueToolTipView = null
            }

        } else if (id == R.id.activity_main_purpletv) {
            if (mPurpleToolTipView == null) {
                addPurpleToolTipView()
            } else {
                mPurpleToolTipView!!.remove()
                mPurpleToolTipView = null
            }

        } else if (id == R.id.activity_main_orangetv) {
            if (mOrangeToolTipView == null) {
                addOrangeToolTipView()
            } else {
                mOrangeToolTipView!!.remove()
                mOrangeToolTipView = null
            }

        }
    }

    override fun onToolTipViewClicked(toolTipView: ToolTipView) {
        if (mRedToolTipView === toolTipView) {
            mRedToolTipView = null
        } else if (mGreenToolTipView === toolTipView) {
            mGreenToolTipView = null
        } else if (mBlueToolTipView === toolTipView) {
            mBlueToolTipView = null
        } else if (mPurpleToolTipView === toolTipView) {
            mPurpleToolTipView = null
        } else if (mOrangeToolTipView === toolTipView) {
            mOrangeToolTipView = null
        }
    }
}

