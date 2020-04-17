package com.linagora.android.linshare.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import com.linagora.android.linshare.model.resources.ResourceColor

class LetterAvatarDrawable(
    ovalShape: OvalShape,
    letterColor: ResourceColor,
    private val letter: Char
) : ShapeDrawable(ovalShape) {

    private val letterPaint = setUpLetterPaint(letterColor)

    private fun setUpLetterPaint(letterColor: ResourceColor): Paint {
        return Paint().apply {
            isAntiAlias = true
            color = letterColor.color
            textAlign = Paint.Align.CENTER
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        letterPaint.textSize = shape.height / 2
        val (xPos, yPos) = calculateCenterPosition()

        canvas.drawText(letter.toUpperCase().toString(), xPos, yPos, letterPaint)
    }

    private fun calculateCenterPosition(): Pair<Float, Float> {
        val bound = RectF(0f, 0f, shape.width, shape.height)

        val textHeight = letterPaint.descent() - letterPaint.ascent()
        val textOffset = (textHeight / 2) - letterPaint.descent()

        val yPos = bound.centerY() + textOffset

        return Pair(bound.centerX(), yPos)
    }
}
