package com.mstoyan.rocket.chattesttask.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.res.ResourcesCompat
import android.util.AttributeSet
import com.mstoyan.rocket.chattesttask.R

class ExtendedActionButton : FloatingActionButton {

    var font : Typeface
    var text : String
    var textSize : Float = 48f
    var textAlpha : Float = 1f
    val paint : Paint = Paint()
    var textBounds : Rect = Rect()
    var fabRadius : Float = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtendedActionButton)

        font = if (a.hasValue(R.styleable.ExtendedActionButton_font))
            ResourcesCompat.getFont(context, a.getResourceId(R.styleable.ExtendedActionButton_font, 0))!!
        else
            Typeface.DEFAULT

        text = if (a.hasValue(R.styleable.ExtendedActionButton_text))
            a.getString(R.styleable.ExtendedActionButton_text)!!
        else
            ""

        textSize = a.getDimension(R.styleable.ExtendedActionButton_textSize, textSize)
        textAlpha = a.getFloat(R.styleable.ExtendedActionButton_textAlpha, textAlpha)
        fabRadius = a.getDimension(R.styleable.ConstraintSet_android_layout_width, fabRadius)

        a.recycle()
    }

    override fun onDrawForeground(canvas: Canvas?) {
        super.onDrawForeground(canvas)
        paint.typeface = font
        paint.textSize = textSize
        paint.alpha = (255 * textAlpha).toInt()
        paint.fontMetrics

        if (canvas != null) {
            paint.getTextBounds(text, 0, text.length, textBounds)

//            val xPos = canvas.width.toFloat() / 2 - textBounds.width() / 2
            val xPos = fabRadius - textBounds.width() / 2
            val yPos = canvas.height / 2 - (paint.descent() + paint.ascent())/2
            canvas.drawText(text, xPos, yPos, paint)
        }
    }
}