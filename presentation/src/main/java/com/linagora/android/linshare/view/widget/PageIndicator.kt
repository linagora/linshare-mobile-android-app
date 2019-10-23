package com.linagora.android.linshare.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
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
        Log.d("DATPH", "drawSelectedDot $selectedDotX")
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
                Log.d("DATPH", "onPageSelected $position")
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
        Log.d("DATPH", "setSelectedPAge $previousPage - $currentPage")
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
