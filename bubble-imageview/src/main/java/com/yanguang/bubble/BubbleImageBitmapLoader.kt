package com.yanguang.bubble

import android.content.Context
import android.graphics.Bitmap

/**
 * @desc TODO
 *
 * @author WKH
 * @date 2018/4/25 0025
 */
interface BubbleImageBitmapLoader {

    fun getBitmap(context: Context, imagePath: Any): Bitmap?

}