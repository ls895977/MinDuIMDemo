package com.css.im_kit.model.message

import com.css.im_kit.db.bean.CommodityMessage
import com.google.gson.Gson

/**
 * 商品类型消息内容
 */
class CommodityMessageBody : BaseMessageBody {
    var commodityId: String? = null
    var commodityName: String? = null
    var commodityImage: String? = null
    var commodityPrice: String? = null

    constructor() : super()
    constructor(commodityId: String?, commodityName: String?, commodityImage: String?, commodityPrice: String?) : super() {
        this.commodityId = commodityId
        this.commodityName = commodityName
        this.commodityImage = commodityImage
        this.commodityPrice = commodityPrice
    }

    constructor(isRead: Boolean, receivedTime: String?, sendTime: String?, isSelf: Boolean, commodityId: String?, commodityName: String?, commodityImage: String?, commodityPrice: String?) : super(isRead, receivedTime, sendTime, isSelf) {
        this.commodityId = commodityId
        this.commodityName = commodityName
        this.commodityImage = commodityImage
        this.commodityPrice = commodityPrice
    }

    companion object {
        fun toCommodityMessageBody(message: CommodityMessage): CommodityMessageBody {
            return CommodityMessageBody(message.commodityId, message.commodityName, message.commodityName, message.commodityPrice)
        }
    }
}