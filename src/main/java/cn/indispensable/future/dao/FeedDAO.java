package cn.indispensable.future.dao;

import cn.indispensable.future.model.Comment;
import cn.indispensable.future.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedDAO {
    /**
     * CREATE TABLE `feed` (
     * `id` int(11) NOT NULL AUTO_INCREMENT,
     * `created_date` datetime DEFAULT NULL,
     * `user_id` int(11) DEFAULT NULL,
     * `data` tinytext,
     * `type` int(11) DEFAULT NULL,
     * PRIMARY KEY (`id`),
     * KEY `user_index` (`user_id`)
     * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */
    String TABLE_NAME = "feed";
    String INSERT_FIELDS = "  user_id, data, type, created_date ";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values( #{userId}, #{data}, #{type}, #{createdDate})"})
    int addFeed(Feed feed);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME, "where id =#{id} "})
    Feed selectFeedById(@Param("id")int id);


    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);

}
