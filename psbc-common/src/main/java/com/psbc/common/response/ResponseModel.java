package com.psbc.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 * @version 1.0
 * @date 2022-09-12 08:58
 */
@Data
@ToString
@AllArgsConstructor
public class ResponseModel {
    // 状态常量
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAILURE = 1;

    //响应码
    private int code;

    //响应信息
    String message;

    //分页信息
    Object pageInfo;

    //数据实体
    Object data;

    public ResponseModel() {
        this.code = STATUS_SUCCESS;
    }

    public ResponseModel(Object data) {
        this.code = STATUS_SUCCESS;
        this.data = data;
    }

    public ResponseModel(int status, Object data) {
        this.code = status;
        this.data = data;
    }

    public ResponseModel(int status, String message) {
        this.code = status;
        this.message = message;
    }

    public ResponseModel(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
