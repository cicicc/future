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

import cn.indispensable.future.model.Comment;
import cn.indispensable.future.model.EntityType;
import cn.indispensable.future.model.HostHolder;
import cn.indispensable.future.service.CommentService;
import cn.indispensable.future.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Date;

/**
 * @author cicicc
 * @since 0.0.1
 */
@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController .class);

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private QuestionService questionService;

    /**
     * 添加评论
     * @param questionId 问题id,(这里考虑的不够全面,应该还要考虑可以回复评论的情况)
     * @param content 回复内容
     * @return 问题详情页面
     */
    @RequestMapping(value = "/comment/addComment", method = RequestMethod.POST)
//    @ResponseBody
    public String addComment(@RequestParam("questionId") int questionId, @RequestParam("content") String content) {
        try {
            if (hostHolder.getUser() == null) {
                //用户未登陆,跳转至登陆页面
//                return JSONUtils.getJSONString(999);
                return "redirect:/login?next=/question/" + questionId;
            } else {
                //用户已登录,更新评论,并刷新页面
                Comment comment = new Comment();
                comment.setCreatedDate(new Date());
                comment.setContent(content);
                comment.setEntityId(questionId);
                comment.setUserId(hostHolder.getUser().getId());
                comment.setEntityType(EntityType.ENTITY_QUESTION);
                if (commentService.addComment(comment) <= 0) {
                    throw new Exception();
                }else{
                    //更新question中的评论数
                    questionService.incrCommentCountById(questionId);
                }
            }
        } catch (Exception e) {
            logger.error("添加评论失败" + e.getMessage());
//            return JSONUtils.getJSONString(1, "添加评论出错");
        }
//        return JSONUtils.getJSONString(0);
        return "redirect:/question/" + String.valueOf(questionId);
    }
}
