package com.css.im_kit.ui.bean

import java.io.Serializable

class EmojiBean : Serializable {
    var text: String? = null
    var id: Int? = null

    constructor()

    constructor(text: String?) {
        this.text = text
    }

    constructor(text: String?, id: Int?) {
        this.text = text
        this.id = id
    }

}
