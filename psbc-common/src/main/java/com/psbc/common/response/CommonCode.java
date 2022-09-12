package com.psbc.common.response;

import lombok.ToString;

/**
 * 公共异常类
 *
 * @author zhangkun
 * @date 2022-09-11 20:09:48
 * @return null
 */

@ToString
public enum CommonCode implements ErrorCode {
    INVALID_PARAM(00001,"非法参数！"),
    SERVER_ERROR(99999,"抱歉，系统繁忙，请稍后重试！");
//    private static ImmutableMap<Integer, CommonCode> codes ;
    //操作代码
    int code;
    //提示信息
    String message;
    private CommonCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }


}
