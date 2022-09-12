package com.psbc.common.exception;


import lombok.Data;

/**
 * 自定义异常类
 *
 * @author zhangkun
 * @date 2022-09-11 20:09:40
 * @return null
 */
@Data
public class CustomException extends RuntimeException {

    //响应码
    private int code;

    //响应信息
    String message;

    //分页信息
    Object pageInfo;

    //数据实体
    Object data;

    public CustomException(int code, String message){
        this.code = code;
        this.message = message;
        this.pageInfo = null;
        this.data = null;
    }
}
