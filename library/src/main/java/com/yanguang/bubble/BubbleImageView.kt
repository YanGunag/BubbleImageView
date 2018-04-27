package com.yanguang.bubble

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import java.lang.IllegalArgumentException
import java.util.*

/**
 * @desc TODO
 *
 * @author WKH
 * @date 2018/4/25 0025
 */
/**
 * @desc TODO
 *
 * @author WKH
 * @date 2018/4/24 0024
 */
class BubbleImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ImageView(context, attrs, defStyleAttr) {

    private val TAG = "BubbleImageView"

    companion object {
        /**
         * 正在加载
         */
        var IMAGE_LOADING = 1

        /**
         * 加载失败
         */
        var IMAGE_LOAD_FAILED = 2

        /**
         * 加载成功
         */
        var IMAGE_LOAD_SUCCESS = 3

        /**
         * 图片最大宽度
         */
        var MAX_DEFAULT_WIDTH = 600

        /**
         * 图片最大高度
         */
        var MAX_DEFAULT_HEIGHT = 600

    }

    /**
     * 图片加载状态
     */
    private var loadingStatus: Int = IMAGE_LOADING

    private var customMaxWidth: Int = 0

    private var customMaxHeight: Int = 0

    private var imageBubbleRes: Int = 0

    private var imageDefaultRes: Int = 0

    private var imageErrorRes: Int = 0

    private var defaultBitmap: Bitmap? = null

    private var smallDefaultBitmap: Bitmap? = null

    private var bubbleBitmap: Bitmap? = null

    private var errorBitmap: Bitmap? = null

    private var pressedColor: Int = 0

    private var defaultColor: Int = 0

    private var isPressedFeedback = false

    private var maxDefaultImageSize = 80

    /**
     * 气泡箭头的宽度
     */
    private var arrowWidth = 10

    /**
     * 处理设定的默认图宽/高大于设置图片的宽/高
     */
    private var defaultPadding = 20;

    /**
     * 加载等待时间
     */
    private var loadingWaitTime: Long = 0

    var listener: BubbleImageListener? = null

