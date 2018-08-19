/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.indispensable.future.dao;

import cn.indispensable.future.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 问题类的dao层
 * @author cicicc
 * @since 0.0.1
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = "question";
    String INSERT_FIELDS = " title, content, user_id, created_date, comment_count ";
    String SELECT_FIELDS = "id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{title}, #{content}, #{userId}, #{createdDate}, #{commentCount})"})
    int addQuestion(Question question);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME,"where id = #{id}"})
    Question selectQuestionById(int id);


//    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where user_id = #{0} order by id desc limit #{1},#{2}"})
    List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Update({"update question set comment_count = comment_count+1 where id = #{id}"})
    void incrCommentCountById(@Param("id") int id);

}
