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

import cn.indispensable.future.dao.QuestionDAO;
import cn.indispensable.future.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * question的Service层
 * @author cicicc
 * @since 0.1.1
 */
@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);
    @Autowired
    private QuestionDAO questionDAO;
    @Autowired
    private SensitiveService sensitiveService;

    public List<Question> selectLatestQuestions(int userId,int offset, int limit){
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    /**
     * 对要添加的问题进行相应的处理,比如敏感词的过滤等,并调用dao层将数据保存到数据库中
     * @param question 包含着问题相关信息的question对象
     * @return 成功返回大于1的数(正常来说是1)
     */
    public int addQuestion(Question question) {
        //过滤敏感词
        question.setTitle(sensitiveService.filter(HtmlUtils.htmlEscape(question.getTitle())));
        question.setContent(sensitiveService.filter(HtmlUtils.htmlEscape(question.getContent())));
        return questionDAO.addQuestion(question);

    }

    public Question selectQuestionById(int questionId) {

        return questionDAO.selectQuestionById(questionId);
    }

    public void incrCommentCountById(int questionId) {
        questionDAO.incrCommentCountById(questionId);
    }
}
