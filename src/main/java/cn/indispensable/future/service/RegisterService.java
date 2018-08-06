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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册的service层
 * @author cicicc
 * @since 0.0.1
 *
 */
@Service
public class RegisterService {

    @Autowired
    private UserDAO userDAO;

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
}
