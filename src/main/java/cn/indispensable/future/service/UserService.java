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

/**
 * user的service层 作为controller层和dao层的交互媒介 提供相关的服务
 * 仅做测试使用 代码实际应按照功能提供service层类
 * @author cicicc
 * @since 0.0.1
 */
@Service
@Deprecated
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserDAO userDao;

    public User selectUserById(int userId){

        return userDao.selectUserById(userId);
    }

}
