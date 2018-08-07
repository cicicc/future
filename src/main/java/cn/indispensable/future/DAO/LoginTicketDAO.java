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
package cn.indispensable.future.DAO;

import cn.indispensable.future.model.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 问题类的dao层
 * @author cicicc
 * @since 0.0.1
 */
@Mapper
public interface LoginTicketDAO {

    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = " user_id, ticket, expired, status ";
    String SELECT_FIELDS = "id," + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values(#{userId}, #{ticket}, #{expired}, #{status})"})
    int addLoginTicket(LoginTicket loginTicket);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME})
    LoginTicket selectLoginTicketById(int LoginTicketId);

    @Select({"select ",SELECT_FIELDS,"from ",TABLE_NAME, "where user_id=#{0} and status=#{1}"})
    List<LoginTicket> selectTicketByIdAndStatus(int userId, int i);

    @Update({"update ",TABLE_NAME,"set status = #{1} where id=#{0}"})
    void updateTicketStatusById(int id, int status);

    /**
     * 此处存在一个bug,ticket可能重复,并且根据ticket查找效率较低,可以考虑下个版本重构
     */
    @Update({"update ",TABLE_NAME,"set status = #{1} where ticket=#{0}"})
    void updateTicketStatus(String ticket, int status);

}
