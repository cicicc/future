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
package cn.indispensable.future.async.handles;

import cn.indispensable.future.async.EventConsumer;
import cn.indispensable.future.async.EventHandler;
import cn.indispensable.future.async.EventModel;
import cn.indispensable.future.async.EventType;
import cn.indispensable.future.model.Message;
import cn.indispensable.future.model.User;
import cn.indispensable.future.service.MessageService;
import cn.indispensable.future.service.UserService;
import cn.indispensable.future.utils.ConstantUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 当用户关注问题时,
 * @author cicicc
 * @since 0.0.1
 */
@Component
public class FollowHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(FollowHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;
    @Override
    public void doHandle(EventModel model) {
        try {
            User user = userService.selectUserById(model.getActorId());
            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(ConstantUtils.SYSTEM_ADMIN_ID);
            message.setToId(model.getEntityOwnerId());
            message.setConversationId(message.getConversationId());
            message.setHasRead(0);
            message.setContent("您好,用户<a href = \"/user/"+user.getId()+"\">" + user.getName() + "</a>关注了您" );
            messageService.addMessage(message);
        } catch (Exception e) {
            logger.error("站内信发送出现错误"+e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
