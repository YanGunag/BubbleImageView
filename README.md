## 说明

仿微信聊天图片消息的气泡效果,通过剪裁bitmap实现，支持气泡图片的自定义点击效果。

纯Kotlin代码实现，不依赖任何第三方框架，但是需要提前知道要加载图片的尺寸！

## 效果图

![img](https://github.com/YanGunag/BubbleImageView/blob/master/20180504161609.jpg)






##使用方式

```xml
  <com.yanguang.bubble.BubbleImageView
        android:id="@+id/bubbleImageView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:scaleType="centerCrop"
        app:arrowWidth="3dp"
        app:defaultColor="#eeeeee"
        app:iconMaxSize="40dp"
        app:imageBubble="@drawable/ic_chat_from_bubble_normal"
        app:imageDefaultIcon="@drawable/ic_default_picture"
        app:imageErrorIcon="@drawable/ic_default_error"
        app:maxHeight="200dp"
        app:maxWidth="200dp"
        app:pressedColor="#22000000" />
```
     
        
        
        
####属性说明

    arrowWidth:气泡箭头的宽度
    defaultColor：默认图片的背景色
    pressedColor：气泡图片点击背景色
    iconMaxSize：默认图标的最大尺寸
    imageBubble：气泡图 9path图
    imageDefaultIcon：默认图标
    imageErrorIcon：加载失败的图标
    maxHeight：气泡图片最大高度
    maxWidth：气泡图片最大宽度
    
```
bubbleImageView.setImage(imageWidth, imageHeight, imagePath, arrowDirect, object : BubbleImageBitmapLoader {

            //此方法实会在子线程中运行
            override fun getBitmap(context: Context, imagePath: Any): Bitmap? {
                var imageBitmap: Bitmap? = null
                try {
                    //通过图片路径获取到bitmap并返回
                    imageBitmap = Glide.with(context).asBitmap().load(imagePath).submit().get()
                } catch (e: Exception) {
                    //TODO 
                }
                return imageBitmap
            }
        })
```




####参数说明

    imageWidth:要加载图片的宽度
    imageHeight：要加载图片的高度
    imagePath：图片路径
    arrowDirect：气泡箭头的方向（参见BubbleImageView.ArrowDirect）
    object：需要继承BubbleImageBitmapLoader接口，实现通过图片路径获取到bitmap



##其他方法
    
    setBubbleImage(int):设置气泡图，优先级高于在xml文件中设置的气泡图
    setImageLoadListener(BubbleImageListener):图片加载监听
    setLoadingWaitTime(long):设置图片加载等待时长
    