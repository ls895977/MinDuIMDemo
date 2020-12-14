package com.css.im_kit.imservice.bean;

public class MessageBean {
    String content;
    String chat_id;

    public MessageBean(String content, String chat_id, String receive_id, String send_id, String type) {
        this.content = content;
        this.chat_id = chat_id;
        this.receive_id = receive_id;
        this.send_id = send_id;
        this.type = type;
    }

    String receive_id;
    String send_id;
    String type;
}