    var defaultImageScale: Float = 0f

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleImageView)
            customMaxWidth = ta.getDimensionPixelSize(R.styleable.BubbleImageView_maxWidth, 0)
            customMaxHeight = ta.getDimensionPixelSize(R.styleable.BubbleImageView_maxHeight, 0)
            imageBubbleRes = ta.getResourceId(R.styleable.BubbleImageView_imageBubble, 0)
            imageDefaultRes = ta.getResourceId(R.styleable.BubbleImageView_imageDefaultIcon, 0)
            imageErrorRes = ta.getResourceId(R.styleable.BubbleImageView_imageErrorIcon, 0)
            pressedColor = ta.getColor(R.styleable.BubbleImageView_pressedColor, 0)
            defaultColor = ta.getColor(R.styleable.BubbleImageView_defaultColor, 0)
            arrowWidth = ta.getDimension(R.styleable.BubbleImageView_arrowWidth, 10f).toInt()
            maxDefaultImageSize = ta.getDimension(R.styleable.BubbleImageView_iconMaxSize, 100f).toInt()

            ta.recycle()
        }

        this.scaleType = ScaleType.CENTER_INSIDE

        this.isClickable = true
        setDefaultData()
    }

    private fun setDefaultData() {
        if (customMaxWidth == 0) {
            customMaxWidth = MAX_DEFAULT_WIDTH
        }

        if (customMaxHeight == 0) {
            customMaxHeight = MAX_DEFAULT_HEIGHT
        }

        defaultImageScale = customMaxHeight.toFloat() / customMaxWidth
        isPressedFeedback = pressedColor != 0
    }

    fun setBubbleImage(@DrawableRes bubbleResource: Int) {
        imageBubbleRes = bubbleResource
    }

    fun setImageLoadListener(listener: BubbleImageListener) {
        this.listener = listener
    }

    fun getLoadingStatus(): Int {
        return loadingStatus
    }

    /**
     * 剪裁出defaultImage的中间区域
     */
    private fun getDefaultBitmap(customMaxWidth: Int, customMaxHeight: Int, arrowDirect: Int): Bitmap? {

        if (defaultBitmap == null) {
            defaultBitmap = BitmapFactory.decodeResource(resources, imageDefaultRes)
        }

        var descBitmap: Bitmap? = null
        if (defaultBitmap != null && !defaultBitmap!!.isRecycled) {
            descBitmap = getResizeBitmap(defaultBitmap!!, customMaxWidth, customMaxHeight, arrowDirect)
        }

        return descBitmap
    }

    private fun getErrorBitmap(customMaxWidth: Int, customMaxHeight: Int, arrowDirect: Int): Bitmap? {

        if (errorBitmap == null && imageErrorRes != 0) {
            errorBitmap = BitmapFactory.decodeResource(resources, imageErrorRes)
        }

        var descBitmap: Bitmap? = null
        if (errorBitmap != null && !errorBitmap!!.isRecycled) {
            descBitmap = getResizeBitmap(errorBitmap!!, customMaxWidth, customMaxHeight, arrowDirect)
        }

        return descBitmap
    }

    private fun getResizeBitmap(iconBitmap: Bitmap, customMaxWidth: Int, customMaxHeight: Int, arrowDirect: Int): Bitmap? {

        var defaultBitmapWidth: Float = 0f
        var defaultBitmapHeight: Float = 0f
        var defaultBitmapScale: Float = 0f
        var tempBitmap: Bitmap? = null

        defaultBitmapWidth = iconBitmap.width.toFloat()
        defaultBitmapHeight = iconBitmap.height.toFloat()

        if (defaultBitmapWidth > defaultBitmapHeight) {
            if (defaultBitmapWidth > maxDefaultImageSize) {
                defaultBitmapScale = maxDefaultImageSize / defaultBitmapWidth
                defaultBitmapWidth = maxDefaultImageSize.toFloat()
                defaultBitmapHeight *= defaultBitmapScale
            }
        } else {
            if (defaultBitmapHeight > maxDefaultImageSize) {
                defaultBitmapScale = maxDefaultImageSize / defaultBitmapHeight
                defaultBitmapHeight = maxDefaultImageSize.toFloat()
                defaultBitmapWidth *= defaultBitmapScale
            }
        }

        if (defaultBitmapScale != 0f) {
            var matrix = Matrix()
            matrix.postScale(defaultBitmapScale, defaultBitmapScale)
            tempBitmap = Bitmap.createBitmap(iconBitmap, 0, 0, iconBitmap.width, iconBitmap.height, matrix, true)
        }

        var isNeedSmallIcon = false
        var smallBitmap: Bitmap? = null
        /**
         * 图标会显示不完整
         */
        if (defaultBitmapWidth > customMaxWidth || defaultBitmapHeight > customMaxHeight) {

            isNeedSmallIcon = true
            var scale: Float
            var defaultScale = defaultBitmapWidth / defaultBitmapHeight
            var customScale = customMaxWidth / customMaxHeight.toFloat()

            scale = if (defaultScale > customScale) {
                var newDefaultWidth = customScale * defaultBitmapHeight - arrowWidth - defaultPadding

                newDefaultWidth / defaultBitmapWidth
            } else {
                var newDefaultHeight = defaultBitmapWidth / customScale - arrowWidth - defaultPadding

                newDefaultHeight / defaultBitmapHeight
            }

            var matrix = Matrix()
            matrix.postScale(scale, scale)
            smallBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap!!.width, tempBitmap!!.height, matrix, true)
        }

        var descBitmap = Bitmap.createBitmap(customMaxWidth, customMaxHeight, Bitmap.Config.ARGB_8888)
        descBitmap.eraseColor(defaultColor)
        val canvas = Canvas(descBitmap)

        var startX: Float = 0f
        var startY: Float = 0f

        when (arrowDirect) {

            ArrowDirect.LEFT -> {
                startX = (customMaxWidth - defaultBitmapWidth) / 2 + arrowWidth
                startY = (customMaxHeight - defaultBitmapHeight) / 2
            }

            ArrowDirect.RIGHT -> {
                startX = (customMaxWidth - defaultBitmapWidth) / 2 - arrowWidth
                startY = (customMaxHeight - defaultBitmapHeight) / 2
            }

            ArrowDirect.TOP -> {
                startX = (customMaxWidth - defaultBitmapWidth) / 2
                startY = (customMaxHeight - defaultBitmapHeight) / 2 + arrowWidth
            }

            ArrowDirect.DOWN -> {
                startX = (customMaxWidth - defaultBitmapWidth) / 2
                startY = (customMaxHeight - defaultBitmapHeight) / 2 - arrowWidth
            }
        }


        if (isNeedSmallIcon && smallBitmap != null && !smallBitmap.isRecycled) {
            canvas.drawBitmap(smallBitmap, startX, startY, null)
        } else {
            canvas.drawBitmap(tempBitmap, startX, startY, null)
        }
        return descBitmap
    }

    private fun getBubbleBitmap(): Bitmap? {
        if (bubbleBitmap == null && imageBubbleRes != 0) {
            bubbleBitmap = BitmapFactory.decodeResource(resources, imageBubbleRes)
        }
        return bubbleBitmap
    }


    /**
     * @param width  图片原始宽度
     * @param height 图片原始高度
     */
    fun setImage(width: Int, height: Int, url: String, arrowDirect: Int, bitmapLoader: BubbleImageBitmapLoader) {

        loadingStatus = IMAGE_LOADING

        if (imageBubbleRes == 0) {
            throw IllegalArgumentException("请设置一张气泡图片")
        } else {
            var resizeHeight: Int = 0
            var resizeWidth: Int = 0
            //根据图片的原始宽高比换算在最大展示区域内的图片大小
            if (0 != height && 0 != width) {
                // 要保证图片的长宽比不变
                val ratio = height.toFloat() / width

                if (ratio > defaultImageScale) {
                    resizeHeight = if (height > customMaxHeight) customMaxHeight else height
                    resizeWidth = (customMaxHeight / ratio).toInt()
                } else {
                    resizeWidth = if (width > customMaxWidth) customMaxWidth else width
                    resizeHeight = (customMaxWidth * ratio).toInt()
                }

            }

            Log.i(TAG, "step -> get min height -> ")

            var parentMinHeight = resources.getDimension(R.dimen.chat_message_content_min_height)
            resizeHeight = if (resizeHeight > parentMinHeight) resizeHeight else parentMinHeight.toInt()

            Log.i(TAG, "step -> start resize height -> height -> " + resizeHeight)

            this.layoutParams.height = resizeHeight

            Log.i(TAG, "step -> start resize width -> width -> " + resizeWidth)
            this.layoutParams.width = resizeWidth

            Log.i(TAG, "step -> end resize size")
//            var params = RelativeLayout.LayoutParams(resizeWidth, resizeHeight)
//
//            when (arrowDirect) {
//                ArrowDirect.LEFT -> params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
//                ArrowDirect.RIGHT -> params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//            }
//
//            this.layoutParams = params

            Thread(BitmapLoaderTask(arrowDirect, url, bitmapLoader, resizeWidth, resizeHeight)).start()
        }
    }

    /**
     * 设置加载等待时长
     */
    fun setLoadingWaitTime(waitTime: Long) {
        loadingWaitTime = waitTime
    }

    @RequiresApi()
    private fun setBitmapIntoView(value: List<Bitmap>?) {

        if (value == null || value.isEmpty()) {
            postErrorMessage("图片加载失败")
            return
        }

        val normal: Bitmap = value[0]
        if (normal == null || normal.isRecycled) {
            postErrorMessage("图片加载失败")
            return
        }

        if (context == null) {
            postErrorMessage("Context已被销毁")
            return
        }

        if (context is Activity) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if ((context as Activity).isDestroyed) {
                    postErrorMessage("Activity已被销毁")
                } else {
                    setBitmapToThis(value, normal)
                    postSuccessMessage()
                }
            } else {
                try {
                    setBitmapToThis(value, normal)
                    postSuccessMessage()
                } catch (e: Exception) {
                    postErrorMessage("" + e.message)
                }
            }
        } else {
            try {
                setBitmapToThis(value, normal)
                postSuccessMessage()
            } catch (e: Exception) {
                postErrorMessage("" + e.message)
            }
        }
    }

    private fun setBitmapToThis(value: List<Bitmap>, normal: Bitmap) {
        if (isPressedFeedback && value.size == 2) {
            val pressed = value[1]

            if (pressed == null || pressed.isRecycled) {
                Log.e(TAG, "Pressed bitmap is null or empty!")
                this@BubbleImageView.setImageBitmap(normal)
            } else {
                val normalDrawable = BitmapDrawable(resources, normal)
                val pressedDrawable = BitmapDrawable(resources, pressed)

                val stateListDrawable = StateListDrawable()
                stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)//有状态的必须写在上面
                stateListDrawable.addState(intArrayOf(), normalDrawable)//没有状态的必须写在下面

                this@BubbleImageView.setImageDrawable(stateListDrawable)
            }
        } else {
            this@BubbleImageView.setImageBitmap(normal)
        }
    }


    private fun getClipBitmaps(source: Bitmap, desc: Bitmap, imageWidth: Int, imageHeight: Int): List<Bitmap>? {

        if (source == null || source.isRecycled) {
            return null
        }

        if (desc == null || desc.isRecycled) {
            return null
        }

        val results = ArrayList<Bitmap>()
        val normal = BitmapUtil.getBubbleBitmap(desc, source, imageWidth, imageHeight)
        results.add(normal)

        if (isPressedFeedback) {
            val pressed = BitmapUtil.getBlurBitmap(normal, pressedColor)
            results.add(pressed)
        }
        return results
    }

    /**
     * Bubble里面箭头的指向
     */
    object ArrowDirect {
        var LEFT = 0x1
        var RIGHT = 0x2
        var TOP = 0x3
        var DOWN = 0x4
    }

    /**
     * 剪裁默认图完成
     */
    private val defaultBitmapClip = 1

    /**
     * 剪裁加载图片失败图
     */
    private val errorBitmapClip = 2

    /**
     * 剪裁要加载的图片完成
     */
    private val successBitmapClip = 3

    /**
     * 图片加载失败
     */
    private var loadFailed = 4

    private var loadSuccess = 5

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                defaultBitmapClip -> {
                    setBitmapIntoView(msg.obj as List<Bitmap>)
                }
                errorBitmapClip -> {
                    setBitmapIntoView(msg.obj as List<Bitmap>)
                    loadingStatus = IMAGE_LOAD_FAILED
                }
                successBitmapClip -> {
                    setBitmapIntoView(msg.obj as List<Bitmap>)
                }
                loadFailed -> {
                    var errorMessage = msg.obj as String
                    if (listener != null) {
                        listener?.onFailed(errorMessage)
                    }
                    loadingStatus = IMAGE_LOAD_FAILED
                }
                loadSuccess -> {
                    loadingStatus = IMAGE_LOAD_SUCCESS
                    if (listener != null) {
                        listener?.onSuccess()
                    }
                }
            }
        }
    }

    private inner class BitmapLoaderTask(arrowDirect: Int, url: String, bitmapLoader: BubbleImageBitmapLoader, imageWidth: Int, imageHeight: Int) : Runnable {

        var arrowDirect = arrowDirect

        var url = url

        var bitmapLoader = bitmapLoader

        var imageWidth = imageWidth

        var imageHeight = imageHeight

        override fun run() {
            var defaultBitmap = getDefaultBitmap(imageWidth, imageHeight, arrowDirect)
            var bubbleBitmap = getBubbleBitmap()
            if (defaultBitmap != null && bubbleBitmap != null && !defaultBitmap.isRecycled && !bubbleBitmap.isRecycled) {
                postBitmapMessage(defaultBitmapClip, defaultBitmap, bubbleBitmap, imageWidth, imageHeight)
            }


            var imageBitmap: Bitmap? = null
            if (loadingWaitTime > 0) {
                var startDownLoadTime = Date().time
                imageBitmap = bitmapLoader.getBitmap(context, url)
                var endDownLoadTime = Date().time

                var downLoadDuration = endDownLoadTime - startDownLoadTime
                if (downLoadDuration < loadingWaitTime) {
                    Thread.sleep(loadingWaitTime - downLoadDuration)
                }
            } else {
                imageBitmap = bitmapLoader.getBitmap(context, url)
            }


            if (imageBitmap == null) {
                postErrorMessage("获取图片失败，加载错误图片")
                var errorBitmap = getErrorBitmap(imageWidth, imageHeight, arrowDirect)
                if (errorBitmap != null && !errorBitmap.isRecycled && bubbleBitmap != null && !bubbleBitmap.isRecycled) {
                    postBitmapMessage(errorBitmapClip, errorBitmap, bubbleBitmap, imageWidth, imageHeight)
                }
            } else {
                if (imageBitmap != null && bubbleBitmap != null && !bubbleBitmap.isRecycled) {
                    postBitmapMessage(successBitmapClip, imageBitmap, bubbleBitmap, imageWidth, imageHeight)
                }
            }
        }
    }

    private fun postBitmapMessage(what: Int, source: Bitmap, direct: Bitmap, imageWidth: Int, imageHeight: Int) {
        var message = mHandler.obtainMessage()
        message.what = what
        message.obj = getClipBitmaps(source, direct, imageWidth, imageHeight)
        mHandler.sendMessage(message)
    }

    private fun postErrorMessage(errorMessage: String) {
        var message = mHandler.obtainMessage()
        message.what = loadFailed
        message.obj = errorMessage
        mHandler.sendMessage(message)
    }

    private fun postSuccessMessage() {
        var message = mHandler.obtainMessage()
        message.what = loadSuccess
        mHandler.sendMessage(message)
    }
}