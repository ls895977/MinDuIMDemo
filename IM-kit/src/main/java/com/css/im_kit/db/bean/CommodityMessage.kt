package com.css.im_kit.db.bean

import com.css.im_kit.db.gson
import com.google.gson.Gson

/**
 * 商品类型消息内容
 */
class CommodityMessage {
    var commodityId: String? = null
    var commodityName: String? = null
    var commodityImage: String? = null
    var commodityPrice: String? = null

    constructor() : super()
    constructor(commodityId: String?, commodityName: String?, commodityImage: String?, commodityPrice: String?) {
        this.commodityId = commodityId
        this.commodityName = commodityName
        this.commodityImage = commodityImage
        this.commodityPrice = commodityPrice
    }

    fun toJsonString(): String {
        return gson.toJson(this)
    }
}