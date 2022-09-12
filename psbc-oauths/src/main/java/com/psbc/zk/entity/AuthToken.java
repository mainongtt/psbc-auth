package com.psbc.zk.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zhangkun
 * @version 1.0
 * @date 2022-09-07 20:00
 */
@Data
@ToString
@NoArgsConstructor
public class AuthToken {
    String access_token;//用户身份token 短的
    String refresh_token;//刷新token
    String jwt_token;//jwt令牌
}

