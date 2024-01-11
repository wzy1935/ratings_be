package com.sc.ratings.mappers;

import com.sc.ratings.entities.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select id, name, password, is_admin from app_user where name=#{name} limit 1")
    UserEntity getUserByName(String name);

    @Insert("insert into app_user (name, password, is_admin) values (#{name}, #{password}, #{is_admin})")
    void addUser(UserEntity user);

    @Update("update app_user set name=#{name},password=#{password},is_admin=#{is_admin} where id =#{id}")
    void updateUserById(UserEntity user);
}
