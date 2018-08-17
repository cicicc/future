package cn.indispensable.future.dao;

import cn.indispensable.future.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;


/**
 * 用作user类操作数据库的Dao层 在这个类中的主要作用是练习注解开发
 * 之后会进行重写
 */
@Mapper
public interface UserDAO {

    String TABLE_NAME = "user";
    String INSERT_FIELDS = " name, password, salt, head_url";
    String SELECT_FIELDS = "id," + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{name}, #{password}, #{salt}, #{headUrl})"})
    @Options(useGeneratedKeys = true)
    int addUser(User user);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME,"where id=#{id}"})
    User selectUserById(int userId);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME,"where name=#{0} and password=#{1}"})
    User selectUserByNameAndPass(String name, String password);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME,"where name=#{name}"})
    User selectUserByName(String name);
}
