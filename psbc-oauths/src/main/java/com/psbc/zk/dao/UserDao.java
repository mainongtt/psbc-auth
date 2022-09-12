package com.psbc.zk.dao;


import com.psbc.zk.entity.XcUserExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;



@Mapper
public interface UserDao {
    XcUserExt getUser(@Param("username") String username);
}
