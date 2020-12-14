package com.example.minduimdemo.bean;

public class ContextBean {
    public ContextBean(){

    }
    public ContextBean(String m_id,
                       String type,
                       String content,
                       String receive_id,
                       String send_id,
                       Long time,
                       String extra,
                       int code) {
        this.m_id = m_id;
        this.type = type;
        this.content = content;
        this.receive_id = receive_id;
        this.send_id = send_id;
        this.time = time;
        this.extra = extra;
        this.code = code;
    }

    /**
     * m_id : 160768300748
     * type : 0
     * content : 2234
     * receive_id : 2
     * send_id : 1
     * time : 1607683007
     * extra :
     * code : 2000
     */

    private String m_id;
    private String type;
    private String content;
    private String receive_id;
    private String send_id;
    private Long time;
    private String extra;
    private int code;

    public String getM_id() {
        return m_id;
    }

    public void setM_id(String m_id) {
        this.m_id = m_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceive_id() {
        return receive_id;
    }

    public void setReceive_id(String receive_id) {
        this.receive_id = receive_id;
    }

    public String getSend_id() {
        return send_id;
    }

    public void setSend_id(String send_id) {
        this.send_id = send_id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
