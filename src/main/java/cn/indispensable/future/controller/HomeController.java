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

import cn.indispensable.future.model.Question;
import cn.indispensable.future.model.User;
import cn.indispensable.future.model.ViewObject;
import cn.indispensable.future.service.QuestionService;
import cn.indispensable.future.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private UserService userService;
    @Autowired
    private QuestionService questionService;

    @RequestMapping(path = {"/","index"})
    public String index(Model model){
        List<ViewObject> viewObjects = new ArrayList<>();
        List<Question> questions = questionService.selectLatestQuestions(0, 0, 10);
        for (Question question:questions) {
            User user = userService.selectUserById(question.getUserId());
            ViewObject viewObject = new ViewObject();
            viewObject.put("question", question);
            viewObject.put("user", user);
            viewObjects.add(viewObject);
        }
        model.addAttribute("viewObjects", viewObjects);
        return "index";
    }

}
