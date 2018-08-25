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

import cn.indispensable.future.model.*;
import cn.indispensable.future.service.*;
import cn.indispensable.future.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;

    /**
     * 添加问题
     * @param title 问题的标题
     * @param content 问题的描述,可不写
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
                return JSONUtils.getJSONString(999,"请登录");
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
        return JSONUtils.getJSONString(1,"问题添加失败,请重试");
    }

    /**
     * 从服务器中查询数据并前往该question的详情页面
     * @param questionId question的id
     * @return question的详情页面
     */
    @RequestMapping("/{questionId}")
    public String toQuestionDetail(Model model,@PathVariable("questionId")int questionId) {
        Question question = questionService.selectQuestionById(questionId);
        List<ViewObject> viewObjects = new ArrayList<>();
        User currentLoginUser = hostHolder.getUser();
        try {
            if (question == null) {
                throw new Exception();
            } else {
                //查询页面所需的用户信息,评论信息,点赞数目并将其与question信息一同封装到model对象中
                model.addAttribute("question", question);
                List<Comment> comments = commentService.SelectLatestComment(questionId, 1, 0, 10);
                List<Integer> followerUserId = followService.getFollowers(EntityType.ENTITY_QUESTION, questionId, 0, 50);
                //添加问题的评论信息到model中
                for (Comment comment : comments) {
                    User user = userService.selectUserById(comment.getUserId());
                    ViewObject viewObject = new ViewObject();
                    viewObject.put("comment", comment);
                    viewObject.put("user", user);
                    viewObject.put("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
                    if (currentLoginUser == null) {
                        viewObject.put("likeStatus", "0");
                    }else {
                        viewObject.put("likeStatus", likeService.getLikeStatus(currentLoginUser.getId(), EntityType.ENTITY_COMMENT, comment.getId()));
                    }
                    viewObjects.add(viewObject);
                }
                model.addAttribute("comments", viewObjects);
                //添加问题的关注者们的信息到model中
                List<User> users = new ArrayList<>();
                for (Integer userId : followerUserId) {
                    User userById = userService.selectUserById(userId);
                    users.add(userById);
                }
                if (currentLoginUser == null) {
                    model.addAttribute("followed", false);
                }else {
                    model.addAttribute("followed", followService.isFollower(currentLoginUser.getId(), EntityType.ENTITY_QUESTION, questionId));
                }
                model.addAttribute("followUsers", users);
            }
        } catch (Exception e) {
            logger.error("查询question出错"+ Arrays.toString(e.getStackTrace()) + Arrays.toString(e.getStackTrace()));
            return "redirect:/error";
        }
        return "detail";
    }
}
