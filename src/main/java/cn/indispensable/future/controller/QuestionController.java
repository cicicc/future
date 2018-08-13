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
package cn.indispensable.future.controller;

import cn.indispensable.future.model.HostHolder;
import cn.indispensable.future.model.Question;
import cn.indispensable.future.service.QuestionService;
import cn.indispensable.future.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 问题的相关处理的controller层
 * @author cicicc
 * @since 0.0.1
 */
@Controller
@RequestMapping("/question")
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    private QuestionService questionService;
    @Autowired
    HostHolder hostHolder;



    /**
     * 添加问题
     * @param title 问题的标题
     * @param content 问题的内容,可不写
     * @return ajax加载数据,返回code
     */
    @RequestMapping(path = {"/add"})
    @ResponseBody
    public String addQuestion(@RequestParam("title")String title,
                              @RequestParam(value = "content",defaultValue = "")String content){
        try {
            Question question = new Question();
            if (hostHolder != null) {
                question.setUserId(hostHolder.getUser().getId());
            } else {
                //用户未登录,将跳转至登录页面,一般情况下用户未登录页面是没有提问按钮的
                return JSONUtils.getJSONString(999);
            }
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            if (questionService.addQuestion(question) > 0) {

                //问题添加成功
                return JSONUtils.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("添加问题失败"+e.getMessage());
        }
        //问题添加失败才会在这里返回
        return JSONUtils.getJSONString(1,"问题添加失败,请重试");
    }
}
