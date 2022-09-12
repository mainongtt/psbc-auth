package com.psbc.common.exception;

import com.psbc.common.response.ErrorCode;

/**
 *
 * @author zhangkun
 * @date 2022-09-11 20:09:41
 * @return null
 */
public class ExceptionCast {

    public static void cast(ErrorCode resultCode){
        throw new CustomException(resultCode.code(), resultCode.message());
    }
}
