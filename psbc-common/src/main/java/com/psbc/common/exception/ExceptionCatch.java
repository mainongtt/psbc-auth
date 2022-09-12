package com.psbc.common.exception;

import com.google.common.collect.ImmutableMap;

import com.psbc.common.response.CommonCode;
import com.psbc.common.response.ErrorCode;
import com.psbc.common.response.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 *
 * @author zhangkun
 * @date 2022-09-11 20:09:13
 * @return null
 */
@ControllerAdvice//控制器增强
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //定义map，配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>, ErrorCode> EXCEPTIONS;
    //定义map的builder对象，去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ErrorCode> builder = ImmutableMap.builder();

    //捕获CustomException此类异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseModel customException(CustomException customException){
        customException.printStackTrace();
        //记录日志
        LOGGER.error("catch exception:{}",customException.getMessage());
        int status = customException.getCode();
        String message = customException.getMessage();
        return new ResponseModel(status, message);
    }
    //捕获Exception此类异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseModel exception(Exception exception) {
        exception.printStackTrace();
        //记录日志
        LOGGER.error("catch exception:{}", exception.getMessage());
        if (EXCEPTIONS == null) {
            EXCEPTIONS = builder.build();//EXCEPTIONS构建成功
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ErrorCode resultCode = EXCEPTIONS.get(exception.getClass());
        if (resultCode != null) {
            return new ResponseModel(resultCode.code(), resultCode.message());
        } else {
            //返回99999异常
            return new ResponseModel(CommonCode.SERVER_ERROR.code(), CommonCode.SERVER_ERROR.message());
        }
    }

        static {
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }
}
