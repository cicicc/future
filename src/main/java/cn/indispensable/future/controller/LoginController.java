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

import cn.indispensable.future.model.LoginTicket;
import cn.indispensable.future.model.User;
import cn.indispensable.future.service.LoginService;
import cn.indispensable.future.service.RegisterService;
import cn.indispensable.future.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 登录以及注册页面的代码
 * 我将在2.0版本中更改这个页面
 * 将登录页面与注册页面分开,所以在这个页面中不将两处代码进行合并
 * @author cicicc
 * @since 0.0.1
 */

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RegisterService registerService;

    @RequestMapping(path = {"/register", "/login"})
    public String toLogin() {

        return "login";
    }

    /**
     * 用户提交登录数据 经由这个表单,仅可以进行post提交
     * @param model 用于封装传递给前端的数据的对象
     * @param username 用户名
     * @param password 密码
     * @param next 是否有原先要访问的页面
     * @param rememberme 记住我选项的勾选与否,默认false
     * @param response response对象
     * @return 成功登录即跳转到首页,登录失败则跳转回登录页并显示错误信息并停留在登录页面
     */
    @RequestMapping(path = {"/login/"},method = {RequestMethod.POST})
    public String Login(Model model, @RequestParam("username")String username,
                        @RequestParam("password")String password,
                        @RequestParam(value = "next", required = false)String next,
                        @RequestParam(value = "rememberme", defaultValue = "false")boolean rememberme,
                        HttpServletResponse response){
        // 这两个if在前端中就应该进行判断
        if ( StringUtils.isEmpty(username)) {
            model.addAttribute("msg", "请输入用户名");
            return "redirect:/login";
        }else if (StringUtils.isEmpty(password)) {
            model.addAttribute("msg", "请输入密码");
            return "redirect:/login";
        }else{
            //用户并未输入空字符串或未输入数据作为用户名和密码
            Map<String, Object> map = loginService.selectUserByNameAndPass(username, password);
            if (map.containsKey("msg")) {
                //用户名或者密码输入错误
                model.addAttribute("msg", map.get("msg"));
                return "redirect:/login";
            } else {
                User user = (User) map.get("user");
                addTicket(user, rememberme, response);
                if (next != null) {
                    //这里应该对next进行判断,因为在一般的情况下我们并不希望用户提交的next路径是非本站的
                    //因为这样的话,如果有人设置了链接指向自己的网站的话,就可以直接获取到我们用户的cookie信息了
                    return "redirect:/" + next;
                }
            }
        }
        return "index";
    }
    /**
     * 用户提交注册数据 经由这个表单,仅可以进行post提交
     * @param model 用于封装传递给前端的数据的对象
     * @param username 用户名
     * @param password 密码
     * @param next 是否有原先要访问的页面
     * @param rememberme 记住我选项的勾选与否,默认false
     * @param response response对象
     * @return 成功注册即自动登录并跳转到next页面或首页,注册失败则扔停留在注册页面并保存错误信息
     */
    @RequestMapping(path = {"/register/"},method = {RequestMethod.POST})
    public String register(Model model, @RequestParam("username")String username,
                        @RequestParam("password")String password,
                           @RequestParam("next")String next,
                        @RequestParam(value = "rememberme",defaultValue = "false")boolean rememberme,
                        HttpServletResponse response){
        // 这两个if在前端中就应该进行判断
        if ( StringUtils.isEmpty(username)) {
            model.addAttribute("msg", "请输入用户名");
            return "redirect:/register";
        }else if (StringUtils.isEmpty(password)) {
            model.addAttribute("msg", "请输入密码");
            return "redirect:/register";
        }else{
            //判断用户名是否被占用
            Map<String, Object> map = registerService.selectUserByName(username);
            if (map.containsKey("msg")) {
                //用户名已经存在
                model.addAttribute("msg", map.get("msg"));
                return "redirect:/register";
            } else {
                User user = (User) map.get("user");
                addTicket(user, rememberme, response);
                if (next != null) {
                    //这里应该对next进行判断,因为在一般的情况下我们并不希望用户提交的next路径是非本站的
                    //因为这样的话,如果有人设置了链接指向自己的网站的话,就可以直接获取到我们用户的cookie信息了
                    return "redirect:/" + next;
                }
            }
        }
        return "index";
    }

    /**
     * 公共方法 添加ticket
     */
    private void addTicket(User user, boolean rememberme, HttpServletResponse response) {

        //获得ticket,由response传递回浏览器
        LoginTicket loginTicket = ticketService.addTicket(user.getId());
        Cookie ticketCookie = new Cookie("ticket", loginTicket.getTicket());
        ticketCookie.setPath("/");
        //判断用户是否选择了记住我,两者的cookie信息保存时间不同
        if (rememberme) {
            ticketCookie.setMaxAge(3600 * 24 * 60);
        } else {
            ticketCookie.setMaxAge(3600 * 24 * 15);
        }
        response.addCookie(ticketCookie);
    }


}
