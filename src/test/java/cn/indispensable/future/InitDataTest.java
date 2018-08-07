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
package cn.indispensable.future;

import cn.indispensable.future.DAO.QuestionDAO;
import cn.indispensable.future.DAO.UserDAO;
import cn.indispensable.future.model.Question;
import cn.indispensable.future.model.User;
import cn.indispensable.future.utils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 *测试类 给数据库添加数据
 * @author cicicc
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FutureApplication.class)
@Sql("/init-schema.sql")
public class InitDataTest {
    //这个实际上是可以注入的 但是IDEA提示无法注入 所以把这个错误屏蔽掉吧
    @Autowired
    UserDAO userDao;
    @Autowired
    QuestionDAO questionDao;


    @Test
    public void addDataToDataBase() {
        for (int i = 0; i < 20; i++) {
            //随机插入用户数据
            User user = new User();
            Random random = new Random();
            Date date = new Date();
            user.setName("user"+i);
            user.setSalt(UUID.randomUUID().toString().replace("-", "").substring(0,10));
            user.setPassword(MD5Util.MD5(String.format("pass%f", random.nextFloat())+user.getSalt()));
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            userDao.addUser(user);
            //随机创建问题数据
            Question question = new Question();
            question.setTitle("A randomly generated test title, please don't worry about this"+i);
            date.setTime(date.getTime() + 1000 * 3600 * random.nextInt(50) * i);
            question.setCreatedDate(date);
            question.setUserId(i+1);
            question.setContent("为了使这一段文字看起来足够的长,"+random.nextDouble()+"以便能够满足文章获得摘要的需求,我就这样瞎写了一堆东西.然后进行复制.为了使这一段文字看起来足够的长,以便能够满足文章获得摘要的需求,我就这样瞎写了一堆东西.然后进行复制.为了使这一段文字看起来足够的长,以便能够满足文章获得摘要的需求,我就这样瞎写了一堆东西.然后进行复制.");
            questionDao.addQuestion(question);

        }
    }
}
