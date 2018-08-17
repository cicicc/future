package cn.indispensable.future.dao;


import cn.indispensable.future.model.Comment;
import cn.indispensable.future.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * comment的DAO层数据,用来操作评论数据
 */
@Mapper
public interface CommentDAO {
    /**
     *    private int id;
     *     private String content;
     *     private int userId;
     *     private int entityId;
     *     private int entityType;
     *     private Date createdDate;
     *     private int status;
     */

    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " content, user_id, entity_id, entity_type, created_date, status ";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{content}, #{userId}, #{entityId}, #{entityType}, #{createdDate}, #{status})"})
    int addQuestion(Comment comment);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME})
    Question selectCommentById(int commentId);

    List<Question> selectLatestComments(@Param("entityId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);



}
