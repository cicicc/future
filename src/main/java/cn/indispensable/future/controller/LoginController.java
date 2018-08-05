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

import cn.indispensable.future.model.Ticket;
import cn.indispensable.future.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录以及注册页面的代码
 * 吐槽一下 我将在2.0版本中更改这个页面
 * 将登录页面与注册页面分开
 * @author cicicc
 * @since 0.0.1
 */

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(path = {"/register", "/login"})
    public String toLogin(Model model) {

        return "login";
    }

    /**
     * 用户提交登录数据 经由这个表单,仅可以进行post提交
     * @return 成功登录即跳转到首页,登录失败则跳转回登录页并显示错误信息
     */
    @RequestMapping(path = {"/login/"},method = {RequestMethod.POST})
    public String Login(Model model, HttpServletRequest request, HttpServletResponse response){
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ( StringUtils.isEmpty(username)) {
            model.addAttribute("msg", "请输入用户名");
            return "redirect:/login";
        }else if (StringUtils.isEmpty(password)) {
            model.addAttribute("msg", "请输入密码");
            return "redirect:/login";
        }else{
            //用户并未输入空字符串或未输入数据作为用户名和密码
            Ticket ticket = loginService.selectUserByPassword(username, password);
        }


        return null;
    }

}
