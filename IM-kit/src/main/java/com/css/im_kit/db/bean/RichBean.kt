package com.css.im_kit.db.bean

import com.css.im_kit.db.gson

/**
 * 商品类型消息内容
 */
data class RichBean<T>(
        var type: String,
        val body: T
) {
    fun toJsonString(): String {
        return gson.toJson(this)
    }
}

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


}