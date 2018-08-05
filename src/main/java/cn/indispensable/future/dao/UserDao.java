package cn.indispensable.future.dao;

import cn.indispensable.future.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


/**
 * 用作user类操作数据库的Dao层 在这个类中的主要作用是练习注解开发
 * 之后会进行重写
 */
@Mapper
public interface UserDao {

    String TABLE_NAME = "user";
    String INSERT_FILEDS = " name, password, salt, head_url";
    String SELECT_FILEDS = "id" + INSERT_FILEDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FILEDS, ") values(#{name}, #{password}, #{salt}, #{headUrl})"})
    int addUser(User user);

    @Select({"select ",INSERT_FILEDS,"from ",TABLE_NAME,"where id=#{id}"})
    User selectUserById(int userId);
}
