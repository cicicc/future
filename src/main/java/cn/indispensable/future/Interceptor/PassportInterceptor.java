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
package cn.indispensable.future.Interceptor;

import cn.indispensable.future.model.HostHolder;
import cn.indispensable.future.model.LoginTicket;
import cn.indispensable.future.model.User;
import cn.indispensable.future.service.TicketService;
import cn.indispensable.future.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 定义拦截器,判断用户是否已经登录,根据ticket是否存在来判断
 * @author cicicc
 * @since 0.0.1
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private HostHolder hostHolder;


    /***
     * 用来判断用户传递的cookie信息中是否有已登录用户的cookie信息
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        Cookie[] cookies = httpServletRequest.getCookies();
        String ticket = null;
        //获取ticket的值
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie:cookies) {
                if (cookie.getName() .equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        //根据ticket的值寻找对应用户
        if (ticket != null) {
            LoginTicket loginTicket = ticketService.selectLoginTicketByTicket(ticket);
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                return true;
            }
            User user = userService.selectUserById(loginTicket.getUserId());
            hostHolder.setUsers(user);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null && hostHolder.getUser() != null) {
            modelAndView.addObject("user", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        hostHolder.clear();
    }
}
