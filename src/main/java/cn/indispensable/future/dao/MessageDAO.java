package cn.indispensable.future.dao;

import cn.indispensable.future.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 信息中心的DAO层,操作message表
 */
@Mapper
public interface MessageDAO {
    /**
     * CREATE TABLE `message` (
     *   `id` int(11) NOT NULL AUTO_INCREMENT,
     *   `from_id` int(11) DEFAULT NULL,
     *   `to_id` int(11) DEFAULT NULL,
     *   `content` text,
     *   `created_date` datetime DEFAULT NULL,
     *   `has_read` int(11) DEFAULT NULL,
     *   `conversation_id` varchar(45) NOT NULL,
     *   PRIMARY KEY (`id`),
     *   KEY `conversation_index` (`conversation_id`),
     *   KEY `created_date` (`created_date`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */

    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead}, #{conversationId})"})
    int addMessage(Message message);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME,"where id = #{id}"})
    Message selectMessageById(int id);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME,"where conversation_id = #{conversationId} "})
    List<Message> selectMessageByConversationId(@Param("conversationId") String conversationId);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where conversation_id = #{conversation_id} order by id desc limit #{offset},#{limit}"})
    List<Message> selectLatestMessages(@Param("conversationId") int conversationId, @Param("offset") int offset, @Param("limit") int limit);

    //最终SQL: select from_id, to_id, content, created_date, has_read, conversation_id ,count(id) as id from ( select * from  message
    //   where from_id=22 or to_id=22 order by id desc) tt group by conversation_id  order by created_date desc limit 0,10
    @Select({"select ", INSERT_FIELDS, " ,count(id) as id from ( select * from ", TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by id desc) tt group by conversation_id  order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from", TABLE_NAME, " where has_read = 0 and to_id = #{toId} and conversation_id =#{conversationId}"})
    int getUnreadMessageCount(@Param("toId") int toId, @Param("conversationId") String ConversationId);

    @Update({"update ", TABLE_NAME, "set has_read = 1 where id=#{id}"})
    void updateHasReadStatus(@Param("id") int id);
}
