package com.css.im_kit.db.bean

import com.css.im_kit.db.gson
import java.io.Serializable

/**
 * 商品类型消息内容
 */
data class RichBean<T>(
        var type: String,
        val content: T
) : Serializable {
    fun toJsonString(): String {
        return gson.toJson(this)
    }
}

class CommodityMessage : Serializable {
    var productId: String? = null
    var productName: String? = null
    var imgUrl: String? = null
    var salePrice: String? = null

    constructor() : super()
    constructor(productId: String?, productName: String?, imgUrl: String?, salePrice: String?) {
        this.productId = productId
        this.productName = productName
        this.imgUrl = imgUrl
        this.salePrice = salePrice
    }


}