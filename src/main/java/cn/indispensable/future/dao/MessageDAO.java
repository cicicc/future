package cn.indispensable.future.dao;

import cn.indispensable.future.model.Message;
import cn.indispensable.future.model.Question;
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
    Question selectMessageById(int id);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME,"where Conversatio_id = #{ConversationId}"})
    Question selectMessageByConversationId(@Param("conversationId") int conversationId);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where conversation_id = #{conversation_id} order by id desc limit #{offset},#{limit}"})
    List<Message> selectLatestMessages(@Param("conversationId") int conversationId, @Param("offset") int offset,
                                       @Param("limit") int limit);


}
