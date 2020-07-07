/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.linagora.android.linshare.R
import kotlin.math.min

class PageIndicator @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : View(context, attributes, defStyle, defStyleRes), View.OnAttachStateChangeListener {

    companion object {
        const val DEFAULT_DOT_SIZE = 6
        const val DEFAULT_GAP = 8

        const val DEFAULT_UNSELECTED_COLOUR = 0xCCCCCC
        const val DEFAULT_SELECTED_COLOUR = 0x37474F
    }

    private var dotBottomY: Float = 0f
    private var dotCenterY: Float = 0f
    private var dotTopY: Float = 0f
    private var selectedDotX: Float = 0f
    private lateinit var dotCenterX: FloatArray
    private var gap: Int
    private var halfDotRadius: Float
    private var dotRadius: Float
    private var dotDiameter: Int

    private var pageCount: Int = 0
    private var previousPage: Int = 0
    private var currentPage: Int = 0

    private val unselectedPaint: Paint
    private val selectedPaint: Paint
    private val combinedUnselectedPath = Path()
    private val unselectedDotPath = Path()

    private var unselectedColour: Int
    private var selectedColour: Int

    private var isAttached: Boolean = false

    private lateinit var viewPager: ViewPager2

    init {
        val density = context.resources.displayMetrics.density.toInt()

        val typedArray = context.obtainStyledAttributes(
            attributes,
            R.styleable.PageIndicator,
            defStyle,
            0)

        dotDiameter = typedArray.getDimensionPixelSize(
            R.styleable.PageIndicator_dotDiameter,
            DEFAULT_DOT_SIZE * density
        )
        dotRadius = (dotDiameter / 2).toFloat()
        halfDotRadius = dotRadius / 2

        gap = typedArray.getDimensionPixelSize(
            R.styleable.PageIndicator_dotGap,
            DEFAULT_GAP * density
        )

        unselectedColour = typedArray.getColor(
            R.styleable.PageIndicator_pageIndicatorColor,
            DEFAULT_UNSELECTED_COLOUR
        )
        selectedColour = typedArray.getColor(
            R.styleable.PageIndicator_currentPageIndicatorColor,
            DEFAULT_SELECTED_COLOUR
        )

        typedArray.recycle()

        unselectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        unselectedPaint.color = unselectedColour
        selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        selectedPaint.color = selectedColour

        addOnAttachStateChangeListener(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = getDesiredHeight()
        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> min(desiredHeight, MeasureSpec.getSize(heightMeasureSpec))
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight
        }

        val desiredWidth = getDesiredWidth()
        val width = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST -> min(desiredWidth, MeasureSpec.getSize(widthMeasureSpec))
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth
        }

        setMeasuredDimension(width, height)
        calculateDotPositions(width)
    }

    override fun onDraw(canvas: Canvas?) {
        if (!::viewPager.isInitialized || pageCount == 0) return
        drawSelectedDot(canvas)
        drawUnselectedDots(canvas)
    }

    private fun calculateDotPositions(width: Int) {
        val left = paddingLeft
        val top = paddingTop
        val right = width - paddingRight

        val requiredWidth = getRequiredWidth()
        val startLeft = left.toFloat() + ((right - left - requiredWidth) / 2).toFloat() + dotRadius

        dotCenterX = FloatArray(pageCount)
        for (i in 0 until pageCount) {
            dotCenterX[i] = startLeft + i * (dotDiameter + gap)
        }
        dotTopY = top.toFloat()
        dotCenterY = top + dotRadius
        dotBottomY = (top + dotDiameter).toFloat()

        setCurrentPageImmediate()
    }

    private fun drawSelectedDot(canvas: Canvas?) {
        canvas?.drawCircle(selectedDotX, dotCenterY, dotRadius, selectedPaint)
    }

    private fun drawUnselectedDots(canvas: Canvas?) {
        combinedUnselectedPath.rewind()

        for (page in 0 until pageCount) {
            combinedUnselectedPath.op(
                getUnselectedPath(page = page, centerXofPage = dotCenterX[page]),
                Path.Op.UNION
            )
        }
        canvas?.drawPath(combinedUnselectedPath, unselectedPaint)
    }

    private fun getUnselectedPath(page: Int, centerXofPage: Float): Path {
        if (page != currentPage) {
            unselectedDotPath.rewind()

            unselectedDotPath.addCircle(centerXofPage, dotCenterY, dotRadius, Path.Direction.CW)
        }
        return unselectedDotPath
    }

    fun setViewPager(viewPager: ViewPager2) {
        this.viewPager = viewPager

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                isAttached.takeIf { isAttached }
                    ?.let {
                        setSelectedPage(position)
                        invalidate()
                    }
                    ?: setCurrentPageImmediate()
            }
        })

        setPageCount(viewPager.adapter?.itemCount ?: 0)
        viewPager.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                setPageCount(viewPager.adapter?.itemCount ?: 0)
            }
        })
        setCurrentPageImmediate()
    }

    private fun setSelectedPage(page: Int) {
        previousPage = currentPage
        currentPage = page
        updateSelectedPosition()
    }

    private fun setPageCount(pageCount: Int) {
        this.pageCount = pageCount
        requestLayout()
    }

    private fun setCurrentPageImmediate() {
        this.currentPage = runCatching {
            viewPager.currentItem
        }.getOrDefault(0)

        updateSelectedPosition()
    }

    private fun updateSelectedPosition() {
        runCatching { selectedDotX = dotCenterX[currentPage] }
    }

    private fun getDesiredHeight(): Int {
        return paddingTop + dotDiameter + paddingBottom
    }

    private fun getRequiredWidth(): Int {
        return pageCount * dotDiameter + (pageCount - 1) * gap
    }

    private fun getDesiredWidth(): Int {
        return paddingLeft + getRequiredWidth() + paddingRight
    }

    override fun onViewDetachedFromWindow(v: View?) {
        isAttached = false
    }

    override fun onViewAttachedToWindow(v: View?) {
        isAttached = true
    }
}
