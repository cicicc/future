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
import cn.indispensable.future.model.Message;
import cn.indispensable.future.model.User;
import cn.indispensable.future.model.ViewObject;
import cn.indispensable.future.service.MessageService;
import cn.indispensable.future.service.SensitiveService;
import cn.indispensable.future.service.UserService;
import cn.indispensable.future.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author cicicc
 * @since 0.0.1
 */
@Controller
@RequestMapping("/msg")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private SensitiveService sensitiveService;
    @Autowired
    private UserService userService;
    /**
     * 查询私信的列表
     * @param model 封装查询数据的model对象
     * @param request 获取请求路径的request1对象
     * @return 私信列表
     */
    @RequestMapping("/list")
    public String toMessageList(Model model, HttpServletRequest request) {
        User currentUser = hostHolder.getUser();
        List<ViewObject> viewObjects = new ArrayList<>();
        if (currentUser == null) {
            return "redirect:/login?next=" + request.getRequestURI();
        }else{
            List<Message> conversationList = messageService.getConversationList(currentUser.getId(), 0, 10);
            for (Message message : conversationList) {
                int targetId = message.getFromId() == currentUser.getId() ? message.getToId() : message.getFromId();
                User conversationUser = userService.selectUserById(targetId);
                int unreadMessageCount = messageService.getUnreadMessageCount(currentUser.getId(), message.getConversationId());
                ViewObject viewObject = new ViewObject();
                viewObject.put("user", conversationUser);
                viewObject.put("message", message);
                viewObject.put("unreadMessageCount", unreadMessageCount);
                viewObjects.add(viewObject);
            }
            model.addAttribute("conversationList", viewObjects);
        }
        return "letter";
    }

    /**
     * 添加消息
     * @param toName 接收者姓名
     * @param content 发送的文本内容
     * @param request 获取请求路径的request请求
     * @return
     */
    @RequestMapping(value = "/addMessage",method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(@RequestParam("toName")String toName, @RequestParam("content")String content, HttpServletRequest request){
        try {
            if (hostHolder.getUser() == null) {
                return JSONUtils.getJSONString(999);
            } else {
                User user = userService.selectUserByName(toName);
                if (user == null) {
                    throw new Exception("用户名不存在");
                }
                Message message = new Message();
                message.setFromId(hostHolder.getUser().getId());
                message.setToId(user.getId());
                message.setContent(sensitiveService.filter(HtmlUtils.htmlEscape(content)));
                message.setHasRead(0);
                message.setConversationId(message.getConversationId());
                message.setCreatedDate(new Date());
                messageService.addMessage(message);
                return JSONUtils.getJSONString(0);

            }
        } catch (Exception e) {
            logger.error("添加消息失败"+e.getMessage());
            return JSONUtils.getJSONString(1,"添加消息失败,请检查用户名是否存在");
        }
    }

    @RequestMapping("/detail")
    public String messageDetail(Model model, @RequestParam("conversationId") String conversationId) {
        List<Message> messages = messageService.getMessageByConversationId(conversationId);
        User currentUser = hostHolder.getUser();
        try {
            if (messages != null) {
                //获取与当前用户通信用户的id
                int targetId = messages.get(0).getFromId() == currentUser.getId() ? messages.get(0).getToId() : messages.get(0).getFromId();
                User targetUser = userService.selectUserById(targetId);
                //更新私信的状态
                for (Message message : messages) {
                    if (message.getHasRead() == 0) {
                        messageService.updateHasReadStatu(message.getId());
                    }
                }
                model.addAttribute("targetUser", targetUser);
                model.addAttribute("messages", messages);
            }
        } catch (Exception e) {
            logger.error("查询私信详细信息出錯"+e.getMessage());
        }
        return "letterDetail";
    }

}
