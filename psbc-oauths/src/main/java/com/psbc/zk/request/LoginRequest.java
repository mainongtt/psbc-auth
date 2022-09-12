package com.psbc.zk.request;

import lombok.Data;
import lombok.ToString;

/**
 * @author zhangkun
 * @version 1.0
 * @date 2022-09-07 20:30
 */
@Data
@ToString
public class LoginRequest extends RequestData {

    String username;
    String password;
    String vertifycode;

}
