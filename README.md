## 介绍

仿微信聊天图片消息的气泡效果

## 效果图

![img](https://github.com/YanGunag/BubbleImageView/blob/master/20180504161609.jpg)

##使用方式

```
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

|id|name|