package com.yanguang.bubble

import android.graphics.*

/**
 * @desc TODO
 *
 * @author WKH
 * @date 2018/4/25 0025
 */
object BitmapUtil {

    fun getBubbleBitmap(bubble: Bitmap, source: Bitmap, width: Int, height: Int): Bitmap {

        val roundCornerImage = getRoundCornerImage(bubble, source, width, height)
        return getShardImage(bubble, roundCornerImage, width, height)
    }

    fun getBlurBitmap(source: Bitmap, color: Int): Bitmap {

        val pressedBitmap = source.copy(Bitmap.Config.ARGB_8888, true)
        val cv = Canvas(pressedBitmap)

        cv.drawColor(color, PorterDuff.Mode.SRC_ATOP)
        return pressedBitmap
    }

    private fun getRoundCornerImage(bitmap_bg: Bitmap, bitmap_in: Bitmap, width: Int, height: Int): Bitmap {
        val roundCornerImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(roundCornerImage)
        val paint = Paint()
        val rect = Rect(0, 0, width, height)
        val width1 = bitmap_in.width
        val height1 = bitmap_in.height

        val rectF = Rect(0, 0, width1, height1)
        paint.isAntiAlias = true
        val patch = NinePatch(bitmap_bg, bitmap_bg.ninePatchChunk, null)
        patch.draw(canvas, rect)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap_in, rectF, rect, paint)
        return roundCornerImage
    }

    private fun getShardImage(bubble: Bitmap, source: Bitmap, width: Int, height: Int): Bitmap {
        val roundCornerImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(roundCornerImage)
        val paint = Paint()
        val rect = Rect(0, 0, width, height)
        paint.isAntiAlias = true
        val patch = NinePatch(bubble, bubble.ninePatchChunk, null)
        patch.draw(canvas, rect)
        val rect2 = Rect(1, 1, width - 1, height - 1)
        canvas.drawBitmap(source, rect, rect2, paint)
        return roundCornerImage
    }
}