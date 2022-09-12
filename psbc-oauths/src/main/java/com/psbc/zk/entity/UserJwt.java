package com.psbc.zk.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @author Gavino
 * @version 1.0
 * @date 2022-09-07 19:53
 */

@ToString
@Getter
@Setter
public class UserJwt extends User {

    private String id;
    private String name;
//    private String userpic;
//    private String utype;
//    private String companyId;



    public UserJwt(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
