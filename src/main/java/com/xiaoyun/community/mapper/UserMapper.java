package com.xiaoyun.community.mapper;

import com.xiaoyun.community.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Update("insert into  user (name,account_id,token,gmt_create,gmt_modified) value (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    void insert(User user);

    @Select(("select * from user where token = #{token}"))
    User findByToken(@Param("token") String token);
}
