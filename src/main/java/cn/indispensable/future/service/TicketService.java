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
package cn.indispensable.future.service;

import cn.indispensable.future.dao.LoginTicketDAO;
import cn.indispensable.future.model.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 为登录用户生成了的cookie信息
 * 取出登录和注册的共调用方法,不够合理
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class TicketService {

    @Autowired
    private LoginTicketDAO ticketDAO;

    /**
     * 向ticket数据库中添加一条ticket数据,并将ticket数据返回给controller层,以便controller层将数据放入cookie中
     * @param userId 用户id
     * @return LoginTicket
     */
    public LoginTicket addTicket(int userId,boolean rememberme) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-",""));
        Date date = new Date();
        if (rememberme) {
            date.setTime(date.getTime() + 1000L * 3600 * 24 * 60);
        } else {
            date.setTime(date.getTime() + 1000L * 3600 * 24 * 15);
        }
        loginTicket.setExpired(date);
        ticketDAO.addLoginTicket(loginTicket);
        return loginTicket;
    }

    /**
     * 通过用户id查询此用户是否已在数据库中存在为状态为0的ticket
     * 如果存在,将其状态设为1
     * @param userId 用户id
     */
    public void updateTicketStatusByUserId(int userId) {
        List<LoginTicket> ticketList = ticketDAO.selectTicketByIdAndStatus(userId, 0);
        if (ticketList != null && ticketList.size() > 0) {
            for (LoginTicket ticket : ticketList) {
                ticketDAO.updateTicketStatusById(ticket.getId(), 1);
            }
        }
    }

    /**
     * 根据ticket值查询用户的登录票(loginTicket)
     * @param ticket ticket的值
     * @return LoginTicket
     */
    public LoginTicket selectLoginTicketByTicket(String ticket) {
        return ticketDAO.selectByTicket(ticket);

    }
    /**
     * 登出
     * @param ticket cookie中的ticket值
     */
    public void loginout(String ticket) {
        ticketDAO.updateTicketStatus(ticket, 1);
    }

}
