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

import cn.indispensable.future.async.EventModel;
import cn.indispensable.future.async.EventProducer;
import cn.indispensable.future.model.EntityType;
import cn.indispensable.future.model.HostHolder;
import cn.indispensable.future.model.User;
import cn.indispensable.future.model.ViewObject;
import cn.indispensable.future.service.FollowService;
import cn.indispensable.future.service.UserService;
import cn.indispensable.future.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关注,被关注,关注列表之类功能的controller层
 * @author cicicc
 * @since 0.0.1
 */
@Controller
public class FellowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(FellowController.class);

    /**
     * 测试页面,用于给当前用户添加关注用户
     * @return 推荐关注页面
     */
    @RequestMapping("/recommendFollow")
    public String toRecommendFollow(Model model) {
        int count = 30;
        List<User> users = userService.getUsers(count);
        List<ViewObject> viewObjects = new ArrayList<>();
        for (User user : users) {
            if (hostHolder.getUser() != null) {
                boolean followed = followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, user.getId());
                ViewObject viewObject = new ViewObject();
                viewObject.put("followed", followed);
                viewObject.put("user",user);
                Long followerCount = followService.getFollowerCount(EntityType.ENTITY_USER, user.getId());
                viewObject.put("followerCount",followerCount);
                Long followeeCount = followService.getFolloweeCount(user.getId(), EntityType.ENTITY_USER);
                viewObject.put("followeeCount",followeeCount);
                viewObjects.add(viewObject);
            }
        }
        model.addAttribute("profileUsers", viewObjects);
        return "recommendFollow";
    }

    /**
     * 关注问题
     * @param questionId 问题的id
     * @return 是否关注成功.如果用户未登陆则跳转到登录页面,否则进行关注该问题,如果关注成功,将当前用户的信息以json格式传递到页面
     */
    @RequestMapping("/followQuestion")
    @ResponseBody
    public String followQuestion(@RequestParam("questionId")int questionId) {
        try {
            User user = hostHolder.getUser();
            if (user == null) {
                return JSONUtils.getJSONString(999);
            }else {
                boolean follow = followService.follow(user.getId(), EntityType.ENTITY_QUESTION, questionId);
//                new EventProducer().fireEvent(new EventModel());
                Map<String, Object> info = new HashMap<>();
                info.put("headUrl", hostHolder.getUser().getHeadUrl());
                info.put("name", hostHolder.getUser().getName());
                info.put("id", hostHolder.getUser().getId());
                info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
                return JSONUtils.getJSONString(follow ? 0 : 1, info);
            }
        } catch (Exception e) {
            logger.error("关注问题出错"+e.getMessage());
            return JSONUtils.getJSONString(1, "关注问题出错");
        }
    }

    /**
     * 取消关注问题
     * @param questionId 问题id
     * @return json数据
     */
    @RequestMapping("/unfollowQuestion")
    @ResponseBody
    public String unFollowQuestion(@RequestParam("questionId")int questionId) {
        try {
            User user = hostHolder.getUser();
            if (user == null) {
                return JSONUtils.getJSONString(999);
            }else{
                boolean result = followService.unFollow(user.getId(), EntityType.ENTITY_QUESTION, questionId);
                // 返回关注的人数
                return JSONUtils.getJSONString(result ? 0 : 1, String.valueOf(followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId)));
            }
        } catch (Exception e) {
            logger.error("取消关注问题出错"+e.getMessage());
            return JSONUtils.getJSONString(1, "取消关注问题出错");
        }
    }

    @RequestMapping("/followUser")
    @ResponseBody
    public String followUser(@RequestParam("userId")int userId) {
        try {
            User user = hostHolder.getUser();
            if (user == null) {
                return JSONUtils.getJSONString(999);
            }else {
                boolean follow = followService.follow(user.getId(), EntityType.ENTITY_USER, userId);
//                new EventProducer().fireEvent(new EventModel());
                Map<String, Object> info = new HashMap<>();
                info.put("headUrl", hostHolder.getUser().getHeadUrl());
                info.put("name", hostHolder.getUser().getName());
                info.put("id", hostHolder.getUser().getId());
                info.put("count", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
                return JSONUtils.getJSONString(follow ? 0 : 1, info);
            }
        } catch (Exception e) {
            logger.error("关注用户出错"+e.getMessage());
            return JSONUtils.getJSONString(1, "关注用户出错");
        }
    }

    @RequestMapping(value = "/unfollowUser", method = RequestMethod.POST)
    @ResponseBody
    public String unfollowUser(@RequestParam("userId")int userId) {
        try {
            User user = hostHolder.getUser();
            if (user == null) {
                return JSONUtils.getJSONString(999);
            }else{
                boolean result = followService.unFollow(user.getId(), EntityType.ENTITY_USER, userId);
                // 返回关注的人数
                return JSONUtils.getJSONString(result ? 0 : 1, String.valueOf(followService.getFollowerCount(EntityType.ENTITY_USER, hostHolder.getUser().getId())));
            }
        } catch (Exception e) {
            logger.error("取消关注用户出错"+e.getMessage());
            return JSONUtils.getJSONString(1, "取消关注用户出错");
        }
    }

    @RequestMapping("/user/{userId}/followers")
    public String getFollowers(Model model, @PathVariable("userId")int userId) {
        List<ViewObject> viewObjects = new ArrayList<>();
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 15);
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        User currentLoginUser = hostHolder.getUser();
        putValue2Model(viewObjects, followers, currentLoginUser);
        model.addAttribute("followers", viewObjects);
        return "followers";

    }
    @RequestMapping("/user/{userId}/followees")
    public String getFollowees(Model model, @PathVariable("userId")int userId) {
        List<ViewObject> viewObjects = new ArrayList<>();
        List<Integer> followees = followService.getFollowees(userId , EntityType.ENTITY_USER,  0, 15);
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        User currentLoginUser = hostHolder.getUser();
        putValue2Model(viewObjects, followees, currentLoginUser);
        model.addAttribute("followees", viewObjects);
        return "followees";

    }

    private void putValue2Model(List<ViewObject> viewObjects, List<Integer> followees, User currentLoginUser) {
        for (Integer id : followees) {
            User user = userService.selectUserById(id);
            if (user == null) {
                continue;
            }else {
                ViewObject viewObject = new ViewObject();
                if (currentLoginUser == null) {
                    viewObject.put("followed", false);
                }else{
                    viewObject.put("followed", followService.isFollower(currentLoginUser.getId(), EntityType.ENTITY_USER, user.getId()));
                }
                viewObject.put("followerCount", followService.getFollowerCount( EntityType.ENTITY_USER, user.getId()));
                viewObject.put("followeeCount", followService.getFolloweeCount(user.getId(), EntityType.ENTITY_USER));
                viewObject.put("user", user);
                //  viewObject.put("commentCount", followService.getFolloweeCount(user.getId(), EntityType.ENTITY_USER));
                viewObjects.add(viewObject);
            }
        }
    }


}
