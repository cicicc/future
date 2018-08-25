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
import cn.indispensable.future.service.FollowService;
import cn.indispensable.future.service.HomeService;
import cn.indispensable.future.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页的Controller
 * @author cicicc
 * @since 0.1.1
 */

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;
    @Autowired
    private UserService userService;
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;


    /**
     * 首页的代码实现 默认向用户推荐十条数据
     * @param model 传递给页面的model对象
     * @return 定位到首页的前端页面
     */
    @RequestMapping(path = {"/","index"})
    public String index(Model model){
        List<ViewObject> viewObjects = obtainQuestions(0, 0, 10);

        model.addAttribute("viewObjects", viewObjects);
        return "index";
    }

    /**
     * 显示某个用户最近十条问题数据
     * @param model  传递给页面的model对象
     * @param userId 用户id
     * @return 定位到首页的前端页面
     */
    @RequestMapping(path = {"/user/{userId}"})
    public String index(Model model, @PathVariable("userId")int userId){
        List<ViewObject> viewObjects = obtainQuestions(userId, 0, 10);
        List<ViewObject> profileUsers = new ArrayList<>();
        User currentLoginUser = hostHolder.getUser();
        List<Integer> followees = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 15);
        for (Integer id : followees) {
            User user = userService.selectUserById(id);
            //判断Redis中所存储的这个用户id是否仍旧存在于数据库中
            if (user != null) {
                ViewObject viewObject = new ViewObject();
                //判断当前登录的用户是否关注了该用户,如果当前用户未登录的话,直接设置为未关注
                if (currentLoginUser != null) {
                    viewObject.put("followed", followService.isFollower(currentLoginUser.getId(), EntityType.ENTITY_USER, user.getId()));
                }else{
                    //未登录状态下,用户点击关注会跳转到登录页面
                    viewObject.put("followed", false);
                }
                viewObject.put("user",user);
                viewObject.put("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER, user.getId()));
                viewObject.put("followeeCount",followService.getFolloweeCount(user.getId(), EntityType.ENTITY_USER));
                profileUsers.add(viewObject);
            }else{
                continue;
            }
        }
        model.addAttribute("profileUsers", profileUsers);
        model.addAttribute("viewObjects", viewObjects);
        return "profile";
    }

    /**
     * 类中私有方法 将查询到的数据封装到ViewObject对象中并返回
     * @param userId 用户id
     * @param offset 偏移量
     * @param limit 查询的数据条数
     * @return List<ViewObject>封装到List集合中的ViewObject对象
     */
    private List<ViewObject> obtainQuestions(int userId, int offset, int limit) {
        List<ViewObject> viewObjects = new ArrayList<>();
        List<Question> questions = homeService.selectLatestQuestions(userId, offset, limit);
        for (Question question:questions) {
            User user = homeService.selectUserById(question.getUserId());
            ViewObject viewObject = new ViewObject();
            viewObject.put("question", question);
            viewObject.put("user", user);
                viewObject.put("followerCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            viewObjects.add(viewObject);
        }
        return viewObjects;
    }

}
