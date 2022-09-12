package com.psbc.zk.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author zhangkun
 * @version 1.0
 * @date 2022-09-07 20:06
 */
@Data
@ToString
public class XcUser {
    private long id;
    private String username;
    private String password;
    private String id_card;
    private String tell_no;
    private String inst_id;
    private int status;
    private Date create_time;
    private String create_teller_no;
    private String create_inst_no;
    private Date mode_time;
    private String mode_teller_no;
    private String mod_inst_no;
}
