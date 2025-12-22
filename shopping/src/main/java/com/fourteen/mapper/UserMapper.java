package com.fourteen.mapper;

import com.fourteen.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select user_id, username, email, password, role, avatar, status, created_at, updated_at from user where username=#{username}")
    User findByUsername(String username);

    @Select("select user_id, username, email, password, role, avatar, status, created_at, updated_at from user where email=#{email}")
    User findByEmail(String email);

    @Insert("insert into user ( username, email, password, role, avatar, status, created_at, updated_at) " +
            "VALUES (#{username},#{email},#{password},#{role},#{avatar},#{status},#{createdAt},#{updatedAt})")
    void addUser(User user);

    @Select("select user_id, username, email, password, role, avatar, status, created_at, updated_at " +
            "from user where email=#{email} and password=#{password}")
    User findByEmailAndPassword(String email,String password);

    @Select("select user_id, username, email, password, role, avatar, status, created_at, updated_at " +
            "from user where username=#{username} and password=#{password}")
    User findByUsernameAndPassword(String username,String password);

    @Select("select user_id, username, email, password, role, avatar, status, created_at, updated_at " +
            "from user where user_id=#{userId}")
    User findByUserId(Integer userId);

    @Update("update user set username=#{username} where user_id=#{userId}")
    void updateUsername(Integer userId, String username);
    
    @Update("update user set avatar=#{avatarUrl}, updated_at=CURRENT_TIMESTAMP where user_id=#{userId}")
    void updateAvatar(Integer userId, String avatarUrl);
}
