package com.yanguang

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dream.yanguang.R
import com.yanguang.bubble.BubbleImageBitmapLoader
import com.yanguang.bubble.BubbleImageListener
import com.yanguang.bubble.BubbleImageView
import com.yanguang.entity.ImageEntity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var adapter: TestImageAdapter

    var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testContainer.layoutManager = LinearLayoutManager(this)
        adapter = TestImageAdapter(null)
        testContainer.adapter = adapter


        testButton.setOnClickListener {

            var entity: ImageEntity? = null
            when (count % 3) {
                0 -> {
                    entity = ImageEntity(759, 987, "http://pic31.photophoto.cn/20140404/0005018350303853_b.jpg")
                }
                1 -> {
                    entity = ImageEntity(510, 850, "http://f2.topitme.com/2/b9/71/112660598401871b92l.jpg")
                }
                2 -> {
                    entity = ImageEntity(1200, 675, "http://imgsrc.baidu.com/imgad/pic/item/34fae6cd7b899e51fab3e9c048a7d933c8950d21.jpg")
                }
            }
            when (count % 2) {
                0 -> {
                    entity!!.isLeft = true
                    entity!!.type = 1
                }
                1 -> {
                    entity!!.isLeft = false
                    entity!!.type = 2
                }
            }

            adapter.addData(entity!!)
            testContainer.smoothScrollToPosition(adapter.itemCount - 1)
            count++
        }

    }

    inner class TestImageAdapter(data: List<ImageEntity>?) : BaseMultiItemQuickAdapter<ImageEntity, BaseViewHolder>(data) {

        init {
            addItemType(1, R.layout.item_bubble_test)
            addItemType(2, R.layout.item_bubble_test)
        }

        override fun convert(helper: BaseViewHolder, item: ImageEntity) {

            var bubbleImageView = helper.getView<BubbleImageView>(R.id.bubbleImageView)
            var leftImage = helper.getView<ImageView>(R.id.leftImage)
            var rightImage = helper.getView<ImageView>(R.id.rightImage)

            var arrowDirect: Int = 0

            when (item.itemType) {
                1 -> {
                    bubbleImageView.setBubbleImage(R.drawable.ic_chat_from_bubble_normal)
                    (bubbleImageView.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    arrowDirect = BubbleImageView.ArrowDirect.LEFT
                    leftImage.visibility = View.VISIBLE
                    rightImage.visibility = View.GONE
                }
                2 -> {
                    bubbleImageView.setBubbleImage(R.drawable.ic_chat_to_bubble_normal)
                    (bubbleImageView.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    arrowDirect = BubbleImageView.ArrowDirect.RIGHT
                    leftImage.visibility = View.GONE
                    rightImage.visibility = View.VISIBLE
                }
            }

            bubbleImageView.setLoadingWaitTime(item.waitTime)
            bubbleImageView.setImage(item.width, item.height, item.url, arrowDirect, object : BubbleImageBitmapLoader {

                //此方法实在子线程中运行
                override fun getBitmap(context: Context, imagePath: Any): Bitmap? {
                    var imageBitmap: Bitmap? = null
                    try {
                        imageBitmap = Glide.with(context).asBitmap().load(imagePath).submit().get()
                    } catch (e: Exception) {

                    }
                    return imageBitmap
                }
            })
            bubbleImageView.setImageLoadListener(object : BubbleImageListener {
                override fun onSuccess() {
                    Log.i("ImageLoadListener", "onSuccess")
                }

                override fun onFailed(errorMessage: String) {
                    Log.i("ImageLoadListener", "" + errorMessage)
                }
            })

            bubbleImageView.setOnClickListener {
                Toast.makeText(this@MainActivity, "onClick", Toast.LENGTH_SHORT).show()
            }

            bubbleImageView.setOnLongClickListener {
                Toast.makeText(this@MainActivity, "onLongClick", Toast.LENGTH_SHORT).show()
                false
            }
        }

    }
}
