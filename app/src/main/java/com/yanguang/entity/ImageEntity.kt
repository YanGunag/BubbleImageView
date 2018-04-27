package com.yanguang.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * @desc TODO
 *
 * @author WKH
 * @date 2018/4/25 0025
 */
class ImageEntity(width: Int, height: Int, url: String) : MultiItemEntity {

    var type: Int = 1

    override fun getItemType(): Int {
        return type
    }

    var width = width
    var height = height
    var url = url
    var isLeft: Boolean = false
    var waitTime: Long = 0
}