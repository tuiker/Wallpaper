package com.wallpaper.myView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

class ImageAvatar constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {
    private var mPaint: Paint = Paint()
    private var bitmap: Bitmap? = null
    private var scaleBitmap: Bitmap? = null

    init {
        mPaint.isAntiAlias = true
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL
    }
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 获取图片
        val drawable: Drawable? = drawable
        if (drawable is BitmapDrawable) {
            bitmap = drawable.bitmap
        }
        scaleBitmap()
    }

    private fun scaleBitmap() {
        bitmap?.let {
            val bitmapWidth: Int = it.width
            val bitmapHeight: Int = it.height
            val width: Int = width
            val height: Int = height
            val scaleX: Float = width.toFloat() / bitmapWidth
            val scaleY: Float = height.toFloat() / bitmapHeight
            val matrix = Matrix()
            matrix.setScale(scaleX, scaleY)
            scaleBitmap = Bitmap.createBitmap(it, 0, 0, bitmapWidth, bitmapHeight, matrix, true)
        }
    }
    override fun onDraw(canvas: Canvas) {
        bitmap?.let {
            drawCircleView(canvas)
        } ?: run {
            super.onDraw(canvas)
        }
    }

    private fun drawCircleView(canvas: Canvas) {
        scaleBitmap?.let { bitmap ->
            val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
            canvas.drawOval(0f, 0f, width.toFloat(), height.toFloat(), mPaint)
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, 0f, 0f, mPaint)
            mPaint.xfermode = null // 清空 XFermode
            canvas.restoreToCount(layerId)
        }
    }
}