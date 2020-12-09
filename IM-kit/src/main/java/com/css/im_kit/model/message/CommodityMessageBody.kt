package com.css.im_kit.model.message

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

    constructor(isRead: Boolean?, receivedTime: String?, sendTime: String?, isSelf: Boolean?, commodityId: String?, commodityName: String?, commodityImage: String?, commodityPrice: String?) : super(isRead, receivedTime, sendTime, isSelf) {
        this.commodityId = commodityId
        this.commodityName = commodityName
        this.commodityImage = commodityImage
        this.commodityPrice = commodityPrice
    }


}