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

import cn.indispensable.future.dao.UserDAO;
import cn.indispensable.future.model.User;
import cn.indispensable.future.utils.MD5Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * user的service层 作为controller层和dao层的交互媒介 提供相关的服务
 * @author cicicc
 * @since 0.0.1
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    public Map<String, Object> selectUserByNameAndPass(String username, String password) {
        User user = userDAO.selectUserByName(username);
        Map<String, Object> map = new HashMap<>();
        if (user.getPassword().equals(MD5Utils.MD5(password + user.getSalt()))) {
            map.put("user", user);
        }else{
            //如果查询的条件不满足,将错误信息封装到map中返回给controller层进行处理
            map.put("msg", "您输入的用户名或密码错误");
        }
        return map;
    }



    public User selectUserById(int userId){

        return userDAO.selectUserById(userId);
    }
    /**
     * 查找用户名在数据库中是否存在
     * @param username 用户姓名
     * @return  Map<String,Object>
     */
    public Map<String,Object> selectUserByName(String username) {
        Map<String, Object> map = new HashMap<>();
        User user = userDAO.selectUserByName(username);
        if (user != null) {
            //返回页面用户名已经被占用的信息
            map.put("msg", "此用户名已被占用");
        }
        return map;
    }

    /**
     * 添加用户
     * @return 封装了数据的User对象
     */
    public User addUser(String username, String password) {
        User user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().replace("-","").substring(0,10));
        user.setPassword(MD5Utils.MD5(password+user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userDAO.addUser(user);
        return user;
    }
}
