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

import cn.indispensable.future.DAO.LoginTicketDAO;
import cn.indispensable.future.model.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 为登录用户生成了的cookie信息
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
    public LoginTicket addTicket(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-",""));
        ticketDAO.addLoginTicket(loginTicket);
        return loginTicket;
    }
}
