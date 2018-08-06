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

import cn.indispensable.future.DAO.UserDAO;
import cn.indispensable.future.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录页面对应service
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
    @Autowired
    UserDAO userDAO;

    public User selectUserById(int userId){

        return userDAO.selectUserById(userId);
    }


    public Map<String, Object> selectUserByNameAndPass(String username, String password) {
        User user = userDAO.selectUserByNameAndPass(username, password);
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            //如果未查询到这个用户的话,将错误信息封装到map中返回给controller层进行处理
            map.put("msg", "您输入的用户名或密码错误");
        }else{
            map.put("user", user);
        }
        return map;
    }
}
